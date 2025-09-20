package com.cgmoffice.core.argument_resolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.cgmoffice.api.common.dto.ExcelDownConfigDto;
import com.cgmoffice.core.constant.CoreConstants;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;
import com.cgmoffice.core.exception.CmmnBizException;
import com.cgmoffice.core.utils.CmmnMap;
import com.cgmoffice.core.utils.CoreStringUtils;
import com.cgmoffice.core.utils.CoreUtils;
import com.cgmoffice.core.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller 에서의 request mapping 된 메소드의 parameter 를 체크하는 곳이다.
 */
@Slf4j
public class CoreArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {

		Class<?> parameterType = parameter.getParameterType();

		if(PageConfig.class.isAssignableFrom(parameterType)
				|| ExcelDownConfigDto.class.isAssignableFrom(parameterType)
				|| CmmnMap.class.isAssignableFrom(parameterType)
				) {
			return true;
		}

		return false;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		Class<?> parameterType = parameter.getParameterType();

		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

		if(PageConfig.class.isAssignableFrom(parameterType)) {
			return resolvePagingConfig(request);
		}

		if(ExcelDownConfigDto.class.isAssignableFrom(parameterType)) {
			return resolveConfigExcelDn(request);
		}

		if(CmmnMap.class.isAssignableFrom(parameterType)) {
			return resolveCmmnMap(request);
		}
		return null;
	}


	private CmmnMap resolveCmmnMap(HttpServletRequest request) throws IOException {
		CmmnMap cmmnMap = convertParamsMap2CmmnMap(request);
		String contentType = request.getHeader("content-type");

		if(StringUtils.contains(contentType, "form")) {
			return cmmnMap;
		}

		if(StringUtils.isNotEmpty(contentType)) {
			String requestBody = CoreUtils.getRequestBody(request);
			if(StringUtils.isNotEmpty(requestBody)) {
				requestBody = CoreStringUtils.encodeXSS(requestBody);
				cmmnMap.putAll(JsonUtils.fromJsonStr(CmmnMap.class, requestBody));
			}
		}

		return cmmnMap;
	}

	private ExcelDownConfigDto resolveConfigExcelDn(HttpServletRequest request) throws UnsupportedEncodingException {
		String configExcelDn_req = request.getParameter("configExcelDn");
		ExcelDownConfigDto configExcelDn;
		if(StringUtils.isNotEmpty(configExcelDn_req)) {
			configExcelDn_req = CoreStringUtils.encodeXSS(URLDecoder.decode(configExcelDn_req,CoreConstants.GLOBAL_CHARSET.name()));
			configExcelDn = JsonUtils.fromJsonStr(ExcelDownConfigDto.class, configExcelDn_req);
		} else {
			configExcelDn = new ExcelDownConfigDto();
		}
		return configExcelDn;
	}

	private PageConfig resolvePagingConfig(HttpServletRequest request) throws IOException {

		String pagingConfig = request.getHeader("pagingConfig");

		if(StringUtils.isNoneEmpty(pagingConfig)) {
			pagingConfig = CoreStringUtils.encodeXSS(URLDecoder.decode(pagingConfig,CoreConstants.GLOBAL_CHARSET.name()));
			PageConfig pageConfig = JsonUtils.fromJsonStr(PageConfig.class, pagingConfig);
			pageConfig.getOrders().forEach(order -> {
				order.setTarget(CoreStringUtils.camelToSnake(order.getTarget()));
			});

			return pageConfig;
		}
		return null;
	}


	private CmmnMap convertParamsMap2CmmnMap(HttpServletRequest request) throws UnsupportedEncodingException {

		CmmnMap rslt = new CmmnMap();
		request.getParameterMap().forEach((key, arrValue) -> {
			if(arrValue.length > 0) {
				rslt.put(key, resolveReqParamVal(arrValue[0]));
			} else {
				rslt.put(key, "");
			}
		});

		return rslt;
	}

	private Object resolveReqParamVal(String value) {

		try {
//			value = StringUtils.encodeXSS(URLDecoder.decode(value, CoreConstants.GLOBAL_CHARSET.name()));

			if(value.startsWith("[{")) {
				List<CmmnMap> list = new ArrayList<>();
				JsonUtils.fromJsonStr(List.class, value)
					.forEach(obj -> {
						list.add(CoreUtils.cast(CmmnMap.class, obj));
					});
			}

			if(value.startsWith("[")) {
				return JsonUtils.fromJsonStr(List.class, value);
			}

			if(value.startsWith("{")) {
				return JsonUtils.fromJsonStr(CmmnMap.class, value);
			}

			return value;
		} catch(Exception e) {
			log.error(CoreUtils.getExceptionStackTrace(e));
			throw new CmmnBizException(e.getMessage());
		}
	}

}
