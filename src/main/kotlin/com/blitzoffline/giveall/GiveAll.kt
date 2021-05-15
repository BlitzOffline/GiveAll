package com.blitzoffline.giveall

import com.blitzoffline.giveall.command.CommandHand
import com.blitzoffline.giveall.command.CommandHelp
import com.blitzoffline.giveall.command.CommandItem
import com.blitzoffline.giveall.command.CommandMoney
import com.blitzoffline.giveall.command.CommandRadius
import com.blitzoffline.giveall.command.CommandReload
import com.blitzoffline.giveall.command.CommandWorld
import com.blitzoffline.giveall.config.holder.Messages
import com.blitzoffline.giveall.config.holder.Settings
import com.blitzoffline.giveall.config.loadConfig
import com.blitzoffline.giveall.config.loadMessages
import com.blitzoffline.giveall.config.messages
import com.blitzoffline.giveall.config.settings
import com.blitzoffline.giveall.config.setupEconomy
import com.blitzoffline.giveall.util.adventure
import com.blitzoffline.giveall.util.log
import com.blitzoffline.giveall.util.msg
import me.mattstudios.mf.base.CommandBase
import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.components.CompletionResolver
import me.mattstudios.mf.base.components.MessageResolver
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin

class GiveAll : JavaPlugin() {
    private lateinit var commandManager: CommandManager

    override fun onEnable() {
        adventure = BukkitAudiences.create(this)

        loadConfig(this)
        loadMessages(this)

        checkDepend("PlaceholderAPI")
        if (settings[Settings.HOOKS_VAULT]) checkDepend("Vault")

        commandManager = CommandManager(this, true)
        registerCommands(
            CommandHand(),
            CommandHelp(),
            CommandItem(),
            CommandMoney(),
            CommandRadius(),
            CommandReload(),
            CommandWorld()
        )
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
        "[GiveAll] Plugin enabled successfully!".log()
    }

    override fun onDisable() = "[GiveAll] Plugin disabled successfully!".log()

    private fun registerCommands(vararg commands: CommandBase) = commands.forEach(commandManager::register)
    private fun registerCompletion(completionId: String, resolver: CompletionResolver) = commandManager.completionHandler.register(completionId, resolver)
    private fun registerMessage(messageId: String, resolver: MessageResolver) = commandManager.messageHandler.register(messageId, resolver)

    private fun checkDepend(plugin: String) {
        if (Bukkit.getPluginManager().getPlugin(plugin) == null) {
            "[GiveAll] Could not find $plugin! This plugin is required".log()
            Bukkit.getPluginManager().disablePlugin(this)
        }
        if (plugin == "Vault" && !setupEconomy()) {
            "[GiveAll] Something went wrong while setting up the economy".log()
            pluginLoader
            Bukkit.getPluginManager().disablePlugin(this)
        }
    }

    fun saveDefaultMessages() {
        if (dataFolder.resolve("messages.yml").exists()) return
        saveResource("messages.yml", false)
    }
}