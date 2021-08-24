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
        "".msg(sender)
        "&7---- &6GiveAll by BlitzOffline &7----".msg(sender)
        "".msg(sender)
        "&7/giveAll <material> [amount] &8-&f give items to all players".msg(sender)
        "&7/giveAll world <world> <material> [amount] &8-&f give items to all players from a world".msg(sender)
        "&7/giveAll radius <radius> <material> [amount] &8-&f give items to all players in a radius".msg(sender)
        "&7/giveAll money <amount> [world/radius] &8-&f give money to all players".msg(sender)
        "&7/giveAll hand [world/radius] &8-&f give the items you hold in your hand to all players".msg(sender)
        "&7/giveAll reload &8-&f reload the plugin".msg(sender)
    }
}