package dev.curseforged.strawberryChat.serverMetadata

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.protocol.status.ServerStatus
import java.util.Optional

data class CustomServerMetadata(
    val description: Component,
    val players: ServerStatus.Players?,
    val version: ServerStatus.Version,
    val favicon: String?,
    val enforcesSecureChat: Boolean,
    val preventsChatReports: Boolean
) {
    companion object {
        val CODEC: Codec<CustomServerMetadata> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<CustomServerMetadata> ->
            instance.group(
                ComponentSerialization.CODEC.fieldOf("description").forGetter { it: CustomServerMetadata -> it.description },
                ServerStatus.Players.CODEC.optionalFieldOf("players").forGetter { it: CustomServerMetadata -> Optional.ofNullable(it.players) },
                ServerStatus.Version.CODEC.fieldOf("version").forGetter { it: CustomServerMetadata -> it.version },
                Codec.STRING.optionalFieldOf("favicon").forGetter { it: CustomServerMetadata -> Optional.ofNullable(it.favicon) },
                Codec.BOOL.fieldOf("enforcesSecureChat").forGetter { it: CustomServerMetadata -> it.enforcesSecureChat },
                Codec.BOOL.fieldOf("preventsChatReports").forGetter { it: CustomServerMetadata -> it.preventsChatReports }
            ).apply(instance) { desc: Component, playersOpt: Optional<ServerStatus.Players>, version: ServerStatus.Version,
                                faviconOpt: Optional<String>, enforce: Boolean, prevent: Boolean ->
                CustomServerMetadata(
                    desc,
                    playersOpt.orElse(null),
                    version,
                    faviconOpt.orElse(null),
                    enforce,
                    prevent
                )
            }
        }
    }
}