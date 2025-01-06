package io.github.linwancen.sql.excel;

import io.github.linwancen.sql.bean.Rel;
import io.github.linwancen.util.plantuml.PlantUML;

import java.util.Set;

public class SqlInfoPlantUml {

    public static void buildPlantUml(StringBuilder builder, Set<Rel> columnRel) {
        builder.append(PlantUML.start());
        columnRel.forEach(r -> {
                    item(builder, r.getTableL(), r.getColumnL(), r.getTableCommentL(), r.getColumnCommentL());
                    item(builder, r.getTableR(), r.getColumnR(), r.getTableCommentR(), r.getColumnCommentR());
                    builder.append("\n")
                            .append(r.getTableL()).append('.').append(r.getColumnL())
                            .append(" --> ")
                            .append(r.getTableR()).append('.').append(r.getColumnR())
                            .append("\n\n");
                });
        builder.append(PlantUML.end());
    }

    public static void item(StringBuilder builder, String table, String column, String tableComment, String columnComment) {
        builder.append("\ncomponent ").append(table);
        if (tableComment != null) {
            builder.append(" as \"").append(column).append("\\n ").append(tableComment).append('"');
        }
        builder.append(" {\n  component ").append(table).append('.').append(column).append(" as \"").append(column);
        if (columnComment != null) {
            builder.append("\\n ").append(columnComment);
        }
        builder.append('"');
        builder.append("\n}");
    }
}
