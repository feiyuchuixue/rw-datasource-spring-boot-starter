package com.fycx.dynamic.datasource.aop;

import com.fycx.dynamic.datasource.DynamicDataSourceHolder;
import com.fycx.dynamic.datasource.MasterDatabaseAnnotationLocal;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 数据源aop
 *
 * @author: fycx
 * @date: 2022/1/6 10:00
 * @since: 1.1.0
 */
@Aspect
public class MasterDatabaseAspect {

    @Pointcut("@annotation(com.fycx.dynamic.datasource.annotation.MasterDatabase)")
    public void writePointcut() {}

    @Pointcut("execution(public * *..*.controller..*.*(..))")
    public void controllerPoint() {}

    @Before("writePointcut()")
    public void writeBefore() {
        MasterDatabaseAnnotationLocal.masterSetting();
    }

    @AfterReturning("writePointcut()")
    public void writeClose() {
        MasterDatabaseAnnotationLocal.clear();
        DynamicDataSourceHolder.clear();
    }

    @AfterReturning("controllerPoint()")
    public void destroyThreadLocal(){
        MasterDatabaseAnnotationLocal.clear();
        DynamicDataSourceHolder.clear();
    }

}
