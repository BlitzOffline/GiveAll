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
import org.bukkit.entity.Player

@Command("giveall", alias = ["gall"])
class CommandRadius(private val plugin: GiveAll) : BaseCommand() {
    @SubCommand("radius")
    @Permission("giveall.use.radius")
    fun radius(sender: Player, radius: Double, @Suggestion("materials") material: String, @Optional amt: Int?) {
        val settings = plugin.settings
        val messages = plugin.messages

        val item = plugin.savedItemsManager.getSavedItemOrMaterial(material, amt ?: -1) ?:
        return plugin.messages.node("WRONG-MATERIAL")
            .getString("&cCould not find the material you specified.")
            .msg(sender)

        val boundingBox = calculateBoundingBox(sender.location, radius);
        val players= sender.world.getNearbyEntities(boundingBox)
            .filterIsInstance(Player::class.java)
            .toList()

        if (players.isEmpty()) {
            messages.node("NO-ONE-ONLINE").getString("&7Could not find any players online.").msg(sender)
            return
        }

        if (players.size == 1 && !settings.node("give-rewards-to-sender").getBoolean(false)) {
            messages.node("ONLY-YOU-ONLINE").getString("&7You are the only player we could find.").msg(sender)
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

        messages.node("ITEMS-SENT-RADIUS").getString("&aYou have given everyone in a &3%radius% blocks&a radius: &3%amount% &ax&3 %material%&a.")
            .replace("%amount%", item.amount.toString())
            .replace("%material%", material.lowercase())
            .replace("%radius%", radius.toInt().toString())
            .msg(sender)
    }
}