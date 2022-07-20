package com.blitzoffline.giveall.util

import org.bukkit.Location
import org.bukkit.util.BoundingBox

fun calculateBoundingBox(location: Location, radius: Double): BoundingBox {
    val hypotenuse = kotlin.math.sqrt(2 * radius * radius)
    return BoundingBox(
        location.x-hypotenuse,
        location.y-radius, location.z-hypotenuse,
        location.x+hypotenuse,
        location.y+radius,
        location.z+hypotenuse
    )
}