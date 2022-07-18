package com.blitzoffline.giveall.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder

/**
 *
 * A method to serialize an [ItemStack] to Base64 String.
 *
 * @param itemStack to turn into a Base64 String.
 * @return Base64 string of the item.
 * @throws IllegalStateException
 */
@Throws(IllegalStateException::class)
fun itemStackToBase64(itemStack: ItemStack): String {
    try {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)
        dataOutput.writeObject(itemStack)
        dataOutput.close()
        return Base64Coder.encodeLines(outputStream.toByteArray())
    } catch (e: Exception) {
        throw IllegalStateException("Unable to save item stacks.", e)
    }
}

/**
 * Gets an [ItemStack] from Base64 string.
 *
 * @param data Base64 string to convert to ItemStack.
 * @return ItemStack created from the Base64 string.
 * @throws IOException
 */
@Throws(IOException::class)
fun itemStackFromBase64(data: String): ItemStack {
    try {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
        val dataInput = BukkitObjectInputStream(inputStream)
        val item = dataInput.readObject() as ItemStack
        dataInput.close()
        return item
    } catch (e: ClassNotFoundException) {
        throw IOException("Unable to decode class type.", e)
    }
}