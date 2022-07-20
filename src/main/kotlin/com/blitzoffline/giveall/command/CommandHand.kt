package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.util.calculateBoundingBox
import com.blitzoffline.giveall.extension.msg
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
        if (argument != null && argument.toDoubleOrNull() == null && Bukkit.getWorld(argument) == null) {
            plugin.settingsManager.messages.wrongRadiusOrWorld.msg(sender)
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
                    plugin.settingsManager.messages.wrongWorld.msg(sender)
                    return
                }
                players = world.players
            }
        }

        if (players.isEmpty()) {
            plugin.settingsManager.messages.noPlayers.msg(sender)
            return
        }

        if (!plugin.settingsManager.settings.giveRewardsToSender && players.contains(sender) && players.size == 1) {
            plugin.settingsManager.messages.onlyYou.msg(sender)
            return
        }

        val item = sender.inventory.itemInMainHand.clone()
        if (item.type.isAir) {
            plugin.settingsManager.messages.itemAir.msg(sender)
            return
        }

        for (player in players) {
            if (!plugin.settingsManager.settings.giveRewardsToSender && player == sender) continue
            if (plugin.settingsManager.settings.requirePermission && !player.hasPermission("giveall.receive")) continue
            player.inventory.addItem(item.clone())
            plugin.settingsManager.messages.itemsReceived
                .replace("%amount%", item.amount.toString())
                .replace("%material%", item.type.name.lowercase())
                .msg(player)
            if (player.inventory.firstEmpty() == -1) plugin.settingsManager.messages.inventoryFull.msg(player)
        }

        when {
            checkWorld -> {
                plugin.settingsManager.messages.itemsSentWorld
                    .replace("%amount%", item.amount.toString())
                    .replace("%material%", item.type.name.lowercase())
                    .replace("%world%", argument.toString())
                    .msg(sender)
            }
            checkRadius -> {
                plugin.settingsManager.messages.itemsSentRadius
                    .replace("%amount%", item.amount.toString())
                    .replace("%material%", item.type.name.lowercase())
                    .replace("%radius%", argument.toString())
                    .msg(sender)
            }
            else -> {
                plugin.settingsManager.messages.itemsSent
                    .replace("%amount%", item.amount.toString())
                    .replace("%material%", item.type.name.lowercase())
                    .msg(sender)
            }
        }
    }
}