package com.blitzoffline.giveall.settings

import com.blitzoffline.giveall.GiveAll
import java.io.File

class SettingsManager(dataFolder: File, plugin: GiveAll) {
    private val factory = SettingsFactory(dataFolder, plugin)

    var settings = factory.settings()
        private set

    var messages = factory.messages()
        private set

    fun reload() {
        settings = factory.settings()
        messages = factory.messages()
    }
}