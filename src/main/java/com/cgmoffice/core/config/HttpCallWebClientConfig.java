package com.cgmoffice.core.config;

import java.time.Duration;

import javax.net.ssl.SSLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.cgmoffice.core.exception.CmmnBizException;
import com.cgmoffice.core.properties.CmmnProperties;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
@RequiredArgsConstructor
public class HttpCallWebClientConfig {

	private final CmmnProperties cmmnProperties;

    @Bean
    WebClient webClient() {

		ConnectionProvider connectionProvider = ConnectionProvider.builder("custom")
		    // connection 최대 연결 수
			.maxConnections(cmmnProperties.getHttpCall().getMaxConnections()) // 최대 연결 수
		    // connection 최대 연결 수 초과시 연결대기 시간
			.pendingAcquireTimeout(Duration.ofSeconds(cmmnProperties.getHttpCall().getPendingAcquireTimeoutSeconds())) // 최대 연결 수 초과시 연결대기 시간
		    .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
        		.secure(sslContextSpec -> {
					try {
						// X.509는 SSL/TLS 인증서의 표준 형식이며, 주로 인증서를 검증하거나 처리하는 과정에서 발생하는 오류인데 이를 무력화하는 로직
		        		// SSL 인증서 확인을 비활성화 => 즉, 클라이언트는 유효하지 않거나 신뢰할 수 없는 인증서라도 모든 SSL 인증서를 수락
		        		// 만일 인증서를 적용하려면... application.yml 에 server.ssl 의 enabled, key-alias, key-store, key-store-password, key-store-type 를 설졍해준다.
		        		//  => 관련예시 ( https://kimyhcj.tistory.com/381 )
						sslContextSpec.sslContext(SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build());
					} catch (SSLException e) {
						throw new CmmnBizException(e.getMessage(), e);
					}
				})
	            .option(
	            		// remote 연결 타임아웃
	            		ChannelOption.CONNECT_TIMEOUT_MILLIS, cmmnProperties.getHttpCall().getConnectTimoutMillis()
	            		)
	            // remote 응답 타임아웃
	            .responseTimeout(Duration.ofSeconds(cmmnProperties.getHttpCall().getResponseTimeoutSeconds())) // remote 응답 타임아웃
	            ;

        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }

}
