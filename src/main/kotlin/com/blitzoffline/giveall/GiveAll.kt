package com.blitzoffline.giveall

import com.blitzoffline.giveall.command.CommandManager
import com.blitzoffline.giveall.database.Database
import com.blitzoffline.giveall.database.GsonDatabase
import com.blitzoffline.giveall.extension.adventure
import com.blitzoffline.giveall.item.SavedItemsManager
import com.blitzoffline.giveall.settings.SettingsManager
import com.blitzoffline.giveall.task.SaveDataTask
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIConfig
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

class GiveAll : JavaPlugin() {
    lateinit var settingsManager: SettingsManager
        private set
    lateinit var database: Database
        private set
    lateinit var savedItemsManager: SavedItemsManager
        private set
    lateinit var econ: Economy
        private set

    private var hooked = false
    private lateinit var commandManager: CommandManager
    private lateinit var saveData: BukkitTask

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIConfig().silentLogs(true))
    }

    override fun onEnable() {
        CommandAPI.onEnable(this)
        adventure = BukkitAudiences.create(this)

        settingsManager = SettingsManager(this, dataFolder)
        commandManager = CommandManager(this)

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            logger.warning("Could not find PlaceholderAPI! This plugin is required!")
            pluginLoader.disablePlugin(this)
        }

        val vaultHook = settingsManager.settings.hooks["vault"]
        if (vaultHook != null && vaultHook == true) {
            val vault = Bukkit.getPluginManager().getPlugin("Vault")

            if (vault == null) {
                logger.warning("Could not find Vault! If you don't want to use it, disable the hook from settings.conf!")
                pluginLoader.disablePlugin(this)
                return
            }

            if (!vault.isEnabled) {
                logger.warning("Vault is disabled! If you don't want to use it, disable the hook from settings.conf!")
                pluginLoader.disablePlugin(this)
                return
            }

            econ = fetchEconomy() ?: run {
                logger.warning("Could not load the economy. Make sure vault is installed and working!")
                pluginLoader.disablePlugin(this)
                return
            }
            hooked = true
        }

        commandManager.createCommands(hooked).register()

        savedItemsManager = SavedItemsManager()
        database = GsonDatabase(this)
        database.loadItems()

        registerTasks()

        logger.info("Plugin enabled successfully!")
    }

    override fun onDisable() {
        if(::saveData.isInitialized && !saveData.isCancelled) saveData.cancel()
        if(::database.isInitialized) database.saveItems(savedItemsManager.clone())

        CommandAPI.onDisable()
        logger.info("Plugin disabled successfully!")
    }

    fun saveDefaultFile(fileName: String) {
        if (dataFolder.resolve(fileName).exists()) return
        saveResource(fileName, false)
    }

    private fun registerTasks() {
        if(::saveData.isInitialized && !saveData.isCancelled) saveData.cancel()
        saveData = SaveDataTask(this).runTaskTimer(this, 300 * 20L, 300 * 20L)
    }

    private fun fetchEconomy(): Economy? {
        return Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)?.provider
    }
}