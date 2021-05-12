package com.blitzoffline.giveall

import ch.jalu.configme.SettingsManager
import com.blitzoffline.giveall.command.CommandGiveAll
import com.blitzoffline.giveall.config.GiveAllConfiguration
import me.blitzgamer_88.giveall.util.*
import me.bristermitten.pdm.PDMBuilder
import me.mattstudios.mf.base.CommandBase
import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.components.CompletionResolver
import me.mattstudios.mf.base.components.MessageResolver
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin


class GiveAll : JavaPlugin() {
    lateinit var econ: Economy
    lateinit var conf: SettingsManager
    private lateinit var commandManager: CommandManager

    override fun onLoad() { PDMBuilder(this).build().loadAllDependencies().join() }

    override fun onEnable() {
        this.saveDefaultConfig()

        checkDepend("PlaceholderAPI")
        checkDepend("Vault")

        setupEconomy()
        loadConfig(this)
        registerValues(this)

        commandManager = CommandManager(this, true)
        registerCommands(CommandGiveAll(this))
        registerMessage("cmd.no.permission") { sender -> noPermission.msg(sender) }
        registerMessage("cmd.wrong.usage") { sender -> wrongUsage.msg(sender) }

        registerCompletion("#worlds") {Bukkit.getWorlds().map(World::getName)}
        registerCompletion("#materials") {Material.values().map{it.name.toLowerCase().capitalize()}}

        "[GiveAll] Plugin enabled successfully!".log()
    }

    private fun registerCommands(vararg commands: CommandBase) = commands.forEach(commandManager::register)
    private fun registerCompletion(completionId: String, resolver: CompletionResolver) = commandManager.completionHandler.register(completionId, resolver)
    private fun registerMessage(messageId: String, resolver: MessageResolver) = commandManager.messageHandler.register(messageId, resolver)

    private fun checkDepend(plugin: String) {
        if (Bukkit.getPluginManager().getPlugin(plugin) == null) {
            "[GiveAll] Could not find $plugin! This plugin is required".log()
            Bukkit.getPluginManager().disablePlugin(this)
        }
    }

    fun reload() {
        conf.reload()
        registerValues(this)
        setupEconomy()
    }

    private fun loadConfig(plugin: GiveAll) {
        val file = plugin.dataFolder.resolve("config.yml")
        if (!file.exists()) this.saveDefaultConfig()
        conf = GiveAllConfiguration(file)
    }

    private fun setupEconomy() {
        val rsp = Bukkit.getServer().servicesManager.getRegistration(Economy::class.java) ?: return
        econ = rsp.provider
    }

    override fun onDisable() = "[GiveAll] Plugin disabled successfully!".log()
}