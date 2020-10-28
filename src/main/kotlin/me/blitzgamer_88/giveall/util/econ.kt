package me.blitzgamer_88.giveall.util

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit.getServer

var econ: Economy? = null

fun setupEconomy(): Boolean {
    if (getServer().pluginManager.getPlugin("Vault") == null) {
        return false
    }
    val rsp = getServer().servicesManager.getRegistration(Economy::class.java) ?: return false
    econ = rsp.provider
    return econ != null
}