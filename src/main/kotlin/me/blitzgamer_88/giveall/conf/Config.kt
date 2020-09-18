package me.blitzgamer_88.giveall.conf
import ch.jalu.configme.Comment
import ch.jalu.configme.SettingsHolder
import ch.jalu.configme.properties.Property
import ch.jalu.configme.properties.PropertyInitializer

internal object Config : SettingsHolder {

    @JvmField
    @Comment("If you enable this, items will be given to the sender if a player uses /giveall")
    val giveItemsToSender: Property<Boolean> = PropertyInitializer.newProperty("giveItemsToSender", false)
    @JvmField
    @Comment("If you enable this, players will need the 'giveAllReceivePermission' to get the items/money")
    val requirePermissionToGetItems: Property<Boolean> = PropertyInitializer.newProperty("requirePermissionToGetItems", false)

    @JvmField
    @Comment(" ", "Permission to use all /giveall commands")
    val giveAllMainPermission: Property<String> = PropertyInitializer.newProperty("giveAllMainPermission", "giveall.use.all")
    @JvmField
    @Comment("Permission to use /giveall")
    val giveAllPermission: Property<String> = PropertyInitializer.newProperty("giveAllPermission", "giveall.use")
    @JvmField
    @Comment("Permission to use /giveall hand")
    val giveAllHandPermission: Property<String> = PropertyInitializer.newProperty("giveAllHandPermission", "giveall.use.hand")
    @JvmField
    @Comment("Permission to use /giveall money")
    val giveAllMoneyPermission: Property<String> = PropertyInitializer.newProperty("giveAllMoneyPermission", "giveall.use.money")
    @JvmField
    @Comment("Permission to use /giveall world")
    val giveAllWorldPermission: Property<String> = PropertyInitializer.newProperty("giveAllWorldPermission", "giveall.use.world")
    @JvmField
    @Comment("Permission to use /giveall radius")
    val giveAllRadiusPermission: Property<String> = PropertyInitializer.newProperty("giveAllRadiusPermission", "giveall.use.radius")
    @JvmField
    @Comment("Permission to use /giveall reload")
    val giveAllReloadPermission: Property<String> = PropertyInitializer.newProperty("giveAllReloadPermission", "giveall.reload")
    @JvmField
    @Comment("Permission to receive items/money when someone uses /giveall", "This can be disabled in 'requirePermissionToGetItems'")
    val giveAllReceivePermission: Property<String> = PropertyInitializer.newProperty("giveAllReceivePermission", "giveall.receive")

    @JvmField
    @Comment(" ", "Customize the messages that are sent by the plugin:")
    val itemCannotBeAir: Property<String> = PropertyInitializer.newProperty("itemCannotBeAir", "&cYou can't send air")
    @JvmField
    val onlyPlayersCanUseRadius: Property<String> = PropertyInitializer.newProperty("onlyPlayersCanUseRadius", "&cOnly players can use a radius.")
    @JvmField
    val amountCannotBeZero: Property<String> = PropertyInitializer.newProperty("amountCannotBeZero", "&cYou can't send 0$")
    @JvmField
    val wrongWorldName: Property<String> = PropertyInitializer.newProperty("wrongWorldName", "&cCould not find the world name.")
    @JvmField
    val fullInventory: Property<String> = PropertyInitializer.newProperty("fullInventory", "&cYour inventory is full!!")

    @JvmField
    @Comment(" ","Only messages where you can use the custom placeholders: %amount%, %material%, %radius%, %world%")
    val itemsReceived: Property<String> = PropertyInitializer.newProperty("itemsReceived", "&3You have received %amount% %material%")
    @JvmField
    val itemsSent: Property<String> = PropertyInitializer.newProperty("itemsSent", "&aYou have given everyone: %amount% %material%")
    @JvmField
    val itemsSentWorld: Property<String> = PropertyInitializer.newProperty("itemsSentWorld", "&aYou have given everyone in %world%: %amount% %material%")
    @JvmField
    val itemsSentRadius: Property<String> = PropertyInitializer.newProperty("itemsSentRadius", "&aYou have given everyone in a %radius% blocks radius: %amount% %material%")

    @JvmField
    val moneyReceived: Property<String> = PropertyInitializer.newProperty("moneyReceived", "&3You have received %amount%$")
    @JvmField
    val moneySent: Property<String> = PropertyInitializer.newProperty("moneySent", "&aYou have given everyone %amount%$")
    @JvmField
    val moneySentWorld: Property<String> = PropertyInitializer.newProperty("moneySentWorld", "&aYou have given everyone in %world%: %amount%$")
    @JvmField
    val moneySentRadius: Property<String> = PropertyInitializer.newProperty("moneySentRadius", "&aYou have given everyone in a %radius% blocks radius: %amount%$")

    @JvmField
    @Comment(" ")
    val wrongUsage: Property<String> = PropertyInitializer.newProperty("wrongUsage", "&cWrong usage!")

    @JvmField
    val noOneOnline: Property<String> = PropertyInitializer.newProperty("noOneOnline", "&cNo player has been found so no items have been sent.")
    @JvmField
    val noOneOnlineMoney: Property<String> = PropertyInitializer.newProperty("noOneOnlineMoney", "&cNo player has been found so no money have been sent.")

    @JvmField
    val noPermission: Property<String> = PropertyInitializer.newProperty("noPermission", "&cYou don't have permission to do that")
    @JvmField
    val configReloaded: Property<String> = PropertyInitializer.newProperty("configReloaded", "&aConfig has been reloaded")
    @JvmField
    @Comment("This message is sent only if 'giveItemsToSender' is disabled")
    val onlyYouOnline: Property<String> = PropertyInitializer.newProperty("onlyYouOnline", "&cYou are the only one online. You can't give yourself items")
}