package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.extension.msg
import dev.triumphteam.cmd.bukkit.annotation.Permission
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.SubCommand
import org.bukkit.command.CommandSender

@Command("giveall", alias = ["gall"])
class CommandHelp : BaseCommand() {
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
        "&7/giveall console-radius <radius> <material> <x> <y> <z> <world-name> [amount] &8-&f give items to all players in a radius".msg(sender)
        "&7/giveall save-item <name> [force] &8-&f save an item to be able to give it later.".msg(sender)
        "&7/giveall remove-saved-item <name> &8-&f remove a saved item.".msg(sender)
        "&7/giveall hand [world/radius] &8-&f give the items you hold in your hand to all players".msg(sender)
        "&7/giveall money <amount> [world/radius] &8-&f give money to all players".msg(sender)
    }
}