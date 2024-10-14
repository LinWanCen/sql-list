package com.github.linwancen.plugin.sql.action

import com.github.linwancen.plugin.compare.ui.I18n
import com.intellij.ide.actions.CopyAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.wm.WindowManager
import com.intellij.util.ui.TextTransferable
import io.github.linwancen.sql.parser.mybatis.file.XmlFileParser
import org.apache.ibatis.session.Configuration
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.charset.Charset


open class SqlCopy : CopyAction() {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun update(e: AnActionEvent) {
        e.presentation.text = I18n.message("sql.copy")
    }

    override fun actionPerformed(e: AnActionEvent) {
        val dataContext = e.dataContext
        val editor = CommonDataKeys.EDITOR.getData(dataContext) ?: return
        val project = CommonDataKeys.PROJECT.getData(dataContext) ?: return
        val virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(dataContext) ?: return
        val psiFile = CommonDataKeys.PSI_FILE.getData(dataContext) ?: return
        object : Task.Backgroundable(project, "Export SQL ") {
            override fun run(indicator: ProgressIndicator) {
                try {
                    runTask()
                } catch (e: Throwable) {
                    log.info("SqlCopy catch Throwable but log to record.", e)
                }
            }

            private fun runTask() {
                val file = File(virtualFile.canonicalPath ?: return)
                val inputStream = psiFile.text.byteInputStream(Charset.forName("UTF-8"))
                val parser = XmlFileParser(inputStream, Configuration(), file, null)
                if (parser.mapper == null) {
                    return
                }
                val line = editor.caretModel.logicalPosition.line + 1
                parser.parserSqlFragments()
                parser.parserSql {
                    if (it.startLine <= line && line <= it.endLine) {
                        val text = it.sql ?: it.xmlErr ?: it.sqlErr ?: return@parserSql false
                        CopyPasteManager.getInstance()?.setContents(TextTransferable(text as CharSequence))
                        WindowManager.getInstance()?.getStatusBar(project)?.info = "Copy SQL for ${it.fullId}"
                        false
                    } else true
                }
            }
        }.queue()
    }
}