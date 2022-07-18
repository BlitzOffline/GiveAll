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
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Command("giveall", alias = ["gall"])
class CommandMoney(private val plugin: GiveAll) : BaseCommand() {
    @SubCommand("money")
    @Permission("giveall.use.money")
    fun money(sender: CommandSender, amount: Int, @Suggestion("worlds") @Optional argument: String?) {
        val settings = plugin.settings
        val messages = plugin.messages

        if (amount == null || amount <= 0) {
            messages.node("AMOUNT-ZERO").getString("&cYou can not send \$0.").msg(sender)
            return
        }

        if (argument != null && argument.toDoubleOrNull() == null && Bukkit.getWorld(argument) == null) {
            messages.node("WRONG-RADIUS-OR-WORLD").getString("&cParameter specified is not a world or a number.").msg(sender)
            return
        }

        if (argument != null && argument.toDoubleOrNull() != null && sender !is Player) {
            messages.node("PLAYERS-ONLY").getString("&cOnly players can use the radius functionality.").msg(sender)
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
                    messages.node("WRONG-WORLD").getString("&cCould not find the world you specified.").msg(sender)
                    return
                }
                players = world.players
                checkWorld = true
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

        for (player in players) {
            if (!settings.node("give-rewards-to-sender").getBoolean(false) && player == sender) continue
            if (!settings.node("requires-permission").getBoolean(false) && !player.hasPermission("giveall.receive")) continue
            plugin.econ.depositPlayer(player, amount.toDouble())
            messages.node("MONEY-RECEIVED").getString("&3You have received &a\$%amount%&3.")
                .replace("%amount%", amount.toString())
                .msg(player)
        }

        when {
            checkWorld -> {
                messages.node("MONEY-SENT-WORLD").getString("&aYou have given everyone in &3%world%&a: &3\$%amount%&a.")
                    .replace("%amount%", amount.toString())
                    .replace("%world%", argument.toString())
                    .msg(sender)
            }
            checkRadius -> {
                messages.node("MONEY-SENT-RADIUS").getString("&aYou have given everyone in a &3%radius% blocks&a radius: &3\$%amount%&a.")
                    .replace("%amount%", amount.toString())
                    .replace("%radius%", argument.toString())
                    .msg(sender)
            }
            else -> {
                messages.node("MONEY-SENT").getString("&aYou have given everyone &3\$%amount%&a.")
                    .replace("%amount%", amount.toString())
                    .msg(sender)
            }
        }
    }
}