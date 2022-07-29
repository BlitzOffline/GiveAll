package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.extension.msg
import dev.triumphteam.cmd.bukkit.annotation.Permission
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.SubCommand
import org.bukkit.command.CommandSender

@Command("giveall", alias = ["gall"])
class CommandHelp(private val plugin: GiveAll) : BaseCommand() {
    @SubCommand("help")
    @Permission("giveall.help")
    fun help(sender: CommandSender) {
        plugin.settingsManager.messages.help.joinToString { System.lineSeparator() }.msg(sender)
    }
}