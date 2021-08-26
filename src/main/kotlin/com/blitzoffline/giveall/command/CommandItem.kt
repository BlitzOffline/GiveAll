package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.config.holder.Messages
import com.blitzoffline.giveall.config.holder.Settings
import com.blitzoffline.giveall.util.msg
import me.mattstudios.mf.annotations.Alias
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Completion
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Optional
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.base.CommandBase
import org.bukkit.Bukkit.getServer
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Alias("gall")
@Command("giveall")
class CommandItem(plugin: GiveAll) : CommandBase() {
    private val settings = plugin.settings
    private val messages = plugin.messages

    @Default
    @Permission("giveall.use")
    fun item(sender: CommandSender, @Completion("#materials") material: Material?, @Optional amt: Int?) {
        if (material == null && amt == null) {
            "&7---- &6GiveAll by BlitzOffline &7----".msg(sender)
            "".msg(sender)
            "&7/giveall help &8-&f show this help menu".msg(sender)
            "&7/giveall reload &8-&f reload the configuration".msg(sender)
            "&7/giveall <material> [amount] &8-&f give items to all players".msg(sender)
            "&7/giveall world <world> <material> [amount] &8-&f give items to all players in a world".msg(sender)
            "&7/giveall radius <radius> <material> [amount] &8-&f give items to all players in a radius".msg(sender)
            "&7/giveall hand [world/radius] &8-&f give the items you hold in your hand to all players".msg(sender)
            "&7/giveall money <amount> [world/radius] &8-&f give money to all players".msg(sender)
            return
        }

        if (material == null) {
            messages[Messages.WRONG_MATERIAL].msg(sender)
            return
        }

        val players = getServer().onlinePlayers
        if (players.isEmpty()) {
            messages[Messages.NO_ONE_ONLINE].msg(sender)
            return
        }

        if (sender is Player && players.size == 1 && !settings[Settings.GIVE_REWARDS_TO_SENDER]) {
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
        messages[Messages.ITEMS_SENT]
            .replace("%amount%", amount.toString())
            .replace("%material%", material.name.lowercase())
            .msg(sender)
    }
}