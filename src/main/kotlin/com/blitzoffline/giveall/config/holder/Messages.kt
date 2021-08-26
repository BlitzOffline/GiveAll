package com.blitzoffline.giveall.config.holder

import me.mattstudios.config.SettingsHolder
import me.mattstudios.config.annotations.Path
import me.mattstudios.config.properties.Property

object Messages : SettingsHolder {

    @Path("ITEM-AIR")
    val ITEM_AIR = Property.create("&cYou can not send air.")

    @Path("AMOUNT-ZERO")
    val AMOUNT_ZERO = Property.create("&cYou can not send \$0.")

    @Path("INVENTORY-FULL")
    val INVENTORY_FULL = Property.create("&cYour inventory is full!!")

    @Path("WRONG-WORLD")
    val WRONG_WORLD = Property.create("&cCould not find the world you specified.")

    @Path("WRONG-MATERIAL")
    val WRONG_MATERIAL = Property.create("&cCould not find the material you specified.")

    @Path("WRONG-RADIUS")
    val WRONG_RADIUS = Property.create("&cRadius specified is not a number.")

    @Path("WRONG-RADIUS-OR-WORLD")
    val WRONG_RADIUS_OR_WORLD = Property.create("&cParameter specified is not a world or a number.")

    @Path("PLAYERS-ONLY")
    val PLAYERS_ONLY = Property.create("&cOnly players can use the radius functionality.")

    @Path("NO-ONE-ONLINE")
    val NO_ONE_ONLINE = Property.create("&7Could not find any players online.")

    @Path("ONLY-YOU-ONLINE")
    val ONLY_YOU_ONLINE = Property.create("&7You are the only player we could find.")

    @Path("WRONG-USAGE")
    val WRONG_USAGE = Property.create("&cWrong usage! Use: &e/giveall help&c to get help")

    @Path("NO-PERMISSION")
    val NO_PERMISSION = Property.create("&cError: &7You don''t have permission to do that!")

    @Path("CONFIG-RELOADED")
    val CONFIG_RELOADED = Property.create("&7Config reloaded successfully.")

    @Path("ITEMS-RECEIVED")
    val ITEMS_RECEIVED = Property.create("&3You have received &a%amount% &3x&a %material%&3.")

    @Path("MONEY-RECEIVED")
    val MONEY_RECEIVED = Property.create("&3You have received &a\$%amount%&3.")

    @Path("ITEMS-SENT")
    val ITEMS_SENT = Property.create("&aYou have given everyone: &3%amount% &ax&3 %material%&a.")

    @Path("ITEMS-SENT-WORLD")
    val ITEMS_SENT_WORLD = Property.create("&aYou have given everyone in &3%world%&a: &3%amount% &ax&3 %material%&a.")

    @Path("ITEMS-SENT-RADIUS")
    val ITEMS_SENT_RADIUS = Property.create("&aYou have given everyone in a &3%radius% blocks&a radius: &3%amount% &ax&3 %material%&a.")

    @Path("MONEY-SENT")
    val MONEY_SENT = Property.create("&aYou have given everyone &3\$%amount%&a.")

    @Path("MONEY-SENT-WORLD")
    val MONEY_SENT_WORLD = Property.create("&aYou have given everyone in &3%world%&a: &3\$%amount%&a.")

    @Path("MONEY-SENT-RADIUS")
    val MONEY_SENT_RADIUS = Property.create("&aYou have given everyone in a &3%radius% blocks&a radius: &3\$%amount%&a.")
}