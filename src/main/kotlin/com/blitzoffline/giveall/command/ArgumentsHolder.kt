package com.blitzoffline.giveall.command

import com.blitzoffline.giveall.GiveAll
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

class ArgumentsHolder(private val plugin: GiveAll) {
    val worldArgument: Argument<World> = CustomArgument(
        StringArgument("world"),
        CustomArgumentInfoParser { info: CustomArgumentInfo<String> ->
            return@CustomArgumentInfoParser Bukkit.getWorld(info.input)
                ?: throw CustomArgumentException(
                    PlainTextComponentSerializer.plainText().serialize(
                        MiniMessage.miniMessage().deserialize(
                            plugin.settingsManager.messages.wrongWorld,
                            Placeholder.unparsed("wrong_value", info.currentInput)
                        )
                    )
                )
        }
    ).replaceSuggestions(plugin.suggestionsHolder.worldSuggestions)

    val materialArgument: Argument<String> = StringArgument("material")
        .replaceSuggestions(plugin.suggestionsHolder.materialSuggestions)
}