package com.blitzoffline.giveall.settings.holder

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.settings.SettingsManager
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentInfo
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentInfoParser
import dev.jorel.commandapi.arguments.StringArgument
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.World

class ArgumentsHolder(private val settingsManager: SettingsManager) {
    val worldArgument: Argument<World> = CustomArgument(
        StringArgument("world"),
        CustomArgumentInfoParser { info: CustomArgumentInfo<String> ->
            return@CustomArgumentInfoParser Bukkit.getWorld(info.input)
                ?: throw CustomArgumentException(
                    PlainTextComponentSerializer.plainText().serialize(
                        MiniMessage.miniMessage().deserialize(
                            settingsManager.messages.wrongWorld,
                            Placeholder.unparsed("wrong_value", info.currentInput)
                        )
                    )
                )
        }
    ).replaceSuggestions(settingsManager.suggestions.worldSuggestions)

    val materialArgument: Argument<String> = StringArgument("material")
        .replaceSuggestions(settingsManager.suggestions.materialSuggestions)
}