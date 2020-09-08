package me.blitzgamer_88.giveall.util

import org.bukkit.ChatColor

fun String.color(): String = ChatColor.translateAlternateColorCodes('&', this)