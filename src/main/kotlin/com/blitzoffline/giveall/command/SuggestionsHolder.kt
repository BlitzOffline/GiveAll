package com.blitzoffline.giveall.command

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import org.bukkit.Bukkit
import org.bukkit.Material

class SuggestionsHolder {
    private val materials = Material.values().map { it.name }

    val materialSuggestions: ArgumentSuggestions = ArgumentSuggestions.strings(*materials.toTypedArray())
    val worldSuggestions: ArgumentSuggestions = ArgumentSuggestions.strings(*Bukkit.getWorlds().map { it.name }.toTypedArray())
}