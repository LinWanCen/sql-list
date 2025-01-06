package io.github.linwancen.sql.bean;

import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;

@SuppressWarnings("unused")
@HeadStyle(wrapped = BooleanEnum.FALSE, horizontalAlignment = HorizontalAlignmentEnum.LEFT)
@HeadFontStyle(fontHeightInPoints = 11, bold = BooleanEnum.FALSE, fontName = "宋体")
public class Dto {
    @ColumnWidth(50)
    private String className;
    @ColumnWidth(25)
    private String tableName;
    @ColumnWidth(25)
    private String tableComment;

    public static Dto of(String className, String tableName, String tableComment) {
        Dto dto = new Dto();
        dto.className = className;
        dto.tableName = tableName;
        dto.tableComment = tableComment;
        return dto;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }
}
