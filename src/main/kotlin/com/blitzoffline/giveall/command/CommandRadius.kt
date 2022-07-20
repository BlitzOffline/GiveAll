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
import org.bukkit.entity.Player

@Command("giveall", alias = ["gall"])
class CommandRadius(private val plugin: GiveAll) : BaseCommand() {
    @SubCommand("radius")
    @Permission("giveall.use.radius")
    fun radius(sender: Player, radius: Double, @Suggestion("materials") material: String, @Optional amt: Int?) {
        val item = plugin.savedItemsManager.getSavedItemOrMaterial(material, amt ?: -1) ?:
        return plugin.settingsManager.messages.wrongMaterial.msg(sender)

        val boundingBox = calculateBoundingBox(sender.location, radius)
        val players= sender.world.getNearbyEntities(boundingBox)
            .filterIsInstance(Player::class.java)
            .toList()

        if (players.isEmpty()) {
            plugin.settingsManager.messages.noPlayers.msg(sender)
            return
        }

        if (players.size == 1 && !plugin.settingsManager.settings.giveRewardsToSender) {
            plugin.settingsManager.messages.onlyYou.msg(sender)
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

        plugin.settingsManager.messages.itemsSentRadius
            .replace("%amount%", item.amount.toString())
            .replace("%material%", material.lowercase())
            .replace("%radius%", radius.toInt().toString())
            .msg(sender)
    }
}