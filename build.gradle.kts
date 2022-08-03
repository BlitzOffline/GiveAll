import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.7.10"
}

group = "com.blitzoffline"
version = "0.0.7-Snapshot"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.triumphteam.dev/snapshots/") }
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
    implementation("dev.jorel:commandapi-shade:8.5.1-SNAPSHOT")
    implementation("org.spongepowered:configurate-hocon:4.1.2")
    implementation("org.spongepowered:configurate-extra-kotlin:4.1.2")
    implementation("net.kyori:adventure-api:4.11.0")
    implementation("net.kyori:adventure-text-serializer-plain:4.11.0")
    implementation("net.kyori:adventure-text-minimessage:4.11.0")
    implementation("net.kyori:adventure-platform-bukkit:4.1.1")

    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    withType<KotlinCompile> {
        withType<ProcessResources> {
            filesMatching("plugin.yml") {
                expand("version" to project.version)
            }
        }

        kotlinOptions {
            jvmTarget = "17"
            javaParameters = true
        }

        withType<ShadowJar> {
            relocate("kotlin", "com.blitzoffline.giveall.libs.kotlin")
            relocate("dev.jorel.commandapi", "com.blitzoffline.giveall.libs.commandapi")
            relocate("org.spongepowered.configurate", "com.blitzoffline.giveall.libs.configurate")
            relocate("net.kyori", "com.blitzoffline.giveall.libs.kyori")
            archiveFileName.set("GiveAll-${project.version}.jar")
        }
    }
}