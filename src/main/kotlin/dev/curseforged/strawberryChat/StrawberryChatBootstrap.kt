package dev.curseforged.strawberryChat

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.DoubleArgumentType
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent


@Suppress("UnstableApiUsage", "unused")
class StrawberryChatBootstrap : PluginBootstrap {
    override fun bootstrap(context: BootstrapContext) {
        context.lifecycleManager.registerEventHandler(
            LifecycleEvents.COMMANDS,
            commands@LifecycleEventHandler { commands ->
                val worldCommand = Commands.literal("world")
                    .requires { sender -> sender.executor is Player }
                    .then(
                        Commands.argument("worldName", ArgumentTypes.world())
                            .executes { ctx ->
                                val world: World? = ctx.getArgument("worldName", World::class.java)
                                val player = ctx.getSource().executor as Player
                                player.teleport(world!!.spawnLocation, PlayerTeleportEvent.TeleportCause.COMMAND)
                                ctx.getSource().sender.sendRichMessage(
                                    "Successfully teleported <player> to <aqua><world></aqua>",
                                    Placeholder.component("player", player.name()),
                                    Placeholder.unparsed("world", world.name)
                                )
                                return@executes Command.SINGLE_SUCCESS
                            }
                    )

                val velocityCommand = Commands.literal("velocity")
                    .requires { sender -> sender.sender.hasPermission("strawberrychat.velocity") }
                    .then(
                        Commands.argument("entities", ArgumentTypes.entities())
                            .then(
                                Commands.argument("x", DoubleArgumentType.doubleArg())
                                    .then(
                                        Commands.argument("y", DoubleArgumentType.doubleArg())
                                            .then(
                                                Commands.argument("z", DoubleArgumentType.doubleArg())
                                                    .executes { ctx ->
                                                        // get entity argument (becomes EntitySelectorArgumentResolver)
                                                        val entities = ctx.getArgument(
                                                            "entities",
                                                            EntitySelectorArgumentResolver::class.java
                                                        )
                                                            .resolve(ctx.getSource())
                                                        val x = ctx.getArgument("x", Double::class.java)
                                                        val y = ctx.getArgument("y", Double::class.java)
                                                        val z = ctx.getArgument("z", Double::class.java)
                                                        // create vec3d
                                                        val velocity = org.bukkit.util.Vector(x, y, z)
                                                        entities.forEach { entity ->
                                                            entity.velocity = entity.velocity.add(velocity)
                                                        }
                                                        ctx.getSource().sender.sendRichMessage(
                                                            "Successfully added velocity <aqua><x>, <y>, <z></aqua>",
                                                            Placeholder.unparsed("x", x.toString()),
                                                            Placeholder.unparsed("y", y.toString()),
                                                            Placeholder.unparsed("z", z.toString())
                                                        )
                                                        return@executes Command.SINGLE_SUCCESS
                                                    }
                                            )
                                    )
                            )
                    )

                val crashCommand = Commands.literal("crash")
                    .requires { sender -> sender.sender.hasPermission("strawberrychat.crash") }
                    .then(
                        Commands.argument("players", ArgumentTypes.players())
                            .executes { ctx ->
                                val players = ctx.getArgument("players", PlayerSelectorArgumentResolver::class.java)
                                    .resolve(ctx.getSource())
                                players.forEach { player ->
                                    if (player is Player) {
                                            player.spawnParticle(Particle.EXPLOSION, player.location, Int.MAX_VALUE)

                                    }
                                }
                                ctx.getSource().sender.sendRichMessage("Successfully sent crash packet to specified players")
                                return@executes Command.SINGLE_SUCCESS
                            }
                    )

                val builtWorldCommand = worldCommand.build()
                val builtVelocityCommand = velocityCommand.build()
                val builtCrashCommand = crashCommand.build()
                commands.registrar().register(builtWorldCommand)
                commands.registrar().register(builtVelocityCommand)
                commands.registrar().register(builtCrashCommand)
            }
        )
    }
}
