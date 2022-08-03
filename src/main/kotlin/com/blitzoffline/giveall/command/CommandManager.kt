package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.extension.isValidName
import com.blitzoffline.giveall.extension.sendMessage
import com.blitzoffline.giveall.util.getPlayersInRadius
import com.blitzoffline.giveall.util.handleItemGiving
import com.blitzoffline.giveall.util.handleMoneyGiving
import com.blitzoffline.giveall.util.handleXpGiving
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.DoubleArgument
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.MultiLiteralArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandManager(private val plugin: GiveAll) {
    fun createCommands(vault: Boolean): CommandAPICommand {
        val mainCommand = createMainCommand()
            .withSubcommands(
                createHelpCommand(),
                createReloadCommand(),

                createMaterialCommand(),
                createMaterialWithAmountCommand(),
                createHandCommand(),
                createHandWithAmountCommand(),
                createXpPointsCommand(),
                createXpLevelsCommand(),

                createRadiusMaterialCommand(),
                createRadiusMaterialWithAmountCommand(),
                createRadiusHandCommand(),
                createRadiusHandWithAmountCommand(),
                createRadiusXpPointsCommand(),
                createRadiusXpLevelsCommand(),
                createRadiusWorldMaterialCommand(),
                createRadiusWorldMaterialWithAmountCommand(),
                createRadiusWorldHandCommand(),
                createRadiusWorldHandWithAmountCommand(),
                createRadiusWorldXpPointsCommand(),
                createRadiusWorldXpLevelsCommand(),

                createWorldMaterialCommand(),
                createWorldMaterialWithAmountCommand(),
                createWorldHandCommand(),
                createWorldHandWithAmountCommand(),
                createWorldXpPointsCommand(),
                createWorldXpLevelsCommand(),

                createSpecialItemSaveCommand(),
                createSpecialItemSaveWithForceCommand(),
                createSpecialItemRemoveCommand()
            )

        if (!vault) {
            return mainCommand
        }

        // Registering the money sub commands only when vault is found.
        return mainCommand.withSubcommands(
            createMoneyCommand(),
            createRadiusMoneyCommand(),
            createRadiusWorldMoneyCommand(),
            createWorldMoneyCommand()
        )
    }

    // Any
    private fun createMainCommand() = CommandAPICommand("giveall")
        .withAliases("gall")
        .withRequirement { sender ->
            ALL_PERMISSIONS.any { sender.hasPermission(it) }
        }

    // Any
    private fun createHelpCommand() = CommandAPICommand("help")
        .withPermission(HELP_PERMISSION)
        .executes(CommandExecutor { sender: CommandSender, _: Array<Any> ->
            sendMessage(
                sender,
                plugin.settingsManager.messages.help
            )
        })

    // Any
    private fun createReloadCommand() = CommandAPICommand("reload")
        .withPermission(RELOAD_PERMISSION)
        .executes(CommandExecutor { sender: CommandSender, _: Array<Any> ->
            plugin.settingsManager.reload()
            sendMessage(
                sender,
                plugin.settingsManager.messages.configReloaded
            )
        })

    // Any
    private fun createMaterialCommand() = CommandAPICommand("material")
        .withPermission(BASE_MATERIAL_PERMISSION)
        .withArguments(
            plugin.settingsManager.arguments.materialArgument
        ).executes(CommandExecutor { sender: CommandSender, args: Array<Any?> ->
            val material = args[0] as String
            val itemStack = plugin.savedItemsManager.getSavedItemOrMaterial(material)
                ?: return@CommandExecutor sendMessage(
                    sender,
                    plugin.settingsManager.messages.wrongMaterial,
                    Placeholder.unparsed("wrong_value", material)
                )

            handleItemGiving(
                plugin,
                sender,
                itemStack,
                Bukkit.getOnlinePlayers(),
                Placeholder.unparsed("material", material),
                Placeholder.unparsed("amount", itemStack.amount.toString())
            )
        })

    // Any
    private fun createMaterialWithAmountCommand() = CommandAPICommand("material")
        .withPermission(BASE_MATERIAL_PERMISSION)
        .withArguments(
            plugin.settingsManager.arguments.materialArgument,
            IntegerArgument("amount", 1)
        ).executes(CommandExecutor { sender: CommandSender, args: Array<Any> ->
            val material = args[0] as String
            val amount = args[1] as Int
            val itemStack = plugin.savedItemsManager.getSavedItemOrMaterial(material, amount)
                ?: return@CommandExecutor sendMessage(
                    sender,
                    plugin.settingsManager.messages.wrongMaterial,
                    Placeholder.unparsed("wrong_value", material)
                )

            handleItemGiving(
                plugin,
                sender,
                itemStack,
                Bukkit.getOnlinePlayers(),
                Placeholder.unparsed("material", material),
                Placeholder.unparsed("amount", itemStack.amount.toString())
            )
        })

    // Player
    private fun createHandCommand() = CommandAPICommand("hand")
        .withPermission(BASE_HAND_PERMISSION)
        .executesPlayer(PlayerCommandExecutor { sender: Player, _: Array<Any?> ->
            val itemStack = sender.inventory.itemInMainHand.clone()
            if (itemStack.type.isAir) return@PlayerCommandExecutor sendMessage(
                sender,
                plugin.settingsManager.messages.itemAir
            )

            handleItemGiving(
                plugin,
                sender,
                itemStack,
                Bukkit.getOnlinePlayers(),
                Placeholder.unparsed("material", itemStack.type.name.lowercase()),
                Placeholder.unparsed("amount", itemStack.amount.toString())
            )
        })

    // Player
    private fun createHandWithAmountCommand() = CommandAPICommand("hand")
        .withPermission(BASE_HAND_PERMISSION)
        .withArguments(
            IntegerArgument("amount", 1)
        )
        .executesPlayer(PlayerCommandExecutor { sender: Player, args: Array<Any?> ->
            val amount = args[0] as Int
            val itemStack = sender.inventory.itemInMainHand.clone()
            if (itemStack.type.isAir) return@PlayerCommandExecutor sendMessage(
                sender,
                plugin.settingsManager.messages.itemAir
            )

            itemStack.amount = amount

            handleItemGiving(
                plugin,
                sender,
                itemStack,
                Bukkit.getOnlinePlayers(),
                Placeholder.unparsed("material", itemStack.type.name.lowercase()),
                Placeholder.unparsed("amount", itemStack.amount.toString())
            )
        })

    // Any
    private fun createXpPointsCommand() = CommandAPICommand("xp")
        .withPermission(BASE_XP_PERMISSION)
        .withArguments(
            IntegerArgument("amount", 1)
        )
        .executes(CommandExecutor { sender: CommandSender, args: Array<Any> ->
            val amount = args[0] as Int
            handleXpGiving(
                plugin,
                sender,
                amount,
                false,
                Bukkit.getOnlinePlayers(),
                Placeholder.unparsed("amount", amount.toString())
            )
        })

    // Any
    private fun createXpLevelsCommand() = CommandAPICommand("xp")
        .withPermission(BASE_XP_PERMISSION)
        .withArguments(
            IntegerArgument("amount", 1),
            MultiLiteralArgument("levels")
        )
        .executes(CommandExecutor { sender: CommandSender, args: Array<Any> ->
            val amount = args[0] as Int
            handleXpGiving(
                plugin,
                sender,
                amount,
                true,
                Bukkit.getOnlinePlayers(),
                Placeholder.unparsed("amount", amount.toString())
            )
        })

    // Any
    private fun createMoneyCommand() = CommandAPICommand("money")
        .withPermission(BASE_MONEY_PERMISSION)
        .withArguments(
            DoubleArgument("amount", 1.0)
        )
        .executes(CommandExecutor { sender: CommandSender, args: Array<Any> ->
            val amount = args[0] as Double
            handleMoneyGiving(
                plugin,
                sender,
                amount,
                Bukkit.getOnlinePlayers(),
                Placeholder.unparsed("amount", amount.toString())
            )
        })

    // Any
    private fun createWorldMaterialCommand() = CommandAPICommand("world")
        .withRequirement { sender ->
            WORLD_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            plugin.settingsManager.arguments.worldArgument,
            MultiLiteralArgument("material")
                .withPermission(WORLD_MATERIAL_PERMISSION),
            plugin.settingsManager.arguments.materialArgument
        )
        .executes(CommandExecutor { sender: CommandSender, args: Array<Any> ->
            val world = args[0] as World
            val material = args[2] as String

            val itemStack = plugin.savedItemsManager.getSavedItemOrMaterial(material)
                ?: return@CommandExecutor sendMessage(
                    sender,
                    plugin.settingsManager.messages.wrongMaterial,
                    Placeholder.unparsed("wrong_value", material)
                )

            handleItemGiving(
                plugin,
                sender,
                itemStack,
                world.players,
                Placeholder.unparsed("material", material),
                Placeholder.unparsed("amount", itemStack.amount.toString()),
                Placeholder.unparsed("world", world.name)
            )
        })

    // Any
    private fun createWorldMaterialWithAmountCommand() = CommandAPICommand("world")
        .withArguments(
            plugin.settingsManager.arguments.worldArgument,
            MultiLiteralArgument("material")
                .withPermission(WORLD_MATERIAL_PERMISSION),
            plugin.settingsManager.arguments.materialArgument,
            IntegerArgument("amount", 1)
        )
        .executes(CommandExecutor { sender: CommandSender, args: Array<Any> ->
            val world = args[0] as World
            val material = args[2] as String
            val amount = args[3] as Int

            val itemStack = plugin.savedItemsManager.getSavedItemOrMaterial(material, amount)
                ?: return@CommandExecutor sendMessage(
                    sender,
                    plugin.settingsManager.messages.wrongMaterial,
                    Placeholder.unparsed("wrong_value", material)
                )

            handleItemGiving(
                plugin,
                sender,
                itemStack,
                world.players,
                Placeholder.unparsed("material", material),
                Placeholder.unparsed("amount", itemStack.amount.toString()),
                Placeholder.unparsed("world", world.name)
            )
        })

    // Player
    private fun createWorldHandCommand() = CommandAPICommand("world")
        .withArguments(
            plugin.settingsManager.arguments.worldArgument,
            MultiLiteralArgument("hand")
                .withPermission(WORLD_HAND_PERMISSION),
        )
        .executesPlayer(PlayerCommandExecutor { sender: Player, args: Array<Any> ->
            val world = args[0] as World

            val itemStack = sender.inventory.itemInMainHand.clone()
            if (itemStack.type.isAir) return@PlayerCommandExecutor sendMessage(
                sender,
                plugin.settingsManager.messages.itemAir
            )

            handleItemGiving(
                plugin,
                sender,
                itemStack,
                world.players,
                Placeholder.unparsed("material", itemStack.type.name.lowercase()),
                Placeholder.unparsed("amount", itemStack.amount.toString()),
                Placeholder.unparsed("world", world.name)
            )
        })

    // Player
    private fun createWorldHandWithAmountCommand() = CommandAPICommand("world")
        .withArguments(
            plugin.settingsManager.arguments.worldArgument,
            MultiLiteralArgument("hand")
                .withPermission(WORLD_HAND_PERMISSION),
            IntegerArgument("amount", 1)
        )
        .executesPlayer(PlayerCommandExecutor { sender: Player, args: Array<Any> ->
            val world = args[0] as World
            val amount = args[2] as Int

            val itemStack = sender.inventory.itemInMainHand.clone()
            if (itemStack.type.isAir) return@PlayerCommandExecutor sendMessage(
                sender,
                plugin.settingsManager.messages.itemAir
            )
            itemStack.amount = amount

            handleItemGiving(
                plugin,
                sender,
                itemStack,
                world.players,
                Placeholder.unparsed("material", itemStack.type.name.lowercase()),
                Placeholder.unparsed("amount", amount.toString()),
                Placeholder.unparsed("world", world.name)
            )
        })

    // Any
    private fun createWorldXpPointsCommand() = CommandAPICommand("world")
        .withArguments(
            plugin.settingsManager.arguments.worldArgument,
            MultiLiteralArgument("xp")
                .withPermission(WORLD_XP_PERMISSION),
            IntegerArgument("amount", 1)
        )
        .executes(CommandExecutor { sender: CommandSender, args: Array<Any> ->
            val world = args[0] as World
            val amount = args[2] as Int

            handleXpGiving(
                plugin,
                sender,
                amount,
                false,
                world.players,
                Placeholder.unparsed("amount", amount.toString()),
                Placeholder.unparsed("world", world.name)
            )
        })

    // Any
    private fun createWorldXpLevelsCommand() = CommandAPICommand("world")
        .withArguments(
            plugin.settingsManager.arguments.worldArgument,
            MultiLiteralArgument("xp")
                .withPermission(WORLD_XP_PERMISSION),
            IntegerArgument("amount", 1),
            MultiLiteralArgument("levels")
        )
        .executes(CommandExecutor { sender: CommandSender, args: Array<Any> ->
            val world = args[0] as World
            val amount = args[2] as Int

            handleXpGiving(
                plugin,
                sender,
                amount,
                true,
                world.players,
                Placeholder.unparsed("amount", amount.toString()),
                Placeholder.unparsed("world", world.name)
            )
        })

    // Any
    private fun createWorldMoneyCommand() = CommandAPICommand("world")
        .withArguments(
            plugin.settingsManager.arguments.worldArgument,
            MultiLiteralArgument("money")
                .withPermission(WORLD_MONEY_PERMISSION),
            DoubleArgument("amount", 1.0)
        )
        .executes(CommandExecutor { sender: CommandSender, args: Array<Any> ->
            val world = args[0] as World
            val amount = args[2] as Double

            handleMoneyGiving(
                plugin,
                sender,
                amount,
                world.players,
                Placeholder.unparsed("amount", amount.toString()),
                Placeholder.unparsed("world", world.name)
            )
        })

    // Player
    private fun createRadiusMaterialCommand() = CommandAPICommand("radius")
        .withRequirement { sender ->
            RADIUS_PERMISSIONS.any { sender.hasPermission(it) }
                    || RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            DoubleArgument("radius", 1.0),
            MultiLiteralArgument("material")
                .withPermission(RADIUS_MATERIAL_PERMISSION),
            plugin.settingsManager.arguments.materialArgument
        )
        .executesPlayer(PlayerCommandExecutor { sender: Player, args: Array<Any> ->
            val radius = args[0] as Double
            val material = args[2] as String

            val itemStack = plugin.savedItemsManager.getSavedItemOrMaterial(material)
                ?: return@PlayerCommandExecutor sendMessage(
                    sender,
                    plugin.settingsManager.messages.wrongMaterial,
                    Placeholder.unparsed("wrong_value", material)
                )

            handleItemGiving(
                plugin,
                sender,
                itemStack,
                getPlayersInRadius(
                    sender,
                    radius
                ),
                Placeholder.unparsed("material", material),
                Placeholder.unparsed("amount", itemStack.amount.toString()),
                Placeholder.unparsed("radius", radius.toString())
            )
        })

    // Player
    private fun createRadiusMaterialWithAmountCommand() = CommandAPICommand("radius")
        .withRequirement { sender ->
            RADIUS_PERMISSIONS.any { sender.hasPermission(it) }
                    || RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            DoubleArgument("radius", 1.0),
            MultiLiteralArgument("material")
                .withPermission(RADIUS_MATERIAL_PERMISSION),
            plugin.settingsManager.arguments.materialArgument,
            IntegerArgument("amount", 1)
        )
        .executesPlayer(PlayerCommandExecutor { sender: Player, args: Array<Any> ->
            val radius = args[0] as Double
            val material = args[2] as String
            val amount = args[3] as Int

            val itemStack = plugin.savedItemsManager.getSavedItemOrMaterial(material, amount)
                ?: return@PlayerCommandExecutor sendMessage(
                    sender,
                    plugin.settingsManager.messages.wrongMaterial,
                    Placeholder.unparsed("wrong_value", material)
                )

            handleItemGiving(
                plugin,
                sender,
                itemStack,
                getPlayersInRadius(
                    sender,
                    radius
                ),
                Placeholder.unparsed("material", material),
                Placeholder.unparsed("amount", amount.toString()),
                Placeholder.unparsed("radius", radius.toString())
            )
        })

    // Player
    private fun createRadiusHandCommand() = CommandAPICommand("radius")
        .withRequirement { sender ->
            RADIUS_PERMISSIONS.any { sender.hasPermission(it) }
                    || RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            DoubleArgument("radius", 1.0),
            MultiLiteralArgument("hand")
                .withPermission(RADIUS_HAND_PERMISSION)
        )
        .executesPlayer(PlayerCommandExecutor { sender: Player, args: Array<Any> ->
            val radius = args[0] as Double

            val itemStack = sender.inventory.itemInMainHand.clone()
            if (itemStack.type.isAir) return@PlayerCommandExecutor sendMessage(
                sender,
                plugin.settingsManager.messages.itemAir
            )

            handleItemGiving(
                plugin,
                sender,
                itemStack,
                getPlayersInRadius(
                    sender,
                    radius
                ),
                Placeholder.unparsed("material", itemStack.type.name.lowercase()),
                Placeholder.unparsed("amount", itemStack.amount.toString()),
                Placeholder.unparsed("radius", radius.toString())
            )
        })

    // Player
    private fun createRadiusHandWithAmountCommand() = CommandAPICommand("radius")
        .withRequirement { sender ->
            RADIUS_PERMISSIONS.any { sender.hasPermission(it) }
                    || RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            DoubleArgument("radius", 1.0),
            MultiLiteralArgument("hand")
                .withPermission(RADIUS_HAND_PERMISSION),
            IntegerArgument("amount", 1)
        )
        .executesPlayer(PlayerCommandExecutor { sender: Player, args: Array<Any> ->
            val radius = args[0] as Double
            val amount = args[2] as Int

            val itemStack = sender.inventory.itemInMainHand.clone()
            if (itemStack.type.isAir) return@PlayerCommandExecutor sendMessage(
                sender,
                plugin.settingsManager.messages.itemAir
            )

            itemStack.amount = amount

            handleItemGiving(
                plugin,
                sender,
                itemStack,
                getPlayersInRadius(
                    sender,
                    radius
                ),
                Placeholder.unparsed("material", itemStack.type.name.lowercase()),
                Placeholder.unparsed("amount", itemStack.amount.toString()),
                Placeholder.unparsed("radius", radius.toString())
            )
        })

    // Player
    private fun createRadiusXpPointsCommand() = CommandAPICommand("radius")
        .withRequirement { sender ->
            RADIUS_PERMISSIONS.any { sender.hasPermission(it) }
                    || RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            DoubleArgument("radius", 1.0),
            MultiLiteralArgument("xp")
                .withPermission(RADIUS_XP_PERMISSION),
            IntegerArgument("amount", 1)
        )
        .executesPlayer(PlayerCommandExecutor { sender: Player, args: Array<Any> ->
            val radius = args[0] as Double
            val amount = args[2] as Int

            handleXpGiving(
                plugin,
                sender,
                amount,
                false,
                getPlayersInRadius(
                    sender,
                    radius
                ),
                Placeholder.unparsed("amount", amount.toString()),
                Placeholder.unparsed("radius", radius.toString())
            )
        })

    // Player
    private fun createRadiusXpLevelsCommand() = CommandAPICommand("radius")
        .withRequirement { sender ->
            RADIUS_PERMISSIONS.any { sender.hasPermission(it) }
                    || RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            DoubleArgument("radius", 1.0),
            MultiLiteralArgument("xp")
                .withPermission(RADIUS_XP_PERMISSION),
            IntegerArgument("amount", 1),
            MultiLiteralArgument("levels")
        )
        .executesPlayer(PlayerCommandExecutor { sender: Player, args: Array<Any> ->
            val radius = args[0] as Double
            val amount = args[2] as Int

            handleXpGiving(
                plugin,
                sender,
                amount,
                true,
                getPlayersInRadius(
                    sender,
                    radius
                ),
                Placeholder.unparsed("amount", amount.toString()),
                Placeholder.unparsed("radius", radius.toString())
            )
        })

    // Player
    private fun createRadiusMoneyCommand() = CommandAPICommand("radius")
        .withRequirement { sender ->
            RADIUS_PERMISSIONS.any { sender.hasPermission(it) }
                    || RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            DoubleArgument("radius", 1.0),
            MultiLiteralArgument("money")
                .withPermission(RADIUS_MONEY_PERMISSION),
            DoubleArgument("amount", 1.0)
        )
        .executesPlayer(PlayerCommandExecutor { sender: Player, args: Array<Any> ->
            val radius = args[0] as Double
            val amount = args[2] as Double

            handleMoneyGiving(
                plugin,
                sender,
                amount,
                getPlayersInRadius(
                    sender,
                    radius
                ),
                Placeholder.unparsed("amount", amount.toString()),
                Placeholder.unparsed("radius", radius.toString())
            )
        })

    // Any
    private fun createRadiusWorldMaterialCommand()  = CommandAPICommand("radius")
        .withRequirement { sender ->
            RADIUS_PERMISSIONS.any { sender.hasPermission(it) }
                    || RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            DoubleArgument("radius", 1.0),
            MultiLiteralArgument("world")
                .withRequirement { sender ->
                    RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
                },
            plugin.settingsManager.arguments.worldArgument,
            MultiLiteralArgument("center"),
            DoubleArgument("x"),
            DoubleArgument("y"),
            DoubleArgument("z"),
            MultiLiteralArgument("material")
                .withPermission(RADIUS_WORLD_MATERIAL_PERMISSION),
            plugin.settingsManager.arguments.materialArgument
        )
        .executes(CommandExecutor { sender: CommandSender, args: Array<Any> ->
            val radius = args[0] as Double
            val world = args[2] as World
            val x = args[4] as Double
            val y = args[5] as Double
            val z = args[6] as Double
            val material = args[8] as String

            val itemStack = plugin.savedItemsManager.getSavedItemOrMaterial(material)
                ?: return@CommandExecutor sendMessage(
                    sender,
                    plugin.settingsManager.messages.wrongMaterial,
                    Placeholder.unparsed("wrong_value", material)
                )

            handleItemGiving(
                plugin,
                sender,
                itemStack,
                getPlayersInRadius(
                    Location(world, x, y, z),
                    radius
                ),
                Placeholder.unparsed("material", material),
                Placeholder.unparsed("amount", itemStack.amount.toString()),
                Placeholder.unparsed("world", world.name),
                Placeholder.unparsed("radius", radius.toString()),
                Placeholder.unparsed("x", x.toString()),
                Placeholder.unparsed("y", y.toString()),
                Placeholder.unparsed("z", z.toString())
            )
        })

    // Any
    private fun createRadiusWorldMaterialWithAmountCommand()  = CommandAPICommand("radius")
        .withRequirement { sender ->
            RADIUS_PERMISSIONS.any { sender.hasPermission(it) }
                    || RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            DoubleArgument("radius", 1.0),
            MultiLiteralArgument("world")
                .withRequirement { sender ->
                    RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
                },
            plugin.settingsManager.arguments.worldArgument,
            MultiLiteralArgument("center"),
            DoubleArgument("x"),
            DoubleArgument("y"),
            DoubleArgument("z"),
            MultiLiteralArgument("material")
                .withPermission(RADIUS_WORLD_MATERIAL_PERMISSION),
            plugin.settingsManager.arguments.materialArgument,
            IntegerArgument("amount", 1)
        )
        .executes(CommandExecutor { sender: CommandSender, args: Array<Any> ->
            val radius = args[0] as Double
            val world = args[2] as World
            val x = args[4] as Double
            val y = args[5] as Double
            val z = args[6] as Double
            val material = args[8] as String
            val amount = args[9] as Int

            val itemStack = plugin.savedItemsManager.getSavedItemOrMaterial(material, amount)
                ?: return@CommandExecutor sendMessage(
                    sender,
                    plugin.settingsManager.messages.wrongMaterial,
                    Placeholder.unparsed("wrong_value", material)
                )

            handleItemGiving(
                plugin,
                sender,
                itemStack,
                getPlayersInRadius(
                    Location(world, x, y, z),
                    radius
                ),
                Placeholder.unparsed("material", material),
                Placeholder.unparsed("amount", amount.toString()),
                Placeholder.unparsed("world", world.name),
                Placeholder.unparsed("radius", radius.toString()),
                Placeholder.unparsed("x", x.toString()),
                Placeholder.unparsed("y", y.toString()),
                Placeholder.unparsed("z", z.toString())
            )
        })

    // Player
    private fun createRadiusWorldHandCommand() = CommandAPICommand("radius")
        .withRequirement { sender ->
            RADIUS_PERMISSIONS.any { sender.hasPermission(it) }
                    || RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            DoubleArgument("radius", 1.0),
            MultiLiteralArgument("world")
                .withRequirement { sender ->
                    RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
                },
            plugin.settingsManager.arguments.worldArgument,
            MultiLiteralArgument("center"),
            DoubleArgument("x"),
            DoubleArgument("y"),
            DoubleArgument("z"),
            MultiLiteralArgument("hand")
                .withPermission(RADIUS_WORLD_HAND_PERMISSION)
        )
        .executesPlayer(PlayerCommandExecutor { sender: Player, args: Array<Any> ->
            val radius = args[0] as Double
            val world = args[2] as World
            val x = args[4] as Double
            val y = args[5] as Double
            val z = args[6] as Double

            val itemStack = sender.inventory.itemInMainHand.clone()
            if (itemStack.type.isAir) return@PlayerCommandExecutor sendMessage(
                sender,
                plugin.settingsManager.messages.itemAir
            )

            handleItemGiving(
                plugin,
                sender,
                itemStack,
                getPlayersInRadius(
                    Location(world, x, y, z),
                    radius
                ),
                Placeholder.unparsed("material", itemStack.type.name.lowercase()),
                Placeholder.unparsed("amount", itemStack.amount.toString()),
                Placeholder.unparsed("world", world.name),
                Placeholder.unparsed("radius", radius.toString()),
                Placeholder.unparsed("x", x.toString()),
                Placeholder.unparsed("y", y.toString()),
                Placeholder.unparsed("z", z.toString())
            )
        })

    // Player
    private fun createRadiusWorldHandWithAmountCommand() = CommandAPICommand("radius")
        .withRequirement { sender ->
            RADIUS_PERMISSIONS.any { sender.hasPermission(it) }
                    || RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            DoubleArgument("radius", 1.0),
            MultiLiteralArgument("world")
                .withRequirement { sender ->
                    RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
                },
            plugin.settingsManager.arguments.worldArgument,
            MultiLiteralArgument("center"),
            DoubleArgument("x"),
            DoubleArgument("y"),
            DoubleArgument("z"),
            MultiLiteralArgument("hand")
                .withPermission(RADIUS_WORLD_HAND_PERMISSION),
            IntegerArgument("amount", 1)
        )
        .executesPlayer(PlayerCommandExecutor { sender: Player, args: Array<Any> ->
            val radius = args[0] as Double
            val world = args[2] as World
            val x = args[4] as Double
            val y = args[5] as Double
            val z = args[6] as Double
            val amount = args[7] as Int

            val itemStack = sender.inventory.itemInMainHand.clone()
            if (itemStack.type.isAir) return@PlayerCommandExecutor sendMessage(
                sender,
                plugin.settingsManager.messages.itemAir
            )
            itemStack.amount = amount

            handleItemGiving(
                plugin,
                sender,
                itemStack,
                getPlayersInRadius(
                    Location(world, x, y, z),
                    radius
                ),
                Placeholder.unparsed("material", itemStack.type.name.lowercase()),
                Placeholder.unparsed("amount", amount.toString()),
                Placeholder.unparsed("world", world.name),
                Placeholder.unparsed("radius", radius.toString()),
                Placeholder.unparsed("x", x.toString()),
                Placeholder.unparsed("y", y.toString()),
                Placeholder.unparsed("z", z.toString())
            )
        })

    // Any
    private fun createRadiusWorldXpPointsCommand() = CommandAPICommand("radius")
        .withRequirement { sender ->
            RADIUS_PERMISSIONS.any { sender.hasPermission(it) }
                    || RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            DoubleArgument("radius", 1.0),
            MultiLiteralArgument("world")
                .withRequirement { sender ->
                    RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
                },
            plugin.settingsManager.arguments.worldArgument,
            MultiLiteralArgument("center"),
            DoubleArgument("x"),
            DoubleArgument("y"),
            DoubleArgument("z"),
            MultiLiteralArgument("xp")
                .withPermission(RADIUS_WORLD_XP_PERMISSION),
            IntegerArgument("amount", 1)
        )
        .executes(CommandExecutor { sender: CommandSender, args: Array<Any> ->
            val radius = args[0] as Double
            val world = args[2] as World
            val x = args[4] as Double
            val y = args[5] as Double
            val z = args[6] as Double
            val amount = args[8] as Int

            handleXpGiving(
                plugin,
                sender,
                amount,
                false,
                getPlayersInRadius(
                    Location(world, x, y, z),
                    radius
                ),
                Placeholder.unparsed("amount", amount.toString()),
                Placeholder.unparsed("world", world.name),
                Placeholder.unparsed("radius", radius.toString()),
                Placeholder.unparsed("x", x.toString()),
                Placeholder.unparsed("y", y.toString()),
                Placeholder.unparsed("z", z.toString())
            )
        })

    // Any
    private fun createRadiusWorldXpLevelsCommand() = CommandAPICommand("radius")
        .withRequirement { sender ->
            RADIUS_PERMISSIONS.any { sender.hasPermission(it) }
                    || RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            DoubleArgument("radius", 1.0),
            MultiLiteralArgument("world")
                .withRequirement { sender ->
                    RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
                },
            plugin.settingsManager.arguments.worldArgument,
            MultiLiteralArgument("center"),
            DoubleArgument("x"),
            DoubleArgument("y"),
            DoubleArgument("z"),
            MultiLiteralArgument("xp")
                .withPermission(RADIUS_WORLD_XP_PERMISSION),
            IntegerArgument("amount", 1),
            MultiLiteralArgument("levels")
        )
        .executes(CommandExecutor { sender: CommandSender, args: Array<Any> ->
            val radius = args[0] as Double
            val world = args[2] as World
            val x = args[4] as Double
            val y = args[5] as Double
            val z = args[6] as Double
            val amount = args[8] as Int

            handleXpGiving(
                plugin,
                sender,
                amount,
                true,
                getPlayersInRadius(
                    Location(world, x, y, z),
                    radius
                ),
                Placeholder.unparsed("amount", amount.toString()),
                Placeholder.unparsed("world", world.name),
                Placeholder.unparsed("radius", radius.toString()),
                Placeholder.unparsed("x", x.toString()),
                Placeholder.unparsed("y", y.toString()),
                Placeholder.unparsed("z", z.toString())
            )
        })

    // Any
    private fun createRadiusWorldMoneyCommand() = CommandAPICommand("radius")
        .withRequirement { sender ->
            RADIUS_PERMISSIONS.any { sender.hasPermission(it) }
                    || RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            DoubleArgument("radius", 1.0),
            MultiLiteralArgument("world")
                .withRequirement { sender ->
                    RADIUS_WORLD_PERMISSIONS.any { sender.hasPermission(it) }
                },
            plugin.settingsManager.arguments.worldArgument,
            MultiLiteralArgument("center"),
            DoubleArgument("x"),
            DoubleArgument("y"),
            DoubleArgument("z"),
            MultiLiteralArgument("money")
                .withPermission(RADIUS_WORLD_MONEY_PERMISSION),
            DoubleArgument("amount", 1.0)
        )
        .executes(CommandExecutor { sender: CommandSender, args: Array<Any> ->
            val radius = args[0] as Double
            val world = args[2] as World
            val x = args[4] as Double
            val y = args[5] as Double
            val z = args[6] as Double
            val amount = args[8] as Double

            handleMoneyGiving(
                plugin,
                sender,
                amount,
                getPlayersInRadius(
                    Location(world, x, y, z),
                    radius
                ),
                Placeholder.unparsed("amount", amount.toString()),
                Placeholder.unparsed("world", world.name),
                Placeholder.unparsed("radius", radius.toString()),
                Placeholder.unparsed("x", x.toString()),
                Placeholder.unparsed("y", y.toString()),
                Placeholder.unparsed("z", z.toString())
            )
        })

    // Player
    private fun createSpecialItemSaveCommand() = CommandAPICommand("special-item")
        .withRequirement { sender ->
            SPECIAL_ITEM_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            MultiLiteralArgument("save")
                .withPermission(SPECIAL_ITEM_SAVE_PERMISSION),
            StringArgument("name")
        )
        .executesPlayer(PlayerCommandExecutor { sender: Player, args: Array<Any?> ->
            val name = args[1] as String

            if (Material.matchMaterial(name) != null)
                return@PlayerCommandExecutor sendMessage(
                    sender,
                    plugin.settingsManager.messages.nameMaterial,
                    Placeholder.unparsed("wrong_value", name)
                )

            if (!name.isValidName())
                return@PlayerCommandExecutor sendMessage(
                    sender,
                    plugin.settingsManager.messages.nameAlphanumerical,
                    Placeholder.unparsed("wrong_value", name)
                )

            if (plugin.savedItemsManager.contains(name))
                return@PlayerCommandExecutor sendMessage(
                    sender,
                    plugin.settingsManager.messages.itemExists,
                    Placeholder.unparsed("wrong_value", name)
                )

            handleItemSaving(sender, name)
        })

    // Player
    private fun createSpecialItemSaveWithForceCommand() = CommandAPICommand("special-item")
        .withRequirement { sender ->
            SPECIAL_ITEM_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            MultiLiteralArgument("save")
                .withPermission(SPECIAL_ITEM_SAVE_PERMISSION),
            StringArgument("name"),
            MultiLiteralArgument("force")
        )
        .executesPlayer(PlayerCommandExecutor { sender: Player, args: Array<Any?> ->
            val name = args[1] as String

            if (Material.matchMaterial(name) != null)
                return@PlayerCommandExecutor sendMessage(
                    sender,
                    plugin.settingsManager.messages.nameMaterial,
                    Placeholder.unparsed("wrong_value", name)
                )

            if (!name.isValidName())
                return@PlayerCommandExecutor sendMessage(
                    sender,
                    plugin.settingsManager.messages.nameAlphanumerical,
                    Placeholder.unparsed("wrong_value", name)
                )

            handleItemSaving(sender, name)
        })

    // Any
    private fun createSpecialItemRemoveCommand() = CommandAPICommand("special-item")
        .withRequirement { sender ->
            SPECIAL_ITEM_PERMISSIONS.any { sender.hasPermission(it) }
        }
        .withArguments(
            MultiLiteralArgument("remove")
                .withPermission(SPECIAL_ITEM_REMOVE_PERMISSION),
            StringArgument("name")
        )
        .executes(CommandExecutor { sender: CommandSender, args: Array<Any?> ->
            val name = args[1] as String

            if (!plugin.savedItemsManager.contains(name))
                return@CommandExecutor sendMessage(
                    sender,
                    plugin.settingsManager.messages.itemInvalid,
                    Placeholder.unparsed("wrong_value", name)
                )

            plugin.savedItemsManager.removeItemStack(name)
            sendMessage(
                sender,
                plugin.settingsManager.messages.itemRemoved,
                Placeholder.unparsed("wrong_value", name)
            )
        })

    private fun handleItemSaving(player: Player, name: String) {
        val item = player.inventory.itemInMainHand.clone()
        if (item.type.isAir) return sendMessage(
            player,
            plugin.settingsManager.messages.itemAir
        )

        plugin.savedItemsManager.addItemStack(name, item.clone(), true)
        sendMessage(
            player,
            plugin.settingsManager.messages.itemSaved,
            Placeholder.unparsed("name", name)
        )
    }

    companion object {
        const val BASE_PERMISSION = "giveall"

        const val HELP_PERMISSION = "$BASE_PERMISSION.help"
        const val RELOAD_PERMISSION = "$BASE_PERMISSION.reload"

        const val BASE_MATERIAL_PERMISSION = "$BASE_PERMISSION.use.base.material"
        const val BASE_HAND_PERMISSION = "$BASE_PERMISSION.use.base.hand"
        const val BASE_XP_PERMISSION = "$BASE_PERMISSION.use.base.xp"
        const val BASE_MONEY_PERMISSION = "$BASE_PERMISSION.use.base.money"

        const val WORLD_MATERIAL_PERMISSION = "$BASE_PERMISSION.use.world.material"
        const val WORLD_HAND_PERMISSION = "$BASE_PERMISSION.use.world.hand"
        const val WORLD_XP_PERMISSION = "$BASE_PERMISSION.use.world.xp"
        const val WORLD_MONEY_PERMISSION = "$BASE_PERMISSION.use.world.money"

        const val RADIUS_MATERIAL_PERMISSION = "$BASE_PERMISSION.use.radius.base.material"
        const val RADIUS_HAND_PERMISSION = "$BASE_PERMISSION.use.radius.base.hand"
        const val RADIUS_XP_PERMISSION = "$BASE_PERMISSION.use.radius.base.xp"
        const val RADIUS_MONEY_PERMISSION = "$BASE_PERMISSION.use.radius.base.money"

        const val RADIUS_WORLD_MATERIAL_PERMISSION = "$BASE_PERMISSION.use.radius.world.material"
        const val RADIUS_WORLD_HAND_PERMISSION = "$BASE_PERMISSION.use.radius.world.hand"
        const val RADIUS_WORLD_XP_PERMISSION = "$BASE_PERMISSION.use.radius.world.xp"
        const val RADIUS_WORLD_MONEY_PERMISSION = "$BASE_PERMISSION.use.radius.world.money"

        const val SPECIAL_ITEM_SAVE_PERMISSION = "$BASE_PERMISSION.use.special-item.save"
        const val SPECIAL_ITEM_REMOVE_PERMISSION = "$BASE_PERMISSION.use.special-item.remove"

        private val BASE_PERMISSIONS = listOf(
            BASE_MATERIAL_PERMISSION,
            BASE_HAND_PERMISSION,
            BASE_XP_PERMISSION,
            BASE_MONEY_PERMISSION
        )

        private val WORLD_PERMISSIONS = listOf(
            WORLD_HAND_PERMISSION,
            WORLD_MATERIAL_PERMISSION,
            WORLD_XP_PERMISSION,
            WORLD_MONEY_PERMISSION
        )

        private val RADIUS_PERMISSIONS = listOf(
            RADIUS_HAND_PERMISSION,
            RADIUS_MATERIAL_PERMISSION,
            RADIUS_XP_PERMISSION,
            RADIUS_MONEY_PERMISSION
        )

        private val RADIUS_WORLD_PERMISSIONS = listOf(
            RADIUS_WORLD_HAND_PERMISSION,
            RADIUS_WORLD_MATERIAL_PERMISSION,
            RADIUS_WORLD_XP_PERMISSION,
            RADIUS_WORLD_MONEY_PERMISSION
        )

        private val SPECIAL_ITEM_PERMISSIONS = listOf(
            SPECIAL_ITEM_SAVE_PERMISSION,
            SPECIAL_ITEM_REMOVE_PERMISSION
        )

        private val ALL_PERMISSIONS = listOf(
            HELP_PERMISSION,
            RELOAD_PERMISSION,

            *BASE_PERMISSIONS.toTypedArray(),
            *WORLD_PERMISSIONS.toTypedArray(),
            *RADIUS_PERMISSIONS.toTypedArray(),
            *RADIUS_WORLD_PERMISSIONS.toTypedArray(),

            *SPECIAL_ITEM_PERMISSIONS.toTypedArray()
        )
    }
}