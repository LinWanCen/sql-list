package io.github.linwancen.sql.excel;

import io.github.linwancen.sql.bean.SqlInfo;

public class SqlInfoErrMd {

    public static boolean appendErr(StringBuilder errMd, SqlInfo info) {
        String errStr = null;
        if (info.getSqlErr() != null) {
            errStr = info.getSqlErr();
        } else if (info.getXmlErr() != null) {
            errStr = info.getXmlErr();
        }
        if (errStr == null) {
            return false;
        }
        errMd.append("### ").append(info.getLastAuthor())
                .append(".(").append(info.getLink()).append(") ")
                .append(info.getId()).append("\n");
        errMd.append(errStr).append("\n");
        if (info.getSql() != null) {
            errMd.append("```sql\n").append(info.getSql()).append("\n```\n");
        }
        errMd.append("\n");
        return true;
    }
}
