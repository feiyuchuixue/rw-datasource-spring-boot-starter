package com.fycx.dynamic.datasource.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: fycx
 * @date: 2022/1/11 9:37
 * @since: 1.1.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = RwDatasourceProperty.PREFIX)
public class RwDatasourceProperty {

    private boolean enable;

    private String dbType;

    public static final String PREFIX = "spring.datasource.rw";

    public static final String PREFIX_MASTER = ".write";

    public static final String PREFIX_SLAVE = ".read";

    public static final String PREFIX_DEFAULT = ".default";

}
