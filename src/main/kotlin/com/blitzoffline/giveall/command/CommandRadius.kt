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
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BoundingBox

@Alias("gall")
@Command("giveall")
class CommandRadius(plugin: GiveAll) : CommandBase() {
    private val settings = plugin.settings
    private val messages = plugin.messages

    @SubCommand("radius")
    @Permission("giveall.use.radius")
    fun radius(sender: Player, radius: Double?, @Completion("#materials") material: Material?, @Optional amt: Int?) {
        if (material == null) {
            messages[Messages.WRONG_MATERIAL].msg(sender)
            return
        }

        if (radius == null) {
            messages[Messages.WRONG_RADIUS].msg(sender)
            return
        }

        val location = sender.location
        val hypotenuse = kotlin.math.sqrt(2 * radius * radius)
        val boundingBox = BoundingBox(
            location.x-hypotenuse,
            location.y-radius, location.z-hypotenuse,
            location.x+hypotenuse,
            location.y+radius,
            location.z+hypotenuse
        )

        val players= sender.world.getNearbyEntities(boundingBox)
            .filterIsInstance(Player::class.java)
            .toList()

        if (players.isEmpty()) {
            messages[Messages.NO_ONE_ONLINE].msg(sender)
            return
        }

        if (players.size == 1 && !settings[Settings.GIVE_REWARDS_TO_SENDER]) {
            messages[Messages.ONLY_YOU_ONLINE].msg(sender)
            return
        }

        val amount = amt ?: material.maxStackSize
        val item = ItemStack(material, amount)

        for (player in players) {
            if (!settings[Settings.GIVE_REWARDS_TO_SENDER] && player == sender) continue
            if (settings[Settings.REQUIRES_PERMISSION] && !player.hasPermission("giveall.receive")) continue
            player.inventory.addItem(item)
            messages[Messages.ITEMS_RECEIVED]
                .replace("%amount%", amount.toString())
                .replace("%material%", material.name.lowercase())
                .msg(player)
            if (player.inventory.firstEmpty() == -1) messages[Messages.INVENTORY_FULL].msg(player)
        }

        messages[Messages.ITEMS_SENT_RADIUS]
            .replace("%amount%", amount.toString())
            .replace("%material%", material.name.lowercase())
            .replace("%radius%", radius.toInt().toString())
            .msg(sender)
    }
}