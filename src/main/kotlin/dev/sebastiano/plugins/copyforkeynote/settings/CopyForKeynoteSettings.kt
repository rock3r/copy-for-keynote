package dev.sebastiano.plugins.copyforkeynote.settings

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Service.Level
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@Service(Level.APP)
@State(
    name = "dev.sebastiano.plugins.copyforkeynote.settings.CopyForKeynoteSettings",
    storages = [Storage("copyforkeynote.xml")],
)
internal class CopyForKeynoteSettings : SimplePersistentStateComponent<CopyForKeynoteSettings.State>(State()) {
    var stripBackground: Boolean
        get() = state.stripBackground
        set(value) {
            state.stripBackground = value
        }

    var fontSize: Int
        get() = state.fontSize
        set(value) {
            state.fontSize = value.coerceIn(1..1000)
        }

    class State : BaseState() {
        var stripBackground by property(true)
        var fontSize: Int by property(30)
    }

    companion object {
        val instance: CopyForKeynoteSettings
            get() = service<CopyForKeynoteSettings>()
    }
}
