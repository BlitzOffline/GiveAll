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
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BoundingBox

@Alias("gall")
@Command("giveall")
class CommandRadius(private val plugin: GiveAll) : CommandBase() {
    @SubCommand("radius")
    @Permission("giveall.use.radius")
    fun radius(sender: Player, radius: Double?, @Completion("#materials") material: Material?, @Optional amt: Int?) {
        val settings = plugin.settings
        val messages = plugin.messages

        if (material == null) {
            messages.node("WRONG-MATERIAL").getString("&cCould not find the material you specified.").msg(sender)
            return
        }

        if (radius == null) {
            messages.node("WRONG-RADIUS").getString("&cRadius specified is not a number.").msg(sender)
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
            messages.node("NO-ONE-ONLINE").getString("&7Could not find any players online.").msg(sender)
            return
        }

        if (players.size == 1 && !settings.node("give-rewards-to-sender").getBoolean(false)) {
            messages.node("ONLY-YOU-ONLINE").getString("&7You are the only player we could find.").msg(sender)
            return
        }

        val amount = amt ?: material.maxStackSize
        val item = ItemStack(material, amount)

        for (player in players) {
            if (!settings.node("give-rewards-to-sender").getBoolean(false) && player == sender) continue
            if (settings.node("requires-permission").getBoolean(false) && !player.hasPermission("giveall.receive")) continue
            player.inventory.addItem(item)
            messages.node("ITEMS-RECEIVED").getString("&3You have received &a%amount% &3x&a %material%&3.")
                .replace("%amount%", amount.toString())
                .replace("%material%", material.name.lowercase())
                .msg(player)
            if (player.inventory.firstEmpty() == -1) messages.node("INVENTORY-FULL").getString("&cYour inventory is full!!").msg(player)
        }

        messages.node("ITEMS-SENT-RADIUS").getString("&aYou have given everyone in a &3%radius% blocks&a radius: &3%amount% &ax&3 %material%&a.")
            .replace("%amount%", amount.toString())
            .replace("%material%", material.name.lowercase())
            .replace("%radius%", radius.toInt().toString())
            .msg(sender)
    }
}