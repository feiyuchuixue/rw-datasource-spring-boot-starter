package com.fycx.dynamic.datasource.autoconfigure;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.fycx.dynamic.datasource.tools.Utils;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import com.fycx.dynamic.datasource.RoutingDataSourceContext;
import com.fycx.dynamic.datasource.aop.MasterDatabaseAspect;
import com.fycx.dynamic.datasource.bean.RwDatasourceProperty;
import com.fycx.dynamic.datasource.enums.DBTypeEnum;
import com.fycx.dynamic.datasource.plugin.SqlReadWriteInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 主从数据源自动注入
 *
 * @author: fycx
 * @date: 2022/1/11 9:35
 * @since: 1.1.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RwDatasourceProperty.class)
@AutoConfigureBefore(value = {DataSourceAutoConfiguration.class, PageHelperAutoConfiguration.class}, name = {"com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure"})
@Import(value = {DruidDynamicDataSourceConfiguration.class})
@ConditionalOnProperty(prefix = RwDatasourceProperty.PREFIX, name = "enable", havingValue = "true", matchIfMissing = false)
public class ReadAndWriteDatasourceAutoConfiguration {

    @Autowired
    private RwDatasourceProperty rwDatasourceProperty;

    @Autowired
    private Environment env;

    @Bean(name = "masterDataSource")
    @ConfigurationProperties(prefix = RwDatasourceProperty.PREFIX + RwDatasourceProperty.PREFIX_MASTER)
    public DataSource masterDataSource() {
        log.info(" [ datasource ] master init ...");
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "slaveDataSource")
    @ConfigurationProperties(prefix = RwDatasourceProperty.PREFIX + RwDatasourceProperty.PREFIX_SLAVE)
    public DataSource slaveDataSource() {
        log.info(" [ datasource ] slave init ...");
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    public DataSource routingDataSource(@Qualifier("masterDataSource") DataSource masterDataSource,
                                        @Qualifier("slaveDataSource") DataSource slaveDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>(2);
        targetDataSources.put(DBTypeEnum.MASTER, masterDataSource);
        targetDataSources.put(DBTypeEnum.SLAVE, slaveDataSource);

        RoutingDataSourceContext context = new RoutingDataSourceContext();
        // 默认【主库】
        context.setDefaultTargetDataSource(masterDataSource);
        context.setTargetDataSources(targetDataSources);
        return context;
    }

    @Bean
    public MasterDatabaseAspect myDataSourceAspect() {
        return new MasterDatabaseAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory customSqlSessionFactory(@Qualifier("masterDataSource") DataSource master,
                                                     @Qualifier("slaveDataSource") DataSource slave) throws Exception {

        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(routingDataSource(master, slave));
        factoryBean.setTypeAliasesPackage(env.getProperty("mybatis-plus.type-aliases-package"));
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(env.getProperty("mybatis-plus.mapper-locations")));
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(false);
        String property = env.getProperty("mybatis-plus.configuration.log-impl");
        if (property != null && !"".equals(property)) {
            configuration.setLogImpl((Class<? extends Log>) Class.forName(property));
        }
        factoryBean.setConfiguration(configuration);
        MybatisPlusInterceptor paginationInterceptor = new MybatisPlusInterceptor();
        // 获取数据库类型
        DbType dbType = Utils.getDbType(rwDatasourceProperty);
        // 添加分页拦截
        paginationInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));
        // 添加拦截器
        factoryBean.setPlugins(new Interceptor[]{
                paginationInterceptor,
                new SqlReadWriteInterceptor()
        });

        return factoryBean.getObject();
    }


}
