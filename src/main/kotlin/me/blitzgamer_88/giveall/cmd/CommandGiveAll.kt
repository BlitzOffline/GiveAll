package me.blitzgamer_88.giveall.cmd

import me.blitzgamer_88.giveall.conf.Config
import me.blitzgamer_88.giveall.util.conf
import me.blitzgamer_88.giveall.util.econ
import me.blitzgamer_88.giveall.util.msg
import me.blitzgamer_88.giveall.util.setupEconomy
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
class CommandGiveAll : CommandBase() {

    @Default
    fun giveAll(sender: CommandSender, @Completion("#materials") material: Material, @Optional amt: Int?) {
        // Requirements
        val giveItemsToSender = conf().getProperty(Config.giveItemsToSender)
        val requirePermissionToGetItems = conf().getProperty(Config.requirePermissionToGetItems)

        // Permissions
        val giveAllPermission = conf().getProperty(Config.giveAllPermission)
        val giveAllReceivePermission = conf().getProperty(Config.giveAllReceivePermission)
        val giveAllMainPermission = conf().getProperty(Config.giveAllMainPermission)

        // Messages
        val itemsReceived = conf().getProperty(Config.itemsReceived)
        val itemsSent = conf().getProperty(Config.itemsSent)
        val noOneOnline = conf().getProperty(Config.noOneOnline)
        val noPermission = conf().getProperty(Config.noPermission)
        val inventoryFull = conf().getProperty(Config.fullInventory)
        val onlyYouOnline = conf().getProperty(Config.onlyYouOnline)

        if (sender is Player && !sender.hasPermission(giveAllPermission) && !sender.hasPermission(giveAllMainPermission)) {
            noPermission.msg(sender)
            return
        }

        val players = getServer().onlinePlayers
        if (players.isEmpty()) {
            noOneOnline.msg(sender)
            return
        }

        if (sender is Player && players.size == 1 && !giveItemsToSender) {
            onlyYouOnline.msg(sender)
            return
        }

        if (sender is Player && !giveItemsToSender) {
            players.remove(sender)
        }

        val amount = if (amt.toString().toIntOrNull() != null) {
            amt.toString().toIntOrNull()
        } else {
            material.maxStackSize
        } ?: return

        val item = ItemStack(material, amount)
        for (player in players) {
            if (requirePermissionToGetItems && !player.hasPermission(giveAllReceivePermission)) continue
            player.inventory.addItem(item)
            if (player.inventory.firstEmpty() != -1) {
                itemsReceived.replace("%amount%", amount.toString()).replace("%material%", material.name.toLowerCase()).msg(player)
            } else {
                inventoryFull.msg(player)
            }
        }
        itemsSent.replace("%amount%", amount.toString()).replace("%material%", material.name.toLowerCase()).msg(sender)
        return
    }


