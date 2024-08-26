package io.github.linwancen.sql.bean;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;

import java.util.LinkedHashSet;

@SuppressWarnings("unused")
@HeadStyle(wrapped = BooleanEnum.FALSE, horizontalAlignment = HorizontalAlignmentEnum.LEFT)
@HeadFontStyle(fontHeightInPoints = 11, bold = BooleanEnum.FALSE, fontName = "宋体")
public class TableColumn {
    @SuppressWarnings("FieldCanBeLocal")
    private String fullId;
    private String namespace;
    private String id;
    private String type;
    @ColumnWidth(25)
    private String table;
    @ExcelIgnore
    private final LinkedHashSet<String> columnUseTypeSet = new LinkedHashSet<>();
    @ColumnWidth(15)
    private String columnUseType;
    private String tableColumn;
    @ExcelIgnore
    private final LinkedHashSet<String> columnSet = new LinkedHashSet<>();
    @ColumnWidth(25)
    private String column;

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

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public LinkedHashSet<String> getColumnUseTypeSet() {
        return columnUseTypeSet;
    }

    public String getColumnUseType() {
        if (columnUseType == null) {
            return String.join(", ", columnUseTypeSet);
        }
        return columnUseType;
    }

    public void setColumnUseType(String columnUseType) {
        this.columnUseType = columnUseType;
    }

    public String getTableColumn() {
        if (column == null) {
            return null;
        }
        if (table == null) {
            return column;
        }
        return table + "." + column;
    }

    public LinkedHashSet<String> getColumnSet() {
        return columnSet;
    }

    public String getColumn() {
        if (column == null) {
            return String.join(", ", columnSet);
        }
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}
