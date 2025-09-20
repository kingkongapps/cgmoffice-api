package com.cgmoffice.core.utils;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import com.cgmoffice.core.constant.BaseResponseCode;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageI18nUtils {

    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
   		MessageI18nUtils.messageSource = messageSource;
    }

    /**
     * @param messageCd - 중괄호 없는 message code
     * @return
     */
	public String getMessage(final String messageCd) {
    	return getMessageWithTryCatch(messageCd, null, LocaleContextHolder.getLocale());
    }

	/**
	 * @param messageCd - 중괄호 없는 message code
	 * @param messageArgs
	 * @return
	 */
    public String getMessage(final String messageCd, final Object[] messageArgs) {
    	return getMessageWithTryCatch(messageCd, messageArgs, LocaleContextHolder.getLocale());
    }

    /**
     * @param messageCd - 중괄호 없는 message code
     * @param messageArgs
     * @param locale
     * @return
     */
    public String getMessage(final String messageCd, final Object[] messageArgs, Locale locale) {
    	return getMessageWithTryCatch(messageCd, messageArgs, locale);
    }

    /**
     * @param messageCd - 중괄호"{}" 포함한 message code
     * @return
     */
    public String getExtMessage(final String messageCd) {
    	String valMsg = ValidationUtils.isMatched4GetMethodParam(ValidationUtils.BD_ERROR_PARAM_2_PATTERN, messageCd);
		if(StringUtils.isNotBlank(valMsg)) {
			return MessageI18nUtils.getMessage(valMsg);
		} else {
			return messageCd;
		}
    }

    /**
     * @param messageCd  - 중괄호"{}" 포함한 message code
     * @param messageArgs arguments
     * @return
     */
    public String getExtMessage(final String messageCd, final Object[] messageArgs) {
    	String valMsg = ValidationUtils.isMatched4GetMethodParam(ValidationUtils.BD_ERROR_PARAM_2_PATTERN, messageCd);
		if(StringUtils.isNotBlank(valMsg)) {
			return MessageI18nUtils.getMessage(valMsg, messageArgs);
		} else {
			return messageCd;
		}
    }

	private String getMessageWithTryCatch(final String messageCd, Object[] args, Locale locale) {
		String rvMsg;
    	try {
    		rvMsg = messageSource.getMessage(messageCd, args, locale);
    	} catch(NoSuchMessageException ne) {
    		ExceptionUtils.printRootCauseStackTrace(ne); // Exception logging!
    		rvMsg = BaseResponseCode.FE10001.msg();
    	}
    	return rvMsg;
	}

}