    @SubCommand("world")
    fun giveAllWorld(sender: CommandSender, @Completion("#worlds") worldName: String, @Completion("#materials") material: Material, @Optional amt: Int?) {
        // Requirements
        val giveItemsToSender = conf().getProperty(Config.giveItemsToSender)
        val requirePermissionToGetItems = conf().getProperty(Config.requirePermissionToGetItems)

        // Permissions
        val giveAllWorldPermission = conf().getProperty(Config.giveAllWorldPermission)
        val giveAllReceivePermission = conf().getProperty(Config.giveAllReceivePermission)
        val giveAllMainPermission = conf().getProperty(Config.giveAllMainPermission)

        // Messages
        val itemsReceived = conf().getProperty(Config.itemsReceived)
        val itemsSent = conf().getProperty(Config.itemsSentWorld)
        val wrongWorldName = conf().getProperty(Config.wrongWorldName)
        val noOneOnline = conf().getProperty(Config.noOneOnline)
        val noPermission = conf().getProperty(Config.noPermission)
        val inventoryFull = conf().getProperty(Config.fullInventory)
        val onlyYouOnline = conf().getProperty(Config.onlyYouOnline)

        if (sender is Player && !sender.hasPermission(giveAllWorldPermission) && !sender.hasPermission(giveAllMainPermission)) {
            noPermission.msg(sender)
            return
        }

        val world = getServer().getWorld(worldName)
        if (world == null) {
            wrongWorldName.msg(sender)
            return
        }

        val players = world.players
        if (players.isEmpty()) {
            noOneOnline.msg(sender)
            return
        }

        if (sender is Player && sender.world == world && players.size == 1 && !giveItemsToSender) {
            onlyYouOnline.msg(sender)
            return
        }

        if (sender is Player && sender.world == world && !giveItemsToSender) players.remove(sender)

        val amount = if (amt.toString().toIntOrNull() != null) {
            amt.toString().toIntOrNull()
        } else {
            material.maxStackSize
        } ?: return

        val item = ItemStack(material, amount)
        for (player in players) {
            if (requirePermissionToGetItems && !player.hasPermission(giveAllReceivePermission)) continue
            player.inventory.addItem(item)

            if (player.inventory.firstEmpty() != -1) { itemsReceived.replace("%amount%", amount.toString()).replace("%material%", material.name.toLowerCase()).msg(player) }
            else { player.sendMessage(PlaceholderAPI.setPlaceholders(player, inventoryFull)) }
        }
        itemsSent.replace("%amount%", amount.toString()).replace("%material%", material.name.toLowerCase()).replace("%world%", worldName).msg(sender)
    }


    @SubCommand("radius")
    fun giveAllRadius(sender: Player, r: String, @Completion("#materials") material: Material, @Optional amt: Int?) {
        // Requirements
        val giveItemsToSender = conf().getProperty(Config.giveItemsToSender)
        val requirePermissionToGetItems = conf().getProperty(Config.requirePermissionToGetItems)

        // Permissions
        val giveAllRadiusPermission = conf().getProperty(Config.giveAllRadiusPermission)
        val giveAllReceivePermission = conf().getProperty(Config.giveAllReceivePermission)
        val giveAllMainPermission = conf().getProperty(Config.giveAllMainPermission)

        // Messages
        val itemsReceived = conf().getProperty(Config.itemsReceived)
        val itemsSent = conf().getProperty(Config.itemsSentRadius)
        val wrongUsage = conf().getProperty(Config.wrongUsage)
        val noOneOnline = conf().getProperty(Config.noOneOnline)
        val noPermission = conf().getProperty(Config.noPermission)
        val inventoryFull = conf().getProperty(Config.fullInventory)
        val onlyYouOnline = conf().getProperty(Config.onlyYouOnline)

        if (!sender.hasPermission(giveAllRadiusPermission) && !sender.hasPermission(giveAllMainPermission)) {
            noPermission.msg(sender)
            return
        }

        val radius = r.toDoubleOrNull()
        if (radius == null) {
            wrongUsage.msg(sender)
            return
        }

        val location = sender.location
        val hypotenuse = kotlin.math.sqrt(2 * radius * radius)
        val boundingBox = BoundingBox(location.x-hypotenuse, location.y-radius, location.z-hypotenuse, location.x+hypotenuse, location.y+radius, location.z+hypotenuse)
        val players= sender.world.getNearbyEntities(boundingBox)
                .filterIsInstance(Player::class.java).toMutableList()

        if (players.isEmpty()) {
            noOneOnline.msg(sender)
            return
        }

        if (players.size == 1 && !giveItemsToSender) {
            onlyYouOnline.msg(sender)
            return
        }

        if (!giveItemsToSender) { players.remove(sender) }

        val amount = if (amt.toString().toIntOrNull() != null) {
            amt.toString().toIntOrNull()
        } else {
            material.maxStackSize
        } ?: return

        val item = ItemStack(material, amount)
        for (player in players) {
            if (requirePermissionToGetItems && !player.hasPermission(giveAllReceivePermission)) continue
            player.inventory.addItem(item)
            if (player.inventory.firstEmpty() != -1) { itemsReceived.replace("%amount%", amount.toString()).replace("%material%", material.name.toLowerCase()).msg(player) }
            else { inventoryFull.msg(sender) }
        }
        itemsSent.replace("%amount%", amount.toString()).replace("%material%", material.name.toLowerCase()).replace("%radius%", radius.toInt().toString()).msg(sender)
    }


