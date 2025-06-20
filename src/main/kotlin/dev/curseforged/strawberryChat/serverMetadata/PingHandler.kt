package dev.curseforged.strawberryChat.serverMetadata

import dev.curseforged.strawberryChat.StrawberryChat
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.game.ClientboundLoginPacket
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket

class PingHandler : ChannelOutboundHandlerAdapter() {
    override fun write(
        ctx: ChannelHandlerContext,
        msg: Any,
        promise: ChannelPromise
    ) {
        when (msg) {
            is ClientboundStatusResponsePacket -> {
                val status = msg.status
                val custom = CustomServerMetadata(
                    status.description,
                    status.players.orElse(null),
                    status.version.orElse(null),
                    status.favicon.orElse(null),
                    StrawberryChat.pluginConfig.getBoolean("spoof-enforces-signatures") || status.enforcesSecureChat,
                    StrawberryChat.pluginConfig.getBoolean("send-prevents-reports")
                )
                val buf = FriendlyByteBuf(ctx.alloc().buffer())
                buf.writeVarInt(0x00)  // STATUS_RESPONSE_PACKET_ID
                buf.writeJsonWithCodec(CustomServerMetadata.CODEC, custom)
                ctx.write(buf, promise)
            }

            is ClientboundLoginPacket -> {
                // reflectively set enforcesSecureChat = true
                val ct = ClientboundLoginPacket::class.java
                    .declaredConstructors
                    .first { it.parameterCount == 11 }
                    .apply { isAccessible = true }

                @Suppress("UNCHECKED_CAST")
                val rewritten = ct.newInstance(
                    msg.playerId,
                    msg.hardcore,
                    msg.levels,
                    msg.maxPlayers,
                    msg.chunkRadius,
                    msg.simulationDistance,
                    msg.reducedDebugInfo,
                    msg.showDeathScreen,
                    msg.doLimitedCrafting,
                    msg.commonPlayerSpawnInfo,
                    StrawberryChat.pluginConfig.getBoolean("spoof-enforces-signatures") || msg.enforcesSecureChat
                ) as ClientboundLoginPacket

                // hand back into pipeline so PacketEncoder & length codec do their job
                ctx.write(rewritten, promise)
            }

            else -> ctx.write(msg, promise)
        }
    }
}