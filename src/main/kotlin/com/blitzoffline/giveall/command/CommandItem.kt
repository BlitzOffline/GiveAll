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
                plugin.settingsManager.messages.help.joinToString { System.lineSeparator() }.msg(sender)
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