package com.blitzoffline.giveall

import com.blitzoffline.giveall.command.CommandHand
import com.blitzoffline.giveall.command.CommandHelp
import com.blitzoffline.giveall.command.CommandItem
import com.blitzoffline.giveall.command.CommandMoney
import com.blitzoffline.giveall.command.CommandRadius
import com.blitzoffline.giveall.command.CommandReload
import com.blitzoffline.giveall.command.CommandWorld
import com.blitzoffline.giveall.command.CommandXp
import com.blitzoffline.giveall.settings.SettingsManager
import com.blitzoffline.giveall.util.adventure
import com.blitzoffline.giveall.util.msg
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
import org.spongepowered.configurate.CommentedConfigurationNode

// TODO: 3/6/22 Refactor the settings and messages
class GiveAll : JavaPlugin() {
    lateinit var settings: CommentedConfigurationNode
        private set
    lateinit var messages: CommentedConfigurationNode
        private set

    lateinit var econ: Economy
        private set
    private var hooked = false

    private lateinit var commandManager: BukkitCommandManager<CommandSender>
    private lateinit var settingsManager: SettingsManager

    override fun onEnable() {
        adventure = BukkitAudiences.create(this)

        settingsManager = SettingsManager(this, dataFolder)
        loadSettings()

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            logger.warning("Could not find PlaceholderAPI! This plugin is required!")
            pluginLoader.disablePlugin(this)
        }

        val hooksNode = settings.node("hooks")

        if (hooksNode.node("vault").getBoolean(true)) {
            val vault = Bukkit.getPluginManager().getPlugin("Vault")
            if (vault == null) {
                logger.warning("Could not find Vault! If you don't want to use it, disable the hook from config.yml!")
                pluginLoader.disablePlugin(this)
                return
            }
            if (!vault.isEnabled) {
                logger.warning("Vault is disabled! If you don't want to use it, disable the hook from config.yml!")
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
            CommandHand(this),
            CommandHelp(),
            CommandItem(this),
            CommandRadius(this),
            CommandReload(this),
            CommandWorld(this),
            CommandXp(this)
        )

        if (hooked) {
            registerCommands(
                CommandMoney(this)
            )
        }
        logger.info("Plugin enabled successfully!")
    }

    override fun onDisable() = logger.info("Plugin disabled successfully!")

    fun saveDefaultFile(fileName: String) {
        if (dataFolder.resolve(fileName).exists()) return
        saveResource(fileName, false)
    }

    fun loadSettings() {
        settings = settingsManager.loadSettings("config.yml")
        messages = settingsManager.loadSettings("messages.yml")
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
            messages.node("NO-PERMISSION").getString("&cError: &7You don''t have permission to do that!").msg(sender)
        }

        commandManager.registerMessage(MessageKey.INVALID_ARGUMENT) { sender, _ ->
            messages.node("WRONG-USAGE").getString("&cWrong usage! Use: &e/giveall help&c to get help.").msg(sender)
        }

        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND) { sender, _ ->
            messages.node("WRONG-USAGE").getString("&cWrong usage! Use: &e/giveall help&c to get help.").msg(sender)
        }

        commandManager.registerMessage(MessageKey.INVALID_FLAG_ARGUMENT) { sender, _ ->
            messages.node("WRONG-USAGE").getString("&cWrong usage! Use: &e/giveall help&c to get help.").msg(sender)
        }

        commandManager.registerMessage(MessageKey.MISSING_REQUIRED_FLAG) { sender, _ ->
            messages.node("WRONG-USAGE").getString("&cWrong usage! Use: &e/giveall help&c to get help.").msg(sender)
        }

        commandManager.registerMessage(MessageKey.MISSING_REQUIRED_FLAG_ARGUMENT) { sender, _ ->
            messages.node("WRONG-USAGE").getString("&cWrong usage! Use: &e/giveall help&c to get help.").msg(sender)
        }

        commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS) { sender, _ ->
            messages.node("WRONG-USAGE").getString("&cWrong usage! Use: &e/giveall help&c to get help.").msg(sender)
        }
        commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS) { sender, _ ->
            messages.node("WRONG-USAGE").getString("&cWrong usage! Use: &e/giveall help&c to get help.").msg(sender)
        }

    }

    private fun fetchEconomy(): Economy? {
        return Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)?.provider
    }
}