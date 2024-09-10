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
import java.io.File


open class SqlCopy : CopyAction() {

    override fun update(e: AnActionEvent) {
        e.presentation.text = I18n.message("sql.copy")
    }

    override fun actionPerformed(e: AnActionEvent) {
        val dataContext = e.dataContext
        val editor = CommonDataKeys.EDITOR.getData(dataContext) ?: return
        val project = CommonDataKeys.PROJECT.getData(dataContext) ?: return
        val virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(dataContext) ?: return
        object : Task.Backgroundable(project, "Export SQL ") {
            override fun run(indicator: ProgressIndicator) {
                val file = File(virtualFile.canonicalPath ?: return)
                val parser = XmlFileParser.build(Configuration(), file, null) ?: return
                val line = editor.caretModel.logicalPosition.line + 1
                parser.parserSqlFragments()
                parser.parserSql {
                    if (it.startLine <= line && line <= it.endLine) {
                        it.sql?.let { sql ->
                            CopyPasteManager.getInstance()?.setContents(TextTransferable(sql as CharSequence))
                            WindowManager.getInstance()?.getStatusBar(project)?.info = "Copy SQL for ${it.fullId}"
                        }
                        false
                    } else true
                }
            }
        }.queue()
    }
}