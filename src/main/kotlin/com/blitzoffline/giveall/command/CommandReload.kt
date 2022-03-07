package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.util.msg
import dev.triumphteam.cmd.bukkit.annotation.Permission
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.SubCommand
import org.bukkit.command.CommandSender

@Command("giveall", alias = ["gall"])
class CommandReload(private val plugin: GiveAll) : BaseCommand() {
    @SubCommand("reload")
    @Permission("giveall.admin")
    fun reload(sender: CommandSender) {
        plugin.loadSettings()
        plugin.messages.node("CONFIG-RELOADED").getString("&7Config reloaded successfully.").msg(sender)
    }
}