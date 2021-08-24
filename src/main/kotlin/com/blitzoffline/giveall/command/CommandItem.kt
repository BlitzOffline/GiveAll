package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.config.holder.Messages
import com.blitzoffline.giveall.config.holder.Settings
import com.blitzoffline.giveall.util.msg
import me.mattstudios.mf.annotations.*
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
    fun item(sender: CommandSender, @Completion("#materials") material: Material, @Optional amt: String?) {
        val players = getServer().onlinePlayers
        if (players.isEmpty()) {
            messages[Messages.NO_ONE_ONLINE].msg(sender)
            return
        }

        if (sender is Player && players.size == 1 && !settings[Settings.GIVE_REWARDS_TO_SENDER]) {
            messages[Messages.ONLY_YOU_ONLINE].msg(sender)
            return
        }

        if (sender is Player && !settings[Settings.GIVE_REWARDS_TO_SENDER]) {
            players.remove(sender)
        }

        if (amt != null && amt.toIntOrNull() == null) {
            messages[Messages.WRONG_USAGE].msg(sender)
            return
        }

        val amount = if (amt?.toIntOrNull() != null) amt.toInt() else material.maxStackSize
        val item = ItemStack(material, amount)

        for (player in players) {
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