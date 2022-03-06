package com.blitzoffline.giveall.settings

import com.blitzoffline.giveall.GiveAll
import java.io.File
import java.io.IOException
import kotlin.system.exitProcess
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.yaml.YamlConfigurationLoader

class SettingsManager(private val plugin: GiveAll, private val dataFolder: File) {
    private val settingsLoaders = hashMapOf<String, YamlConfigurationLoader>()

    fun saveSettings(fileName: String, settings: CommentedConfigurationNode) {
        val settingsLoader = settingsLoaders[fileName] ?: createSettingsLoader(fileName)
        settingsLoader.save(settings)
    }

    fun loadSettings(fileName: String): CommentedConfigurationNode {
        val settingsLoader = settingsLoaders[fileName] ?: createSettingsLoader(fileName)

        try {
            return settingsLoader.load()
        } catch (ex: IOException) {
            plugin.logger.severe("An error occurred while loading a configuration file $fileName")
            ex.printStackTrace()
            exitProcess(1)
        }
    }

    private fun createSettingsLoader(fileName: String, override: Boolean = false): YamlConfigurationLoader {
        if (settingsLoaders.containsKey(fileName) && !override) {
            return settingsLoaders[fileName]!!
        }

        if (!dataFolder.resolve(fileName).exists()) {
            plugin.saveDefaultFile(fileName)
        }

        val settingsLoader = YamlConfigurationLoader.builder()
            .path(dataFolder.resolve(fileName).toPath())
            .build()

        settingsLoaders[fileName] = settingsLoader
        return settingsLoader
    }
}