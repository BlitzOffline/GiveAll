package com.blitzoffline.giveall.util

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

// TODO: 3/6/22 Switch to MiniMessages.
val specialSerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build()
val legacySerializer = LegacyComponentSerializer.legacyAmpersand()

fun String.color() = specialSerializer.serialize(legacySerializer.deserialize(this))
fun List<String>.color() = map { it.color() }

fun String.parsePAPI(player: Player?) = PlaceholderAPI.setPlaceholders(player, this)
fun String.parsePAPI(player: OfflinePlayer?) = PlaceholderAPI.setPlaceholders(player, this)