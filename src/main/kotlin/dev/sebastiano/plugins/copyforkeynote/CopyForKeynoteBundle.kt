package dev.sebastiano.plugins.copyforkeynote

import com.intellij.DynamicBundle
import java.util.function.Supplier
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

@NonNls private const val BUNDLE = "messages.CopyForKeynoteBundle"

internal object CopyForKeynoteBundle {
    private val INSTANCE = DynamicBundle(CopyForKeynoteBundle::class.java, BUNDLE)

    fun message(key: @PropertyKey(resourceBundle = BUNDLE) String, vararg params: Any): @Nls String =
        INSTANCE.getMessage(key, *params)

    fun lazyMessage(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): Supplier<@Nls String> =
        INSTANCE.getLazyMessage(key, *params)
}
