package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.config.holder.Messages
import com.blitzoffline.giveall.util.msg
import me.mattstudios.mf.annotations.Alias
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import org.bukkit.command.CommandSender

@Alias("gall")
@Command("giveall")
class CommandReload(private val plugin: GiveAll) : CommandBase() {
    @SubCommand("reload")
    @Permission("giveall.admin")
    fun reload(sender: CommandSender) {
        plugin.settings.reload()
        plugin.messages.reload()
        plugin.messages[Messages.CONFIG_RELOADED].msg(sender)
    }
}