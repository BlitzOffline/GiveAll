package com.blitzoffline.giveall.util

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox

fun calculateBoundingBox(location: Location, radius: Double): BoundingBox {
    val hypotenuse = kotlin.math.sqrt(2 * radius * radius)
    return BoundingBox(
        location.x-hypotenuse,
        location.y-radius,
        location.z-hypotenuse,
        location.x+hypotenuse,
        location.y+radius,
        location.z+hypotenuse
    )
}

fun getPlayersInRadius(sender: Player, radius: Double) = getPlayersInRadius(sender.location, radius)

fun getPlayersInRadius(center: Location, radius: Double): Collection<Player> {
    val boundingBox = calculateBoundingBox(center, radius)
    return center.world.getNearbyEntities(boundingBox)
        .filterIsInstance(Player::class.java)
        .toList()
}