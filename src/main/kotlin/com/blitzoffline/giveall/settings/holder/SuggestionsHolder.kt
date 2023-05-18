package com.blitzoffline.giveall.settings.holder

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender

class SuggestionsHolder {
    private val materials = Material.values().map { it.name }

    val materialSuggestions: ArgumentSuggestions<CommandSender> = ArgumentSuggestions.strings(*materials.toTypedArray())
    val worldSuggestions: ArgumentSuggestions<CommandSender> = ArgumentSuggestions.strings(*Bukkit.getWorlds().map { it.name }.toTypedArray())
}