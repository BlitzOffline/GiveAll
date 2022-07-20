package com.blitzoffline.giveall.manager

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class SavedItemsManager {
    private val savedItems = hashMapOf<String, ItemStack>()

    /**
     * Check if the backing map contains an item stack associated with a name
     *
     * @param name the name associated with the item stack
     * @return true if the map contains an item stack associated with the specified key, false otherwise
     */
    fun contains(name: String) = savedItems.contains(name.lowercase())

    /**
     * Get an item stack associated with a name
     *
     * @param name the name associated with the item stack
     * @return the item stack associated with the name if there is one, null otherwise
     */
    fun getItemStack(name: String) = savedItems[name.lowercase()]?.clone()

    /**
     * Add an item stack to the map
     *
     * @param name the name associated with the item stack
     * @param itemStack the item stack that will be added
     * @param force if this is true and there's already an item stack associated with that name it will be replaced
     * otherwise the item stack will be placed in the map only if there isn't any item associated with the name
     */
    fun addItemStack(name: String, itemStack: ItemStack, force: Boolean = false): Boolean {
        if (contains(name) && !force) return false
        savedItems[name.lowercase()] = itemStack
        return true;
    }

    /**
     * Remove an item stack based on its name
     *
     * @param name the name associated with the item stack
     * @return the item stack that was removed if there was one and null if there was no item stack associated with that
     * name
     */
    fun removeItemStack(name: String) = savedItems.remove(name.lowercase())

    /**
     * Clear the backing map
     */
    fun clear() = savedItems.clear()

    /**
     * Clone the backing map
     *
     * @return an immutable copy of the backing map
     */
    fun clone() = savedItems.toMap()

    fun getSavedItemOrMaterial(name: String, amount: Int = -1): ItemStack? {
        if (contains(name)) return getItemStack(name)
        val material = Material.matchMaterial(name) ?: return null
        return if (amount < 1) ItemStack(material, material.maxStackSize) else ItemStack(material, amount)
    }
}