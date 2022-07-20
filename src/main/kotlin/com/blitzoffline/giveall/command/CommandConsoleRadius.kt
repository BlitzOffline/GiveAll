package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.util.calculateBoundingBox
import com.blitzoffline.giveall.extension.msg
import dev.triumphteam.cmd.bukkit.annotation.Permission
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Optional
import dev.triumphteam.cmd.core.annotation.SubCommand
import dev.triumphteam.cmd.core.annotation.Suggestion
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Command("giveall", alias = ["gall"])
class CommandConsoleRadius(private val plugin: GiveAll) : BaseCommand() {
    @SubCommand("console-radius")
    @Permission("giveall.use.console-radius")
    fun consoleRadius(
        sender: CommandSender,
        radius: Double,
        @Suggestion("materials") material: String,
        x: Double, y: Double, z: Double,
        @Suggestion("worlds") world: World,
        @Optional amt: Int?
    ) {
        val item = plugin.savedItemsManager.getSavedItemOrMaterial(material, amt ?: -1)
            ?: return plugin.settingsManager.messages.wrongRadius.msg(sender)

        val boundingBox = calculateBoundingBox(Location(world, x, y, z), radius)
        val players= world.getNearbyEntities(boundingBox)
            .filterIsInstance(Player::class.java)
            .toList()

        if (players.isEmpty()) {
            plugin.settingsManager.messages.noPlayers.msg(sender)
            return
        }

        for (player in players) {
            if (!plugin.settingsManager.settings.giveRewardsToSender && player == sender) continue
            if (plugin.settingsManager.settings.requirePermission && !player.hasPermission("giveall.receive")) continue
            player.inventory.addItem(item.clone())
            plugin.settingsManager.messages.itemsReceived
                .replace("%amount%", item.amount.toString())
                .replace("%material%", material.lowercase())
                .msg(player)
            if (player.inventory.firstEmpty() == -1) plugin.settingsManager.messages.inventoryFull.msg(player)
        }

        plugin.settingsManager.messages.itemsSentConsoleRadius
            .replace("%amount%", item.amount.toString())
            .replace("%material%", material.lowercase())
            .replace("%radius%", radius.toInt().toString())
            .replace("%x%", x.toString())
            .replace("%y%", y.toString())
            .replace("%z%", z.toString())
            .replace("%world%", world.name)
            .msg(sender)
    }
}