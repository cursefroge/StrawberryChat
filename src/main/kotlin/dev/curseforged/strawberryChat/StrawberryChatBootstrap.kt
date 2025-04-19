package dev.curseforged.strawberryChat

import com.destroystokyo.paper.profile.PlayerProfile
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.DoubleArgumentType
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin


@Suppress("UnstableApiUsage", "unused")
class StrawberryChatBootstrap : PluginBootstrap {
    override fun bootstrap(context: BootstrapContext) {
        context.lifecycleManager.registerEventHandler(
            LifecycleEvents.COMMANDS,
            LifecycleEventHandler { commands ->
                val worldCommand = Commands.literal("world")
                    .requires { sender -> sender.executor is Player }
                    .then(
                        Commands.argument("worldName", ArgumentTypes.world())
                            .executes { ctx ->
                                val world: World? = ctx.getArgument("worldName", World::class.java)
                                val player = ctx.getSource().executor as Player
                                // teleport player to the world
                                if (world == null) {
                                    ctx.getSource().sender.sendRichMessage("<red>World not found!</red>")
                                    return@executes Command.SINGLE_SUCCESS
                                }
                                //player.teleport(world!!.spawnLocation, PlayerTeleportEvent.TeleportCause.COMMAND)
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
                                ctx.getSource().sender.sendRichMessage("<aqua>Successfully sent crash packet to specified players!</aqua>")
                                return@executes Command.SINGLE_SUCCESS
                            }
                    )
                
                val demoCommand = Commands.literal("demo")
                    .requires { sender -> sender.sender.hasPermission("strawberrychat.demo") }
                    .then(
                        Commands.argument("players", ArgumentTypes.players())
                            .executes { ctx ->
                                val players = ctx.getArgument("players", PlayerSelectorArgumentResolver::class.java)
                                    .resolve(ctx.getSource())
                                players.forEach { player ->
                                    player.showDemoScreen()
                                }
                                ctx.getSource().sender.sendRichMessage("<aqua>Successfully sent demo packet to specified players!</aqua>")
                                return@executes Command.SINGLE_SUCCESS
                            }
                    )
                
                val creditsCommand = Commands.literal("credits")
                    .requires { sender -> sender.sender.hasPermission("strawberrychat.credits") }
                    .then(
                        Commands.argument("players", ArgumentTypes.players())
                            .executes { ctx ->
                                val players = ctx.getArgument("players", PlayerSelectorArgumentResolver::class.java)
                                    .resolve(ctx.getSource())
                                players.forEach { player ->
                                    player.showWinScreen()
                                }
                                ctx.getSource().sender.sendRichMessage("<aqua>Successfully sent credits packet to specified players!</aqua>")
                                return@executes Command.SINGLE_SUCCESS
                            }
                    )

                val skullCommand = Commands.literal("skull")
                    .requires { sender -> sender.executor is Player && sender.sender.hasPermission("strawberrychat.skull") }
                    .then(Commands.argument("players", ArgumentTypes.playerProfiles())
                        .executes { ctx ->
                            val profilesResolver = ctx.getArgument("players", PlayerProfileListResolver::class.java)
                            val profiles = profilesResolver.resolve(ctx.getSource())
                            val sender = ctx.source.executor as Player

                            fun giveSkull(profile: PlayerProfile) {
                                val skull = org.bukkit.inventory.ItemStack(org.bukkit.Material.PLAYER_HEAD)
                                val skullMeta = skull.itemMeta as org.bukkit.inventory.meta.SkullMeta
                                skullMeta.playerProfile = profile
                                skull.itemMeta = skullMeta

                                sender.inventory.addItem(skull)
                                ctx.source.sender.sendRichMessage("<aqua>Successfully gave skull of ${profile.name}!</aqua>")
                            }
                            
                            for (profile in profiles) {
                                if (!profile.hasTextures()) {
                                    // break off and complete the profile, but wait for the result
                                    sender.sendRichMessage("<gray>Skull profile ${profile.name} does not have textures, waiting for completion...</gray>")
                                    val plugin = Bukkit.getPluginManager().getPlugin("StrawberryChat") as Plugin
                                    StrawberryChat.scheduler.runTaskAsynchronously(plugin, Runnable {
                                        profile.complete(true)
                                        // when complete, return to the main thread
                                        StrawberryChat.scheduler.runTask(plugin, Runnable {
                                            if (profile.hasTextures()) {
                                                giveSkull(profile)
                                            } else {
                                                sender.sendRichMessage("<red>Failed to complete profile ${profile.name}!</red>")
                                            }
                                        })
                                    })
                                } else {
                                    giveSkull(profile)
                                }
                            }
                            Command.SINGLE_SUCCESS
                        })

                val toggleNametagCommand = Commands.literal("togglenametag")
                    .requires { sender -> sender.executor is Player && sender.sender.hasPermission("strawberrychat.togglenametag") }
                    .executes { ctx ->
                        val player = ctx.source.executor as Player
                        val teamName = "__strawberrychat_hidden_nametag"
                        var team = player.scoreboard.getTeam(teamName)

                        if (team == null) {
                            team = player.scoreboard.registerNewTeam(teamName).apply {
                                setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER)
                            }
                        }

                        team.let {
                            if (it.hasEntry(player.name)) {
                                it.removeEntry(player.name)
                                player.sendRichMessage("<aqua>Successfully enabled nametag!</aqua>")
                            } else {
                                it.addEntry(player.name)
                                player.sendRichMessage("<aqua>Successfully disabled nametag!</aqua>")
                            }
                        }

                        return@executes Command.SINGLE_SUCCESS
                    }
                

                val builtWorldCommand = worldCommand.build()
                val builtVelocityCommand = velocityCommand.build()
                val builtCrashCommand = crashCommand.build()
                val builtDemoCommand = demoCommand.build()
                val builtCreditsCommand = creditsCommand.build()
                val builtSkullCommand = skullCommand.build()
                val builtToggleNametagCommand = toggleNametagCommand.build()
                commands.registrar().register(builtWorldCommand)
                commands.registrar().register(builtVelocityCommand)
                commands.registrar().register(builtCrashCommand)
                commands.registrar().register(builtDemoCommand)
                commands.registrar().register(builtCreditsCommand)
                commands.registrar().register(builtSkullCommand)
                commands.registrar().register(builtToggleNametagCommand)
            }
        )
    }
}
