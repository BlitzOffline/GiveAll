package me.blitzgamer_88.giveall

import ch.jalu.configme.SettingsManager
import me.blitzgamer_88.giveall.cmd.CommandGiveAll
import me.blitzgamer_88.giveall.conf.GiveAllConfiguration
import me.bristermitten.pdm.PDMBuilder
import me.mattstudios.mf.base.CommandManager
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin


class GiveAll : JavaPlugin() {

    // TODO: GiveAll Inventory.
    // TODO: Check for permission and if no players that respects the other requirements have the required permission to receive items send the noOneOnline message.

    var econ: Economy? = null
    private var conf = null as? SettingsManager?


    override fun onEnable() {

        PDMBuilder(this).build().loadAllDependencies().join()

        loadConfig()

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            logger.warning("Could not find PlaceholderAPI! This plugin is required")
            Bukkit.getPluginManager().disablePlugin(this)
        }

        if (!setupEconomy()) {
            logger.warning(String.format("[%s] - Disabled due to no Vault dependency found!", description.name))
            server.pluginManager.disablePlugin(this)
        }

        val cmdManager = CommandManager(this, true)
        cmdManager.completionHandler.register("#worlds") { Bukkit.getWorlds().map(World::getName) }
        cmdManager.completionHandler.register("#materials") { Material.values().map { it.name } }
        cmdManager.register(CommandGiveAll(this))
        logger.info("Plugin enabled!")
    }


    override fun onDisable() {
        logger.info("Plugin disabled!")
    }


    private fun loadConfig() {
        val file = this.dataFolder.resolve("config.yml")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        this.conf = GiveAllConfiguration(file)
    }

    fun conf(): SettingsManager {
        return checkNotNull(conf)
    }

    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rsp = server.servicesManager.getRegistration(Economy::class.java) ?: return false
        econ = rsp.provider
        return econ != null
    }

}