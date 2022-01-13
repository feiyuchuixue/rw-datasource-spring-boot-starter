package com.fycx.dynamic.datasource;

import com.fycx.dynamic.datasource.enums.DBTypeEnum;

/**
 * 主从数据源切换
 *
 * @author: fycx
 * @date: 2022/1/6 9:31
 * @since: 1.1.0
 */
public class DynamicDataSourceHolder {
    private static final ThreadLocal<DBTypeEnum> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void set(DBTypeEnum dbType) {
        CONTEXT_HOLDER.set(dbType);
    }

    public static DBTypeEnum get() {
        return CONTEXT_HOLDER.get();
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    public static void master() {
        set(DBTypeEnum.MASTER);
    }

    public static void slave() {
        set(DBTypeEnum.SLAVE);
    }
}
