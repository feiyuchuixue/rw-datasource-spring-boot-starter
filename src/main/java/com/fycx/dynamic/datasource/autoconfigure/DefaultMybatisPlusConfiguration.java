package com.fycx.dynamic.datasource.autoconfigure;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.fycx.dynamic.datasource.bean.RwDatasourceProperty;
import com.fycx.dynamic.datasource.tools.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: fycx
 * @date: 2022/1/12 8:35
 * @since: 1.1.0
 */
@ConditionalOnProperty(prefix = RwDatasourceProperty.PREFIX, name = "enable", havingValue = "false", matchIfMissing = true)
@EnableConfigurationProperties(RwDatasourceProperty.class)
@Configuration
public class DefaultMybatisPlusConfiguration {

    @Autowired
    private RwDatasourceProperty rwDatasourceProperty;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {

        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        DbType dbType = Utils.getDbType(rwDatasourceProperty);

        // 添加分页拦截
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));
        return interceptor;
    }


}
