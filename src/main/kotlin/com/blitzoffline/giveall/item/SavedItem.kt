package com.blitzoffline.giveall.item

import com.blitzoffline.giveall.formating.Formattable
import com.blitzoffline.giveall.util.VersionHelper
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Locale
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder

data class SavedItem(
    val name: String,
    val displayName: String,
    private val backingItemStack: ItemStack
) : Formattable {
    val itemStack: ItemStack
        get() = backingItemStack.clone()

    fun clone() = SavedItem(name, displayName, backingItemStack.clone())

    companion object {
        @Throws(IllegalStateException::class)
        fun toBase64(item: SavedItem): String {
            try {
                val outputStream = ByteArrayOutputStream()
                val dataOutput = BukkitObjectOutputStream(outputStream)
                dataOutput.writeObject(item.name)
                dataOutput.writeObject(item.displayName)
                dataOutput.writeObject(item.backingItemStack)
                dataOutput.close()
                return Base64Coder.encodeLines(outputStream.toByteArray())
            } catch (exception: Exception) {
                throw IllegalStateException("Unable to serialize item to base64.", exception)
            }
        }

        @Throws(IOException::class)
        fun fromBase64(data: String): SavedItem {
            try {
                val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
                val dataInput = BukkitObjectInputStream(inputStream)
                val name = dataInput.readObject() as String
                val displayName = dataInput.readObject() as String
                val item = dataInput.readObject() as ItemStack
                dataInput.close()
                return SavedItem(name, displayName, item)
            } catch (exception: ClassNotFoundException) {
                throw IOException("Unable to decode item from base64.", exception)
            }
        }
    }

    override fun format(): Component {
        return Component.empty()
            .append(Component.text(name, NamedTextColor.YELLOW))
            .append(Component.text(":", NamedTextColor.GRAY))
            .append(Component.space())
            .append(createItemComponent())
    }

    private fun createItemComponent(): Component {
        val displayComponent = MiniMessage.miniMessage().deserialize(displayName)
        val materialInfo = Component.empty()
            .append(Component.text(
                "${itemStack.type.name.lowercase()} x ${backingItemStack.amount}",
                NamedTextColor.GRAY
            ))

        val materialName = if (VersionHelper.IS_PAPER)
            Component.translatable(backingItemStack.type.translationKey())
        else {
            Component.text(
                backingItemStack.type.name
                    .lowercase()
                    .split("_")
                    .joinToString(" ") { word ->
                        word.replaceFirstChar { char ->
                            if (char.isLowerCase()) char.titlecase(Locale.getDefault())
                            else char.toString()
                        }
                    }
            )
        }

        if (!backingItemStack.hasItemMeta()) {
            return displayComponent.hoverEvent(materialName
                .append(Component.newline())
                .append(Component.newline())
                .append(materialInfo)
            )
        }

        val meta = backingItemStack.itemMeta
            ?: return displayComponent.hoverEvent(materialName
                .append(Component.newline())
                .append(Component.newline())
                .append(materialInfo)
            )

        val name =
            if (meta.hasDisplayName()) LegacyComponentSerializer.legacyAmpersand().deserialize(meta.displayName)
            else materialName

        val enchants =
            if (meta.hasEnchants() && !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS))
                meta.enchants.entries.map { formattedEnchantment(it.key, it.value).color(NamedTextColor.GRAY) }
            else
                emptyList()

        val lore = meta.lore
            ?.map { LegacyComponentSerializer.legacyAmpersand().deserialize(it) }
            ?.map { if (it.hasStyling()) it else it.color(NamedTextColor.DARK_PURPLE) }
            ?: emptyList()

        val builder = Component.text()

        builder.append(name)
        builder.append(Component.newline())

        if (enchants.isNotEmpty()) {
            builder.append(Component.join(JoinConfiguration.newlines(), *enchants.toTypedArray()))
            builder.append(Component.newline())
        }

        if (lore.isNotEmpty()) {
            builder.append(Component.join(JoinConfiguration.newlines(), *lore.toTypedArray()))
            builder.append(Component.newline())
        }

        builder.append(Component.newline())
        builder.append(materialInfo)


        return displayComponent.hoverEvent(builder.build())
    }

    private fun formattedEnchantment(enchantment: Enchantment?, level: Int?): Component {
        if (enchantment == null) {
            return Component.empty()
        }

        val isVanilla = enchantment.key.namespace == NamespacedKey.MINECRAFT
        val enchantmentName: Component =
            if (isVanilla) Component.translatable("enchantment.minecraft." + enchantment.key.key)
            else Component.text(enchantment.name)

        if (enchantment.maxLevel == 1) {
            return enchantmentName
        }
        if (level == null) {
            return enchantmentName.append(Component.text(" I"))
        }

        val roman = when (level) {
            1 -> "I"
            2 -> "II"
            3 -> "III"
            4 -> "IV"
            5 -> "V"
            else -> level.toString()
        }
        return enchantmentName.append(Component.space()).append(Component.text(roman))
    }
}
