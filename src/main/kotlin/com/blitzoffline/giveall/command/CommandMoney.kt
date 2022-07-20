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
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Command("giveall", alias = ["gall"])
class CommandMoney(private val plugin: GiveAll) : BaseCommand() {
    @SubCommand("money")
    @Permission("giveall.use.money")
    fun money(sender: CommandSender, amount: Int, @Suggestion("worlds") @Optional argument: String?) {
        if (amount == null || amount <= 0) {
            plugin.settingsManager.messages.amountZero.msg(sender)
            return
        }

        if (argument != null && argument.toDoubleOrNull() == null && Bukkit.getWorld(argument) == null) {
            plugin.settingsManager.messages.wrongRadiusOrWorld.msg(sender)
            return
        }

        if (argument != null && argument.toDoubleOrNull() != null && sender !is Player) {
            plugin.settingsManager.messages.playersOnly.msg(sender)
            return
        }

        var checkWorld = false
        var checkRadius = false
        val players: List<Player>
        when {
            argument == null -> players = Bukkit.getServer().onlinePlayers.toList()
            argument.toDoubleOrNull() != null -> {
                if (sender !is Player) return
                checkRadius = true
                val boundingBox = calculateBoundingBox(sender.location, argument.toDouble())
                players = sender.world.getNearbyEntities(boundingBox)
                    .filterIsInstance(Player::class.java).toList()
            }
            else -> {
                val world = Bukkit.getServer().getWorld(argument) ?: run {
                    plugin.settingsManager.messages.wrongWorld.msg(sender)
                    return
                }
                players = world.players
                checkWorld = true
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

        for (player in players) {
            if (!plugin.settingsManager.settings.giveRewardsToSender && player == sender) continue
            if (!plugin.settingsManager.settings.requirePermission && !player.hasPermission("giveall.receive")) continue
            plugin.econ.depositPlayer(player, amount.toDouble())
            plugin.settingsManager.messages.moneyReceived
                .replace("%amount%", amount.toString())
                .msg(player)
        }

        when {
            checkWorld -> {
                plugin.settingsManager.messages.moneySentWorld
                    .replace("%amount%", amount.toString())
                    .replace("%world%", argument.toString())
                    .msg(sender)
            }
            checkRadius -> {
                plugin.settingsManager.messages.moneySentRadius
                    .replace("%amount%", amount.toString())
                    .replace("%radius%", argument.toString())
                    .msg(sender)
            }
            else -> {
                plugin.settingsManager.messages.moneySent
                    .replace("%amount%", amount.toString())
                    .msg(sender)
            }
        }
    }
}