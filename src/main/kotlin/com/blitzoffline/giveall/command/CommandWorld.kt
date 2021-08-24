package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.config.holder.Messages
import com.blitzoffline.giveall.config.holder.Settings
import com.blitzoffline.giveall.util.msg
import me.clip.placeholderapi.PlaceholderAPI
import me.mattstudios.mf.annotations.Alias
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Completion
import me.mattstudios.mf.annotations.Optional
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Alias("gall")
@Command("giveall")
class CommandWorld(plugin: GiveAll) : CommandBase() {
    private val settings = plugin.settings
    private val messages = plugin.messages

    @SubCommand("world")
    @Permission("giveall.use.world")
    fun world(sender: CommandSender, @Completion("#worlds") worldName: String, @Completion("#materials") material: Material, @Optional amt: String?) {
        val world = Bukkit.getWorld(worldName) ?: run {
            messages[Messages.WORLD_NAME_WRONG].msg(sender)
            return
        }

        val players = world.players
        if (players.isEmpty()) {
            messages[Messages.NO_ONE_ONLINE].msg(sender)
            return
        }

        if (sender is Player && sender.world == world && players.size == 1 && !settings[Settings.GIVE_REWARDS_TO_SENDER]) {
            messages[Messages.ONLY_YOU_ONLINE].msg(sender)
            return
        }

        val amount = if (amt?.toIntOrNull() != null) amt.toInt() else material.maxStackSize
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
            .replace("%world%", world.name)
            .msg(sender)
    }
}