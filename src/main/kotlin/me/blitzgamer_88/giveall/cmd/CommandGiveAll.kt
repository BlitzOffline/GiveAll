package me.blitzgamer_88.giveall.cmd

import me.blitzgamer_88.giveall.GiveAll
import me.blitzgamer_88.giveall.conf.Config
import me.blitzgamer_88.giveall.util.color
import me.clip.placeholderapi.PlaceholderAPI
import me.mattstudios.mf.annotations.*
import me.mattstudios.mf.base.CommandBase
import org.bukkit.Bukkit.getServer
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Command("giveall")
@Alias("gall")
class CommandGiveAll(private val mainClass: GiveAll) : CommandBase() {

    @Default
    fun giveAll(sender: CommandSender, material: Material?, @Optional amt: Int?) {
        val giveItemsToSender = mainClass.conf().getProperty(Config.giveItemsToSender)

        val giveAllPermission = mainClass.conf().getProperty(Config.giveAllPermission)

        val itemsReceived = mainClass.conf().getProperty(Config.itemsReceived).color()
        val itemsSent = mainClass.conf().getProperty(Config.itemsSent).color()
        val wrongUsage = mainClass.conf().getProperty(Config.wrongUsage).color()
        val noOneOnline = mainClass.conf().getProperty(Config.noOneOnline).color()
        val noPermission = mainClass.conf().getProperty(Config.noPermission).color()
        val inventoryFull = mainClass.conf().getProperty(Config.fullInventory).color()
        val onlyYouOnline = mainClass.conf().getProperty(Config.onlyYouOnline).color()

        if (sender is Player && sender.hasPermission(giveAllPermission)) {
            sender.sendMessage(noPermission)
            return
        }

        val players = getServer().onlinePlayers

        if (players.isEmpty()) {
            sender.sendMessage(noOneOnline)
            return
        }

        if (material == null) {
            sender.sendMessage(wrongUsage)
            return
        }

        if (sender is Player && players.size == 1 && giveItemsToSender) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, onlyYouOnline))
            return
        }

        val amount = amt.toString().toIntOrNull()
        val materialName = material.name.toLowerCase()
        val amountIfNull = material.maxStackSize
        var item = ItemStack(material, amountIfNull)
        if (amount != null) {
            val iReceived = itemsReceived.replace("%amount%", amount.toString()).replace("%material%", materialName)
            val iSent = itemsSent.replace("%amount%", amount.toString()).replace("%material%", materialName)
            item = ItemStack(material, amount)
            for (p in players) {
                if (p.inventory.firstEmpty() != -1) {
                    p.inventory.addItem(item)
                    p.sendMessage(PlaceholderAPI.setPlaceholders(p, iReceived))
                }else {
                    p.sendMessage(PlaceholderAPI.setPlaceholders(p, inventoryFull))
                }
            }
            sender.sendMessage(iSent)
            return
        }
        val iReceived = itemsReceived.replace("%amount%", amountIfNull.toString()).replace("%material%", materialName)
        val iSent = itemsSent.replace("%amount%", amountIfNull.toString()).replace("%material%", materialName)
        for (p in players) {
            if (p.inventory.firstEmpty() != -1) {
                if (giveItemsToSender && p == sender) {
                    p.inventory.addItem(item)
                    p.sendMessage(PlaceholderAPI.setPlaceholders(p, iReceived))
                }else if (p != sender) {
                    p.inventory.addItem(item)
                    p.sendMessage(PlaceholderAPI.setPlaceholders(p, iReceived))
                }
            }else {
                p.sendMessage(PlaceholderAPI.setPlaceholders(p, inventoryFull))
            }
        }
        sender.sendMessage(iSent)
        return
    }

    @SubCommand("hand")
    fun giveAllHand(sender: Player) {
        val giveItemsToSender = mainClass.conf().getProperty(Config.giveItemsToSender)

        val giveAllHandPermission = mainClass.conf().getProperty(Config.giveAllHandPermission)

        val itemsReceived = mainClass.conf().getProperty(Config.itemsReceived).color()
        val itemsSent = mainClass.conf().getProperty(Config.itemsSent).color()
        val noPermission = mainClass.conf().getProperty(Config.noPermission).color()
        val inventoryFull = mainClass.conf().getProperty(Config.fullInventory).color()
        val itemCannotBeAir = mainClass.conf().getProperty(Config.itemCannotBeAir).color()
        val onlyYouOnline = mainClass.conf().getProperty(Config.onlyYouOnline).color()

        if (!sender.hasPermission(giveAllHandPermission)) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noPermission))
            return
        }

        val players = getServer().onlinePlayers

        if (players.size == 1 && giveItemsToSender) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, onlyYouOnline))
            return
        }

        val item = sender.inventory.itemInMainHand
        if (item.type == Material.AIR) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, itemCannotBeAir))
            return
        }

        val materialName = item.type.name.toLowerCase()
        val amount = item.amount
        val iReceived = itemsReceived.replace("%amount%", amount.toString()).replace("%material%", materialName)
        val iSent = itemsSent.replace("%amount%", amount.toString()).replace("%material%", materialName)
        for (p in players) {
            if (p.inventory.firstEmpty() != -1) {
                if (giveItemsToSender && p == sender) {
                    p.inventory.addItem(item)
                    p.sendMessage(PlaceholderAPI.setPlaceholders(p, iReceived))
                }else if (p != sender) {
                    p.inventory.addItem(item)
                    p.sendMessage(PlaceholderAPI.setPlaceholders(p, iReceived))
                }
            }else {
                p.sendMessage(PlaceholderAPI.setPlaceholders(p, inventoryFull))
            }
        }
        sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, iSent))
    }

    @SubCommand("reload")
    fun giveAllReload(sender: CommandSender) {
        val configReloaded = mainClass.conf().getProperty(Config.configReloaded).color()
        val noPermission = mainClass.conf().getProperty(Config.noPermission).color()

        val giveAllReloadPermission = mainClass.conf().getProperty(Config.giveAllReloadPermission)

        if (sender is Player && !sender.hasPermission(giveAllReloadPermission)) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noPermission))
            return
        }

        mainClass.conf().reload()
        sender.sendMessage(configReloaded)
    }
}