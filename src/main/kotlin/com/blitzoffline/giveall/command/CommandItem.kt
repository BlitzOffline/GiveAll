package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.extension.msg
import dev.triumphteam.cmd.bukkit.annotation.Permission
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Optional
import dev.triumphteam.cmd.core.annotation.Suggestion
import org.bukkit.Bukkit.getServer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Command("giveall", alias = ["gall"])
class CommandItem(private val plugin: GiveAll) : BaseCommand() {
    @Default
    @Permission("giveall.use")
    @Suggestion("materials")
    fun item(sender: CommandSender, @Optional args: List<String>) {
        if (args.isEmpty()) {
            if (sender.hasPermission("giveall.help")) {
                "&7---- &6GiveAll by BlitzOffline &7----".msg(sender)
                "".msg(sender)
                "&7/giveall help &8-&f show this help menu".msg(sender)
                "&7/giveall reload &8-&f reload the configuration".msg(sender)
                "&7/giveall <material> [amount] &8-&f give items to all players".msg(sender)
                "&7/giveall world <world> <material> [amount] &8-&f give items to all players in a world".msg(sender)
                "&7/giveall radius <radius> <material> [amount] &8-&f give items to all players in a radius".msg(sender)
                "&7/giveall console-radius <radius> <material> <x> <y> <z> <world-name> [amount] &8-&f give items to all players in a radius".msg(sender)
                "&7/giveall save-item <name> [force] &8-&f save an item to be able to give it later.".msg(sender)
                "&7/giveall remove-saved-item <name> &8-&f remove a saved item.".msg(sender)
                "&7/giveall hand [world/radius] &8-&f give the items you hold in your hand to all players".msg(sender)
                "&7/giveall money <amount> [world/radius] &8-&f give money to all players".msg(sender)
            } else {
                plugin.settingsManager.messages.wrongUsage.msg(sender)
            }
            return
        }

        val item = plugin.savedItemsManager.getSavedItemOrMaterial(
            args[0],
            if (args.size >= 2 && args[1].toIntOrNull() != null) args[1].toInt() else -1
        ) ?: return plugin.settingsManager.messages.wrongMaterial.msg(sender)

        val players = getServer().onlinePlayers
        if (players.isEmpty()) {
            plugin.settingsManager.messages.noPlayers.msg(sender)
            return
        }

        if (sender is Player && players.size == 1 && !plugin.settingsManager.settings.giveRewardsToSender) {
            plugin.settingsManager.messages.onlyYou.msg(sender)
            return
        }

        for (player in players) {
            if (!plugin.settingsManager.settings.giveRewardsToSender && player == sender) continue
            if (plugin.settingsManager.settings.requirePermission && !player.hasPermission("giveall.receive")) continue
            player.inventory.addItem(item.clone())
            plugin.settingsManager.messages.itemsReceived
                .replace("%amount%", item.amount.toString())
                .replace("%material%", args[0].lowercase())
                .msg(player)
            if (player.inventory.firstEmpty() == -1) plugin.settingsManager.messages.inventoryFull.msg(player)
        }
        plugin.settingsManager.messages.itemsSent
            .replace("%amount%", item.amount.toString())
            .replace("%material%", args[0].lowercase())
            .msg(sender)
    }
}