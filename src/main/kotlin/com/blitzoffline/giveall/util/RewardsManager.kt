package com.blitzoffline.giveall.util

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.command.CommandManager
import com.blitzoffline.giveall.extension.sendMessage
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
    if (!handleBasicChecks(plugin, sender, receivers, *placeholders)) return

    var count = 0
    for (receiver in receivers) {
        if (!handleBasicReceiverChecks(plugin, sender, receiver)) continue

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
    if (!handleBasicChecks(plugin, sender, receivers, *placeholders)) return

    if (levels) return handleXpLevelsGiving(plugin, sender, amount, receivers, *placeholders)
    handleXpPointsGiving(plugin, sender, amount, receivers, *placeholders)
}

fun handleMoneyGiving(
    plugin: GiveAll,
    sender: CommandSender,
    amount: Double,
    receivers: Collection<Player>,
    vararg placeholders: TagResolver
) {
    if (!handleBasicChecks(plugin, sender, receivers, *placeholders)) return

    var count = 0
    for (receiver in receivers) {
        if (!handleBasicReceiverChecks(plugin, sender, receiver)) continue

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
        if (!handleBasicReceiverChecks(plugin, sender, receiver)) continue

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
        if (!handleBasicReceiverChecks(plugin, sender, receiver)) continue

        receiver.giveExpLevels(amount)
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

    if (sender is Player && receivers.size == 1 && receivers.first() == sender &&
        !plugin.settingsManager.settings.giveRewardsToSender
    ) {
        sendMessage(
            sender,
            plugin.settingsManager.messages.noPlayers,
            *placeholders
        )
        return false
    }

    return true
}

private fun handleBasicReceiverChecks(
    plugin: GiveAll,
    sender: CommandSender,
    receiver: Player
): Boolean {
    if (!plugin.settingsManager.settings.giveRewardsToSender && receiver == sender)
        return false
    if (plugin.settingsManager.settings.requirePermission && !receiver.hasPermission(RECEIVE_PERMISSION))
        return false

    return true
}
