package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.util.msg
import dev.triumphteam.cmd.bukkit.annotation.Permission
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.SubCommand
import org.bukkit.entity.Player

@Command("giveall", alias = ["gall"])
class CommandRemoveItem(private val plugin: GiveAll) : BaseCommand() {

    @SubCommand("remove-saved-item")
    @Permission("giveall.use.remove-saved-item")
    fun removeSavedItem(sender: Player, name: String) {
        if (!plugin.savedItemsManager.contains(name)) {
            plugin.messages.node("NON-EXISTENT-ITEM")
                .getString("&cThere is no item saved unther this name.")
                .replace("%wrong-value%", name)
                .msg(sender)
            return
        }

        plugin.savedItemsManager.removeItemStack(name)
        plugin.messages.node("ITEM-REMOVED")
            .getString("&aThe item with the name %name% was successfully removed.")
            .replace("%name%", name)
            .msg(sender)
    }
}