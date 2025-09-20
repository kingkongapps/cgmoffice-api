## Springboot 3.5.6 Refactoring

### ERROR-1 : logback error
* Error 내용 : logback-spring.xml 설정에서 filter로 JaninoEventEvaluator 클래스를 못찾음
* 원인 : logback-1.5.18 version에서 저 클래스가 없어짐
```
ERROR in ch.qos.logback.core.filter.EvaluatorFilter@1cce0354 - No evaluator set for filter null
Suppressed: java.lang.ClassNotFoundException: ch.qos.logback.classic.boolex.JaninoEventEvaluator
```

* 해결책
  * logback-spring.xml 에서 filter를 그냥 주석 처리함
  * 대안은 아래 처럼 다른 Filter를 쓰라고 해야하는데, 더 찾아봐야 함
```
<filter class="ch.qos.logback.classic.filter.LevelFilter">
    <level>ERROR</level>
    <onMatch>ACCEPT</onMatch>
    <onMismatch>DENY</onMismatch>
</filter>
```

---

### ERROR-2 : DB 설정을 못찾음
* 원인 : application.yml 에 spring.app.datasource.url 로 설정이 되어 있었음
* 해결 : spring.datasource.url 로 변경함, 왜 중간에 app. 이 들어갔는지 알아봐야 함
```
Description:

Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.

Reason: Failed to determine a suitable driver class

```

### ERROR-3 : *.java / *.xml 안의 패키지 경로 오류
* 해결 : com.cgm.xxx -> com.cgmoffice.xxx 로 수정한다.
  * pom.xml 에서 아래 resources 위치로 설정 추가 , 즉 *.xml을 main/java 밑에서 찾으라고 알려줌.
```
	    <resources>
	        <resource>
	            <directory>src/main/java</directory> <!-- Java 소스 디렉토리 -->
	            <includes>
	                <include>**/*.xml</include> <!-- XML 파일만 포함 -->
	            </includes>
	        </resource>
	        <resource>
	            <directory>src/main/resources</directory> <!-- 기존 리소스 디렉토리 -->
	        </resource>
	    </resources>
```
### ERROR-4 : 하드코딩 된 경로 오류 
* 원인 : ./main/java/com/cgmoffice/core/datasource_utils/mybatis/MybatisSettingConstants.java 안의 하드코딩된 경로 참조로 오류.
* 해결 : classpath:com/cgm/ -> classpath:com/cgmoffice/ 로 수정
```
Caused by: java.io.FileNotFoundException: class path resource [com/cgm/core/datasource_utils/mybatis/setting/mybatis-setting-mysql.xml] cannot be opened because it does not exist
	at org.springframework.core.io.ClassPathResource.getInputStream(ClassPathResource.java:215)
	at org.mybatis.spring.SqlSessionFactoryBean.buildSqlSessionFactory(SqlSessionFactoryBean.java:600)
```

### ERROR-5 : BeanInstantiationException
* 원인 : ./main/java/com/cgmoffice/core/datasource_utils/core/DataSourceCore.java 에 *_SQL.xml 경로가 하드코딩 되어 있음
* 해결 : 경로 수정 , com/cgm/ -> com/cgmoffice/
  * applicationContext.getResources("classpath*:/com/cgmoffice/**/*_SQL.xml");
* 원인2 : aspectj dependency 가 없어 NoClassDefFoundError 발생함
* 해결2 : dependency add...
```
Caused by: java.lang.NoClassDefFoundError: org/aspectj/weaver/reflect/ReflectionWorld$ReflectionWorldException
at com.cgmoffice.core.datasource_utils.core.DataSourceCore.makeAdvisor(DataSourceCore.java:69)
at com.cgmoffice.core.config.DataSourceConfig.appAdvisor(DataSourceConfig.java:102)
at com.cgmoffice.core.config.DataSourceConfig$$SpringCGLIB$$0.CGLIB$appAdvisor$6(<generated>)

# 찾기 명령어
pisnetdev2023@pisnetdev2023ui-MacBookAir src % find . -name "*.java" -exec grep 'com\/cgm\/' -l {} \;
./main/java/com/cgmoffice/core/datasource_utils/core/DataSourceCore.java

# dependency add
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.22.1</version>
</dependency>
```
