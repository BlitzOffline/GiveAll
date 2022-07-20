package com.blitzoffline.giveall

import com.blitzoffline.giveall.command.CommandConsoleRadius
import com.blitzoffline.giveall.command.CommandHand
import com.blitzoffline.giveall.command.CommandHelp
import com.blitzoffline.giveall.command.CommandItem
import com.blitzoffline.giveall.command.CommandMoney
import com.blitzoffline.giveall.command.CommandRadius
import com.blitzoffline.giveall.command.CommandReload
import com.blitzoffline.giveall.command.CommandRemoveItem
import com.blitzoffline.giveall.command.CommandSaveItem
import com.blitzoffline.giveall.command.CommandWorld
import com.blitzoffline.giveall.command.CommandXp
import com.blitzoffline.giveall.database.Database
import com.blitzoffline.giveall.database.GsonDatabase
import com.blitzoffline.giveall.extension.adventure
import com.blitzoffline.giveall.extension.msg
import com.blitzoffline.giveall.manager.SavedItemsManager
import com.blitzoffline.giveall.settings.SettingsManager
import com.blitzoffline.giveall.task.SaveDataTask
import dev.triumphteam.cmd.bukkit.BukkitCommandManager
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey
import dev.triumphteam.cmd.core.BaseCommand
import dev.triumphteam.cmd.core.message.MessageKey
import dev.triumphteam.cmd.core.suggestion.SuggestionKey
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

// TODO: 3/6/22 Refactor the settings and messages
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

    private lateinit var commandManager: BukkitCommandManager<CommandSender>
    private lateinit var saveData: BukkitTask

    override fun onEnable() {
        adventure = BukkitAudiences.create(this)

        settingsManager = SettingsManager(dataFolder, this)

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            logger.warning("Could not find PlaceholderAPI! This plugin is required!")
            pluginLoader.disablePlugin(this)
        }

        if (settingsManager.settings.vaultHook) {
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

        commandManager = BukkitCommandManager.create(this)
        registerMessage()
        registerCompletion()

        registerCommands(
            CommandConsoleRadius(this),
            CommandHand(this),
            CommandHelp(),
            CommandItem(this),
            CommandRadius(this),
            CommandReload(this),
            CommandRemoveItem(this),
            CommandSaveItem(this),
            CommandWorld(this),
            CommandXp(this)
        )

        if (hooked) {
            registerCommands(
                CommandMoney(this)
            )
        }
        savedItemsManager = SavedItemsManager()
        database = GsonDatabase(this)
        database.loadItemStacks()

        registerTasks()

        logger.info("Plugin enabled successfully!")
    }

    override fun onDisable() {
        if(::saveData.isInitialized && !saveData.isCancelled) saveData.cancel()
        if(::database.isInitialized) database.saveItemStacks(savedItemsManager.clone())

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
    private fun registerCommands(vararg commands: BaseCommand) = commands.forEach(commandManager::registerCommand)
    private fun registerCompletion() {
        commandManager.registerSuggestion(SuggestionKey.of("worlds")) { _, _ ->
            return@registerSuggestion Bukkit.getWorlds().map(World::getName)
        }

        commandManager.registerSuggestion(SuggestionKey.of("materials")) { _, _ ->
            return@registerSuggestion Material.values().map { material -> material.name.lowercase() }
        }
    }
    private fun registerMessage() {
        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION) { sender, _ ->
            settingsManager.messages.noPermission.msg(sender)
        }

        commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY) { sender, _ ->
            settingsManager.messages.consoleOnly.msg(sender)
        }

        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND) { sender, _ ->
            settingsManager.messages.wrongUsage.msg(sender)
        }

        commandManager.registerMessage(MessageKey.INVALID_ARGUMENT) { sender, context ->
            when {
                context.argumentType == World::class.java ->
                    settingsManager.messages.wrongWorld.msg(sender)

                context.argumentType == Double::class.java && context.name == "radius" ->
                    settingsManager.messages.wrongRadius.msg(sender)

                context.argumentType == Double::class.java &&
                        (context.name == "x" || context.name == "y" || context.name == "z") ->
                    settingsManager.messages.wrongCoords.msg(sender)

                context.argumentType == Int::class.java && context.name == "amount" ->
                    settingsManager.messages.wrongAmount.msg(sender)

                else -> settingsManager.messages.wrongUsage.msg(sender)
            }
        }

        commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS) { sender, _ ->
            settingsManager.messages.wrongUsage.msg(sender)
        }

        commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS) { sender, _ ->
            settingsManager.messages.wrongUsage.msg(sender)
        }
    }

    private fun fetchEconomy(): Economy? {
        return Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)?.provider
    }
}