package com.github.linwancen.plugin.sql.action

import com.alibaba.excel.EasyExcelFactory
import com.alibaba.excel.ExcelWriter
import com.github.linwancen.plugin.compare.ui.I18n
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.vfs.newvfs.RefreshQueue
import io.github.linwancen.sql.DiffSql
import io.github.linwancen.sql.bean.SqlInfo
import io.github.linwancen.sql.bean.TableColumn
import io.github.linwancen.sql.excel.SqlInfoWriter
import io.github.linwancen.sql.parser.AllParser
import io.github.linwancen.util.excel.ExcelUtils
import io.github.linwancen.util.git.GitUtils
import java.io.File


open class SqlList : AnAction() {

    override fun update(e: AnActionEvent) {
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return
        if (files.size == 2) {
            e.presentation.text = I18n.message("sql.diff")
        } else {
            e.presentation.text = I18n.message("sql.list")
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        actionPerformedGit(e, false);
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
                ExcelUtils.write("${dir.canonicalPath ?: return}/sql-list") { excelWriter: ExcelWriter ->
                    val sql = ExcelUtils.sheet(excelWriter, "sql", SqlInfo::class.java)
                    val table = ExcelUtils.sheet(excelWriter, "table", TableColumn::class.java)
                    val column = ExcelUtils.sheet(excelWriter, "column", TableColumn::class.java)
                    val fileList = files.map { File(it.path) }.toList()
                    if (files.size == 2) {
                        DiffSql.diffDir(fileList[0], fileList[1], false)
                        { sqlInfo: List<SqlInfo?>? -> SqlInfoWriter.write(excelWriter, sqlInfo, sql, table, column) }
                    } else {
                        val sqlInfos = AllParser.parse(fileList, null, git)
                        SqlInfoWriter.write(excelWriter, sqlInfos, sql, table, column)
                    }

                    val data = files.map { listOf(it.path) }.toList()
                    val cmd = EasyExcelFactory.writerSheet("cmd").build()
                    excelWriter.write(data, cmd)
                }
                RefreshQueue.getInstance().refresh(true, false, null, dir)
            }
        }.queue()
    }
}