package com.blitzoffline.giveall.item

import com.blitzoffline.giveall.pagination.Paginable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class SavedItemsManager : Paginable {
    private val items = hashMapOf<String, SavedItem>()

    /**
     * Check if the backing map contains an item stack associated with a name
     *
     * @param name the name associated with the item stack
     * @return true if the map contains an item stack associated with the specified key, false otherwise
     */
    fun contains(name: String) = items.contains(name.lowercase())

    /**
     * Get an item stack associated with a name
     *
     * @param name the name associated with the item stack
     * @return the item stack associated with the name if there is one, null otherwise
     */
    fun getItem(name: String) = items[name.lowercase()]?.clone()

    /**
     * Add an item stack to the map
     *
     * @param name the name associated with the item stack
     * @param itemStack the item stack that will be added
     * @param force if this is true and there's already an item stack associated with that name it will be replaced
     * otherwise the item stack will be placed in the map only if there isn't any item associated with the name
     */
    fun addItem(name: String, displayName: String, itemStack: ItemStack, force: Boolean = false): Boolean {
        return addItem(SavedItem(name, displayName, itemStack), force)
    }

    fun addItem(savedItem: SavedItem, force: Boolean): Boolean {
        validateName(savedItem.name)
        if (contains(savedItem.name) && !force) return false
        items[savedItem.name.lowercase()] = savedItem.clone()
        return true
    }

    /**
     * Remove an item stack based on its name
     *
     * @param name the name associated with the item stack
     * @return the item stack that was removed if there was one and null if there was no item stack associated with that
     * name
     */
    fun removeItem(name: String) = items.remove(name.lowercase())

    /**
     * Clear the backing map
     */
    fun clear() = items.clear()

    /**
     * Clone the backing map
     *
     * @return an immutable copy of the backing map
     */
    fun clone() = items.toMap()

    fun getItemOrMaterial(name: String, amount: Int = -1): SavedItem? {
        if (contains(name)) return getItem(name)
        val material = Material.matchMaterial(name) ?: return null
        return if (amount < 1)
            SavedItem(name, name.lowercase(), ItemStack(material, material.maxStackSize))
        else
            SavedItem(name, name.lowercase(), ItemStack(material, amount))
    }

    companion object {
        @Throws(IllegalArgumentException::class)
        fun validateName(name: String) {
            if (name.all { c -> c.isLetterOrDigit() || c == '_' }) return
            throw IllegalArgumentException("Invalid name: $name. Must match: [a-zA-Z0-9_]")
        }
    }

    override fun getPageSize(): Int {
        return 5
    }

    override fun getItemsCount(): Int {
        return items.count()
    }

    override fun getItems(): List<Any> {
        return items.values.map { it.clone() }
    }

    override fun getPageDisplay(pageNumber: Int): Component {
        if (pageNumber < 1 || pageNumber > getPageCount()) return Component.empty()
        val items = getPageItems(pageNumber)

        return Component.join(
            JoinConfiguration.newlines(),
            items.map { it as SavedItem }
                .map { it.format() }
                .mapIndexed { index, component ->
                    Component.text("${(pageNumber - 1) * getPageSize() + index + 1}.", NamedTextColor.GOLD)
                        .append(Component.space())
                        .append(component)
                }
        )
    }

    override fun getFullPageDisplay(pageNumber: Int): Component {
        if (pageNumber < 1 || pageNumber > getPageCount()) return Component.empty()
        val title = Component.empty()
            .append(Component.space())
            .append(Component.space())
            .append(Component.text("--- Page $pageNumber/${getPageCount()} ---", NamedTextColor.GREEN))

        val page = getPageDisplay(pageNumber)
        if (page == Component.empty()) return Component.empty()


        val footer = when {
            pageNumber == 1 && getPageCount() == 1 -> Component.empty()
            pageNumber == 1 -> Component.empty()
                .append(Component.space())
                .append(Component.space())
                .append(
                    Component.text("Next »", NamedTextColor.GREEN)
                        .clickEvent(
                            ClickEvent.runCommand("/giveall special-item list ${pageNumber + 1}")
                        ))
            pageNumber == getPageCount() -> Component.empty()
                .append(Component.space())
                .append(Component.space())
                .append(
                    Component.text("« Previous", NamedTextColor.GREEN)
                        .clickEvent(
                            ClickEvent.runCommand("/giveall special-item list ${pageNumber - 1}")
                        ))
            else -> Component.empty()
                .append(Component.space())
                .append(Component.space())
                .append(
                    Component.text("« Previous", NamedTextColor.GREEN)
                        .clickEvent(
                            ClickEvent.runCommand("/giveall special-item list ${pageNumber - 1}")
                        ))
                .append(Component.space())
                .append(Component.text("|", NamedTextColor.GRAY))
                .append(Component.space())
                .append(
                    Component.text("Next »", NamedTextColor.GREEN)
                        .clickEvent(
                            ClickEvent.runCommand("/giveall special-item list ${pageNumber + 1}")
                        ))
        }

        if (footer == Component.empty()) return Component.empty()
            .append(Component.newline())
            .append(title)
            .append(Component.newline())
            .append(Component.newline())
            .append(page)

        return Component.empty()
            .append(Component.newline())
            .append(title)
            .append(Component.newline())
            .append(Component.newline())
            .append(page)
            .append(Component.newline())
            .append(Component.newline())
            .append(footer)
    }
}