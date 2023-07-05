import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "1.8.21"
}

group = "com.blitzoffline"
version = "1.0.1"

repositories {
    // Adventure, Configurate, CommandAPI
    mavenCentral()

    // VaultAPI
    maven { url = uri("https://jitpack.io") }

    // NBTAPI (CommandAPI dependency)
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }

    // Paper
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }

    // PlaceholderAPI
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }

    // Adventure snapshots
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
    implementation("dev.jorel:commandapi-bukkit-shade:9.0.3")

    implementation("org.spongepowered:configurate-hocon:4.1.2")
    implementation("org.spongepowered:configurate-extra-kotlin:4.1.2")

    implementation("net.kyori:adventure-api:4.14.0")
    implementation("net.kyori:adventure-text-serializer-plain:4.14.0")
    implementation("net.kyori:adventure-text-minimessage:4.13.1")
    implementation("net.kyori:adventure-platform-bukkit:4.3.0")

    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
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