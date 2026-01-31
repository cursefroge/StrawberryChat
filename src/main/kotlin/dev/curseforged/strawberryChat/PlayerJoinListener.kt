package dev.curseforged.strawberryChat

import dev.curseforged.strawberryChat.serverMetadata.InboundHandler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener : Listener {
    // on player join
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        // tell player they have permission level 2
        /*if (StrawberryChat.pluginConfig.getBoolean("spoof-op")) {
            event.player.sendOpLevel(2)
        }*/

        if (StrawberryChat.pluginConfig.getBoolean("tablist.enabled")) {
            // send header and footer for tablist
            val header: Component = MiniMessage.miniMessage().deserialize(
                "<bold><gradient:#ff0000:#ff69b4>The Strawberry Farm</gradient></bold>"
            )
            val randomQuote = StrawberryChat.pluginConfig.getStringList("tablist.quotes").random()
            val footer: Component = MiniMessage.miniMessage().deserialize(
                "<gradient:#0000ff:#800080>$randomQuote</gradient>"
            )
            event.player.sendPlayerListHeaderAndFooter(header, footer)
        }
        if ((event.player as CraftPlayer).hasPermission("strawberrychat.allow-gamemode-change") && StrawberryChat.pluginConfig.getBoolean("spoof-op")) {
            val nmsPlayer = (event.player as CraftPlayer).handle
            nmsPlayer.connection.connection.channel.pipeline().addAfter(
                "decoder", "strawberry-change-gamemode",
                InboundHandler(nmsPlayer)
            )
        }
    }
}
