package com.blitzoffline.giveall.settings

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.settings.holder.ArgumentsHolder
import com.blitzoffline.giveall.settings.holder.SuggestionsHolder
import java.io.File

class SettingsManager(private val plugin: GiveAll, dataFolder: File) {
    private val factory = SettingsFactory(dataFolder, plugin)

    var suggestions = SuggestionsHolder()
        private set

    var arguments = ArgumentsHolder(plugin)
        private set

    var settings = factory.settings()
        private set

    var messages = factory.messages()
        private set

    fun reload() {
        suggestions = SuggestionsHolder()
        arguments = ArgumentsHolder(plugin)
        settings = factory.settings()
        messages = factory.messages()
    }
}