package me.blitzgamer_88.giveall.commands

import me.blitzgamer_88.giveall.GiveAll
import me.blitzgamer_88.giveall.util.*
import me.clip.placeholderapi.PlaceholderAPI
import me.mattstudios.mf.annotations.*
import me.mattstudios.mf.base.CommandBase
import org.bukkit.Bukkit.getServer
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BoundingBox


@Command("giveall")
@Alias("gall")
class CommandGiveAll(private val plugin: GiveAll) : CommandBase() {

    @Default
    @Permission("giveall.use", "giveall.use.all")
    fun giveAll(sender: CommandSender, @Completion("#materials") material: Material, @Optional amt: String?) {
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

        if (amt != null && amt.toIntOrNull() == null) {
            wrongUsage.msg(sender)
            return
        }

        val amount = if (amt?.toIntOrNull() != null) amt.toInt() else material.maxStackSize
        val item = ItemStack(material, amount)

        for (player in players) {
            if (requirePermissionToGetItems && !player.hasPermission("giveall.receive")) continue
            player.inventory.addItem(item)
            itemsReceived
                .replace("%amount%", amount.toString())
                .replace("%material%", material.name.toLowerCase())
                .msg(player)
            if (player.inventory.firstEmpty() == -1) fullInventory.msg(player)
        }
        itemsSent
            .replace("%amount%", amount.toString())
            .replace("%material%", material.name.toLowerCase())
            .msg(sender)
    }

    @SubCommand("world")
    @Permission("giveall.use.world", "giveall.use.all")
    fun giveAllWorld(sender: CommandSender, @Completion("#worlds") world: World, @Completion("#materials") material: Material, @Optional amt: String?) {
        val players = world.players
        if (players.isEmpty()) {
            noOneOnline.msg(sender)
            return
        }

        if (sender is Player && sender.world == world && players.size == 1 && !giveItemsToSender) {
            onlyYouOnline.msg(sender)
            return
        }

        if (sender is Player && players.contains(sender) && !giveItemsToSender) players.remove(sender)

        val amount = if (amt?.toIntOrNull() != null) amt.toInt() else material.maxStackSize
        val item = ItemStack(material, amount)

        for (player in players) {
            if (requirePermissionToGetItems && !player.hasPermission("giveall.receive")) continue
            player.inventory.addItem(item)
            itemsReceived
                .replace("%amount%", amount.toString())
                .replace("%material%", material.name.toLowerCase())
                .msg(player)
            if (player.inventory.firstEmpty() != -1)  player.sendMessage(PlaceholderAPI.setPlaceholders(player, fullInventory))
        }
        itemsSent
            .replace("%amount%", amount.toString())
            .replace("%material%", material.name.toLowerCase())
            .replace("%world%", world.name).msg(sender)
    }


    @SubCommand("radius")
    @Permission("giveall.use.radius", "giveall.use.all")
    fun giveAllRadius(sender: Player, radius: Double, @Completion("#materials") material: Material, @Optional amt: String?) {
        val location = sender.location
        val hypotenuse = kotlin.math.sqrt(2 * radius * radius)
        val boundingBox = BoundingBox(
            location.x-hypotenuse,
            location.y-radius, location.z-hypotenuse,
            location.x+hypotenuse,
            location.y+radius,
            location.z+hypotenuse
        )

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

        if (!giveItemsToSender) players.remove(sender)

        val amount = if (amt?.toIntOrNull() != null) amt.toInt() else material.maxStackSize
        val item = ItemStack(material, amount)

        for (player in players) {
            if (requirePermissionToGetItems && !player.hasPermission("giveall.receive")) continue
            player.inventory.addItem(item)
            itemsReceived
                .replace("%amount%", amount.toString())
                .replace("%material%", material.name.toLowerCase())
                .msg(player)
            if (player.inventory.firstEmpty() == -1) fullInventory.msg(player)
        }
        itemsSent
            .replace("%amount%", amount.toString())
            .replace("%material%", material.name.toLowerCase())
            .replace("%radius%", radius.toInt().toString()).msg(sender)
    }


