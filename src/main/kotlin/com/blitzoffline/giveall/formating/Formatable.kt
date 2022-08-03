package com.blitzoffline.giveall.formating

import net.kyori.adventure.text.Component

interface Formattable {
    fun format(): Component
}