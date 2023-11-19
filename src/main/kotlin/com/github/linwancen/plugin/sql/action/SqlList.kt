package com.github.linwancen.plugin.sql.action

import com.alibaba.druid.DbType
import com.alibaba.excel.EasyExcelFactory
import com.alibaba.excel.ExcelWriter
import com.github.linwancen.plugin.compare.ui.I18n
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.RefreshQueue
import io.github.linwancen.sql.DiffSql
import io.github.linwancen.sql.bean.SqlInfo
import io.github.linwancen.sql.parser.AllParser
import io.github.linwancen.util.excel.ExcelUtils
import io.github.linwancen.util.git.GitUtils
import io.github.linwancen.util.java.EnvUtils
import java.io.File


object SqlList : AnAction() {

    override fun update(e: AnActionEvent) {
        super.update(e)
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return
        if (files.size == 2) {
            e.presentation.text = I18n.message("sql.diff")
        } else {
            e.presentation.text = I18n.message("sql.list")
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return
        ApplicationManager.getApplication().runWriteAction {
            object : Task.Backgroundable(project, "Delete same") {
                override fun run(indicator: ProgressIndicator) {
                    GitUtils.utf8()
                    // 可以通过可选加载注入当前项目的方言类型方便用户使用
                    val defaultType = "mysql"
                    val name = EnvUtils.get("dbType", null, defaultType)
                    val dbType = DbType.of(name)
                    if (files.isEmpty()) {
                        return
                    }
                    val parent = files[0].parent ?: return
                    val dir = parent.canonicalPath ?: return
                    ExcelUtils.write("$dir/sql-list") { excelWriter: ExcelWriter ->
                        val sql = ExcelUtils.sheet("sql", SqlInfo::class.java)
                        val fileList = files.map { File(it.path) }.toList()
                        if (files.size == 2) {
                            DiffSql.diffDir(dbType, fileList[0], fileList[1])
                            { sqlInfo: List<SqlInfo?>? -> excelWriter.write(sqlInfo, sql) }
                        } else {
                            val sqlInfos = AllParser.parse(fileList, dbType, null)
                            excelWriter.write(sqlInfos, sql)
                        }

                        val data = files.map { listOf(it.path) }.toList()
                        val cmd = EasyExcelFactory.writerSheet("cmd").build()
                        excelWriter.write(data, cmd)
                    }
                    RefreshQueue.getInstance().refresh(true, false, null, parent)
                }
            }.queue()
        }
    }
}