package dev.curseforged.strawberryChat

import dev.curseforged.strawberryChat.util.convertToReadableDate
import dev.curseforged.strawberryChat.util.convertToReadableTime
import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.`object`.ObjectContents
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatListener : Listener, ChatRenderer {
    // Disable FreedomChat chat rewrite!
    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        // cancel event if strip-message-signatures is true
        if (StrawberryChat.pluginConfig.getBoolean("chat.strip-message-signatures")) {
            event.isCancelled = true
            // call renderer and broadcast message
            for (viewer in event.viewers()) {
                val renderedMessage = render(
                    event.player,
                    event.player.displayName(),
                    event.message(),
                    viewer
                )
                viewer.sendMessage(renderedMessage)
            }
        }
        event.renderer(this)
    }

    override fun render(
        source: Player,
        sourceDisplayName: Component,
        message: Component,
        viewer: Audience
    ): Component {
        val head = source.playerProfile.id?.let { Component.`object`(ObjectContents.playerHead(it)) }
        val chead = head?.compact()  ?: Component.empty()
        val playerTeam = source.scoreboard.getEntryTeam(source.name)
        val hoverEvent = sourceDisplayName
            .appendNewline()
            .append()
            .appendNewline()
            .append(Component.text("Role: ")).append(playerTeam?.prefix() ?: Component.empty()).append(playerTeam?.displayName() ?: Component.text("None"))
            .appendNewline()
            .append(Component.text("Member since: ")).append(Component.text(convertToReadableDate(source.firstPlayed)))
            .appendNewline()
            .append(Component.text("Message time: ")).append(Component.text(convertToReadableTime(System.currentTimeMillis())))
                .color(NamedTextColor.GRAY)
                .style { it.hoverEvent(null).build() }
        
        
        // get message content
        val messageContent = PlainTextComponentSerializer.plainText().serialize(message)
        val greentext = messageContent.startsWith(">")
        
        val displayName = source.teamDisplayName()
            .hoverEvent(hoverEvent)

        val separator = Component.text(": ")

        val messageComponent = message
            .color(if (greentext) NamedTextColor.GREEN else NamedTextColor.WHITE)


        return chead
            .append(Component.space())
            .append(displayName)
            .append(separator)
            .append(messageComponent)
    }
}
