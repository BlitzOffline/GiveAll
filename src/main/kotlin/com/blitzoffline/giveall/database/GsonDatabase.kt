package com.blitzoffline.giveall.database

import com.blitzoffline.giveall.GiveAll
import com.blitzoffline.giveall.item.SavedItem
import com.google.gson.GsonBuilder
import com.google.gson.JsonIOException
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.util.logging.Level

class GsonDatabase(private val plugin: GiveAll) : Database {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val file = plugin.dataFolder.resolve("saved-items.json")
    private val token = object : TypeToken<HashMap<String, String>>() {}.type

    init {
        if (file.createNewFile())
            plugin.logger.info("Successfully created the saved-items.json file.")
    }

    override fun loadItems(): Map<String, SavedItem> {
        try {
            if (file.length() == 0L) return hashMapOf()
            val serializedItems: HashMap<String, String> = gson.fromJson(file.readText(), token)

            plugin.savedItemsManager.clear()
            serializedItems.forEach { (_, serializedItem) ->
                plugin.savedItemsManager.addItem(
                    savedItem = SavedItem.fromBase64(serializedItem),
                    force = false
                )
            }
        } catch (exception: IOException) {
            plugin.logger.log(Level.SEVERE, "Could not deserialize the item stacks!", exception)
        }

        return plugin.savedItemsManager.clone()
    }

    override fun saveItems(map: Map<String, SavedItem>) {
        try {
            val serializedItems = hashMapOf<String, String>()
            map.forEach { (name, item) ->
                serializedItems[name] = SavedItem.toBase64(item)
            }

            file.writeText(gson.toJson(serializedItems))
        } catch (exception: JsonIOException) {
            plugin.logger.log(Level.SEVERE, "Could not save the item stacks!", exception)
        } catch (exception: IOException) {
            plugin.logger.log(Level.SEVERE, "Could not serialize the item stacks!", exception)
        }
    }
}