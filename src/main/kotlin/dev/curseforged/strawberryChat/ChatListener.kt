package dev.curseforged.strawberryChat

import dev.curseforged.strawberryChat.util.convertToReadable
import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatListener : Listener, ChatRenderer {
    // Disable FreedomChat chat rewrite!
    @EventHandler()
    fun onChat(event: AsyncChatEvent) {
        event.renderer(this)
        
    }

    override fun render(
        source: Player,
        sourceDisplayName: Component,
        message: Component,
        viewer: Audience
    ): Component {
        
        
        val playerTeam = source.scoreboard.getEntryTeam(source.name)
        val hoverEvent = sourceDisplayName
            .append(Component.text("\nRole: ")).append(playerTeam?.prefix() ?: Component.empty()).append(playerTeam?.displayName() ?: Component.text("None"))
            .appendNewline()
            .append(Component.text("Member since: ")).append(Component.text(
                convertToReadable(source.firstPlayed)))
                .color(NamedTextColor.GRAY)
                .style { it.hoverEvent(null).build() }
        
        
        // get message content
        val messageContent = PlainTextComponentSerializer.plainText().serialize(message)
        val greentext = messageContent.startsWith(">")
        
        val displayName = source.teamDisplayName()
            .hoverEvent(hoverEvent)

        val separator = Component.text(": ")
            .color(NamedTextColor.WHITE)
            .style { it.hoverEvent(null).build() }

        val messageComponent = message
            .color(if (greentext) NamedTextColor.GREEN else NamedTextColor.WHITE)
            .style { it.hoverEvent(null).build() }

        return Component.empty()
            .append(displayName)
            .append(separator)
            .append(messageComponent)
    }
}
