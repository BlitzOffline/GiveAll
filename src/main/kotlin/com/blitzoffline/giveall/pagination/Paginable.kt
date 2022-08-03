package com.blitzoffline.giveall.pagination

import kotlin.math.ceil
import net.kyori.adventure.text.Component

interface Paginable {
    fun getPageSize(): Int
    fun getItemsCount(): Int
    fun getItems(): List<Any>
    fun getPageDisplay(pageNumber: Int): Component
    fun getFullPageDisplay(pageNumber: Int): Component

    fun getPageItems(pageNumber: Int): List<Any> {
        val start = pageNumber * getPageSize() - getPageSize()
        val end = start + getPageSize()
        val items = getItems()

        if (start >= items.size) {
            return emptyList()
        }

        return items.subList(start, end.coerceAtMost(getItemsCount()))
    }

    fun getPageCount(): Int {
        return ceil(getItemsCount().toDouble() / getPageSize()).toInt()
    }
}