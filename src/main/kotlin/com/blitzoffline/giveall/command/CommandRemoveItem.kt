package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.extension.msg
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
            plugin.settingsManager.messages.invalidItem
                .replace("%wrong-value%", name)
                .msg(sender)
            return
        }

        plugin.savedItemsManager.removeItemStack(name)
        plugin.settingsManager.messages.itemRemoved
            .replace("%name%", name)
            .msg(sender)
    }
}