# rw-datasource-spring-boot-starter

## 概述

springboot + mybatis-plus + druid  读写分离的实现

## 功能

- 根据sql语句**自动切换**主从库
- 使用**事务**时切换到**主库**
- **@MasterDatabase**自定义注解，经过该方法标识的service方法中的所有sql都使用主库
- 根据配置自动加载单库环境与多库环境
- 继承mybatis-plus、druid  configuration配置

## 约定

- 一主一从，主读写，从写。

## 使用方式

### gradle

```groovy
dependencies {
	...
    // 依赖aop    
    implementation 'org.springframework.boot:spring-boot-starter-aop:2.6.2'
	// 自定义starter
    implementation 'com.simplexi:rw-datasource-spring-boot-starter:1.1.0'
    ...
}

```

### yml配置

```yaml
spring:
  datasource:
    #  标识的数据源
    rw:
      # 注入方式：读写分离=>true; 单数据源=>false; 默认false
      enable: true
      # 数据库类型，默认mariadb
      dbType：mariadb
      # 读数据源
      write:
        url: jdbc:mysql://127.0.0.1:13306/db_master?useUnicode=true&characterEncoding=UTF-8&userSSL=false&serverTimezone=GMT%2B8
        driver-class-name: org.mariadb.jdbc.Driver
        type: com.alibaba.druid.pool.DruidDataSource
	# ...
      # 写数据源
      read:
        url: jdbc:mysql://10.15.1.32:23306/db_master?useUnicode=true&characterEncoding=UTF-8&userSSL=false&serverTimezone=GMT%2B8
        driver-class-name: org.mariadb.jdbc.Driver
        type: com.alibaba.druid.pool.DruidDataSource
        username: db_slave
        password: 123456
     	# ...
      # 默认数据源	
      default:
        url: jdbc:mysql://127.0.0.1:13306/db_slave?useUnicode=true&characterEncoding=UTF-8&userSSL=false&serverTimezone=GMT%2B8
        driver-class-name: org.mariadb.jdbc.Driver
        type: com.alibaba.druid.pool.DruidDataSource
        username: root
        password: root
	# ...
```

当**enbale**=**true**时，必须配置 **spring.datasource.rw.write** 和**spring.datasource.rw.read**

**enable**为**false**或未设置时，必须配置 **spring.datasource.rw.default**

