package io.github.linwancen.sql.bean;

import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;

import java.util.Objects;

@SuppressWarnings("unused")
@HeadStyle(wrapped = BooleanEnum.FALSE, horizontalAlignment = HorizontalAlignmentEnum.LEFT)
@HeadFontStyle(fontHeightInPoints = 11, bold = BooleanEnum.FALSE, fontName = "宋体")
public class Rel implements Comparable<Rel> {
    @ColumnWidth(25)
    private String tableL;
    @ColumnWidth(25)
    private String columnL;
    @ColumnWidth(25)
    private String tableR;
    @ColumnWidth(25)
    private String columnR;
    @ColumnWidth(25)
    private String tableCommentL;
    @ColumnWidth(25)
    private String columnCommentL;
    @ColumnWidth(25)
    private String tableCommentR;
    @ColumnWidth(25)
    private String columnCommentR;

    public static Rel sortRelOrNull(String tableL, String columnL, String tableR, String columnR) {
        if (tableL == null || columnL == null || tableR == null || columnR == null) {
            return null;
        }
        tableL = tableL.toLowerCase();
        tableR = tableR.toLowerCase();
        columnL = columnL.toLowerCase();
        columnR = columnR.toLowerCase();
        int compare = tableL.compareTo(tableR);
        if (compare == 0) {
            compare = columnL.compareTo(columnR);
        }
        if (compare == 0) {
            return null;
        }
        Rel rel = new Rel();
        if (compare < 0) {
            rel.tableL = tableL;
            rel.columnL = columnL;
            rel.tableR = tableR;
            rel.columnR = columnR;
        } else {
            rel.tableL = tableR;
            rel.columnL = columnR;
            rel.tableR = tableL;
            rel.columnR = columnL;
        }
        return rel;
    }

    @Override
    public String toString() {
        return tableL + "." + columnL + " --> " + tableR + "." + tableCommentR;
    }

    @Override
    public int compareTo(Rel o) {
        return toString().compareTo((o.toString()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rel rel = (Rel) o;
        return Objects.equals(tableL, rel.tableL)
                && Objects.equals(columnL, rel.columnL)
                && Objects.equals(tableR, rel.tableR)
                && Objects.equals(columnR, rel.columnR);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableL, columnL, tableR, columnR);
    }

    public String getTableL() {
        return tableL;
    }

    public void setTableL(String tableL) {
        this.tableL = tableL;
    }

    public String getColumnL() {
        return columnL;
    }

    public void setColumnL(String columnL) {
        this.columnL = columnL;
    }

    public String getTableR() {
        return tableR;
    }

    public void setTableR(String tableR) {
        this.tableR = tableR;
    }

    public String getColumnR() {
        return columnR;
    }

    public void setColumnR(String columnR) {
        this.columnR = columnR;
    }

    public String getTableCommentL() {
        return tableCommentL;
    }

    public void setTableCommentL(String tableCommentL) {
        this.tableCommentL = tableCommentL;
    }

    public String getColumnCommentL() {
        return columnCommentL;
    }

    public void setColumnCommentL(String columnCommentL) {
        this.columnCommentL = columnCommentL;
    }

    public String getTableCommentR() {
        return tableCommentR;
    }

    public void setTableCommentR(String tableCommentR) {
        this.tableCommentR = tableCommentR;
    }

    public String getColumnCommentR() {
        return columnCommentR;
    }

    public void setColumnCommentR(String columnCommentR) {
        this.columnCommentR = columnCommentR;
    }
}
