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
class CommandXp(private val plugin: GiveAll) : BaseCommand() {
    @SubCommand("xp")
    @Permission("giveall.use.xp")
    fun xp(sender: CommandSender, xpInput: String, @Suggestion("worlds") @Optional argument: String?) {
        val settings = plugin.settings
        val messages = plugin.messages

        val levels = xpInput.last().equals('l', true)
        val node = if (levels) "LEVELS" else "XP"
        val xp = if (levels) xpInput.dropLast(1) else xpInput

        if (xp.toIntOrNull() == null) {
            messages.node("WRONG-USAGE").getString("&cWrong usage! Use: &e/giveall help&c to get help.").msg(sender)
            return
        }

        val amount = xp.toInt()
        if (amount <= 0) {
            messages.node("XP-ZERO").getString("&cYou can not send 0 xp.").msg(sender)
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
            if (levels) {
                player.giveExpLevels(amount)
            } else {
                player.giveExp(amount)
            }

            val default = if (levels) "&3You have received &a%amount%&3 xp levels." else "&3You have received &a%amount%&3 xp points"
            messages.node("XP-RECEIVED", node).getString(default)
                .replace("%amount%", amount.toString())
                .msg(player)
        }

        when {
            checkWorld -> {
                val default = if (levels) "&aYou have given everyone in &3%world%&a: &3%amount% &axp levels." else "&aYou have given everyone in &3%world%&a: &3%amount% &axp points."
                 messages.node("XP-SENT-WORLD", node).getString(default)
                    .replace("%amount%", amount.toString())
                    .replace("%world%", argument.toString())
                    .msg(sender)
            }
            checkRadius -> {
                val default = if (levels) "&aYou have given everyone in a &3%radius% blocks&a radius: &3%amount%&a xp levels." else "&aYou have given everyone in a &3%radius% blocks&a radius: &3%amount%&a xp points."
                messages.node("XP-SENT-RADIUS", node).getString(default)
                    .replace("%amount%", amount.toString())
                    .replace("%radius%", argument.toString())
                    .msg(sender)
            }
            else -> {
                val default = if (levels) "&aYou have given everyone: &3%amount% &axp levels." else "&aYou have given everyone: &3%amount% &axp points."
                messages.node("XP-SENT", node).getString(default)
                    .replace("%amount%", amount.toString())
                    .msg(sender)
            }
        }
    }
}