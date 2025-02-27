package dev.curseforged.strawberryChat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener : Listener {
    // on player join
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        // tell player they have permission level 2
        event.player.sendOpLevel(2)
        // send header and footer for tablist
        val header: Component = MiniMessage.miniMessage().deserialize(
            "<bold><gradient:#ff0000:#ff69b4>The Strawberry Farm</gradient></bold>"
        )
        val randomQuote = StrawberryChat.pluginConfig.getStringList("quotes").random()
        val footer: Component = MiniMessage.miniMessage().deserialize(
            "<gradient:#0000ff:#800080>$randomQuote</gradient>"
        )
        event.player.sendPlayerListHeaderAndFooter(header, footer)
    }
}
