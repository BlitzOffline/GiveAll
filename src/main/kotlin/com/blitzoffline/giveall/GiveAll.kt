package com.blitzoffline.giveall

import com.blitzoffline.giveall.command.CommandHand
import com.blitzoffline.giveall.command.CommandHelp
import com.blitzoffline.giveall.command.CommandItem
import com.blitzoffline.giveall.command.CommandMoney
import com.blitzoffline.giveall.command.CommandRadius
import com.blitzoffline.giveall.command.CommandReload
import com.blitzoffline.giveall.command.CommandWorld
import com.blitzoffline.giveall.settings.SettingsManager
import com.blitzoffline.giveall.util.adventure
import com.blitzoffline.giveall.util.msg
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
import org.spongepowered.configurate.CommentedConfigurationNode

class GiveAll : JavaPlugin() {
    lateinit var settings: CommentedConfigurationNode
        private set
    lateinit var messages: CommentedConfigurationNode
        private set

    lateinit var econ: Economy
        private set
    private var hooked = false

    private lateinit var commandManager: CommandManager
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
            if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
                logger.warning("Could not find Vault! If you don't want to use vault, disable the hook from config.yml!")
                pluginLoader.disablePlugin(this)
            }
            econ = fetchEconomy() ?: run {
                logger.warning("Could not load the economy. Make sure vault is installed and working!")
                pluginLoader.disablePlugin(this)
                return
            }
            hooked = true
        }

        commandManager = CommandManager(this, true)
        registerMessage("cmd.no.permission") { sender ->
            messages.node("NO-PERMISSION").getString("&cError: &7You don''t have permission to do that!").msg(sender)
        }
        registerMessage("cmd.wrong.usage") { sender ->
            messages.node("WRONG-USAGE").getString("&cWrong usage! Use: &e/giveall help&c to get help.").msg(sender)
        }

        registerCompletion("#worlds") { Bukkit.getWorlds().map(World::getName) }
        registerCompletion("#materials") { Material.values().map { material -> material.name.lowercase() } }

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

    fun saveDefaultFile(fileName: String) {
        if (dataFolder.resolve(fileName).exists()) return
        saveResource(fileName, false)
    }

    fun loadSettings() {
        settings = settingsManager.loadSettings("config.yml")
        messages = settingsManager.loadSettings("messages.yml")
    }

    private fun registerCommands(vararg commands: CommandBase) = commands.forEach(commandManager::register)
    private fun registerCompletion(completionId: String, resolver: CompletionResolver) = commandManager.completionHandler.register(completionId, resolver)
    private fun registerMessage(messageId: String, resolver: MessageResolver) = commandManager.messageHandler.register(messageId, resolver)

    private fun fetchEconomy(): Economy? {
        if (Bukkit.getServer().pluginManager.getPlugin("Vault") == null) return null
        val rsp = Bukkit.getServer().servicesManager.getRegistration(Economy::class.java) ?: return null
        return rsp.provider
    }
}