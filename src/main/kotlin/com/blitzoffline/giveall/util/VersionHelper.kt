package com.blitzoffline.giveall.util

import com.google.common.primitives.Ints
import java.util.regex.Pattern
import org.bukkit.Bukkit

object VersionHelper {
    val CURRENT_VERSION = currentVersion
    val IS_PAPER = checkPaper()

    /**
     * Check if the server has access to the Paper API
     * Taken from [PaperLib](https://github.com/PaperMC/PaperLib)
     *
     * @return True if on Paper server (or forks), false anything else
     */
    private fun checkPaper(): Boolean {
        return try {
            Class.forName("com.destroystokyo.paper.PaperConfig")
            true
        } catch (_: ClassNotFoundException) {
            false
        }
    }

    /**
     * Gets the current server version
     *
     * @return A protocol like number representing the version, for example 1.19.1 - 1191
     */
    private val currentVersion: Int
        get() {
            // No need to cache since will only run once
            val matcher = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?")
                .matcher(Bukkit.getBukkitVersion())

            val stringBuilder = StringBuilder()
            if (matcher.find()) {
                stringBuilder.append(matcher.group("version").replace(".", ""))

                val patch = matcher.group("patch")

                if (patch == null) stringBuilder.append("0")
                else stringBuilder.append(patch.replace(".", ""))
            }

            // Should never fail
            return Ints.tryParse(stringBuilder.toString())
                ?: throw RuntimeException("Could not retrieve server version!")
        }
}