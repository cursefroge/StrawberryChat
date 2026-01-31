package dev.curseforged.strawberryChat.serverMetadata

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import net.minecraft.network.protocol.game.ServerboundChangeGameModePacket
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.GameType
import org.bukkit.GameMode
import org.bukkit.craftbukkit.entity.CraftPlayer

class InboundHandler(private val player: Player) : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        when (msg) {
            is ServerboundChangeGameModePacket -> {
                //if (true) {
                if ((player.bukkitEntity as CraftPlayer).hasPermission("strawberrychat.allow-gamemode-change")) {
                    // do not pass the packet further, change the gamemode directly
                    (player.bukkitEntity as CraftPlayer).gameMode = when (msg.mode) {
                        GameType.CREATIVE -> GameMode.CREATIVE
                        GameType.SURVIVAL -> GameMode.SURVIVAL
                        GameType.ADVENTURE -> GameMode.ADVENTURE
                        GameType.SPECTATOR -> GameMode.SPECTATOR
                    }
                } else {
                    // pass the packet further
                    super.channelRead(ctx, msg)
                }
            }
            else -> super.channelRead(ctx, msg)
        }
    }
}