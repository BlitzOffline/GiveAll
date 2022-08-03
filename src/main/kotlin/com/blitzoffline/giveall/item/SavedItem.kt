package com.blitzoffline.giveall.item

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder

data class SavedItem(
    val name: String,
    val displayName: String,
    private val backingItemStack: ItemStack
) {
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
                dataOutput.writeObject(item.itemStack)
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
}
