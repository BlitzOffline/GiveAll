package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.util.isValidName
import com.blitzoffline.giveall.util.msg
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
            plugin.messages.node("NAME-MATERIAL")
                .getString("&cSaved item name cannot be a material's name.")
                .replace("%wrong-value%", name)
                .msg(sender)
            return
        }

        if (!name.isValidName()) {
            plugin.messages.node("NAME-ALPHANUMERICAL")
                .getString("&cSaved item name can only contain letters, digits, underscores and hyphens.")
                .replace("%wrong-value%", name)
                .msg(sender)
            return
        }

        if (force != null && force.equals("force", true)) {
            handleItemSaving(sender, name)
            return
        }

        if (plugin.savedItemsManager.contains(name)) {
            plugin.messages.node("ITEM-EXISTS")
                .getString(
                    "&cAn item named %name% already exists. Use \"&e/giveall save-item %name% force&c\" to force its replacement."
                ).replace("%name%", name)
                .msg(sender)
            return
        }
        handleItemSaving(sender, name)
    }

    private fun handleItemSaving(player: Player, name: String) {
        val item = player.inventory.itemInMainHand.clone()
        if (item.type.isAir) {
            plugin.messages.node("ITEM-AIR").getString("&cItem cannot be air.").msg(player)
            return
        }

        plugin.savedItemsManager.addItemStack(name, item.clone(), true)
        plugin.messages.node("ITEM-SAVED")
            .getString("&aThe item was saved successfully with the name: %name%")
            .replace("%name%", name)
            .msg(player)
    }
}