    @SubCommand("hand")
    fun giveAllHand(sender: Player, @Completion("#worlds") @Optional argument: String?) {
        // Requirements
        val giveItemsToSender = conf().getProperty(Config.giveItemsToSender)
        val requirePermissionToGetItems = conf().getProperty(Config.requirePermissionToGetItems)

        // Permissions
        val giveAllHandPermission = conf().getProperty(Config.giveAllHandPermission)
        val giveAllReceivePermission = conf().getProperty(Config.giveAllReceivePermission)
        val giveAllMainPermission = conf().getProperty(Config.giveAllMainPermission)

        // Messages
        val itemsReceived = conf().getProperty(Config.itemsReceived)
        val itemsSent = conf().getProperty(Config.itemsSent)
        val itemsSentWorld = conf().getProperty(Config.itemsSentWorld)
        val itemsSentRadius = conf().getProperty(Config.itemsSentRadius)
        val noPermission = conf().getProperty(Config.noPermission)
        val inventoryFull = conf().getProperty(Config.fullInventory)
        val itemCannotBeAir = conf().getProperty(Config.itemCannotBeAir)
        val noOneOnline = conf().getProperty(Config.noOneOnline)
        val wrongWorldName = conf().getProperty(Config.wrongWorldName)
        val wrongUsage = conf().getProperty(Config.wrongUsage)

        if (!sender.hasPermission(giveAllHandPermission) && !sender.hasPermission(giveAllMainPermission)) {
            noPermission.msg(sender)
            return
        }

        if (argument != null && argument.contains(" ")) {
            wrongUsage.msg(sender)
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
                    wrongWorldName.msg(sender)
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

        if (!giveItemsToSender) players.remove(sender)

        if (players.isEmpty()) {
            noOneOnline.msg(sender)
            return
        }

        // The item is cloned because it seems like if the item is changed in the player inventory's in the middle of the process, it will give the new items to the player.
        val item = sender.inventory.itemInMainHand.clone()
        val amount = item.amount
        if (item.type == Material.AIR) {
            itemCannotBeAir.msg(sender)
            return
        }

        for (player in players) {
            if (requirePermissionToGetItems && !player.hasPermission(giveAllReceivePermission)) continue
            player.inventory.addItem(item.clone())

            if (player.inventory.firstEmpty() != -1) { itemsReceived.replace("%amount%", item.amount.toString()).replace("%material%", item.type.name.toLowerCase()).msg(player) }
            else { inventoryFull.msg(player) }
        }
        when {
            checkWorld -> {
                itemsSentWorld.replace("%amount%", amount.toString()).replace("%material%", item.type.name.toLowerCase()).replace("%world%", argument.toString()).msg(sender)
            }
            checkRadius -> {
                itemsSentRadius.replace("%amount%", amount.toString()).replace("%material%", item.type.name.toLowerCase()).replace("%radius%", argument.toString()).msg(sender)
            }
            else -> {
                itemsSent.replace("%amount%", amount.toString()).replace("%material%", item.type.name.toLowerCase()).msg(sender)
            }
        }
    }


    @SubCommand("money")
    fun giveAllMoney(sender: CommandSender, amt: Int?, @Completion("#worlds") @Optional argument: String?) {
        // Requirements
        val giveItemsToSender = conf().getProperty(Config.giveItemsToSender)
        val requirePermissionToGetItems = conf().getProperty(Config.requirePermissionToGetItems)

        // Permissions
        val giveAllMoneyPermission = conf().getProperty(Config.giveAllMoneyPermission)
        val giveAllReceivePermission = conf().getProperty(Config.giveAllReceivePermission)
        val giveAllMainPermission = conf().getProperty(Config.giveAllMainPermission)

        // Messages
        val moneyReceived = conf().getProperty(Config.moneyReceived)
        val moneySent = conf().getProperty(Config.moneySent)
        val moneySentWorld = conf().getProperty(Config.moneySentWorld)
        val moneySentRadius = conf().getProperty(Config.moneySentRadius)
        val noPermission = conf().getProperty(Config.noPermission)
        val amountCannotBeZero = conf().getProperty(Config.amountCannotBeZero)
        val onlyPlayersCanUseRadius = conf().getProperty(Config.onlyPlayersCanUseRadius)
        val noOneOnline = conf().getProperty(Config.noOneOnlineMoney)
        val wrongWorldName = conf().getProperty(Config.wrongWorldName)
        val wrongUsage = conf().getProperty(Config.wrongUsage)

        if (!setupEconomy()) return

        if (sender is Player && !sender.hasPermission(giveAllMoneyPermission) && !sender.hasPermission(giveAllMainPermission)) {
            noPermission.msg(sender)
            return
        }

        if (argument != null && argument.contains(" ")) {
            wrongUsage.msg(sender)
            return
        }

        if (argument != null && argument.toDoubleOrNull() != null && sender !is Player) {
            onlyPlayersCanUseRadius.msg(sender)
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
                    wrongWorldName.msg(sender)
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
                    onlyPlayersCanUseRadius.msg(sender)
                    return
                }
            }
        }

