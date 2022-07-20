package com.blitzoffline.giveall.settings.holder

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class MessagesHolder(
    val inventoryFull: String = "<red>Your inventory is full!!",
    val playersOnly: String = "<red>This functionality can only be used by a player.",
    val consoleOnly: String = "<red>This functionality can only be used from console.",
    val noPlayers: String = "<red>Could not find any players.",
    val onlyYou: String = "<red>You are the only player we could find.",

    val itemAir: String = "<red>Item cannot be air.",
    val nameMaterial: String = "<red>Name cannot be a material.",
    val nameAlphanumerical: String = "<red>Name can only contain letters, digits, underscores and hyphens.",
    val amountZero: String = "<red>You can not send $0.",
    val xpZero: String = "<red>You can not send 0 xp.",
    val wrongCoords: String = "<red>Coordinates must be numbers.",
    val wrongAmount: String = "<red>Amount must be a number.",
    val wrongWorld: String = "<red>Could not find the world you specified.",
    val wrongMaterial: String = "<red>Could not find the material you specified.",
    val wrongRadius: String = "<red>Radius specified is not a number.",
    val wrongRadiusOrWorld: String = "<red>Parameter specified is not a world or a number.",
    val wrongUsage: String = "<red>Wrong Usage! Use: <yellow>/giveall help<red> to get help.",

    val noPermission: String = "<red> Error: <gray>You don't have permission to do that!",
    val configReloaded: String = "<gray>Config reloaded successfully.",

    val itemSaved: String = "<green>The item was successfully saved with the name: %name%",
    val itemRemoved: String = "<green>The item with the name %name% was successfully removed",
    val invalidItem: String = "<red>There is no item saved with this name.",
    val itemExists: String = "<red>An item named %name% already exists. Use: <yellow>/giveall save-item %name% force <red>to force its replacement.",

    val itemsReceived: String = "<dark_blue>You have received <green>%amount% <dark_blue>x <green>%material%<dark_blue>.",
    val moneyReceived: String = "<dark_blue>You have received <green>$%amount%<dark_blue>.",
    val xpLevelsReceived: String = "<dark_blue>You have received <green>%amount%<dark_blue> xp levels.",
    val xpPointsReceived: String = "<dark_blue>You have received <green>%amount%<dark_blue> xp points.",

    val itemsSent: String = "<green>You have given everyone: <dark_blue>%amount% <green>x <dark_blue>%material%<green.>",
    val itemsSentWorld: String
    = "<green>You have given everyone in <dark_blue>%world%<green>: <dark_blue>%amount% <green>x " +
            "<dark_blue>%material%<green>.",
    val itemsSentRadius: String
    = "<green>You have given everyone in a <dark_blue>%radius% blocks <green>radius: <dark_blue>%amount% <green>x " +
            "<dark_blue>%material%<green>.",
    val itemsSentConsoleRadius: String
    = "<green>You have given everyone in a <dark_blue>%radius% blocks <green> radius from <dark_blue>%x% %y% %z% " +
            "%world%<green>: <dark_blue>%amount% <green>x <dark_blue>%material%<green>.",

    val moneySent: String = "<green>You have given everyone <dark_blue>$%amount%<green>.",
    val moneySentWorld: String = "<green>You have given everyone in <dark_blue>%world%<green>: <dark_blue>$%amount%<green>.",
    val moneySentRadius: String
    = "<green>You have given everyone in a <dark_blue>%radius% blocks <green>radius: <dark_blue>$%amount%<green>.",

    val xpLevelsSent: String = "<green>You have given everyone: <dark_green>%amount% <green>xp levels.",
    val xpPointsSent: String = "<green>You have given everyone: <dark_green>%amount% <green>xp points.",
    val xpLevelsSentWorld: String
    = "<green>You have given everyone in <dark_blue>%world%<green>: <dark_blue>%amount% <green>xp levels.",
    val xpPointsSentWorld: String
    = "<green>You have given everyone in <dark_blue>%world%<green>: <dark_blue>%amount% <green>xp points.",
    val xpLevelsSentRadius: String
    = "<green>You have given everyone in a <dark_blue>%radius% blocks <green>radius: <dark_blue>%amount% <green>xp levels.",
    val xpPointsSentRadius: String
    = "<green>You have given everyone in a <dark_blue>%radius% blocks <green>radius: <dark_blue>%amount% <green>xp points.",
)