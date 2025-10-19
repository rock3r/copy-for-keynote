package dev.sebastiano.intellij.plugins.copyforkeynote.settings

import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindIntValue
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import dev.sebastiano.intellij.plugins.copyforkeynote.CopyForKeynoteBundle

class CopyForKeynoteConfigurable :
    BoundSearchableConfigurable(
        displayName = CopyForKeynoteBundle.message("configuration.title"),
        helpTopic = CopyForKeynoteBundle.message("configuration.title"),
        _id = "dev.sebastiano.intellij.plugins.copyforkeynote.configurable",
    ) {

    private val settings = CopyForKeynoteSettings.instance

    override fun createPanel(): DialogPanel = panel {
        row { checkBox("Strip background color when copying").bindSelected(settings::stripBackground) }
        row("Font size:") { spinner(1..1000).bindIntValue(settings::fontSize) }
    }
}
