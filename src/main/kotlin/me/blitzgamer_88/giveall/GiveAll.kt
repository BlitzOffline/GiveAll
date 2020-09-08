package me.blitzgamer_88.giveall

import ch.jalu.configme.SettingsManager
import me.blitzgamer_88.giveall.cmd.CommandGiveAll
import me.blitzgamer_88.giveall.conf.GiveAllConfiguration
import me.bristermitten.pdm.PDMBuilder
import me.mattstudios.mf.base.CommandManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class GiveAll : JavaPlugin() {

    private var conf = null as? SettingsManager?

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

    override fun onEnable() {
        PDMBuilder(this).build().loadAllDependencies().join()
        loadConfig()
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            logger.warning("Could not find PlaceholderAPI! This plugin is required")
            Bukkit.getPluginManager().disablePlugin(this)
        }
        val cmdManager = CommandManager(this, true)
        cmdManager.register(CommandGiveAll(this))
        logger.info("Plugin enabled!")
    }

    override fun onDisable() {
        logger.info("Plugin disabled!")
    }

}