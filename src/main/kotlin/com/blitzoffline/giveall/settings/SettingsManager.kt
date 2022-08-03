package com.blitzoffline.giveall.settings

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.settings.holder.ArgumentsHolder
import com.blitzoffline.giveall.settings.holder.SuggestionsHolder
import java.io.File

class SettingsManager(plugin: GiveAll, dataFolder: File) {
    private val factory = SettingsFactory(dataFolder, plugin)

    var settings = factory.settings()
        private set

    var messages = factory.messages()
        private set

    var suggestions = SuggestionsHolder()
        private set

    var arguments = ArgumentsHolder(this)
        private set

    fun reload() {
        settings = factory.settings()
        messages = factory.messages()
        suggestions = SuggestionsHolder()
        arguments = ArgumentsHolder(this)
    }
}