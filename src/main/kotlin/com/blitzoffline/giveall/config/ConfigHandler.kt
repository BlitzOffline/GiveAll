package com.blitzoffline.giveall.config

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.config.holder.Messages
import com.blitzoffline.giveall.config.holder.Settings
import me.mattstudios.config.SettingsManager
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit

class ConfigHandler(private val plugin: GiveAll) {
    fun fetchSettings(): SettingsManager {
        val file = plugin.dataFolder.resolve("config.yml")
        if (!file.exists()) plugin.saveDefaultConfig()
        return SettingsManager
            .from(file)
            .configurationData(Settings::class.java)
            .create()
    }

    fun fetchMessages(): SettingsManager {
        val file = plugin.dataFolder.resolve("messages.yml")
        if (!file.exists()) plugin.saveDefaultMessages()
        return SettingsManager
            .from(file)
            .configurationData(Messages::class.java)
            .create()
    }

    fun fetchEconomy(): Economy? {
        if (Bukkit.getServer().pluginManager.getPlugin("Vault") == null) return null
        val rsp = Bukkit.getServer().servicesManager.getRegistration(Economy::class.java) ?: return null
        return rsp.provider
    }
}
