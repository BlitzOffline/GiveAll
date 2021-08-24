package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.config.holder.Messages
import com.blitzoffline.giveall.config.holder.Settings
import com.blitzoffline.giveall.util.msg
import me.mattstudios.mf.annotations.Alias
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Completion
import me.mattstudios.mf.annotations.Optional
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox

@Alias("gall")
@Command("giveall")
class CommandMoney(private val plugin: GiveAll) : CommandBase() {
    private val settings = plugin.settings
    private val messages = plugin.messages

    @SubCommand("money")
    @Permission("giveall.use.money")
    fun money(sender: CommandSender, amount: Int?, @Completion("#worlds") @Optional argument: String?) {
        if (amount == null || amount <= 0) {
            messages[Messages.AMOUNT_ZERO].msg(sender)
            return
        }

        if (argument != null && argument.toDoubleOrNull() == null && Bukkit.getWorld(argument) == null) {
            messages[Messages.WRONG_USAGE].msg(sender)
            return
        }

        if (argument != null && argument.toDoubleOrNull() != null && sender !is Player) {
            messages[Messages.PLAYERS_ONLY].msg(sender)
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
                val location = sender.location
                val radius = argument.toDouble()
                val hypotenuse = kotlin.math.sqrt(2 * radius * radius)
                val boundingBox = BoundingBox(
                    location.x - hypotenuse,
                    location.y - radius,
                    location.z - hypotenuse,
                    location.x + hypotenuse,
                    location.y + radius,
                    location.z + hypotenuse
                )
                players = sender.world.getNearbyEntities(boundingBox)
                    .filterIsInstance(Player::class.java).toList()
            }
            else -> {
                val world = Bukkit.getServer().getWorld(argument) ?: run {
                    messages[Messages.WORLD_NAME_WRONG].msg(sender)
                    return
                }
                players = world.players
                checkWorld = true
            }
        }

        if (players.isEmpty()) {
            messages[Messages.NO_ONE_ONLINE].msg(sender)
            return
        }

        if (!settings[Settings.GIVE_REWARDS_TO_SENDER] && players.contains(sender) && players.size == 1) {
            messages[Messages.ONLY_YOU_ONLINE].msg(sender)
            return
        }

        for (player in players) {
            if (!settings[Settings.GIVE_REWARDS_TO_SENDER] && player == sender) continue
            if (settings[Settings.REQUIRES_PERMISSION] && !player.hasPermission("giveall.receive")) continue
            plugin.econ.depositPlayer(player, amount.toDouble())
            messages[Messages.MONEY_RECEIVED]
                .replace("%amount%", amount.toString())
                .msg(player)
        }

        when {
            checkWorld -> {
                messages[Messages.MONEY_SENT_WORLD]
                    .replace("%amount%", amount.toString())
                    .replace("%world%", argument.toString())
                    .msg(sender)
            }
            checkRadius -> {
                messages[Messages.MONEY_SENT_RADIUS]
                    .replace("%amount%", amount.toString())
                    .replace("%radius%", argument.toString())
                    .msg(sender)
            }
            else -> {
                messages[Messages.MONEY_SENT]
                    .replace("%amount%", amount.toString())
                    .msg(sender)
            }
        }
    }
}