package com.fycx.dynamic.datasource.plugin;

import com.fycx.dynamic.datasource.DynamicDataSourceHolder;
import com.fycx.dynamic.datasource.MasterDatabaseAnnotationLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mybatis-plus读写分离拦截器
 *
 * @author: fycx
 * @date: 2022/1/5 14:14
 * @since: 1.1.0
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
@Slf4j
public class SqlReadWriteInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        try {
            // 只针对非事务操作进行读写分离,事务直接走主库
            if (!synchronizationActive) {
                //获取执行参数
                Object[] objects = invocation.getArgs();
                MappedStatement ms = (MappedStatement) objects[0];

                // 如果配置了自定义注解,那么当前线程里的所有db操作都使用master库，否则根据执行的sql_type去判断
                if (MasterDatabaseAnnotationLocal.get()) {
                    DynamicDataSourceHolder.master();
                } else {
                    // 判断当前sql是[读操作]还是[写操作]
                    if (SqlCommandType.SELECT.equals(ms.getSqlCommandType())) {
                        DynamicDataSourceHolder.slave();
                    } else {
                        DynamicDataSourceHolder.master();
                    }
                }

                BoundSql boundSql = ms.getSqlSource().getBoundSql(objects[1]);
                String sql = boundSql.getSql().toLowerCase(Locale.CHINA);
                sql = replaceBlank(sql);
                log.info(" [{}] SQL: {}", ms.getSqlCommandType().toString(), sql);
            } else {
                DynamicDataSourceHolder.master();
            }
        } finally {
            // 如果非事务,那么sql执行完毕后clear
            if (!synchronizationActive) {
                DynamicDataSourceHolder.clear();
            }
        }

        return invocation.proceed();
    }

    @Override
    public void setProperties(Properties properties) {
        //读取mybatis配置文件中属性
    }

    /**
     * 换行、回车符替换
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll(" ").replaceAll("\\s+", " ");
        }
        return dest;
    }

}
