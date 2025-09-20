package com.cgmoffice.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LicenseInfoDto {

	// 이 라이선스가 적용되는 대상(제품명)을 의미
    private String subject;

    // 라이선스를 소유한 주체
    private DistinguishedNameDto holder;

    // 라이선스를 발급한 조직(또는 인증기관)
    private DistinguishedNameDto issuer;

    // 라이선스 발급 날짜
    private String issued;

    // 이 라이선스가 유효해지기 시작하는 날짜. (null이면 즉시 사용 가능)
    private String notBefore;

    // 이 라이선스의 만료일. (null이면 무제한이거나 만료 조건 없음)
    private String notAfter;

    // 이 라이선스가 적용되는 사용자 유형. 예: "User", "Company", "Developer" 등.
    private String consumerType;

    // 이 라이선스로 허용된 사용자 수/인스턴스 수야. 예: 1이면 1명 또는 1대의 컴퓨터에서만 사용 가능
    private Integer consumerAmount;

    // 기타 정보. 확장 가능한 필드로, 메모나 추가 정보가 들어갈 수 있음. 예: 제품 버전, 서브 도메인 등. (null이면 없음)
    private String info;

    @Builder
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static public class DistinguishedNameDto {

    	// Common Name: 개인 이름, 서버 도메인 이름 등 인증서의 이름 식별자
        private String cn;

        // Organizational Unit: 소속 부서 (예: IT 부서, 보안팀 등)
        private String ou;

        // Organization Name: 조직 이름 또는 회사 이름
        private String o;

        // Country Code: 국가 코드 (ISO 3166-1 alpha-2 기준)
        private String c;
    }
}
