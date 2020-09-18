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
import org.bukkit.util.BoundingBox


@Command("giveall")
@Alias("gall")
class CommandGiveAll(private val mainClass: GiveAll) : CommandBase() {

    @Default
    fun giveAll(sender: CommandSender, @Completion("#materials") material: Material, @Optional amt: Int?) {
        val giveItemsToSender = mainClass.conf().getProperty(Config.giveItemsToSender)
        val requirePermissionToGetItems = mainClass.conf().getProperty(Config.requirePermissionToGetItems)

        val giveAllPermission = mainClass.conf().getProperty(Config.giveAllPermission)
        val giveAllReceivePermission = mainClass.conf().getProperty(Config.giveAllReceivePermission)
        val giveAllMainPermission = mainClass.conf().getProperty(Config.giveAllMainPermission)

        val itemsReceived = mainClass.conf().getProperty(Config.itemsReceived).color()
        val itemsSent = mainClass.conf().getProperty(Config.itemsSent).color()
        val noOneOnline = mainClass.conf().getProperty(Config.noOneOnline).color()
        val noPermission = mainClass.conf().getProperty(Config.noPermission).color()
        val inventoryFull = mainClass.conf().getProperty(Config.fullInventory).color()
        val onlyYouOnline = mainClass.conf().getProperty(Config.onlyYouOnline).color()

        if (sender is Player && !sender.hasPermission(giveAllPermission) && !sender.hasPermission(giveAllMainPermission)) {
            sender.sendMessage(noPermission)
            return
        }

        val players = getServer().onlinePlayers
        if (players.isEmpty()) {
            sender.sendMessage(noOneOnline)
            return
        }

        if (sender is Player && players.size == 1 && !giveItemsToSender) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, onlyYouOnline))
            return
        }

        if (sender is Player && !giveItemsToSender) {
            players.remove(sender)
        }

        val amount = if (amt.toString().toIntOrNull() != null) {
            amt.toString().toIntOrNull()
        } else {
            material.maxStackSize
        }

        val materialName = material.name.toLowerCase()
        val item = amount?.let { ItemStack(material, it) }
        val iReceived = itemsReceived.replace("%amount%", amount.toString()).replace("%material%", materialName)
        val iSent = itemsSent.replace("%amount%", amount.toString()).replace("%material%", materialName)
        for (p in players) {
            if (requirePermissionToGetItems && !p.hasPermission(giveAllReceivePermission)) {
                continue
            }
            if (p.inventory.firstEmpty() != -1) {
                p.inventory.addItem(item)
                p.sendMessage(PlaceholderAPI.setPlaceholders(p, iReceived))
            } else {
                p.inventory.addItem(item)
                p.sendMessage(PlaceholderAPI.setPlaceholders(p, inventoryFull))
            }
        }
        sender.sendMessage(iSent)
        return
    }


    @SubCommand("world")
    fun giveAllWorld(sender: CommandSender, @Completion("#worlds") worldName: String, @Completion("#materials") material: Material, @Optional amt: Int?) {
        val giveItemsToSender = mainClass.conf().getProperty(Config.giveItemsToSender)
        val requirePermissionToGetItems = mainClass.conf().getProperty(Config.requirePermissionToGetItems)

        val giveAllWorldPermission = mainClass.conf().getProperty(Config.giveAllWorldPermission)
        val giveAllReceivePermission = mainClass.conf().getProperty(Config.giveAllReceivePermission)
        val giveAllMainPermission = mainClass.conf().getProperty(Config.giveAllMainPermission)

        val itemsReceived = mainClass.conf().getProperty(Config.itemsReceived).color()
        val itemsSent = mainClass.conf().getProperty(Config.itemsSentWorld).color()
        val wrongWorldName = mainClass.conf().getProperty(Config.wrongWorldName).color()
        val noOneOnline = mainClass.conf().getProperty(Config.noOneOnline).color()
        val noPermission = mainClass.conf().getProperty(Config.noPermission).color()
        val inventoryFull = mainClass.conf().getProperty(Config.fullInventory).color()
        val onlyYouOnline = mainClass.conf().getProperty(Config.onlyYouOnline).color()

        if (sender is Player && !sender.hasPermission(giveAllWorldPermission) && !sender.hasPermission(giveAllMainPermission)) {
            sender.sendMessage(noPermission)
            return
        }

        val world = getServer().getWorld(worldName)
        if (world == null) {
            sender.sendMessage(wrongWorldName)
            return
        }

        val players = world.players
        if (players.isEmpty()) {
            sender.sendMessage(noOneOnline)
            return
        }

        if (sender is Player && sender.world == world && players.size == 1 && !giveItemsToSender) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, onlyYouOnline))
            return
        }

        if (sender is Player && sender.world == world && !giveItemsToSender) {
            players.remove(sender)
        }

        val amount = if (amt.toString().toIntOrNull() != null) {
            amt.toString().toIntOrNull()
        } else {
            material.maxStackSize
        }

        val materialName = material.name.toLowerCase()
        val item = amount?.let { ItemStack(material, it) }
        val iReceived = itemsReceived.replace("%amount%", amount.toString()).replace("%material%", materialName)
        val iSent = itemsSent.replace("%amount%", amount.toString()).replace("%material%", materialName).replace("%world%", worldName)
        for (p in players) {
            if (requirePermissionToGetItems && !p.hasPermission(giveAllReceivePermission)) {
                continue
            }
            if (p.inventory.firstEmpty() != -1) {
                p.inventory.addItem(item)
                p.sendMessage(PlaceholderAPI.setPlaceholders(p, iReceived))
            } else {
                p.inventory.addItem(item)
                p.sendMessage(PlaceholderAPI.setPlaceholders(p, inventoryFull))
            }
        }
        sender.sendMessage(iSent)
        return
    }


    @SubCommand("radius")
    fun giveAllRadius(sender: Player, r: String, @Completion("#materials") material: Material, @Optional amt: Int?) {
        val giveItemsToSender = mainClass.conf().getProperty(Config.giveItemsToSender)
        val requirePermissionToGetItems = mainClass.conf().getProperty(Config.requirePermissionToGetItems)

        val giveAllRadiusPermission = mainClass.conf().getProperty(Config.giveAllRadiusPermission)
        val giveAllReceivePermission = mainClass.conf().getProperty(Config.giveAllReceivePermission)
        val giveAllMainPermission = mainClass.conf().getProperty(Config.giveAllMainPermission)

        val itemsReceived = mainClass.conf().getProperty(Config.itemsReceived).color()
        val itemsSent = mainClass.conf().getProperty(Config.itemsSentRadius).color()
        val wrongUsage = mainClass.conf().getProperty(Config.wrongUsage).color()
        val noOneOnline = mainClass.conf().getProperty(Config.noOneOnline).color()
        val noPermission = mainClass.conf().getProperty(Config.noPermission).color()
        val inventoryFull = mainClass.conf().getProperty(Config.fullInventory).color()
        val onlyYouOnline = mainClass.conf().getProperty(Config.onlyYouOnline).color()

        if (!sender.hasPermission(giveAllRadiusPermission) && !sender.hasPermission(giveAllMainPermission)) {
            sender.sendMessage(noPermission)
            return
        }

        val radius = r.toDoubleOrNull()
        if (radius == null) {
            sender.sendMessage(wrongUsage)
            return
        }

        val location = sender.location
        val hypotenuse = kotlin.math.sqrt(2 * radius * radius)
        val boundingBox = BoundingBox(location.x-hypotenuse, location.y-radius, location.z-hypotenuse, location.x+hypotenuse, location.y+radius, location.z+hypotenuse)
        val players= sender.world.getNearbyEntities(boundingBox)
                .filterIsInstance(Player::class.java).toMutableList()

        if (players.isEmpty()) {
            sender.sendMessage(noOneOnline)
            return
        }

        if (players.size == 1 && !giveItemsToSender) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, onlyYouOnline))
            return
        }

        if (!giveItemsToSender) {
            players.remove(sender)
        }

        val amount = if (amt.toString().toIntOrNull() != null) {
            amt.toString().toIntOrNull()
        } else {
            material.maxStackSize
        }

        val materialName = material.name.toLowerCase()
        val item = amount?.let { ItemStack(material, it) }
        val iReceived = itemsReceived.replace("%amount%", amount.toString()).replace("%material%", materialName)
        val iSent = itemsSent.replace("%amount%", amount.toString()).replace("%material%", materialName).replace("%radius%", radius.toString())
        for (p in players) {
            if (requirePermissionToGetItems && !p.hasPermission(giveAllReceivePermission)) {
                continue
            }
            if (p.inventory.firstEmpty() != -1) {
                p.inventory.addItem(item)
                p.sendMessage(PlaceholderAPI.setPlaceholders(p, iReceived))
            } else {
                p.inventory.addItem(item)
                p.sendMessage(PlaceholderAPI.setPlaceholders(p, inventoryFull))
            }
        }
        sender.sendMessage(iSent)
        return
    }


    @SubCommand("hand")
    fun giveAllHand(sender: Player, @Completion("#worlds") @Optional argument: String?) {
        val giveItemsToSender = mainClass.conf().getProperty(Config.giveItemsToSender)
        val requirePermissionToGetItems = mainClass.conf().getProperty(Config.requirePermissionToGetItems)

        val giveAllHandPermission = mainClass.conf().getProperty(Config.giveAllHandPermission)
        val giveAllReceivePermission = mainClass.conf().getProperty(Config.giveAllReceivePermission)
        val giveAllMainPermission = mainClass.conf().getProperty(Config.giveAllMainPermission)

        val itemsReceived = mainClass.conf().getProperty(Config.itemsReceived).color()
        val itemsSent = mainClass.conf().getProperty(Config.itemsSent).color()
        val itemsSentWorld = mainClass.conf().getProperty(Config.itemsSentWorld).color()
        val itemsSentRadius = mainClass.conf().getProperty(Config.itemsSentRadius).color()
        val noPermission = mainClass.conf().getProperty(Config.noPermission).color()
        val inventoryFull = mainClass.conf().getProperty(Config.fullInventory).color()
        val itemCannotBeAir = mainClass.conf().getProperty(Config.itemCannotBeAir).color()
        val noOneOnline = mainClass.conf().getProperty(Config.noOneOnline).color()
        val onlyYouOnline = mainClass.conf().getProperty(Config.onlyYouOnline).color()
        val wrongWorldName = mainClass.conf().getProperty(Config.wrongWorldName).color()
        val wrongUsage = mainClass.conf().getProperty(Config.wrongUsage).color()

        if (!sender.hasPermission(giveAllHandPermission) && !sender.hasPermission(giveAllMainPermission)) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noPermission))
            return
        }

        if (argument != null && argument.contains(" ")) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, wrongUsage))
            return
        }

        var checkWorld = false
        var checkRadius = false

        val players: MutableCollection<out Player>?
        when {
            argument == null -> {
                players = getServer().onlinePlayers
            }
            argument.toDoubleOrNull() == null -> {
                val world = getServer().getWorld(argument)
                if (world == null) {
                    sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, wrongWorldName))
                    return
                }
                players = world.players
                checkWorld = true
            }
            else -> {
                val location = sender.location
                val radius = argument.toDouble()
                val hypotenuse = kotlin.math.sqrt(2 * radius * radius)
                val boundingBox = BoundingBox(location.x - hypotenuse, location.y - radius, location.z - hypotenuse, location.x + hypotenuse, location.y + radius, location.z + hypotenuse)
                players = sender.world.getNearbyEntities(boundingBox)
                        .filterIsInstance(Player::class.java).toMutableList()
                checkRadius = true
            }
        }

        if (players.size == 1 && !giveItemsToSender) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, onlyYouOnline))
            return
        }
        if (players.isEmpty()) {
            sender.sendMessage(noOneOnline)
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
        val iSent = when {
            checkWorld -> {
                itemsSentWorld.replace("%amount%", amount.toString()).replace("%material%", materialName).replace("%world%", argument.toString())
            }
            checkRadius -> {
                itemsSentRadius.replace("%amount%", amount.toString()).replace("%material%", materialName).replace("%radius%", argument.toString())
            }
            else -> {
                itemsSent.replace("%amount%", amount.toString()).replace("%material%", materialName)
            }
        }

        for (p in players) {
            if (requirePermissionToGetItems && !p.hasPermission(giveAllReceivePermission)) {
                continue
            }
            if (p.inventory.firstEmpty() != -1) {
                if (p == sender && giveItemsToSender) {
                    p.inventory.addItem(item)
                    p.sendMessage(PlaceholderAPI.setPlaceholders(p, iReceived))
                } else if (p != sender) {
                    p.inventory.addItem(item)
                    p.sendMessage(PlaceholderAPI.setPlaceholders(p, iReceived))
                }
            } else {
                p.sendMessage(PlaceholderAPI.setPlaceholders(p, inventoryFull))
            }
        }
        sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, iSent))
        return
    }


    @SubCommand("money")
    fun giveAllMoney(sender: CommandSender, amt: Int?, @Completion("#worlds") @Optional argument: String?) {
        val giveItemsToSender = mainClass.conf().getProperty(Config.giveItemsToSender)
        val requirePermissionToGetItems = mainClass.conf().getProperty(Config.requirePermissionToGetItems)

        val giveAllMoneyPermission = mainClass.conf().getProperty(Config.giveAllMoneyPermission)
        val giveAllReceivePermission = mainClass.conf().getProperty(Config.giveAllReceivePermission)
        val giveAllMainPermission = mainClass.conf().getProperty(Config.giveAllMainPermission)


        val moneyReceived = mainClass.conf().getProperty(Config.moneyReceived).color()
        val moneySent = mainClass.conf().getProperty(Config.moneySent).color()
        val moneySentWorld = mainClass.conf().getProperty(Config.moneySentWorld).color()
        val moneySentRadius = mainClass.conf().getProperty(Config.moneySentRadius).color()
        val noPermission = mainClass.conf().getProperty(Config.noPermission).color()
        val amountCannotBeZero = mainClass.conf().getProperty(Config.amountCannotBeZero).color()
        val onlyPlayersCanUseRadius = mainClass.conf().getProperty(Config.onlyPlayersCanUseRadius).color()
        val onlyYouOnline = mainClass.conf().getProperty(Config.onlyYouOnline).color()
        val noOneOnline = mainClass.conf().getProperty(Config.noOneOnlineMoney).color()
        val wrongWorldName = mainClass.conf().getProperty(Config.wrongWorldName).color()
        val wrongUsage = mainClass.conf().getProperty(Config.wrongUsage).color()

        if (sender is Player && !sender.hasPermission(giveAllMoneyPermission) && !sender.hasPermission(giveAllMainPermission)) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, noPermission))
            return
        }

        if (argument != null && argument.contains(" ")) {
            sender.sendMessage(wrongUsage)
            return
        }

        if (argument != null && argument.toDoubleOrNull() != null && sender !is Player) {
            sender.sendMessage(onlyPlayersCanUseRadius)
            return
        }

        var checkWorld = false
        var checkRadius = false

        val players: MutableCollection<out Player>?
        when {
            argument == null -> {
                players = getServer().onlinePlayers
            }
            argument.toDoubleOrNull() == null -> {
                val world = getServer().getWorld(argument)
                if (world == null) {
                    sender.sendMessage(wrongWorldName)
                    return
                }
                players = world.players
                checkWorld = true
            }
            else -> {
                if (sender is Player) {
                    val location = sender.location
                    val radius = argument.toDouble()
                    val hypotenuse = kotlin.math.sqrt(2 * radius * radius)
                    val boundingBox = BoundingBox(location.x - hypotenuse, location.y - radius, location.z - hypotenuse, location.x + hypotenuse, location.y + radius, location.z + hypotenuse)
                    players = sender.world.getNearbyEntities(boundingBox)
                            .filterIsInstance(Player::class.java).toMutableList()
                    checkRadius = true
                } else {
                    sender.sendMessage(onlyPlayersCanUseRadius)
                    return
                }
            }
        }

        if (sender is Player && players.size == 1 && !giveItemsToSender) {
            sender.sendMessage(PlaceholderAPI.setPlaceholders(sender, onlyYouOnline))
            return
        }

        if (players.isEmpty()) {
            sender.sendMessage(noOneOnline)
            return
        }

        val amount = amt.toString().toIntOrNull()
        if (amount == null) {
            sender.sendMessage(amountCannotBeZero)
            return
        }

        val mReceived = moneyReceived.replace("%amount%", amount.toString())
        val mSent = when {
            checkWorld -> {
                moneySentWorld.replace("%amount%", amount.toString()).replace("%world%", argument.toString())
            }
            checkRadius -> {
                moneySentRadius.replace("%amount%", amount.toString()).replace("%radius%", argument.toString())
            }
            else -> {
                moneySent.replace("%amount%", amount.toString())
            }
        }

        for (p in players) {
            if (requirePermissionToGetItems && !p.hasPermission(giveAllReceivePermission)) {
                continue
            }
            if (p == sender && giveItemsToSender) {
                mainClass.econ?.depositPlayer(p, amount.toDouble())
                p.sendMessage(PlaceholderAPI.setPlaceholders(p, mReceived))
            } else if (p != sender) {
                mainClass.econ?.depositPlayer(p, amount.toDouble())
                p.sendMessage(PlaceholderAPI.setPlaceholders(p, mReceived))
            }
        }
        sender.sendMessage(mSent)
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