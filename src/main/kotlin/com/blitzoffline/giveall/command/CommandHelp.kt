package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.util.msg
import me.mattstudios.mf.annotations.Alias
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import org.bukkit.command.CommandSender

@Alias("gall")
@Command("giveall")
class CommandHelp : CommandBase() {
    @SubCommand("help")
    @Permission("giveall.help")
    fun help(sender: CommandSender) {
        "&7---- &6GiveAll by BlitzOffline &7----".msg(sender)
        "".msg(sender)
        "&7/giveall help &8-&f show this help menu".msg(sender)
        "&7/giveall reload &8-&f reload the configuration".msg(sender)
        "&7/giveall <material> [amount] &8-&f give items to all players".msg(sender)
        "&7/giveall world <world> <material> [amount] &8-&f give items to all players in a world".msg(sender)
        "&7/giveall radius <radius> <material> [amount] &8-&f give items to all players in a radius".msg(sender)
        "&7/giveall hand [world/radius] &8-&f give the items you hold in your hand to all players".msg(sender)
        "&7/giveall money <amount> [world/radius] &8-&f give money to all players".msg(sender)
    }
}