package com.blitzoffline.giveall.util

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.command.CommandManager
import com.blitzoffline.giveall.extension.sendMessage
import com.blitzoffline.giveall.settings.holder.IpMode
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

private const val RECEIVE_PERMISSION = "${CommandManager.BASE_PERMISSION}.receive"

fun handleItemGiving(
    plugin: GiveAll,
    sender: CommandSender,
    item: ItemStack,
    receivers: Collection<Player>,
    vararg placeholders: TagResolver
) {
    val filteredReceivers = filterReceivers(plugin, sender, receivers)
    if (!handleBasicChecks(plugin, sender, filteredReceivers, *placeholders)) return

    var count = 0
    for (receiver in filteredReceivers) {
        receiver.inventory.addItem(item.clone())
        count++

        sendMessage(
            receiver,
            plugin.settingsManager.messages.itemsReceived,
            *placeholders
        )

        if (receiver.inventory.firstEmpty() == -1)
            sendMessage(receiver, plugin.settingsManager.messages.inventoryFull)
    }

    sendMessage(
        sender,
        plugin.settingsManager.messages.itemsSent,
        *placeholders,
        Placeholder.unparsed("count", count.toString())
    )
}

fun handleXpGiving(
    plugin: GiveAll,
    sender: CommandSender,
    amount: Int,
    levels: Boolean,
    receivers: Collection<Player>,
    vararg placeholders: TagResolver
) {
    val filteredReceivers = filterReceivers(plugin, sender, receivers)
    if (!handleBasicChecks(plugin, sender, filteredReceivers, *placeholders)) return

    if (levels) return handleXpLevelsGiving(plugin, sender, amount, filteredReceivers, *placeholders)
    handleXpPointsGiving(plugin, sender, amount, filteredReceivers, *placeholders)
}

fun handleMoneyGiving(
    plugin: GiveAll,
    sender: CommandSender,
    amount: Double,
    receivers: Collection<Player>,
    vararg placeholders: TagResolver
) {
    val filteredReceivers = filterReceivers(plugin, sender, receivers)
    if (!handleBasicChecks(plugin, sender, filteredReceivers, *placeholders)) return

    var count = 0
    for (receiver in filteredReceivers) {
        plugin.econ.depositPlayer(receiver, amount)
        count++

        sendMessage(
            receiver,
            plugin.settingsManager.messages.moneyReceived,
            *placeholders
        )
    }

    sendMessage(
        sender,
        plugin.settingsManager.messages.moneySent,
        *placeholders,
        Placeholder.unparsed("count", count.toString())
    )
}

private fun handleXpLevelsGiving(
    plugin: GiveAll,
    sender: CommandSender,
    amount: Int,
    receivers: Collection<Player>,
    vararg placeholders: TagResolver
) {
    var count = 0
    for (receiver in receivers) {
        receiver.giveExpLevels(amount)
        count++

        sendMessage(
            receiver,
            plugin.settingsManager.messages.xpLevelsReceived,
            *placeholders
        )
    }

    sendMessage(
        sender,
        plugin.settingsManager.messages.xpLevelsSent,
        *placeholders,
        Placeholder.unparsed("count", count.toString())
    )
}

private fun handleXpPointsGiving(
    plugin: GiveAll,
    sender: CommandSender,
    amount: Int,
    receivers: Collection<Player>,
    vararg placeholders: TagResolver
) {
    var count = 0
    for (receiver in receivers) {
        receiver.giveExp(amount)
        count++

        sendMessage(
            receiver,
            plugin.settingsManager.messages.xpPointsReceived,
            *placeholders
        )
    }

    sendMessage(
        sender,
        plugin.settingsManager.messages.xpPointsSent,
        *placeholders,
        Placeholder.unparsed("count", count.toString())
    )
}

private fun handleBasicChecks(
    plugin: GiveAll,
    sender: CommandSender,
    receivers: Collection<Player>,
    vararg placeholders: TagResolver
): Boolean {
    if (receivers.isEmpty()) {
        sendMessage(
            sender,
            plugin.settingsManager.messages.noPlayers,
            *placeholders
        )
        return false
    }

    return true
}

private fun filterReceivers(
    plugin: GiveAll,
    sender: CommandSender,
    receivers: Collection<Player>
): Collection<Player> {
    val filteredReceivers = receivers
        .asSequence()
        .filter { plugin.settingsManager.settings.giveRewardsToSender || it != sender }
        .filter { !plugin.settingsManager.settings.requirePermission || it.hasPermission(RECEIVE_PERMISSION) }
        .toList()

    val ipMode = plugin.settingsManager.settings.ipMode
    if (ipMode == IpMode.ALL) return filteredReceivers

    val ipMap = filteredReceivers.associateWith { it.address.address.hostAddress }

    return when (ipMode) {
        IpMode.NONE -> {
            val counts = ipMap.values.groupingBy { it }.eachCount()
            ipMap.filter { (_, ip) -> counts[ip] == 1 }.keys
        }

        IpMode.RANDOM -> {
            ipMap.entries
                .groupBy { it.value }
                .map { (_, entries) -> entries.random() }
                .map { it.key }
        }

        IpMode.FIRST -> {
            ipMap.entries
                .groupBy { it.value }
                .map { (_, entries) -> entries.minByOrNull { it.key.name }!! }
                .map { it.key }
        }

        IpMode.LAST -> {
            ipMap.entries
                .groupBy { it.value }
                .map { (_, entries) -> entries.maxByOrNull { it.key.name }!! }
                .map { it.key }
        }

        else -> filteredReceivers
    }
}