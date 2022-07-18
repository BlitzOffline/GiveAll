package com.blitzoffline.giveall.database

import org.bukkit.inventory.ItemStack

interface Database {
    fun loadItemStacks(): Map<String, ItemStack>
    fun saveItemStacks(map: Map<String, ItemStack>)
}