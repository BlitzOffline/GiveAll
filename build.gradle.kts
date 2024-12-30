import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.gradleup.shadow") version "8.3.3"
    kotlin("jvm") version "2.0.21"
}

group = "com.blitzoffline"
version = "1.0.6"

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
    implementation("dev.jorel:commandapi-bukkit-shade:9.7.0")

    implementation("org.spongepowered:configurate-hocon:4.1.2")
    implementation("org.spongepowered:configurate-extra-kotlin:4.1.2")

    implementation("net.kyori:adventure-api:4.18.0")
    implementation("net.kyori:adventure-text-serializer-plain:4.18.0")
    implementation("net.kyori:adventure-text-minimessage:4.18.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.4")

    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    withType<KotlinCompile> {
        withType<ProcessResources> {
            filesMatching("plugin.yml") {
                expand("version" to project.version)
            }
        }

        this.jvmTargetValidationMode = JvmTargetValidationMode.IGNORE

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            javaParameters = true
        }

        withType<ShadowJar> {
            relocate("kotlin", "com.blitzoffline.giveall.libs.kotlin")
            relocate("dev.jorel", "com.blitzoffline.giveall.libs.jorel")
            relocate("org.spongepowered", "com.blitzoffline.giveall.libs.spongepowered")
            relocate("com.typesafe", "com.blitzoffline.giveall.libs.typesafe")
            relocate("io.leangen", "com.blitzoffline.giveall.libs.leangen")
            relocate("net.kyori", "com.blitzoffline.giveall.libs.kyori")
            archiveFileName.set("GiveAll-${project.version}.jar")
        }
    }
}