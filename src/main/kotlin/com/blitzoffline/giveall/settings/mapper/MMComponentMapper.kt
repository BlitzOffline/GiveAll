package com.blitzoffline.giveall.settings.mapper

import java.lang.reflect.Type
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer

class MMComponentMapper : TypeSerializer<Component> {
    private val miniMessage = MiniMessage.miniMessage()

    @Throws(SerializationException::class)
    override fun deserialize(type: Type, node: ConfigurationNode): Component {
        val key = node.key() ?: throw SerializationException("A config key cannot be null! " + node.path())
        val value = node.string ?: throw SerializationException("No value was given to $key")

        return miniMessage.deserialize(value)
    }

    @Throws(SerializationException::class)
    override fun serialize(type: Type, component: Component?, target: ConfigurationNode) {
        if (component == null) {
            target.raw(null)
            return
        }

        target.set(miniMessage.serialize(component))
    }
}