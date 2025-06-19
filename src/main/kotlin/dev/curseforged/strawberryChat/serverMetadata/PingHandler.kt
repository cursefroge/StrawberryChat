package dev.curseforged.strawberryChat.serverMetadata

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket

class PingHandler : MessageToByteEncoder<Packet<*>>() {
    override fun acceptOutboundMessage(msg: Any?): Boolean {
        return msg is ClientboundStatusResponsePacket
    }

    override fun encode(ctx: ChannelHandlerContext, msg: Packet<*>, out: ByteBuf) {
        if (msg is ClientboundStatusResponsePacket) {
            val status = msg.status
            val customStatus = CustomServerMetadata(
                status.description,
                status.players.orElse(null),
                status.version.orElse(null),
                status.favicon.orElse(null)?.toString(), // Convert Favicon to String?
                status.enforcesSecureChat,
                true // preventsChatReports = true
            )
            val buf = FriendlyByteBuf(out)
            buf.writeVarInt(0x00) // STATUS_RESPONSE_PACKET_ID
            buf.writeJsonWithCodec(CustomServerMetadata.CODEC, customStatus)
        }
    }
}