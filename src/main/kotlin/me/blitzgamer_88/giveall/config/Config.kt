package me.blitzgamer_88.giveall.config

import ch.jalu.configme.Comment
import ch.jalu.configme.SettingsHolder
import ch.jalu.configme.properties.Property
import ch.jalu.configme.properties.PropertyInitializer

internal object Config : SettingsHolder {

    @JvmField
    @Comment("If you enable this, items will be given to the player that uses /giveall as well.")
    val giveItemsToSender: Property<Boolean> = PropertyInitializer.newProperty("giveItemsToSender", false)
    @JvmField
    @Comment("If you enable this, only players with the permission 'giveAllReceivePermission' will receive the items/money.")
    val requirePermissionToGetItems: Property<Boolean> = PropertyInitializer.newProperty("requirePermissionToGetItems", false)



    @JvmField
    @Comment("Permission to use all /giveall commands")
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
    @Comment("Permission to use /giveall help")
    val giveAllHelpPermission: Property<String> = PropertyInitializer.newProperty("giveAllHelpPermission", "giveall.help")
    @JvmField
    @Comment("Permission to receive items/money when someone uses /giveall", "This can be disabled in 'requirePermissionToGetItems'")
    val giveAllReceivePermission: Property<String> = PropertyInitializer.newProperty("giveAllReceivePermission", "giveall.receive")



    @JvmField
    @Comment("Customize the messages that are sent by the plugin:")
    val itemCannotBeAir: Property<String> = PropertyInitializer.newProperty("itemCannotBeAir", "&cYou can't send air.")
    @JvmField
    val onlyPlayersCanUseRadius: Property<String> = PropertyInitializer.newProperty("onlyPlayersCanUseRadius", "&cOnly players can use the radius functionality.")
    @JvmField
    val amountCannotBeZero: Property<String> = PropertyInitializer.newProperty("amountCannotBeZero", "&cYou can''t send \$0.")
    @JvmField
    val wrongWorldName: Property<String> = PropertyInitializer.newProperty("wrongWorldName", "&cCould not find the world you specified.")
    @JvmField
    val fullInventory: Property<String> = PropertyInitializer.newProperty("fullInventory", "&cYour inventory is full!!")

    @JvmField
    @Comment("Only messages where you can use the custom placeholders: %amount%, %material%, %radius% and %world%")
    val itemsReceived: Property<String> = PropertyInitializer.newProperty("itemsReceived", "&3You have received &a%amount% %material%&3.")
    @JvmField
    val itemsSent: Property<String> = PropertyInitializer.newProperty("itemsSent", "&aYou have given everyone: &3%amount% %material%&a.")
    @JvmField
    val itemsSentWorld: Property<String> = PropertyInitializer.newProperty("itemsSentWorld", "&aYou have given everyone in &3%world%&a: &3%amount% %material%&a")
    @JvmField
    val itemsSentRadius: Property<String> = PropertyInitializer.newProperty("itemsSentRadius", "&aYou have given everyone in a %radius% blocks radius: %amount% %material%")
    @JvmField
    val moneyReceived: Property<String> = PropertyInitializer.newProperty("moneyReceived", "&3You have received &a\$%amount%&3.")
    @JvmField
    val moneySent: Property<String> = PropertyInitializer.newProperty("moneySent", "&aYou have given everyone &3\$%amount%&a.")
    @JvmField
    val moneySentWorld: Property<String> = PropertyInitializer.newProperty("moneySentWorld", "&aYou have given everyone in &3%world%&a: &3\$%amount%&a.")
    @JvmField
    val moneySentRadius: Property<String> = PropertyInitializer.newProperty("moneySentRadius", "&aYou have given everyone in a &3%radius% blocks&a radius: &3\$%amount%&a.")

    @JvmField
    val wrongUsage: Property<String> = PropertyInitializer.newProperty("wrongUsage", "&cWrong usage!")
    @JvmField
    val noOneOnline: Property<String> = PropertyInitializer.newProperty("noOneOnline", "&7No player has been found so no items have been sent.")
    @JvmField
    val noOneOnlineMoney: Property<String> = PropertyInitializer.newProperty("noOneOnlineMoney", "&7No player has been found so no money has been sent.")
    @JvmField
    val noPermission: Property<String> = PropertyInitializer.newProperty("noPermission", "&cError: &7You don't have permission to do that!")
    @JvmField
    val configReloaded: Property<String> = PropertyInitializer.newProperty("configReloaded", "&7Config reloaded.")
    @JvmField
    @Comment("This message is sent only if 'giveItemsToSender' is disabled")
    val onlyYouOnline: Property<String> = PropertyInitializer.newProperty("onlyYouOnline", "&7You are the only one online.")
}