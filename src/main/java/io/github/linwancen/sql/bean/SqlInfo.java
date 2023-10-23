package io.github.linwancen.sql.bean;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;

import java.io.File;
import java.util.Date;

@SuppressWarnings("unused")
@HeadStyle(wrapped = BooleanEnum.FALSE, horizontalAlignment = HorizontalAlignmentEnum.LEFT)
@HeadFontStyle(fontHeightInPoints = 11, bold = BooleanEnum.FALSE, fontName="宋体")
public class SqlInfo {
    private String namespace;
    private String type;
    private String id;
    private String parameterType;
    private String resultMap;
    @ColumnWidth(40)
    private String sql;
    @ColumnWidth(25)
    private String table;
    private String where;
    private Date inDate;
    @ExcelIgnore
    private GitRootInfo gitRootInfo;
    @ExcelIgnore
    private File file;
    private String fileName;
    private int startLine;
    private int endLine;
    @ColumnWidth(20)
    private Date lastDate;
    @ColumnWidth(16)
    private String lastAuthor;
    private String link;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getResultMap() {
        return resultMap;
    }

    public void setResultMap(String resultMap) {
        this.resultMap = resultMap;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public Date getInDate() {
        return inDate;
    }

    public void setInDate(Date inDate) {
        this.inDate = inDate;
    }

    public GitRootInfo getGitRootInfo() {
        return gitRootInfo;
    }

    public void setGitRootInfo(GitRootInfo gitRootInfo) {
        this.gitRootInfo = gitRootInfo;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        this.fileName = file.getName();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    public String getLastAuthor() {
        return lastAuthor;
    }

    public void setLastAuthor(String lastAuthor) {
        this.lastAuthor = lastAuthor;
    }

    public String getLink() {
        return fileName + ":" + startLine;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
