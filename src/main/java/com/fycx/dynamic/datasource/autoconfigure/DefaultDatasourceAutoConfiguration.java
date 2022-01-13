package com.fycx.dynamic.datasource.autoconfigure;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.fycx.dynamic.datasource.bean.RwDatasourceProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 默认数据源自动注入
 *
 * @author: fycx
 * @date: 2022/1/11 9:35
 * @since: 1.1.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RwDatasourceProperty.class)
@AutoConfigureBefore(value = DataSourceAutoConfiguration.class, name = "com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure")
@Import(value = {DruidDynamicDataSourceConfiguration.class,DefaultMybatisPlusConfiguration.class})
@ConditionalOnProperty(prefix = RwDatasourceProperty.PREFIX, name = "enable", havingValue = "false", matchIfMissing = true)
public class DefaultDatasourceAutoConfiguration {

    @Primary
    @Bean(name = "defaultDataSource")
    @ConfigurationProperties(prefix = RwDatasourceProperty.PREFIX + RwDatasourceProperty.PREFIX_DEFAULT)
    public DataSource masterDataSource() {
        log.info(" [ datasource ] default init ...");
        return DruidDataSourceBuilder.create().build();
    }


}
