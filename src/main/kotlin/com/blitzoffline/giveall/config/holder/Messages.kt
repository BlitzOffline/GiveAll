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

    @Path("WORLD-NAME-WRONG")
    val WORLD_NAME_WRONG = Property.create("&cCould not find the world you specified.")

    @Path("PLAYERS-ONLY")
    val PLAYERS_ONLY = Property.create("&cOnly players can use the radius functionality.")

    @Path("NO-ONE-ONLINE")
    val NO_ONE_ONLINE = Property.create("&7Could not find any players online.")

    @Path("ONLY-YOU-ONLINE")
    val ONLY_YOU_ONLINE = Property.create("&7You are the only one online.")

    @Path("WRONG-USAGE")
    val WRONG_USAGE = Property.create("&cWrong usage!")

    @Path("NO-PERMISSION")
    val NO_PERMISSION = Property.create("&cError: &7You don''t have permission to do that!")

    @Path("CONFIG-RELOADED")
    val CONFIG_RELOADED = Property.create("&7Config reloaded successfully.")

    @Path("ITEMS-RECEIVED")
    val ITEMS_RECEIVED = Property.create("&3You have received &a%amount% %material%&3.")

    @Path("MONEY-RECEIVED")
    val MONEY_RECEIVED = Property.create("&3You have received &a\$%amount%&3.")

    @Path("ITEMS-SENT")
    val ITEMS_SENT = Property.create("&aYou have given everyone: &3%amount% %material%&a.")

    @Path("ITEMS-SENT-WORLD")
    val ITEMS_SENT_WORLD = Property.create("&aYou have given everyone in &3%world%&a: &3%amount% %material%&a.")

    @Path("ITEMS-SENT-RADIUS")
    val ITEMS_SENT_RADIUS = Property.create("&aYou have given everyone in a &3%radius% blocks&a radius: &3%amount% %material%&a.")

    @Path("MONEY-SENT")
    val MONEY_SENT = Property.create("&aYou have given everyone &3\$%amount%&a.")

    @Path("MONEY-SENT-WORLD")
    val MONEY_SENT_WORLD = Property.create("&aYou have given everyone in &3%world%&a: &3\$%amount%&a.")

    @Path("MONEY-SENT-RADIUS")
    val MONEY_SENT_RADIUS = Property.create("&aYou have given everyone in a &3%radius% blocks&a radius: &3\$%amount%&a.")
}