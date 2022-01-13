package com.fycx.dynamic.datasource;

/**
 * @author: fycx
 * @date: 2022/1/6 15:13
 * @since: 1.1.0
 */
public class MasterDatabaseAnnotationLocal {

    private static final ThreadLocal<Boolean> local = new ThreadLocal<>();

    public static void masterSetting() {
        local.set(true);
    }

    public static boolean get() {
        if (local.get() == null) {
            return false;
        }
        return local.get();
    }

    public static void clear() {
        local.remove();
    }


}