        if (!giveItemsToSender) players.remove(sender)

        if (players.isEmpty()) {
            noOneOnline.msg(sender)
            return
        }

        val amount = amt.toString().toIntOrNull()
        if (amount == null) {
            amountCannotBeZero.msg(sender)
            return
        }

        for (player in players) {
            if (requirePermissionToGetItems && !player.hasPermission(giveAllReceivePermission)) continue
            econ?.depositPlayer(player, amount.toDouble())
            moneyReceived.replace("%amount%", amount.toString()).msg(player)
        }

        when {
            checkWorld -> {
                moneySentWorld.replace("%amount%", amount.toString()).replace("%world%", argument.toString()).msg(sender)
            }
            checkRadius -> {
                moneySentRadius.replace("%amount%", amount.toString()).replace("%radius%", argument.toString()).msg(sender)
            }
            else -> {
                moneySent.replace("%amount%", amount.toString()).msg(sender)
            }
        }
    }


    @SubCommand("help")
    fun giveAllHelp(sender: CommandSender) {
        val giveAllHelpPermission = conf().getProperty(Config.giveAllHelpPermission)
        val noPermission = conf().getProperty(Config.noPermission)

        if (sender is Player && !sender.hasPermission(giveAllHelpPermission)) {
            noPermission.msg(sender)
            return
        }

        "".msg(sender)
        "&7---- &6GiveAll by BlitzGamer_88 &7----".msg(sender)
        "".msg(sender)
        "&7/giveAll [material] <amount> &8-&f give items to all players".msg(sender)
        "&7/giveAll world [world] [material] <amount> &8-&f give items to all players from a world".msg(sender)
        "&7/giveAll radius [radius] [material] <amount> &8-&f give items to all players in a radius".msg(sender)
        "&7/giveAll money [amount] <world/radius> &8-&f give money to all players".msg(sender)
        "&7/giveAll hand <world/radius> &8-&f give the items you hold in your hand to all players".msg(sender)
        "&7/giveAll reload &8-&f reload the plugin".msg(sender)

    }


    @SubCommand("reload")
    fun giveAllReload(sender: CommandSender) {
        val configReloaded = conf().getProperty(Config.configReloaded)
        val noPermission = conf().getProperty(Config.noPermission)

        val giveAllReloadPermission = conf().getProperty(Config.giveAllReloadPermission)

        if (sender is Player && !sender.hasPermission(giveAllReloadPermission)) {
            noPermission.msg(sender)
            return
        }

        conf().reload()
        configReloaded.msg(sender)
    }
}