    @SubCommand("hand")
    @Permission("giveall.use.hand", "giveall.use.all")
    fun giveAllHand(sender: Player, @Completion("#worlds") @Optional argument: String?) {
        if (argument != null && argument.contains(" ")) {
            wrongUsage.msg(sender)
            return
        }

        var checkWorld = false
        var checkRadius = false

        val players: MutableCollection<out Player>
        when {
            argument == null -> players = getServer().onlinePlayers
            argument.toDoubleOrNull() != null -> {
                checkRadius = true
                val location = sender.location
                val radius = argument.toDouble()
                val hypotenuse = kotlin.math.sqrt(2 * radius * radius)
                val boundingBox = BoundingBox(
                    location.x - hypotenuse,
                    location.y - radius,
                    location.z - hypotenuse,
                    location.x + hypotenuse,
                    location.y + radius,
                    location.z + hypotenuse
                )
                players = sender.world.getNearbyEntities(boundingBox)
                    .filterIsInstance(Player::class.java).toMutableList()
            }
            else -> {
                checkWorld = true
                val world = getServer().getWorld(argument)
                if (world == null) {
                    wrongWorldName.msg(sender)
                    return
                }
                players = world.players
            }
        }

        if (!giveItemsToSender) players.remove(sender)

        if (players.isEmpty()) {
            noOneOnline.msg(sender)
            return
        }

        // The item is cloned because it seems like if the item is changed in the player inventory's in the middle of the process, it will give the new items to the player.
        val item = sender.inventory.itemInMainHand.clone()
        if (item.type == Material.AIR) {
            itemCannotBeAir.msg(sender)
            return
        }

        for (player in players) {
            if (requirePermissionToGetItems && !player.hasPermission("giveall.receive")) continue
            player.inventory.addItem(item.clone())
            itemsReceived
                .replace("%amount%", item.amount.toString())
                .replace("%material%", item.type.name.toLowerCase())
                .msg(player)
            if (player.inventory.firstEmpty() == -1) fullInventory.msg(player)
        }

        when {
            checkWorld -> {
                itemsSentWorld
                    .replace("%amount%", item.amount.toString())
                    .replace("%material%", item.type.name.toLowerCase())
                    .replace("%world%", argument.toString())
                    .msg(sender)
            }
            checkRadius -> {
                itemsSentRadius
                    .replace("%amount%", item.amount.toString())
                    .replace("%material%", item.type.name.toLowerCase())
                    .replace("%radius%", argument.toString())
                    .msg(sender)
            }
            else -> {
                itemsSent
                    .replace("%amount%", item.amount.toString())
                    .replace("%material%", item.type.name.toLowerCase())
                    .msg(sender)
            }
        }
    }


    @SubCommand("money")
    @Permission("giveall.use.money", "giveall.use.all")
    fun giveAllMoney(sender: CommandSender, amount: Int?, @Completion("#worlds") @Optional argument: String?) {
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

        val players: MutableCollection<out Player>
        when {
            argument == null -> players = getServer().onlinePlayers
            argument.toDoubleOrNull() != null -> {
                if (sender !is Player) return
                checkRadius = true
                val location = sender.location
                val radius = argument.toDouble()
                val hypotenuse = kotlin.math.sqrt(2 * radius * radius)
                val boundingBox = BoundingBox(
                    location.x - hypotenuse,
                    location.y - radius,
                    location.z - hypotenuse,
                    location.x + hypotenuse,
                    location.y + radius,
                    location.z + hypotenuse
                )
                players = sender.world.getNearbyEntities(boundingBox)
                    .filterIsInstance(Player::class.java).toMutableList()
            }
            else -> {
                val world = getServer().getWorld(argument)
                if (world == null) {
                    wrongWorldName.msg(sender)
                    return
                }
                players = world.players
                checkWorld = true
            }
        }

        if (!giveItemsToSender) players.remove(sender as Player)

        if (players.isEmpty()) {
            noOneOnlineMoney.msg(sender)
            return
        }

        if (amount == null || amount <= 0) {
            amountCannotBeZero.msg(sender)
            return
        }

        for (player in players) {
            if (requirePermissionToGetItems && !player.hasPermission("giveall.receive")) continue
            plugin.econ.depositPlayer(player, amount.toDouble())
            moneyReceived
                .replace("%amount%", amount.toString())
                .msg(player)
        }

        when {
            checkWorld -> {
                moneySentWorld
                    .replace("%amount%", amount.toString())
                    .replace("%world%", argument.toString())
                    .msg(sender)
            }
            checkRadius -> {
                moneySentRadius
                    .replace("%amount%", amount.toString())
                    .replace("%radius%", argument.toString())
                    .msg(sender)
            }
            else -> {
                moneySent
                    .replace("%amount%", amount.toString())
                    .msg(sender)
            }
        }
    }


    @SubCommand("help")
    @Permission("giveall.help", "giveall.use.all")
    fun giveAllHelp(sender: CommandSender) {
        "".msg(sender)
        "&7---- &6GiveAll by BlitzGamer_88 &7----".msg(sender)
        "".msg(sender)
        "&7/giveAll <material> [amount] &8-&f give items to all players".msg(sender)
        "&7/giveAll world <world> <material> [amount] &8-&f give items to all players from a world".msg(sender)
        "&7/giveAll radius <radius> <material> [amount] &8-&f give items to all players in a radius".msg(sender)
        "&7/giveAll money <amount> [world/radius] &8-&f give money to all players".msg(sender)
        "&7/giveAll hand [world/radius] &8-&f give the items you hold in your hand to all players".msg(sender)
        "&7/giveAll reload &8-&f reload the plugin".msg(sender)

    }

    @SubCommand("reload")
    @Permission("giveall.reload", "giveall.use.all")
    fun giveAllReload(sender: CommandSender) {
        plugin.reload()
        configReloaded.msg(sender)
    }
}