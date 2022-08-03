package com.blitzoffline.giveall.extension

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

fun String.parsePAPI(player: Player?) = PlaceholderAPI.setPlaceholders(player, this)
fun String.parsePAPI(player: OfflinePlayer?) = PlaceholderAPI.setPlaceholders(player, this)