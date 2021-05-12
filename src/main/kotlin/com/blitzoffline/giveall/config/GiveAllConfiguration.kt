package com.blitzoffline.giveall.config

import ch.jalu.configme.SettingsManagerImpl
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder
import ch.jalu.configme.migration.PlainMigrationService
import ch.jalu.configme.resource.YamlFileResource
import java.io.File

class GiveAllConfiguration(file: File) : SettingsManagerImpl(
        YamlFileResource(file.toPath()),
        ConfigurationDataBuilder.createConfiguration(Config::class.java),
        PlainMigrationService()
    )