package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.util.calculateBoundingBox
import com.blitzoffline.giveall.util.msg
import dev.triumphteam.cmd.bukkit.annotation.Permission
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Optional
import dev.triumphteam.cmd.core.annotation.SubCommand
import dev.triumphteam.cmd.core.annotation.Suggestion
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Command("giveall", alias = ["gall"])
class CommandConsoleRadius(private val plugin: GiveAll) : BaseCommand() {
    @SubCommand("console-radius")
    @Permission("giveall.use.console-radius")
    fun consoleRadius(
        sender: CommandSender,
        radius: Double,
        @Suggestion("materials") material: String,
        x: Double, y: Double, z: Double,
        @Suggestion("worlds") world: World,
        @Optional amt: Int?
    ) {
        val settings = plugin.settings
        val messages = plugin.messages

        val item = plugin.savedItemsManager.getSavedItemOrMaterial(material, amt ?: -1) ?:
            return plugin.messages.node("WRONG-MATERIAL")
                .getString("&cCould not find the material you specified.")
                .msg(sender)

        val boundingBox = calculateBoundingBox(Location(world, x, y, z), radius)
        val players= world.getNearbyEntities(boundingBox)
            .filterIsInstance(Player::class.java)
            .toList()

        if (players.isEmpty()) {
            messages.node("NO-ONE-ONLINE").getString("&7Could not find any players online.").msg(sender)
            return
        }

        for (player in players) {
            if (!settings.node("give-rewards-to-sender").getBoolean(false) && player == sender) continue
            if (settings.node("requires-permission").getBoolean(false) && !player.hasPermission("giveall.receive")) continue
            player.inventory.addItem(item)
            messages.node("ITEMS-RECEIVED").getString("&3You have received &a%amount% &3x&a %material%&3.")
                .replace("%amount%", item.amount.toString())
                .replace("%material%", material.lowercase())
                .msg(player)
            if (player.inventory.firstEmpty() == -1) messages.node("INVENTORY-FULL").getString("&cYour inventory is full!!").msg(player)
        }

        messages.node("ITEMS-SENT-CONSOLE-RADIUS")
            .getString("&aYou have given everyone in a &3%radius% blocks&a radius from &e%x% %y% %z% %world%&a : &3%amount% &ax&3 %material%&a.")
            .replace("%amount%", item.amount.toString())
            .replace("%material%", material.lowercase())
            .replace("%radius%", radius.toInt().toString())
            .replace("%x%", x.toString())
            .replace("%y%", y.toString())
            .replace("%z%", z.toString())
            .replace("%world%", world.name)
            .msg(sender)
    }
}