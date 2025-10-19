package dev.sebastiano.intellij.plugins.copyforkeynote

import com.intellij.codeInsight.editorActions.CopyPastePostProcessor
import com.intellij.codeInsight.editorActions.CopyPastePreProcessor
import com.intellij.codeInsight.editorActions.TextBlockTransferable
import com.intellij.codeInsight.editorActions.TextBlockTransferableData
import com.intellij.ide.CopyProvider
import com.intellij.ide.lightEdit.LightEditCompatible
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.ActionUpdateThreadAware
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.RawText
import com.intellij.openapi.editor.impl.EditorCopyPasteHelperImpl
import com.intellij.openapi.editor.richcopy.view.HtmlTransferableData
import com.intellij.openapi.editor.richcopy.view.RtfTransferableData
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.util.ui.EDT
import dev.sebastiano.intellij.plugins.copyforkeynote.settings.CopyForKeynoteSettings
import java.util.function.Consumer
import java.util.function.Supplier

/** Mostly adapted from [com.intellij.ide.actions.CopyAction] amd CopyWithLineNumbers plugin */
class CopyForKeynoteAction : DumbAwareAction(), LightEditCompatible {

    init {
        isEnabledInModalContext = true
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val editor = CommonDataKeys.EDITOR.getData(e.dataContext) ?: return
        val project = CommonDataKeys.PROJECT.getData(e.dataContext) ?: return
        val file = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return

        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val selectionModel = editor.selectionModel
        val startOffsets = selectionModel.blockSelectionStarts
        val endOffsets = selectionModel.blockSelectionEnds

        if (startOffsets.isEmpty() || endOffsets.isEmpty()) return

        val transferableDataList = ArrayList<TextBlockTransferableData>()
        DumbService.getInstance(project).withAlternativeResolveEnabled {
            for (processor in CopyPastePostProcessor.EP_NAME.extensionList) {
                transferableDataList.addAll(processor.collectTransferableData(file, editor, startOffsets, endOffsets))
            }
        }

        var rawText =
            if (editor.caretModel.supportsMultipleCarets()) {
                EditorCopyPasteHelperImpl.getSelectedTextForClipboard(editor, transferableDataList)
            } else {
                selectionModel.selectedText
            }

        rawText = TextBlockTransferable.convertLineSeparators(rawText, "\n", transferableDataList)

        var escapedText: String? = null
        for (processor in CopyPastePreProcessor.EP_NAME.extensionList) {
            try {
                escapedText = processor.preprocessOnCopy(file, startOffsets, endOffsets, rawText)
            } catch (ex: ProcessCanceledException) {
                throw ex
            } catch (e: Throwable) {
                thisLogger().error(e)
            }
            if (escapedText != null) {
                break
            }
        }

        CopyPasteManager.getInstance()
            .setContents(
                RichTextTransferable(
                    escapedText ?: rawText,
                    transferableDataList.filterIsInstance<RtfTransferableData>().firstOrNull(),
                    transferableDataList.filterIsInstance<HtmlTransferableData>().firstOrNull(),
                    CopyForKeynoteSettings.instance,
                )
            )
    }

    override fun update(event: AnActionEvent) {
        updateWithProvider<CopyProvider?>(
            event,
            provider = event.getData(PlatformDataKeys.COPY_PROVIDER),
            checkDumbAwareness = false,
        ) { provider: CopyProvider? ->
            val isEditorPopup = event.place == ActionPlaces.EDITOR_POPUP
            event.presentation.setEnabled(provider!!.isCopyEnabled(event.dataContext))
            event.presentation.setVisible(!isEditorPopup || provider.isCopyVisible(event.dataContext))
        }
    }

    private fun <T : ActionUpdateThreadAware?> updateWithProvider(
        event: AnActionEvent,
        provider: T?,
        checkDumbAwareness: Boolean,
        consumer: Consumer<T?>,
    ) {
        val project = event.getData(CommonDataKeys.PROJECT)

        if (provider == null || project == null || dumbModePreventsAction(project, provider, checkDumbAwareness)) {
            event.presentation.setEnabled(false)
            event.presentation.setVisible(true)
            return
        }

        val updateThread = provider.actionUpdateThread
        if (updateThread == ActionUpdateThread.BGT || EDT.isCurrentThreadEdt()) {
            consumer.accept(provider)
        } else {
            event.updateSession.compute<Any?>(
                provider,
                "update",
                updateThread,
                Supplier {
                    consumer.accept(provider)
                    null
                },
            )
        }
    }

    private fun <T : ActionUpdateThreadAware> dumbModePreventsAction(
        project: Project,
        provider: T,
        checkDumbAwareness: Boolean,
    ) = checkDumbAwareness && !DumbService.getInstance(project).isUsableInCurrentContext(provider)
}
