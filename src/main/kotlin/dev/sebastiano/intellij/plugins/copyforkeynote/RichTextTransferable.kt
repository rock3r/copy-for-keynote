package dev.sebastiano.intellij.plugins.copyforkeynote

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.richcopy.view.HtmlTransferableData
import com.intellij.openapi.editor.richcopy.view.RtfTransferableData
import dev.sebastiano.intellij.plugins.copyforkeynote.settings.CopyForKeynoteSettings
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.ByteArrayInputStream

internal class RichTextTransferable(
    private val text: String,
    private val rtfTextData: RtfTransferableData?,
    private val htmlTextData: HtmlTransferableData?,
    private val settings: CopyForKeynoteSettings,
) : Transferable {
    private val rtf: String

    init {
        val rawRtf =
            rtfTextData?.reader()?.use { it.readText() } ?: throw UnsupportedFlavorException(RtfTransferableData.FLAVOR)
        rtf = RtfProcessor.process(rawRtf, settings)
        thisLogger().info("RichTextTransferable: $rtf")
    }

    private val flavors: List<DataFlavor> = buildList {
        add(DataFlavor.stringFlavor)

        //        if (htmlTextData != null) {
        //            add(HtmlTransferableData.FLAVOR)
        //        }

        if (rtfTextData != null) {
            add(RtfTransferableData.FLAVOR)
        }
    }

    override fun getTransferDataFlavors(): Array<DataFlavor> = flavors.toTypedArray()

    override fun isDataFlavorSupported(flavor: DataFlavor?): Boolean = flavor in flavors

    override fun getTransferData(flavor: DataFlavor?): Any {
        if (flavor == null) throw UnsupportedFlavorException(flavor)

        return when (flavor) {
            DataFlavor.stringFlavor -> text
            //            HtmlTransferableData.FLAVOR -> {
            //                val rawHtml = htmlTextData?.readText() ?: throw UnsupportedFlavorException(flavor)
            //                preprocessHtml(rawHtml)
            //            }
            RtfTransferableData.FLAVOR -> ByteArrayInputStream(rtf.toByteArray())
            else -> throw UnsupportedFlavorException(flavor)
        }
    }

    //    private fun preprocessHtml(html: String): String = html
}
