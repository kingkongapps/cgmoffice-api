package com.cgmoffice.core.license;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.cgmoffice.core.dto.LicenseInfoDto;
import com.cgmoffice.core.dto.LicenseInfoDto.DistinguishedNameDto;
import com.cgmoffice.core.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LicenseVerifier implements ApplicationRunner {

	@Value("${cmmn-properties.license.mgr.jar.path}")
	private String licenseMgrJarPath;

	@Value("${cmmn-properties.active}")
	private String active;


	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (!isLicenseValid()) {
            System.err.println("라이선스 키가 유효하지 않습니다.");
            System.exit(1); // Spring Boot 강제 종료
        }
	}

	private boolean isLicenseValid() throws IOException, InterruptedException {

		// 로컬,개발의 경우는 그냥 pass
        if("local".equals(active) || "dev".equals(active)) return true;

		String jarPath = new File(licenseMgrJarPath).getAbsolutePath();

        ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath, "load");
//        ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath, "verify");
        Process process = pb.start();

     // 표준 출력 읽기
        BufferedReader stdReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder outputBuilder = new StringBuilder();
        String line;
        while ((line = stdReader.readLine()) != null) {
            outputBuilder.append(line).append(System.lineSeparator());
        }

        // 표준 에러 출력 읽기 (선택 사항)
        BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder errorBuilder = new StringBuilder();
        while ((line = errReader.readLine()) != null) {
            errorBuilder.append(line).append(System.lineSeparator());
        }

        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("라이센스가 검증되었습니다.");
            LicenseInfoDto dto = parse(outputBuilder.toString());
            System.out.println(JsonUtils.toJsonStr(dto));

            return true;
        } else {
            System.err.println("라이센스가 검증되지 않았습니다.");
            System.err.println("비정상 종료 (exit code: " + exitCode + ")");
            System.err.println("오류 로그 : " + errorBuilder.toString());
            return false;
        }
	}

	private LicenseInfoDto parse(String log) {

        LicenseInfoDto dto = new LicenseInfoDto();

		if(StringUtils.isEmpty(log)) return dto;

        int start = log.indexOf('[');
        int end = log.lastIndexOf(']');
        if (start == -1 || end == -1 || start >= end) {
            throw new IllegalArgumentException("잘못된 로그 형식입니다.");
        }

        String content = log.substring(start + 1, end);
        String[] parts = content.split(", (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // 쉼표로 분리하되 따옴표 안 쉼표는 제외

        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length != 2) continue;

            String key = kv[0].trim();
            String value = kv[1].trim().replaceAll("^\"|\"$", ""); // 양쪽 따옴표 제거

            switch (key) {
                case "subject": dto.setSubject(value); break;
                case "holder": dto.setHolder(distinguishedNameDto(value)); break;
                case "issuer": dto.setIssuer(distinguishedNameDto(value)); break;
                case "issued": dto.setIssued(value); break;
                case "notBefore": dto.setNotBefore("null".equals(value) ? null : value); break;
                case "notAfter": dto.setNotAfter("null".equals(value) ? null : value); break;
                case "consumerType": dto.setConsumerType(value); break;
                case "consumerAmount":
                    try {
                        dto.setConsumerAmount(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        dto.setConsumerAmount(null);
                    }
                    break;
                case "info": dto.setInfo("null".equals(value) ? null : value); break;
            }
        }

        return dto;
    }

	private DistinguishedNameDto distinguishedNameDto(String dn) {
        DistinguishedNameDto dto = new DistinguishedNameDto();
        if (dn == null) return dto;

        String[] parts = dn.split(",\\s*");
        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length != 2) continue;
            String key = kv[0].trim().toUpperCase();
            String value = kv[1].trim();

            switch (key) {
                case "CN": dto.setCn(value); break;
                case "OU": dto.setOu(value); break;
                case "O": dto.setO(value); break;
                case "C": dto.setC(value); break;
            }
        }
        return dto;
    }

}
