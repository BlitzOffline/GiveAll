package com.blitzoffline.giveall.util

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

lateinit var adventure: BukkitAudiences

fun String.log() = adventure.console().sendMessage(legacySerializer.deserialize(this))
fun String.msg(player: Player) = adventure.player(player).sendMessage(legacySerializer.deserialize(this.parsePAPI(player)))
fun String.msg(sender: CommandSender) = adventure.sender(sender).sendMessage(legacySerializer.deserialize(this))
fun String.broadcast() = adventure.all().sendMessage(legacySerializer.deserialize(this))