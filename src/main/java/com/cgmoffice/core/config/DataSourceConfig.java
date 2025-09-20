package com.cgmoffice.core.config;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.cgmoffice.core.datasource_utils.core.DataSourceCore;
import com.cgmoffice.core.datasource_utils.mybatis.MybatisSettingConstants;
import com.cgmoffice.core.datasource_utils.vo.DataSourceFactoryVO;

import lombok.RequiredArgsConstructor;

/**
 * Datasource 설정
 */
@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

	private final ApplicationContext applicationContext;

	@Value("${spring.app.datasource.driver-class-name}")
	private String appDriverClassName;

	@Value("${spring.module.datasource.driver-class-name}")
	private String moduleDriverClassName;

	@Bean("appFactoryVO")
    @ConfigurationProperties(prefix = "spring.app.datasource")
	DataSourceFactoryVO appFactoryVO() {
        return new DataSourceFactoryVO();
    }

	@Bean("moduleFactoryVO")
    @ConfigurationProperties(prefix = "spring.module.datasource")
	DataSourceFactoryVO moduleFactoryVO() {
        return new DataSourceFactoryVO();
    }

	@Bean("appDataSource")
	DataSource appDataSource() {
		return DataSourceCore.makeDataSourceByJdbc(appFactoryVO());
	}

	@Bean("moduleDataSource")
	DataSource moduleDataSource() {
		return DataSourceCore.makeDataSourceByJdbc(moduleFactoryVO());
	}

    @Bean("appSqlSessionFactory")
    SqlSessionFactory appSqlSessionFactory() throws Exception{

    	String confFile;
    	if(StringUtils.containsIgnoreCase(appDriverClassName, "mysql")
    			|| StringUtils.containsIgnoreCase(appDriverClassName, "mariadb")
    			) {
    		confFile = MybatisSettingConstants.MYSQL_CONF;
    	} else if(StringUtils.containsIgnoreCase(appDriverClassName, "postgresql")) {
    		confFile = MybatisSettingConstants.POSTGRESQL_CONF;
    	} else if(StringUtils.containsIgnoreCase(appDriverClassName, "db2")) {
    		confFile = MybatisSettingConstants.DB2_CONF;
    	} else if(StringUtils.containsIgnoreCase(appDriverClassName, "sqlserver")) {
    		confFile = MybatisSettingConstants.SQLSERVER2005_CONF;
    	} else {
    		confFile = MybatisSettingConstants.ORACLE_CONF;
    	}

    	return DataSourceCore.makeSqlSession(applicationContext, confFile, appDataSource());
    }

    @Bean("moduleSqlSessionFactory")
    SqlSessionFactory moduleSqlSessionFactory() throws Exception{

    	String confFile;
    	if(StringUtils.containsIgnoreCase(moduleDriverClassName, "mysql")
    			|| StringUtils.containsIgnoreCase(moduleDriverClassName, "mariadb")
    			) {
    		confFile = MybatisSettingConstants.MYSQL_CONF;
    	} else if(StringUtils.containsIgnoreCase(moduleDriverClassName, "postgresql")) {
    		confFile = MybatisSettingConstants.POSTGRESQL_CONF;
    	} else if(StringUtils.containsIgnoreCase(moduleDriverClassName, "db2")) {
    		confFile = MybatisSettingConstants.DB2_CONF;
    	} else if(StringUtils.containsIgnoreCase(moduleDriverClassName, "sqlserver")) {
    		confFile = MybatisSettingConstants.SQLSERVER2005_CONF;
    	} else {
    		confFile = MybatisSettingConstants.ORACLE_CONF;
    	}

    	return DataSourceCore.makeSqlSession(applicationContext, confFile, moduleDataSource());
    }

	@Bean("appAdvisor")
	Advisor appAdvisor() {
		return DataSourceCore.makeAdvisor(appDataSource());
	}

	@Bean("moduleAdvisor")
	Advisor moduleAdvisor() {
		return DataSourceCore.makeAdvisor(moduleDataSource());
	}

	@Bean("appTransactionManager")
	DataSourceTransactionManager appTransactionManager() {
		return new DataSourceTransactionManager(appDataSource());
	}

	@Bean("moduleTransactionManager")
	DataSourceTransactionManager moduleTransactionManager() {
		return new DataSourceTransactionManager(moduleDataSource());
	}

}
