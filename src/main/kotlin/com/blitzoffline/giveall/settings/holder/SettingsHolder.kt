package com.blitzoffline.giveall.settings.holder

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
data class SettingsHolder(
    val giveRewardsToSender: Boolean = false,
    val requirePermission: Boolean = false,
    // TODO: 7/21/22 Fix node looking for the literal hooks.value instead of the hooks - value keys.
    @Setting(value = "hooks.vault") val vaultHook: Boolean = true,
)
