import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
}

group = "com.blitzoffline"
// TODO: 3/6/22 Bump version to 0.0.6 before updating plugin.
// Also don't forget to mention that versioning will change after the big update following https://semver.org/
version = "0.0.5"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.triumphteam.dev/snapshots/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
}

dependencies {
    // TODO: 3/6/22 Migrate to triumph commands 2.0.0-Snapshot
    implementation("me.mattstudios.utils:matt-framework:1.4.6")
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("net.kyori:adventure-api:4.10.0")
    implementation("net.kyori:adventure-platform-bukkit:4.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")

    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
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
            jvmTarget = "11"
            javaParameters = true
        }

        withType<ShadowJar> {
            relocate("kotlin", "com.blitzoffline.giveall.libs.kotlin")
            relocate("me.mattstudios.mf", "com.blitzoffline.giveall.libs.commands")
            relocate("org.spongepowered.configurate", "com.blitzoffline.giveall.libs.configurate")
            relocate("net.kyori", "com.blitzoffline.giveall.libs.adventure")
            archiveFileName.set("GiveAll-${project.version}.jar")
        }
    }
}