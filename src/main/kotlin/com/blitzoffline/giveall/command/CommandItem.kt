package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.util.msg
import dev.triumphteam.cmd.bukkit.annotation.Permission
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Default
import dev.triumphteam.cmd.core.annotation.Optional
import dev.triumphteam.cmd.core.annotation.Suggestion
import org.bukkit.Bukkit.getServer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Command("giveall", alias = ["gall"])
class CommandItem(private val plugin: GiveAll) : BaseCommand() {
    @Default
    @Permission("giveall.use")
    @Suggestion("materials")
    fun item(sender: CommandSender, @Optional args: List<String>) {
        val settings = plugin.settings
        val messages = plugin.messages

        if (args.isEmpty()) {
            if (sender.hasPermission("giveall.help")) {
                "&7---- &6GiveAll by BlitzOffline &7----".msg(sender)
                "".msg(sender)
                "&7/giveall help &8-&f show this help menu".msg(sender)
                "&7/giveall reload &8-&f reload the configuration".msg(sender)
                "&7/giveall <material> [amount] &8-&f give items to all players".msg(sender)
                "&7/giveall world <world> <material> [amount] &8-&f give items to all players in a world".msg(sender)
                "&7/giveall radius <radius> <material> [amount] &8-&f give items to all players in a radius".msg(sender)
                "&7/giveall console-radius <radius> <material> <x> <y> <z> <world-name> [amount] &8-&f give items to all players in a radius".msg(sender)
                "&7/giveall hand [world/radius] &8-&f give the items you hold in your hand to all players".msg(sender)
                "&7/giveall money <amount> [world/radius] &8-&f give money to all players".msg(sender)
            } else {
                messages.node("WRONG-USAGE").getString("&cWrong usage! Use: &e/giveall help&c to get help").msg(sender)
            }
            return
        }

        val item = plugin.savedItemsManager.getSavedItemOrMaterial(
            args[0],
            if (args.size >= 2 && args[1].toIntOrNull() != null) args[1].toInt() else -1
        ) ?: return messages.node("WRONG-MATERIAL")
            .getString("&cCould not find the material you specified.")
            .msg(sender)

        val players = getServer().onlinePlayers
        if (players.isEmpty()) {
            messages.node("NO-ONE-ONLINE").getString("&7Could not find any players online.").msg(sender)
            return
        }

        if (sender is Player && players.size == 1 && !settings.node("give-rewards-to-sender").getBoolean(false)) {
            messages.node("ONLY-YOU-ONLINE").getString("&7You are the only player we could find.").msg(sender)
            return
        }

        for (player in players) {
            if (!settings.node("give-rewards-to-sender").getBoolean(false) && player == sender) continue
            if (settings.node("requires-permission").getBoolean(false) && !player.hasPermission("giveall.receive")) continue
            player.inventory.addItem(item.clone())
            messages.node("ITEMS-RECEIVED").getString("&3You have received &a%amount% &3x&a %material%&3.")
                .replace("%amount%", item.amount.toString())
                .replace("%material%", args[0].lowercase())
                .msg(player)
            if (player.inventory.firstEmpty() == -1) messages.node("INVENTORY-FULL").getString("&cYour inventory is full!!").msg(player)
        }
        messages.node("ITEMS-SENT").getString("&aYou have given everyone: &3%amount% &ax&3 %material%&a.")
            .replace("%amount%", item.amount.toString())
            .replace("%material%", args[0].lowercase())
            .msg(sender)
    }
}