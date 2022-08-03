package com.blitzoffline.giveall.extension

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

lateinit var adventure: BukkitAudiences

fun sendMessage(player: Player, message: Component) {
    adventure.player(player).sendMessage(message)
}

fun sendMessage(sender: CommandSender, message: Component) {
    adventure.sender(sender).sendMessage(message)
}

fun sendMessage(player: Player, message: String, vararg placeholders: TagResolver) {
    if (message.isBlank()) return

    adventure.player(player).sendMessage(
        MiniMessage.miniMessage().deserialize(
            message.parsePAPI(player),
            *placeholders
        )
    )
}

fun sendMessage(sender: CommandSender, message: String, vararg placeholders: TagResolver) {
    if (message.isBlank()) return

    adventure.sender(sender).sendMessage(
        MiniMessage.miniMessage().deserialize(
            message.parsePAPI(sender as? Player),
            *placeholders
        )
    )
}

fun sendMessage(
    player: Player,
    messages: List<String>,
    separator: Component = Component.newline(),
    vararg placeholders: TagResolver
) {
    adventure.player(player).sendMessage(
        messages.stream()
            .map { it.parsePAPI(player) }
            .map { MiniMessage.miniMessage().deserialize(it, *placeholders) }
            .collect(Component.toComponent(separator))
    )
}

fun sendMessage(
    sender: CommandSender,
    messages: List<String>,
    separator: Component = Component.newline(),
    vararg placeholders: TagResolver
) {
    adventure.sender(sender).sendMessage(
        messages.stream()
            .map { it.parsePAPI(sender as? Player) }
            .map { MiniMessage.miniMessage().deserialize(it, *placeholders) }
            .collect(Component.toComponent(separator))
    )
}