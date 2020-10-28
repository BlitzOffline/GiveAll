package me.blitzgamer_88.giveall.util

import ch.jalu.configme.SettingsManager
import me.blitzgamer_88.giveall.GiveAll
import me.blitzgamer_88.giveall.conf.GiveAllConfiguration

private var conf = null as? SettingsManager?

fun loadConfig(plugin: GiveAll) {
    val file = plugin.dataFolder.resolve("config.yml")
    if (!file.exists()) {
        file.parentFile.mkdirs()
        file.createNewFile()
    }
    conf = GiveAllConfiguration(file)
}

fun conf(): SettingsManager {
    return checkNotNull(conf)
}