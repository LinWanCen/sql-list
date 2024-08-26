package com.github.linwancen.plugin.sql.action

import com.github.linwancen.plugin.compare.ui.I18n
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys


class SqlListGit : SqlList() {

    override fun update(e: AnActionEvent) {
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return
        if (files.size == 2) {
            e.presentation.text = I18n.message("sql.diff.git")
        } else {
            e.presentation.text = I18n.message("sql.list.git")
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        actionPerformedGit(e, true);
    }
}