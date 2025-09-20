package com.cgmoffice.core.properties;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.cgmoffice.core.dao.AppDao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "cmmn-properties")
@Getter @Setter
@RequiredArgsConstructor
public class CmmnProperties {
	private final AppDao appDao;

	private String rscVer;
	private String ehcacheConfig; // ehcache 설정파일경로

	// QR코드생성용 URL
	public String getQrUrl() {
		return appDao.selectOne("cmmnproperties.getQrUrl");
	}

	private FileMng fileMng;

	private String aes256Key;

	private String active;

	private String comCode;

	private String isLogin;

	private String pdfDir;  // pdf 파일을 저장한 폴더명

	@Value("${spring.app.datasource.driver-class-name}")
	private String appDriverClassName;

	public String getAppDatabaseId() {

    	if(StringUtils.containsIgnoreCase(appDriverClassName, "mysql")
    			|| StringUtils.containsIgnoreCase(appDriverClassName, "mariadb")
    			) {
    		return "mysql";
    	} else if(StringUtils.containsIgnoreCase(appDriverClassName, "postgresql")) {
    		return "postgresql";
    	} else if(StringUtils.containsIgnoreCase(appDriverClassName, "db2")) {
    		return "db2";
    	} else if(StringUtils.containsIgnoreCase(appDriverClassName, "sqlserver")) {
    		return "sqlserver";
    	} else if(StringUtils.containsIgnoreCase(appDriverClassName, "oracle")) {
    		return "oracle";
    	} else {
    		return "";
    	}
	}

	@Value("${spring.module.datasource.driver-class-name}")
	private String moduleDriverClassName;

	public String getModuleDatabaseId() {

    	if(StringUtils.containsIgnoreCase(moduleDriverClassName, "mysql")
    			|| StringUtils.containsIgnoreCase(moduleDriverClassName, "mariadb")
    			) {
    		return "mysql";
    	} else if(StringUtils.containsIgnoreCase(moduleDriverClassName, "postgresql")) {
    		return "postgresql";
    	} else if(StringUtils.containsIgnoreCase(moduleDriverClassName, "db2")) {
    		return "db2";
    	} else if(StringUtils.containsIgnoreCase(moduleDriverClassName, "sqlserver")) {
    		return "sqlserver";
    	} else if(StringUtils.containsIgnoreCase(moduleDriverClassName, "oracle")) {
    		return "oracle";
    	} else {
    		return "";
    	}
	}

	@Getter @Setter
	public static class FileMng{
		private String uploadRootPath;  // 파일업로드 root 경로
		private String uploadDenyExtList;  // 파일업로드 거부 확장자목록
		private long uploadVideoMaxSize; // 동영상파일 업로드 최대사이즈 (Byte)
		private long uploadAudioMaxSize; // 오디오파일 업로드 최대사이즈 (Byte)
		private long uploadMaxSize;  // 파일업로드 최대사이즈 (Byte)
	}

	private Jwt jwt;

	@Getter @Setter
	public static class Jwt{
		int tokenValidityInSeconds;
	}

	private HttpCall httpCall;

	@Getter @Setter
	public static class HttpCall{
		int maxConnections; // connection 최대 연결 수
		int pendingAcquireTimeoutSeconds; // connection 최대 연결 수 초과시 연결대기 시간
		int connectTimoutMillis; // remote 연결 타임아웃
		int responseTimeoutSeconds; // remote 응답 타임아웃
		List<String> allowedOrigins;
	}

	private ScheduleSetting scheduleSetting;

	@Getter @Setter
	public static class ScheduleSetting {
		private String cron01;
	}
}
