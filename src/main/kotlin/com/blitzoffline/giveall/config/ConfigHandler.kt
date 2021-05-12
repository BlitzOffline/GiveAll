package com.blitzoffline.giveall.config

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.config.holder.Messages
import com.blitzoffline.giveall.config.holder.Settings
import me.mattstudios.config.SettingsManager
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit

lateinit var econ: Economy
lateinit var settings: SettingsManager
lateinit var messages: SettingsManager

fun loadConfig(plugin: GiveAll) {
    val file = plugin.dataFolder.resolve("config.yml")
    if (!file.exists()) plugin.saveDefaultConfig()
    settings = SettingsManager
        .from(file)
        .configurationData(Settings::class.java)
        .create()
}

fun loadMessages(plugin: GiveAll) {
    val file = plugin.dataFolder.resolve("messages.yml")
    if (!file.exists()) plugin.saveDefaultMessages()
    messages =  SettingsManager
        .from(file)
        .configurationData(Messages::class.java)
        .create()
}

fun setupEconomy(): Boolean {
    if (Bukkit.getServer().pluginManager.getPlugin("Vault") == null) return false
    val rsp = Bukkit.getServer().servicesManager.getRegistration(Economy::class.java) ?: return false
    econ = rsp.provider
    return true
}
