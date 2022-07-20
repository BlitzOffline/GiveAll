package com.blitzoffline.giveall.extension

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

lateinit var adventure: BukkitAudiences

fun Component.msg(player: Player) {
    adventure.player(player).sendMessage(this)
}

fun Component.msg(sender: CommandSender) {
    adventure.sender(sender).sendMessage(this)
}

fun String.msg(player: Player, legacy: Boolean = false) {
    if (legacy) {
        return adventure.player(player).sendMessage(
            LegacyComponentSerializer.legacyAmpersand()
                .deserialize(
                    this.parsePAPI(player)
                )
        )
    }

    adventure.player(player).sendMessage(MiniMessage.miniMessage().deserialize(this.parsePAPI(player)))
}

fun String.msg(sender: CommandSender, legacy: Boolean = false) {
    if (legacy) {
        return adventure.sender(sender).sendMessage(
            LegacyComponentSerializer.legacyAmpersand()
                .deserialize(
                    this.parsePAPI(sender as? Player)
                )
        )
    }

    adventure.sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(this.parsePAPI(sender as? Player)))
}