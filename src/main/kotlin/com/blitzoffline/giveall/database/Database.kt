package com.blitzoffline.giveall.database

import com.blitzoffline.giveall.item.SavedItem

interface Database {
    fun loadItems(): Map<String, SavedItem>
    fun saveItems(map: Map<String, SavedItem>)
}