package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.util.msg
import me.mattstudios.mf.annotations.Alias
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Completion
import me.mattstudios.mf.annotations.Optional
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox

@Alias("gall")
@Command("giveall")
class CommandHand(private val plugin: GiveAll) : CommandBase() {
    @SubCommand("hand")
    @Permission("giveall.use.hand")
    fun giveAllHand(sender: Player, @Completion("#worlds") @Optional argument: String?) {
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

        // The item is cloned because it seems like if the item is changed in the player inventory's in the middle of the process, it will give the new items to the players.
        val item = sender.inventory.itemInMainHand.clone()
        if (item.type == Material.AIR) {
            messages.node("ITEM-AIR").getString("&cYou can not send air.").msg(sender)
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