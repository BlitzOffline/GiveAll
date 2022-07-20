package com.blitzoffline.giveall.task

import com.blitzoffline.giveall.GiveAll
import org.bukkit.scheduler.BukkitRunnable

class SaveDataTask(private val plugin: GiveAll) : BukkitRunnable() {
    override fun run() = plugin.database.saveItemStacks(plugin.savedItemsManager.clone())
}