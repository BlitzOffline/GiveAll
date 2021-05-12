package com.blitzoffline.giveall.config.holder

import me.mattstudios.config.SettingsHolder
import me.mattstudios.config.annotations.Path
import me.mattstudios.config.properties.Property

object Settings : SettingsHolder {
    @Path("give-rewards-to-sender")
    val GIVE_REWARDS_TO_SENDER = Property.create(false)

    @Path("requires-permission")
    val REQUIRES_PERMISSION = Property.create(false)

    @Path("hooks.vault")
    val HOOKS_VAULT = Property.create(true)
}