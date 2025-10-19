package dev.sebastiano.plugins.copyforkeynote

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.sebastiano.plugins.copyforkeynote.settings.CopyForKeynoteSettings
import org.junit.jupiter.api.Test

class RtfProcessorTest {

    private val sampleRtf =
        """
        {\rtf1\ansi\deff0{\colortbl;\red174\green176\blue183;\red19\green20\blue21;\red195\green123\blue90;\red185\green101\blue173;}
        {\fonttbl{\f1\fmodern JetBrains Mono;}}

        \s0\box\cbpat2\cb2\cf1\fs26
        \cf3
        \f1
        \i0\b0

        if \cf1

        (\cf4

        rtfTextData \cf1

        != \cf3

        null\cf1

        ) \{\

            add(RtfTransferableData.\cf4
        \i\b0

        FLAVOR\cf1
        \i0\b0

        )\

        \}\par}
        """
            .trimIndent()

    @Test
    fun `strips background colours when stripBackground is enabled`() {
        val settings = CopyForKeynoteSettings().apply {
            stripBackground = true
            fontSize = 32
        }
        val expected =
            """
            {\rtf1\ansi\deff0{\colortbl;\red174\green176\blue183;\red19\green20\blue21;\red195\green123\blue90;\red185\green101\blue173;}
            {\fonttbl{\f1\fmodern JetBrains Mono;}}\fs64

            \s0\box\cf1
            \cf3
            \f1
            \i0\b0

            if \cf1

            (\cf4

            rtfTextData \cf1

            != \cf3

            null\cf1

            ) \{\

                add(RtfTransferableData.\cf4
            \i\b0

            FLAVOR\cf1
            \i0\b0

            )\

            \}\par}
            """
                .trimIndent()

        val actual = RtfProcessor.process(sampleRtf, settings)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `preserves background colours when stripBackground is disabled`() {
        val settings = CopyForKeynoteSettings().apply {
            stripBackground = false
            fontSize = 32
        }
        val expected =
            """
            {\rtf1\ansi\deff0{\colortbl;\red174\green176\blue183;\red19\green20\blue21;\red195\green123\blue90;\red185\green101\blue173;}
            {\fonttbl{\f1\fmodern JetBrains Mono;}}\fs64

            \s0\box\cbpat2\cb2\cf1
            \cf3
            \f1
            \i0\b0

            if \cf1

            (\cf4

            rtfTextData \cf1

            != \cf3

            null\cf1

            ) \{\

                add(RtfTransferableData.\cf4
            \i\b0

            FLAVOR\cf1
            \i0\b0

            )\

            \}\par}
            """
                .trimIndent()
        val actual = RtfProcessor.process(sampleRtf, settings)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `removes font sizes and sets a global font size`() {
        val settings = CopyForKeynoteSettings().apply {
            stripBackground = false
            fontSize = 32
        }
        val expected =
            """
            {\rtf1\ansi\deff0{\colortbl;\red174\green176\blue183;\red19\green20\blue21;\red195\green123\blue90;\red185\green101\blue173;}
            {\fonttbl{\f1\fmodern JetBrains Mono;}}\fs64

            \s0\box\cbpat2\cb2\cf1
            \cf3
            \f1
            \i0\b0

            if \cf1

            (\cf4

            rtfTextData \cf1

            != \cf3

            null\cf1

            ) \{\

                add(RtfTransferableData.\cf4
            \i\b0

            FLAVOR\cf1
            \i0\b0

            )\

            \}\par}
            """
                .trimIndent()
        val actual = RtfProcessor.process(sampleRtf, settings)
        assertThat(actual).isEqualTo(expected)
    }
}
