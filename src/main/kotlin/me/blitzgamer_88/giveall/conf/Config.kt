package me.blitzgamer_88.giveall.conf

import ch.jalu.configme.Comment
import ch.jalu.configme.SettingsHolder
import ch.jalu.configme.properties.Property
import ch.jalu.configme.properties.PropertyInitializer

internal object Config : SettingsHolder {

    @JvmField
    @Comment("If you enable this, items will be given to the sender if a player uses /giveall or /giveall hand")
    val giveItemsToSender: Property<Boolean> = PropertyInitializer.newProperty("giveItemsToSender", false)

    @JvmField
    @Comment("Permission to use /giveall")
    val giveAllPermission: Property<String> = PropertyInitializer.newProperty("giveAllPermission", "giveall.use")
    @JvmField
    @Comment("Permission to use /giveall hand")
    val giveAllHandPermission: Property<String> = PropertyInitializer.newProperty("giveAllHandPermission", "giveall.use.hand")
    @JvmField
    @Comment("Permission to use /giveall reload")
    val giveAllReloadPermission: Property<String> = PropertyInitializer.newProperty("giveAllReloadPermission", "giveall.reload")

    @JvmField
    @Comment("Customize the messages that are sent by the plugin:")
    val itemCannotBeAir: Property<String> = PropertyInitializer.newProperty("itemCannotBeAir", "&cYou can't send air")
    @JvmField
    val fullInventory: Property<String> = PropertyInitializer.newProperty("fullInventory", "&cYour inventory is full!!")
    @JvmField
    val itemsReceived: Property<String> = PropertyInitializer.newProperty("itemsReceived", "&3You have received %amount% %material%")
    @JvmField
    val itemsSent: Property<String> = PropertyInitializer.newProperty("itemsSent", "&aYou have given everyone %amount% %material%")
    @JvmField
    val wrongUsage: Property<String> = PropertyInitializer.newProperty("wrongUsage", "&cWrong usage!")
    @JvmField
    val noOneOnline: Property<String> = PropertyInitializer.newProperty("noOneOnline", "&cNo one is online so no item have been sent.")
    @JvmField
    val noPermission: Property<String> = PropertyInitializer.newProperty("noPermission", "&cYou don't have permission to do that")
    @JvmField
    val configReloaded: Property<String> = PropertyInitializer.newProperty("configReloaded", "&aConfig has been reloaded")
    @JvmField
    @Comment("This message is sent only if 'giveItemsToSender' is disabled")
    val onlyYouOnline: Property<String> = PropertyInitializer.newProperty("onlyYouOnline", "&cYou are the only one online. You can't give yourself items")
}