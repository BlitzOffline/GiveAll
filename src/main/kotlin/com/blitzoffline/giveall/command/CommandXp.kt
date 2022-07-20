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
class CommandXp(private val plugin: GiveAll) : BaseCommand() {
    @SubCommand("xp")
    @Permission("giveall.use.xp")
    fun xp(sender: CommandSender, xpInput: String, @Suggestion("worlds") @Optional argument: String?) {
        val levels = xpInput.last().equals('l', true)
        val xp = if (levels) xpInput.dropLast(1) else xpInput

        if (xp.toIntOrNull() == null) {
            plugin.settingsManager.messages.wrongUsage.msg(sender)
            return
        }

        val amount = xp.toInt()
        if (amount <= 0) {
            plugin.settingsManager.messages.xpZero.msg(sender)
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
            if (levels) {
                player.giveExpLevels(amount)
            } else {
                player.giveExp(amount)
            }

            if (levels) {
                plugin.settingsManager.messages.xpLevelsReceived
                    .replace("%amount%", amount.toString())
                    .msg(player)
            } else {
                plugin.settingsManager.messages.xpPointsReceived
                    .replace("%amount%", amount.toString())
                    .msg(player)
            }
        }

        when {
            checkWorld -> {
                if (levels) {
                    return plugin.settingsManager.messages.xpLevelsSentWorld
                        .replace("%amount%", amount.toString())
                        .replace("%world%", argument.toString())
                        .msg(sender)
                }

                plugin.settingsManager.messages.xpPointsSentWorld
                    .replace("%amount%", amount.toString())
                    .replace("%world%", argument.toString())
                    .msg(sender)
            }
            checkRadius -> {
                if (levels) {
                    return plugin.settingsManager.messages.xpLevelsSentRadius
                        .replace("%amount%", amount.toString())
                        .replace("%radius%", argument.toString())
                        .msg(sender)
                }

                plugin.settingsManager.messages.xpPointsSentRadius
                    .replace("%amount%", amount.toString())
                    .replace("%radius%", argument.toString())
                    .msg(sender)
            }
            else -> {
                if (levels) {
                    return plugin.settingsManager.messages.xpLevelsSent
                        .replace("%amount%", amount.toString())
                        .msg(sender)
                }

                plugin.settingsManager.messages.xpPointsSent
                    .replace("%amount%", amount.toString())
                    .msg(sender)
            }
        }
    }
}