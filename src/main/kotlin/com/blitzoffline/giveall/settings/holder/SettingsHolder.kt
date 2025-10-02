package com.blitzoffline.giveall.settings.holder

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class SettingsHolder(
    val giveRewardsToSender: Boolean = false,
    val requirePermission: Boolean = false,
    val hooks: Map<String, Boolean> = mapOf("vault" to true),
    val ipMode: IpMode = IpMode.ALL
)
