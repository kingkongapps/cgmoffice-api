package com.cgmoffice.core.exception;

import org.apache.commons.lang3.StringUtils;

import com.cgmoffice.core.constant.BaseResponseCode;
import com.cgmoffice.core.utils.MessageI18nUtils;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CmmnBizException extends RuntimeException {

	public static final int DEFAULT_HTTP_STATUS_CODE    = 500;
	public static final String DEFAULT_RESPONSE_CODE    = BaseResponseCode.FE50001.code();
	public static final String DEFAULT_RESPONSE_MESSAGE = BaseResponseCode.FE50001.msg();
	public static final String DEFAULT_MESSAGE_KEY      = "{global.error-server.message}";

	protected final int    httpStatusCode;
	protected final String responseCode;
	protected final String responseMessage;

	private static final long serialVersionUID = 4070245570834594903L;

	@Builder
	public CmmnBizException(int httpStatusCode, String responseCode, String message, Throwable cause) {
		super(message, cause);
		this.httpStatusCode  = httpStatusCode == 0 ? DEFAULT_HTTP_STATUS_CODE : httpStatusCode;
		this.responseCode    = StringUtils.isBlank(responseCode) ? DEFAULT_RESPONSE_CODE : responseCode;
		this.responseMessage = StringUtils.isBlank(message) ? MessageI18nUtils.getExtMessage(DEFAULT_MESSAGE_KEY) : MessageI18nUtils.getExtMessage(message);
	}

	public CmmnBizException() {
		this(DEFAULT_HTTP_STATUS_CODE, DEFAULT_RESPONSE_CODE, MessageI18nUtils.getExtMessage(DEFAULT_MESSAGE_KEY), (Throwable)null);
	}

	public CmmnBizException(String message) {
		this(DEFAULT_HTTP_STATUS_CODE, DEFAULT_RESPONSE_CODE, MessageI18nUtils.getExtMessage(message), (Throwable)null);
	}

	public CmmnBizException(String message, Throwable cause) {
		this(DEFAULT_HTTP_STATUS_CODE, DEFAULT_RESPONSE_CODE, MessageI18nUtils.getExtMessage(message), cause);
	}

	public CmmnBizException(String message, Object[] msgArgs) {
		this(DEFAULT_HTTP_STATUS_CODE, DEFAULT_RESPONSE_CODE, MessageI18nUtils.getExtMessage(message, msgArgs), (Throwable)null);
	}

	public CmmnBizException(String message, Object[] msgArgs, Throwable cause) {
		this(DEFAULT_HTTP_STATUS_CODE, DEFAULT_RESPONSE_CODE, MessageI18nUtils.getExtMessage(message, msgArgs), cause);
	}

	public CmmnBizException(String responseCode, String message) {
		this(DEFAULT_HTTP_STATUS_CODE, responseCode, MessageI18nUtils.getExtMessage(message), (Throwable)null);
	}

	public CmmnBizException(String responseCode, String message, Throwable cause) {
		this(DEFAULT_HTTP_STATUS_CODE, responseCode, MessageI18nUtils.getExtMessage(message), cause);
	}

	public CmmnBizException(String responseCode, String message, Object[] msgArgs) {
		this(DEFAULT_HTTP_STATUS_CODE, responseCode, MessageI18nUtils.getExtMessage(message, msgArgs), (Throwable)null);
	}

	public CmmnBizException(String responseCode, String message, Object[] msgArgs, Throwable cause) {
		this(DEFAULT_HTTP_STATUS_CODE, responseCode, MessageI18nUtils.getExtMessage(message, msgArgs), cause);
	}

	public CmmnBizException(int httpStatusCode, String responseCode, String message) {
		this(httpStatusCode, responseCode, MessageI18nUtils.getExtMessage(message), (Throwable)null);
	}

	public CmmnBizException(int httpStatusCode, String responseCode, String message, Object[] msgArgs) {
		this(httpStatusCode, responseCode, MessageI18nUtils.getExtMessage(message, msgArgs), (Throwable)null);
	}

	@Builder
	public CmmnBizException(int httpStatusCode, String responseCode, String message, Object[] msgArgs, Throwable cause) {
		this(httpStatusCode, responseCode, MessageI18nUtils.getExtMessage(message, msgArgs), cause);
	}

}