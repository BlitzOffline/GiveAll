package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.extension.isValidName
import com.blitzoffline.giveall.extension.msg
import dev.triumphteam.cmd.bukkit.annotation.Permission
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Optional
import dev.triumphteam.cmd.core.annotation.SubCommand
import org.bukkit.Material
import org.bukkit.entity.Player

@Command("giveall", alias = ["gall"])
class CommandSaveItem(private val plugin: GiveAll) : BaseCommand() {

    @SubCommand("save-item")
    @Permission("giveall.use.save-item")
    fun saveItem(sender: Player, name: String, @Optional force: String?) {
        if (Material.matchMaterial(name) != null) {
            plugin.settingsManager.messages.nameMaterial
                .replace("%wrong-value%", name)
                .msg(sender)
            return
        }

        if (!name.isValidName()) {
            plugin.settingsManager.messages.nameAlphanumerical
                .replace("%wrong-value%", name)
                .msg(sender)
            return
        }

        if (force != null && force.equals("force", true)) {
            handleItemSaving(sender, name)
            return
        }

        if (plugin.savedItemsManager.contains(name)) {
            plugin.settingsManager.messages.itemExists
                .replace("%name%", name)
                .msg(sender)
            return
        }
        handleItemSaving(sender, name)
    }

    private fun handleItemSaving(player: Player, name: String) {
        val item = player.inventory.itemInMainHand.clone()
        if (item.type.isAir) {
            plugin.settingsManager.messages.itemAir.msg(player)
            return
        }

        plugin.savedItemsManager.addItemStack(name, item.clone(), true)
        plugin.settingsManager.messages.itemSaved
            .replace("%name%", name)
            .msg(player)
    }
}