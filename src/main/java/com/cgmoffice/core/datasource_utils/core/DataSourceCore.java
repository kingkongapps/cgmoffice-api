package com.cgmoffice.core.datasource_utils.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.cgmoffice.core.datasource_utils.vo.DataSourceFactoryVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataSourceCore {

	private static final String AOP_POINTCUT_EXPRESSION = new StringBuilder()
			.append("!execution(* org.springdoc..*.*(..))")
			.append(" && ")
			.append("!execution(* org.springframework..*.*(..))")
			.append(" && ")
			.append(" ( ")
			.append("execution(* *..*.*Svc.*(..))")
			.append(" || ")
			.append("execution(* *..*.*Service.*(..))")
			.append(" ) ")
			.toString();

	public static DataSource makeDataSourceByJndi(String jndiNm) {
		return new JndiDataSourceLookup().getDataSource(jndiNm);
	}

	public static DataSource makeDataSourceByJdbc(DataSourceFactoryVO dateSourcePropVO) {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName(dateSourcePropVO.getDriverClassName());
		ds.setUrl(dateSourcePropVO.getUrl());
		ds.setUsername(dateSourcePropVO.getUsername());
		ds.setPassword(dateSourcePropVO.getPassword());
        return ds;
	}

	public static SqlSessionFactory makeSqlSession(ApplicationContext applicationContext, String confFile, DataSource ds) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(ds);

        Resource[] resources = applicationContext.getResources("classpath*:/com/cgmoffice/**/*_SQL.xml");
        Resource resource = applicationContext.getResource(confFile);

        sqlSessionFactoryBean.setMapperLocations(resources); // 이러식의 패턴형의 설정경우 엔진에 따라 classpath* 라고 표기하지 않으면 인식을 못한다.
        sqlSessionFactoryBean.setConfigLocation(resource);
        return sqlSessionFactoryBean.getObject();
	}

	public static Advisor makeAdvisor(DataSource ds) {
		AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
		aspectJExpressionPointcut.setExpression(AOP_POINTCUT_EXPRESSION);

		TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
		Properties properties = new Properties();

		List<RollbackRuleAttribute> rollbackRules = new ArrayList<RollbackRuleAttribute>();
		rollbackRules.add(new RollbackRuleAttribute(Exception.class));

		RuleBasedTransactionAttribute ruleBasedTransactionAttribute =
				new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED, rollbackRules);
		String writeTransactionAttributesDefinition = ruleBasedTransactionAttribute.toString();
		properties.setProperty("*", writeTransactionAttributesDefinition);

		transactionInterceptor.setTransactionAttributes(properties);
		transactionInterceptor.setTransactionManager(new DataSourceTransactionManager(ds));

		return new DefaultPointcutAdvisor(aspectJExpressionPointcut, transactionInterceptor);
	}

}
