package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.util.msg
import dev.triumphteam.cmd.bukkit.annotation.Permission
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.annotation.Command
import dev.triumphteam.cmd.core.annotation.Optional
import dev.triumphteam.cmd.core.annotation.SubCommand
import dev.triumphteam.cmd.core.annotation.Suggestion
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Command("giveall", alias = ["gall"])
class CommandWorld(private val plugin: GiveAll) : BaseCommand() {
    @SubCommand("world")
    @Permission("giveall.use.world")
    fun world(sender: CommandSender, @Suggestion("worlds") world: World, @Suggestion("materials") material: Material, @Optional amt: Int?) {
        val settings = plugin.settings
        val messages = plugin.messages

        val players = world.players
        if (players.isEmpty()) {
            messages.node("NO-ONE-ONLINE").getString("&7Could not find any players online.").msg(sender)
            return
        }

        if (sender is Player && sender.world == world && players.size == 1 && !settings.node("give-rewards-to-sender").getBoolean(false)) {
            messages.node("ONLY-YOU-ONLINE").getString("&7You are the only player we could find.").msg(sender)
            return
        }

        val amount = amt ?: material.maxStackSize
        val item = ItemStack(material, amount)

        for (player in players) {
            if (!settings.node("give-rewards-to-sender").getBoolean(false) && player == sender) continue
            if (settings.node("requires-permission").getBoolean(false) && !player.hasPermission("giveall.receive")) continue
            player.inventory.addItem(item)
            messages.node("ITEMS-RECEIVED").getString("&3You have received &a%amount% &3x&a %material%&3.")
                .replace("%amount%", amount.toString())
                .replace("%material%", material.name.lowercase())
                .msg(player)
            if (player.inventory.firstEmpty() == -1) messages.node("INVENTORY-FULL").getString("&cYour inventory is full!!").msg(player)
        }

        messages.node("ITEMS-SENT-WORLD").getString("&aYou have given everyone in &3%world%&a: &3%amount% &ax&3 %material%&a.\"")
            .replace("%amount%", amount.toString())
            .replace("%material%", material.name.lowercase())
            .replace("%world%", world.name)
            .msg(sender)
    }
}