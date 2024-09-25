package com.github.linwancen.plugin.sql.action

import com.alibaba.excel.EasyExcelFactory
import com.alibaba.excel.ExcelWriter
import com.github.linwancen.plugin.compare.ui.I18n
import com.intellij.ide.actions.CopyAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.vfs.newvfs.RefreshQueue
import com.intellij.openapi.wm.WindowManager
import io.github.linwancen.sql.DiffSql
import io.github.linwancen.sql.excel.SqlInfoWriter
import io.github.linwancen.sql.parser.AllParser
import io.github.linwancen.util.excel.ExcelUtils
import io.github.linwancen.util.git.GitUtils
import java.io.File


open class SqlList : CopyAction() {

    override fun update(e: AnActionEvent) {
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return
        if (files.size == 2) {
            e.presentation.text = I18n.message("sql.diff")
        } else {
            e.presentation.text = I18n.message("sql.list")
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        actionPerformedGit(e, false)
    }

    fun actionPerformedGit(e: AnActionEvent, git: Boolean) {
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return
        object : Task.Backgroundable(project, "Export SQL ") {
            override fun run(indicator: ProgressIndicator) {
                GitUtils.utf8()
                if (files.isEmpty()) {
                    return
                }
                val dir = if (files.size == 1 && files[0].isDirectory) {
                    files[0]
                } else {
                    files[0].parent ?: return
                }
                val sqlListDir = "${dir.canonicalPath ?: return}/sql-list"
                val path = ExcelUtils.write(sqlListDir) { excelWriter: ExcelWriter ->
                    val sqlInfoWriter = SqlInfoWriter(excelWriter)
                    val fileList = files.mapNotNull { it.canonicalPath?.let { s -> File(s) } }.toList()
                    if (files.size == 2) {
                        DiffSql.diffDir(fileList[0], fileList[1], false, sqlInfoWriter::write)
                    } else {
                        val sqlInfos = AllParser.parse(fileList, null, git)
                        sqlInfoWriter.write(sqlInfos)
                    }

                    val data = files.mapNotNull { it.canonicalPath?.let { s -> listOf(s) } }.toList()
                    val cmd = EasyExcelFactory.writerSheet("cmd").build()
                    excelWriter.write(data, cmd)
                }
                RefreshQueue.getInstance().refresh(true, false, null, dir)
                WindowManager.getInstance()?.getStatusBar(project)?.info = "Export SQL $path"
            }
        }.queue()
    }
}