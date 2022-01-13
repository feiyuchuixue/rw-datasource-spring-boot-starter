package com.fycx.dynamic.datasource.tools;

import com.baomidou.mybatisplus.annotation.DbType;
import com.fycx.dynamic.datasource.bean.RwDatasourceProperty;

/**
 * @author: curyu
 * @date: 2022/1/13 22:17
 * @description:
 */
public class Utils {

    public static DbType getDbType(RwDatasourceProperty rwDatasourceProperty) {
        DbType dbType = DbType.MARIADB;
        if (rwDatasourceProperty != null && rwDatasourceProperty.getDbType() != null && !"".equals(rwDatasourceProperty.getDbType())) {
            dbType = DbType.getDbType(rwDatasourceProperty.getDbType());
        }
        return dbType;
    }

}
