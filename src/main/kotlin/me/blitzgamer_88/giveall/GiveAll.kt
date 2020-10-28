package me.blitzgamer_88.giveall

import me.blitzgamer_88.giveall.cmd.CommandGiveAll
import me.blitzgamer_88.giveall.util.loadConfig
import me.blitzgamer_88.giveall.util.log
import me.blitzgamer_88.giveall.util.setupEconomy
import me.bristermitten.pdm.PDMBuilder
import me.mattstudios.mf.base.CommandManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin


class GiveAll : JavaPlugin() {

    // TODO: GiveAll Inventory.

    override fun onEnable() {

        PDMBuilder(this).build().loadAllDependencies().join()

        this.saveDefaultConfig()

        loadConfig(this)

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            "&cCould not find PlaceholderAPI! This plugin is required".log()
            Bukkit.getPluginManager().disablePlugin(this)
        }

        if (!setupEconomy()) "&cCould not find Vault! Give Money function will not be enabled.".log()

        val cmdManager = CommandManager(this, true)
        cmdManager.completionHandler.register("#worlds") { Bukkit.getWorlds().map(World::getName) }
        cmdManager.completionHandler.register("#materials") { Material.values().map { it.name.toLowerCase() } }
        cmdManager.register(CommandGiveAll())

        "&f[GiveAll]&3 Plugin enabled!".log()
    }

    override fun onDisable() {
        "&f[GiveAll]&d Plugin disabled!".log()
    }
}