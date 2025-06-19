package dev.curseforged.strawberryChat

import dev.curseforged.strawberryChat.serverMetadata.PingHandler
import net.kyori.adventure.key.Key
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitScheduler
import io.papermc.paper.network.ChannelInitializeListenerHolder.addListener
import io.netty.channel.Channel

class StrawberryChat : JavaPlugin() {
    
    companion object {
        lateinit var pluginConfig: FileConfiguration
            private set
        lateinit var scheduler: BukkitScheduler
            private set
    }

    override fun onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig()
        pluginConfig = this.config
        scheduler = this.server.scheduler
        // set companion object config to plugin config
        if (pluginConfig.getBoolean("chat-formatter")) {
            server.pluginManager.registerEvents(ChatListener(), this)
        }
        server.pluginManager.registerEvents(PlayerJoinListener(), this)
        if (pluginConfig.getBoolean("disallow-kill-potions")) {
            server.pluginManager.registerEvents(PotionUseListener(), this)
        }
        if (pluginConfig.getBoolean("send-prevents-reports")) {
            addListener(Key.key("strawberrychat", "ping_handler")) { channel: Channel ->
                channel.pipeline().addAfter("packet_handler", "strawberrychat_ping_handler", PingHandler())
            }
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
