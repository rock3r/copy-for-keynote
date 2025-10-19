package dev.sebastiano.intellij.plugins.copyforkeynote

import dev.sebastiano.intellij.plugins.copyforkeynote.settings.CopyForKeynoteSettings

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

//        if (settings.stripIndent) {
//            val lines = result.lines()
//            val minIndent = lines.filter { it.trim().isNotEmpty() }
//                .minOfOrNull { it.takeWhile { char -> char.isWhitespace() }.length }
//                ?: 0
//
//            if (minIndent > 0) {
//                result = lines.joinToString(separator = "\n") { if (it.trim().isEmpty()) it else it.substring(minIndent) }
//            }
//        }

        return result
    }
}
