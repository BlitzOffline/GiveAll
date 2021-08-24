package com.blitzoffline.giveall

import com.blitzoffline.giveall.command.CommandHand
import com.blitzoffline.giveall.command.CommandHelp
import com.blitzoffline.giveall.command.CommandItem
import com.blitzoffline.giveall.command.CommandMoney
import com.blitzoffline.giveall.command.CommandRadius
import com.blitzoffline.giveall.command.CommandReload
import com.blitzoffline.giveall.command.CommandWorld
import com.blitzoffline.giveall.config.ConfigHandler
import com.blitzoffline.giveall.config.holder.Messages
import com.blitzoffline.giveall.config.holder.Settings
import com.blitzoffline.giveall.util.adventure
import com.blitzoffline.giveall.util.msg
import me.mattstudios.config.SettingsManager
import me.mattstudios.mf.base.CommandBase
import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.components.CompletionResolver
import me.mattstudios.mf.base.components.MessageResolver
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin

class GiveAll : JavaPlugin() {
    lateinit var settings: SettingsManager
        private set
    lateinit var messages: SettingsManager
        private set
    lateinit var econ: Economy
        private set
    private var hooked = false

    private lateinit var commandManager: CommandManager
    private lateinit var configHandler: ConfigHandler

    override fun onEnable() {
        adventure = BukkitAudiences.create(this)

        configHandler = ConfigHandler(this)

        settings = configHandler.fetchSettings()
        messages = configHandler.fetchMessages()

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            logger.warning("Could not find PlaceholderAPI! This plugin is required!")
            pluginLoader.disablePlugin(this)
        }
        if (settings[Settings.HOOKS_VAULT]) {
            if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
                logger.warning("Could not find Vault! This plugin is required!")
                pluginLoader.disablePlugin(this)
            }
            econ = configHandler.fetchEconomy() ?: run {
                logger.warning("Could not find Vault! This plugin is required!")
                pluginLoader.disablePlugin(this)
                return
            }
            hooked = true
        }

        commandManager = CommandManager(this, true)
        registerMessage("cmd.no.permission") { sender -> messages[Messages.NO_PERMISSION].msg(sender) }
        registerMessage("cmd.wrong.usage") { sender -> messages[Messages.WRONG_USAGE].msg(sender) }

        registerCompletion("#worlds") { Bukkit.getWorlds().map(World::getName) }
        registerCompletion("#materials") {
            Material.values().map { value -> value
                .name
                .lowercase()
                .replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else it.toString() }
            }
        }

        registerCommands(
            CommandHand(this),
            CommandHelp(),
            CommandItem(this),
            CommandRadius(this),
            CommandReload(this),
            CommandWorld(this)
        )
        if (hooked) {
            registerCommands(
                CommandMoney(this)
            )
        }
        logger.info("Plugin enabled successfully!")
    }

    override fun onDisable() = logger.info("Plugin disabled successfully!")

    private fun registerCommands(vararg commands: CommandBase) = commands.forEach(commandManager::register)
    private fun registerCompletion(completionId: String, resolver: CompletionResolver) = commandManager.completionHandler.register(completionId, resolver)
    private fun registerMessage(messageId: String, resolver: MessageResolver) = commandManager.messageHandler.register(messageId, resolver)

    fun saveDefaultMessages() {
        if (dataFolder.resolve("messages.yml").exists()) return
        saveResource("messages.yml", false)
    }
}