package com.blitzoffline.giveall.settings.holder

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class MessagesHolder(
    val inventoryFull: String = "<red>Your inventory is full!!",
    val noPlayers: String = "<red>Could not find any players!",

    val itemAir: String = "<red>Item cannot be air.",
    val nameMaterial: String = "<red>Name cannot be a material.",
    val nameAlphanumerical: String = "<red>Name can only contain letters, digits, underscores and hyphens.",
    val wrongWorld: String = "<red>Could not find the world you specified.",
    val wrongMaterial: String = "<red>Could not find the material you specified.",

    val configReloaded: String = "<gray>Config reloaded successfully.",

    val itemSaved: String = "<green>The item was successfully saved with the name: <name>",
    val itemRemoved: String = "<green>The item with the name <name> was successfully removed",
    val itemInvalid: String = "<red>There is no item saved with this name.",
    val itemExists: String = "<red>An item named <name> already exists. Use: <yellow>/giveall special-item save <name> force <red>to force its replacement.",

    val itemsReceived: String = "<dark_aqua>You have received <green><amount> <dark_aqua>x <green><material><dark_aqua>.",
    val moneyReceived: String = "<dark_aqua>You have received <green>$<amount><dark_aqua>.",
    val xpLevelsReceived: String = "<dark_aqua>You have received <green><amount><dark_aqua> xp levels.",
    val xpPointsReceived: String = "<dark_aqua>You have received <green><amount><dark_aqua> xp points.",

    val itemsSent: String = "<green>You have given <count> players: <dark_aqua><amount> <green>x <dark_aqua><material><green.>",
    val moneySent: String = "<green>You have given <count> players: <dark_aqua>$<amount><green>.",
    val xpLevelsSent: String = "<green>You have given <count> players: <dark_aqua><amount> <green>xp levels.",
    val xpPointsSent: String = "<green>You have given <count> players: <dark_aqua><amount> <green>xp points.",

    val help: List<String> = listOf(
        "<gray>---- <gold>GiveAll by BlitzOffline <gray>----",
        "",
        "<yellow>Available Commands:",
        "",
        "<gray>/giveall <dark_gray>- <white>list help commands",
        "<gray>/giveall help <dark_gray>- <white>list help commands",
        "<gray>/giveall reload <dark_gray>- <white>reload settings and messages",
        "",
        "<gray>/giveall material <material> [amount] <dark_gray>- <white>give everyone online the item specified",
        "<gray>/giveall hand [amount] <dark_gray>- <white>give everyone online the item you are holding in your hand",
        "<gray>/giveall xp <amount> [\"levels\"] <dark_gray>- <white>give everyone online exp points",
        "<gray>/giveall money <amount> <dark_gray>- <white>give everyone online money",
        "",
        "<gray>/giveall world <world> material <material> [amount] <dark_gray>- <white>give everyone in specified world the item specified",
        "<gray>/giveall world <world> hand [amount] <dark_gray>- <white>give everyone in specified world the item you are holding in your hand",
        "<gray>/giveall world <world> xp <amount> [\"levels\"] <dark_gray>- <white>give everyone in specified world exp points",
        "<gray>/giveall world <world> money <amount> <dark_gray>- <white>give everyone in specified world money",
        "",
        "<gray>/giveall radius <radius> world <world> center <x> <y> <z> material <material> [amount] <dark_gray>- <white>give everyone in specified world in specified radius from coordinates the item specified",
        "<gray>/giveall radius <radius> world <world> center <x> <y> <z> hand [amount] <dark_gray>- <white>give everyone in specified world in specified radius from coordinates the item you are holding in your hand",
        "<gray>/giveall radius <radius> world <world> center <x> <y> <z> xp <amount> [\"levels\"] <dark_gray>- <white>give everyone in specified world in specified radius from coordinates exp points",
        "<gray>/giveall radius <radius> world <world> center <x> <y> <z> money <amount> <dark_gray>- <white>give everyone in specified world in specified radius from coordinates money",
        "",
        "<gray>/giveall radius <radius> material <material> [amount] <dark_gray>- <white>give everyone in specified radius from you the item specified",
        "<gray>/giveall radius <radius> hand [amount] <dark_gray>- <white>give everyone in specified radius from you the item you are holding in your hand",
        "<gray>/giveall radius <radius> xp <amount> [\"levels\"] <dark_gray>- <white>give everyone in specified radius from you exp points",
        "<gray>/giveall radius <radius> money <amount> <dark_gray>- <white>give everyone in specified radius from you money",
        "",
        "<gray>/giveall special-item save <name> [\"force\"] <dark_gray>- <white>save the item you're holding in the database under the specified name",
        "<gray>/giveall special-item remove <name> <dark_gray>- <white>remove the item that was saved in the database under the specified name",
        "",
        "<yellow>Parameters inside [] are optional, Parameters inside <> are required.",
        "<gold>Materials can also be special items that you saved to the database."
    )
)