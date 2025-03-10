package dev.curseforged.strawberryChat

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitScheduler

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
        server.pluginManager.registerEvents(ChatListener(), this)
        server.pluginManager.registerEvents(PlayerJoinListener(), this)
        server.pluginManager.registerEvents(PotionUseListener(), this)
        
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
