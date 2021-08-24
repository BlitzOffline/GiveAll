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
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox

@Alias("gall")
@Command("giveall")
class CommandHand(plugin: GiveAll) : CommandBase() {
    private val settings = plugin.settings
    private val messages = plugin.messages

    @SubCommand("hand")
    @Permission("giveall.use.hand")
    fun giveAllHand(sender: Player, @Completion("#worlds") @Optional argument: String?) {
        if (argument != null && argument.contains(" ")) {
            messages[Messages.WRONG_USAGE].msg(sender)
            return
        }

        var checkWorld = false
        var checkRadius = false

        val players: MutableCollection<out Player>
        when {
            argument == null -> players = Bukkit.getServer().onlinePlayers
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
                players = sender.world.getNearbyEntities(boundingBox)
                    .filterIsInstance(Player::class.java).toMutableList()
            }
            else -> {
                checkWorld = true
                val world = Bukkit.getServer().getWorld(argument)
                if (world == null) {
                    messages[Messages.WORLD_NAME_WRONG].msg(sender)
                    return
                }
                players = world.players
            }
        }

        if (!settings[Settings.GIVE_REWARDS_TO_SENDER]) players.remove(sender)

        if (players.isEmpty()) {
            messages[Messages.NO_ONE_ONLINE].msg(sender)
            return
        }

        // The item is cloned because it seems like if the item is changed in the player inventory's in the middle of the process, it will give the new items to the player.
        val item = sender.inventory.itemInMainHand.clone()
        if (item.type == Material.AIR) {
            messages[Messages.ITEM_AIR].msg(sender)
            return
        }

        for (player in players) {
            if (settings[Settings.REQUIRES_PERMISSION] && !player.hasPermission("giveall.receive")) continue
            player.inventory.addItem(item.clone())
            messages[Messages.ITEMS_RECEIVED]
                .replace("%amount%", item.amount.toString())
                .replace("%material%", item.type.name.lowercase())
                .msg(player)
            if (player.inventory.firstEmpty() == -1) messages[Messages.INVENTORY_FULL].msg(player)
        }

        when {
            checkWorld -> {
                messages[Messages.ITEMS_SENT_WORLD]
                    .replace("%amount%", item.amount.toString())
                    .replace("%material%", item.type.name.lowercase())
                    .replace("%world%", argument.toString())
                    .msg(sender)
            }
            checkRadius -> {
                messages[Messages.ITEMS_SENT_RADIUS]
                    .replace("%amount%", item.amount.toString())
                    .replace("%material%", item.type.name.lowercase())
                    .replace("%radius%", argument.toString())
                    .msg(sender)
            }
            else -> {
                messages[Messages.ITEMS_SENT]
                    .replace("%amount%", item.amount.toString())
                    .replace("%material%", item.type.name.lowercase())
                    .msg(sender)
            }
        }
    }
}