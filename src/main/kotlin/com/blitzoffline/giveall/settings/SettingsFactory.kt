package com.blitzoffline.giveall.settings

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.settings.holder.MessagesHolder
import com.blitzoffline.giveall.settings.holder.SettingsHolder
import com.blitzoffline.giveall.settings.mapper.MMComponentMapper
import java.io.File
import java.io.IOException
import java.util.Objects
import net.kyori.adventure.text.Component
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.hocon.HoconConfigurationLoader

class SettingsFactory(private val dataFolder: File, private val plugin: GiveAll) {
    init {
        if (!dataFolder.exists()) dataFolder.mkdirs()
        if (!dataFolder.isDirectory) throw RuntimeException("The data folder needs to be a folder...")
    }

    fun settings(): SettingsHolder {
        val config = create(SettingsHolder::class.java, "settings.conf")
        return Objects.requireNonNullElseGet(config) { SettingsHolder() }
    }

    fun messages(): MessagesHolder {
        val config = create(MessagesHolder::class.java, "messages.conf")
        return Objects.requireNonNullElseGet(config) { MessagesHolder() }
    }

    private fun <T> create(clazz: Class<T>, fileName: String): T? {
        try {
            dataFolder.mkdirs()

            val file = dataFolder.resolve(fileName)
            if (!file.exists()) {
                plugin.saveDefaultFile(fileName)
            }
            val loader = loader(file)
            val node = loader.load()
            val config = node.get(clazz)

            if (!file.exists()) {
                file.createNewFile()
                node.set(clazz, config)
            }

            loader.save(node)
            return config
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
        return null
    }

    private fun loader(file: File): HoconConfigurationLoader {
        return HoconConfigurationLoader.builder()
            .file(file)
            .defaultOptions { options: ConfigurationOptions ->
                options.shouldCopyDefaults(true)
                    .serializers { it.register(Component::class.java, MMComponentMapper()) }
            }
            .build()
    }
}