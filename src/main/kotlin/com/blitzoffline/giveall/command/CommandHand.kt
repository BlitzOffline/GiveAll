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
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@Command("giveall", alias = ["gall"])
class CommandHand(private val plugin: GiveAll) : BaseCommand() {
    @SubCommand("hand")
    @Permission("giveall.use.hand")
    fun giveAllHand(sender: Player, @Suggestion("worlds") @Optional argument: String?) {
        val settings = plugin.settings
        val messages = plugin.messages

        if (argument != null && argument.toDoubleOrNull() == null && Bukkit.getWorld(argument) == null) {
            messages.node("WRONG-RADIUS-OR-WORLD").getString("&cParameter specified is not a world or a number.").msg(sender)
            return
        }

        var checkWorld = false
        var checkRadius = false
        val players: List<Player>
        when {
            argument == null -> players = Bukkit.getServer().onlinePlayers.toList()
            argument.toDoubleOrNull() != null -> {
                checkRadius = true
                val boundingBox = calculateBoundingBox(sender.location, argument.toDouble())
                players = sender.world.getNearbyEntities(boundingBox).filterIsInstance<Player>().toList()
            }
            else -> {
                checkWorld = true
                val world = Bukkit.getServer().getWorld(argument) ?: run {
                    messages.node("WRONG-WORLD").getString("&cCould not find the world you specified.").msg(sender)
                    return
                }
                players = world.players
            }
        }

        if (players.isEmpty()) {
            messages.node("NO-ONE-ONLINE").getString("&7Could not find any players online.").msg(sender)
            return
        }

        if (!settings.node("give-rewards-to-sender").getBoolean(false) && players.contains(sender) && players.size == 1) {
            messages.node("ONLY-YOU-ONLINE").getString("&7You are the only player we could find.").msg(sender)
            return
        }

        val item = sender.inventory.itemInMainHand.clone()
        if (item.type.isAir) {
            messages.node("ITEM-AIR").getString("&cItem cannot be air.").msg(sender)
            return
        }

        for (player in players) {
            if (!settings.node("give-rewards-to-sender").getBoolean(false) && player == sender) continue
            if (settings.node("requires-permission").getBoolean(false)&& !player.hasPermission("giveall.receive")) continue
            player.inventory.addItem(item.clone())
            messages.node("ITEMS-RECEIVED").getString("&3You have received &a%amount% &3x&a %material%&3.")
                .replace("%amount%", item.amount.toString())
                .replace("%material%", item.type.name.lowercase())
                .msg(player)
            if (player.inventory.firstEmpty() == -1) messages.node("INVENTORY-FULL").getString("&cYour inventory is full!!").msg(player)
        }

        when {
            checkWorld -> {
                messages.node("ITEMS-SENT-WORLD").getString("&aYou have given everyone in &3%world%&a: &3%amount% &ax&3 %material%&a.")
                    .replace("%amount%", item.amount.toString())
                    .replace("%material%", item.type.name.lowercase())
                    .replace("%world%", argument.toString())
                    .msg(sender)
            }
            checkRadius -> {
                messages.node("ITEMS-SENT-WORLD").getString("&aYou have given everyone in &3%world%&a: &3%amount% &ax&3 %material%&a.")
                    .replace("%amount%", item.amount.toString())
                    .replace("%material%", item.type.name.lowercase())
                    .replace("%radius%", argument.toString())
                    .msg(sender)
            }
            else -> {
                messages.node("ITEMS-SENT").getString("&aYou have given everyone: &3%amount% &ax&3 %material%&a.")
                    .replace("%amount%", item.amount.toString())
                    .replace("%material%", item.type.name.lowercase())
                    .msg(sender)
            }
        }
    }
}