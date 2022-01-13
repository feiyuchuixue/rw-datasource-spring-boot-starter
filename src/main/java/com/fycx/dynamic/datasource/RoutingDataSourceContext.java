package com.fycx.dynamic.datasource;

import com.fycx.dynamic.datasource.enums.DBTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 核心切换数据源方式
 * @author: fycx
 * @date: 2022/1/6 9:33
 * @since: 1.1.0
 */
@Slf4j
public class RoutingDataSourceContext extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        DBTypeEnum dbTypeEnum = null;
        if (DynamicDataSourceHolder.get() == null) {
            dbTypeEnum = DBTypeEnum.MASTER;
        } else {
            dbTypeEnum = DynamicDataSourceHolder.get();
        }
        log.info(" [ datasource ] change to :{}", dbTypeEnum);
        return dbTypeEnum;
    }
}
