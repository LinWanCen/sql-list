package io.github.linwancen.sql.bean;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@HeadStyle(wrapped = BooleanEnum.FALSE, horizontalAlignment = HorizontalAlignmentEnum.LEFT)
@HeadFontStyle(fontHeightInPoints = 11, bold = BooleanEnum.FALSE, fontName="宋体")
public class SqlInfo {
    @SuppressWarnings("FieldCanBeLocal")
    private String fullId;
    private String namespace;
    private String id;
    private String type;
    private String parameterType;
    private String resultMap;
    private String resultType;
    @ColumnWidth(40)
    private String sql;
    /** TableName, TableExcelLine */
    @ExcelIgnore
    private final TreeMap<String, TableColumn> tableMap = new TreeMap<>();
    @SuppressWarnings("FieldCanBeLocal")
    @ColumnWidth(25)
    private String table;
    /** ColumnName, ColumnExcelLine */
    @ExcelIgnore
    private final List<TreeMap<String, TableColumn>> columnList = new ArrayList<>();
    @SuppressWarnings("FieldCanBeLocal")
    private String column;
    /** tableName.Column ---  */
    @ExcelIgnore
    private final Set<List<String>> columnRel = new HashSet<>();
    /** gitRootInfo.startDate != null && gitRootInfo.endDate != null */
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
    @SuppressWarnings("FieldCanBeLocal")
    private String link;
    private String xmlErr;
    private String sqlErr;
    @ExcelIgnore
    private final Map<String, String> aliasMap = new HashMap<>();

    public String getFullId() {
        return namespace + "." + id;
    }

    public void setFullId(String fullId) {
        this.fullId = fullId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    Pattern BASE_PATTERN = Pattern.compile("^java\\.|^(int|long|short|double|float|boolean|char|byte" +
            "|String|map|integer|Integer|Long|Short|Double|Float|Boolean|Char|Byte)$");

    public String getResultClass() {
        if (resultType == null || BASE_PATTERN.matcher(resultType).find()) {
            return null;
        }
        return resultType;
    }

    public String getParameterClass() {
        if (parameterType == null || BASE_PATTERN.matcher(parameterType).find()) {
            return null;
        }
        return parameterType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public TreeMap<String, TableColumn> getTableMap() {
        return tableMap;
    }

    public String getTable() {
        return String.join(", ", tableMap.keySet());
    }

    public List<TreeMap<String, TableColumn>> getColumnList() {
        return columnList;
    }

    public String getColumn() {
        return columnList.stream()
                .filter(map -> !map.isEmpty())
                .map(map -> String.join(", ", map.keySet()))
                .collect(Collectors.joining("\n"));
    }

    public Set<List<String>> getColumnRel() {
        return columnRel;
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

    public String getXmlErr() {
        return xmlErr;
    }

    public void setXmlErr(String xmlErr) {
        this.xmlErr = xmlErr;
    }

    public String getSqlErr() {
        return sqlErr;
    }

    public void setSqlErr(String sqlErr) {
        this.sqlErr = sqlErr;
    }

    public Map<String, String> getAliasMap() {
        return aliasMap;
    }
}
