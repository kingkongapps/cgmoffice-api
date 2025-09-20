package com.cgmoffice.core.datasource_utils.factory;

import java.sql.Driver;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.jdbc.datasource.embedded.DataSourceFactory;

import com.cgmoffice.core.datasource_utils.vo.DataSourceFactoryVO;


public class CoreDataSourceFactory implements DataSourceFactory {
	
	private final DriverManagerDataSource dataSource = new DriverManagerDataSource();
	
	DataSourceFactoryVO factoryVO;
	
	
	public CoreDataSourceFactory(DataSourceFactoryVO factoryVO) {
		this.factoryVO = factoryVO;
	}
	

	@Override
	public ConnectionProperties getConnectionProperties() {
		return new ConnectionProperties() {


			@Override
			public void setDriverClass(Class<? extends Driver> driverClass) {
				dataSource.setDriverClassName(factoryVO.getDriverClassName());
			}

			@Override
			public void setUrl(String url) {
				dataSource.setUrl(factoryVO.getUrl());
			}

			@Override
			public void setUsername(String username) {
				dataSource.setUsername(factoryVO.getUsername());
			}

			@Override
			public void setPassword(String password) {
				dataSource.setPassword(factoryVO.getPassword());
			}
			
		};
	}

	@Override
	public DataSource getDataSource() {
		return this.dataSource;
	}

}
