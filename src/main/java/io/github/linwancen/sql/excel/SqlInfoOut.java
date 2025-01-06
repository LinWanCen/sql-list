package io.github.linwancen.sql.excel;

import io.github.linwancen.sql.bean.Dto;
import io.github.linwancen.sql.bean.Rel;
import io.github.linwancen.sql.bean.SqlInfo;
import io.github.linwancen.sql.bean.TableColumn;
import io.github.linwancen.util.format.LineFormat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class SqlInfoOut {

    public final StringBuilder errMd = new StringBuilder();
    public final StringBuilder puml = new StringBuilder();
    public final TreeSet<Rel> columnRel = new TreeSet<>();
    public final LinkedHashMap<String, Dto> dtoMap = new LinkedHashMap<>();
    public final ArrayList<TableColumn> tableList = new ArrayList<>();
    public final ArrayList<TableColumn> columnList = new ArrayList<>();

    public void write(List<SqlInfo> sqlInfoList) {
        for (SqlInfo info : sqlInfoList) {
            boolean isErr = SqlInfoErrMd.appendErr(errMd, info);
            String sqlStr = info.getSql();
            if (sqlStr != null && !isErr) {
                sqlStr = LineFormat.itemsOneLine(sqlStr);
                sqlStr = LineFormat.deleteSpaceLine(sqlStr);
            }
            info.setSql(sqlStr);

            if (info.getTableMap().size() == 1) {
                String oneTableName = info.getTableMap().keySet().iterator().next();
                String parameterClass = info.getParameterClass();
                if (parameterClass != null) {
                    dtoMap.computeIfAbsent(parameterClass, s -> Dto.of(oneTableName, parameterClass, info.getTableComment()));
                }
                String resultClass = info.getResultClass();
                if (resultClass != null) {
                    dtoMap.put(resultClass, Dto.of(oneTableName, resultClass, info.getTableComment()));
                }
            }
            for (TreeMap<String, TableColumn> map : info.getColumnList()) {
                columnList.addAll(map.values());
            }
            tableList.addAll(info.getTableMap().values());
            columnRel.addAll(info.getColumnRel());
        }
        SqlInfoPlantUml.buildPlantUml(puml, columnRel);
    }

}
