package dev.sebastiano.plugins.copyforkeynote

import dev.sebastiano.plugins.copyforkeynote.settings.CopyForKeynoteSettings

internal object RtfProcessor {

    private val BACKGROUND_COLOR_REGEX = Regex("\\\\cb(pat)?\\d+")
    private val FONT_SIZE_REGEX = Regex("\\\\fs\\d+")
    private val FONT_TABLE_REGEX = Regex("\\{\\\\fonttbl.*?\\}\\}")

    fun process(rtf: String, settings: CopyForKeynoteSettings): String {
        var result = rtf

        if (settings.stripBackground) {
            result = result.replace(BACKGROUND_COLOR_REGEX, "")
        }

        result = result.replace(FONT_SIZE_REGEX, "")

        val globalFontSize = "\\fs${settings.fontSize * 2}"
        result = result.replace(FONT_TABLE_REGEX) { match -> "${match.value}$globalFontSize" }

        return result
    }
}
