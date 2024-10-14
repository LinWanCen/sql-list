package com.github.linwancen.plugin.sql.inspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.XmlElementVisitor
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import io.github.linwancen.sql.bean.SqlInfo
import io.github.linwancen.sql.parser.jsqlparser.JSqlParser
import io.github.linwancen.sql.parser.mybatis.file.XmlFileParser
import io.github.linwancen.util.format.LineColumnTip
import org.apache.ibatis.session.Configuration
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.charset.Charset
import java.util.regex.Matcher

class XmlSqlInspection : LocalInspectionTool() {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : XmlElementVisitor() {

            override fun visitXmlFile(xmlFile: XmlFile?) {
                try {
                    visit(xmlFile)
                } catch (e: Throwable) {
                    log.info("XmlSqlInspection.visitXmlFile() catch Throwable but log to record.", e)
                }
            }

            private fun visit(xmlFile: XmlFile?) {
                val file = File(xmlFile?.virtualFile?.canonicalPath ?: return)
                val inputStream = xmlFile.text.byteInputStream(Charset.forName("UTF-8"))
                val parser = XmlFileParser(inputStream, Configuration(), file, null)
                if (parser.mapper == null) {
                    return
                }

                // build idMap
                val idMap = mutableMapOf<String, XmlTag>()
                val rootTags = xmlFile.rootTag?.children ?: return
                rootTags.forEach {
                    if (it is XmlTag) {
                        val id = it.getAttributeValue("id") ?: return@forEach
                        idMap[id] = it
                    }
                }

                // parser and registerProblem
                parser.parserSqlFragments()
                parser.parserSql {
                    JSqlParser.parseSQL(it)
                    val err = it.sqlErr ?: it.xmlErr ?: return@parserSql true
                    val xmlTag = idMap[it.id]
                    if (xmlTag != null) {
                        if (!registeredInChild(err, xmlFile, xmlTag, it)) {
                            holder.registerProblem(xmlTag, "$err\n\n${it.sql ?: ""}")
                        }
                    }
                    true
                }
            }

            private fun registeredInChild(err: String, xmlFile: XmlFile, xmlTag: XmlTag, it: SqlInfo): Boolean {
                val m: Matcher = LineColumnTip.LINE_COLUMN_PATTERN.matcher(err)
                if (!m.find()) {
                    return false
                }
                val line = m.group(1).toInt()
                val column = m.group(2).toInt()
                val tagStart = xmlTag.textRange?.startOffset ?: return false
                val viewProvider: FileViewProvider = xmlFile.viewProvider
                val document = viewProvider.document ?: return false
                val tagLine = document.getLineNumber(tagStart)
                val startOffset = document.getLineStartOffset(tagLine + line)
                val psiElement = viewProvider.findElementAt(startOffset + column - 1) ?: return false
                holder.registerProblem(psiElement, "$err\n\n${it.sql ?: ""}")
                return true
            }

        }
    }

}