/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * <p/>
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * <p/>
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * <p/>
 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.rest.service.base;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.clinovo.enums.BaseEnum;
import com.clinovo.enums.StudyConfigurationParameterType;
import com.clinovo.i18n.LocaleResolver;
import com.clinovo.rest.annotation.EnumBasedParameters;
import com.clinovo.rest.annotation.EnumBasedParametersHolder;
import com.clinovo.rest.annotation.PossibleValues;
import com.clinovo.rest.annotation.PossibleValuesHolder;
import com.clinovo.rest.model.wadl.Application;
import com.clinovo.rest.model.wadl.Doc;
import com.clinovo.rest.model.wadl.Method;
import com.clinovo.rest.model.wadl.Param;
import com.clinovo.rest.model.wadl.Request;
import com.clinovo.rest.model.wadl.Resource;
import com.clinovo.rest.model.wadl.Resources;
import com.clinovo.rest.model.wadl.Values;

/**
 * BaseWadlService.
 */
@SuppressWarnings("unchecked")
public abstract class BaseWadlService extends BaseService {

	public static final String QUERY = "query";
	public static final String TEMPLATE = "template";
	public static final String NOT_USED = "[not used]";
	public static final String HTTP_WADL_DEV_JAVA_NET_2009_02 = "http://wadl.dev.java.net/2009/02";
	public static final String HTTP_WWW_W3_ORG_2001_XMLSCHEMA = "http://www.w3.org/2001/XMLSchema";

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private MessageSource messageSource;

	private class PossibleValuesInfo {
		private String values;
		private String dependOn;
		private String valueDescriptions;
	}

	private String convertJavaToXMLType(Class<?> type) {
		String result = "";
		String className = type.toString();
		if (className.contains("String")) {
			result = "xs:string".concat(className.contains("[L") ? "[]" : "");
		} else if (className.contains("Integer")) {
			result = "xs:integer".concat(className.contains("[L") ? "[]" : "");
		} else if (className.contains("int")) {
			result = "xs:int";
		} else if (className.contains("boolean") || className.contains("Boolean")) {
			result = "xs:boolean";
		} else if (className.contains("java.util.Date")) {
			result = "xs:date";
		}
		return result;
	}

	private Resource createOrFind(String uri, Resources resources) {
		List<Resource> current = resources.getResourceList();
		for (Resource resource : current) {
			if (resource.getPath().equalsIgnoreCase(uri)) {
				return resource;
			}
		}
		Resource resource = new Resource();
		current.add(resource);
		return resource;
	}

	private String getBaseUrl(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		return request.getScheme().concat("://").concat(request.getServerName()).concat(":")
				.concat(Integer.toString(request.getServerPort())).concat(requestUri.replaceAll("/wadl.*", ""));
	}

	private void setPossibleValues(Param param, String dependOn, String value, String values, String description) {
		Values paramValues = new Values();
		paramValues.setValue(values);
		if (description != null && !description.trim().isEmpty()) {
			paramValues.setDescription(description);
		}
		if (dependOn != null && !dependOn.trim().isEmpty()) {
			paramValues.setDependOn(dependOn);
			paramValues.setDependOnValue(value);
		}
		param.getValuesList().add(paramValues);
	}

	private void setPossibleValues(Param param, Map<String, PossibleValuesInfo> possibleValuesMap) {
		PossibleValuesInfo possibleValuesInfo = possibleValuesMap.get(param.getName());
		if (possibleValuesInfo != null) {
			if (possibleValuesInfo.dependOn.isEmpty()) {
				setPossibleValues(param, "", "", possibleValuesInfo.values,
						!possibleValuesInfo.valueDescriptions.isEmpty()
								? messageSource.getMessage(possibleValuesInfo.valueDescriptions, null,
										LocaleResolver.getLocale())
								: "");

			} else {
				PossibleValuesInfo dependOnPossibleValuesInfo = possibleValuesMap.get(possibleValuesInfo.dependOn);
				for (String value : dependOnPossibleValuesInfo.values.split(",")) {
					setPossibleValues(param, possibleValuesInfo.dependOn, value,
							messageSource.getMessage(possibleValuesInfo.values.replace("{#}", value), null,
									LocaleResolver.getLocale()),
							messageSource.getMessage(possibleValuesInfo.valueDescriptions.replace("{#}", value), null,
									LocaleResolver.getLocale()));
				}
			}
		}
	}

	private String getValues(String[] values) {
		String result = "";
		for (String value : values) {
			result += (result.isEmpty() ? "" : ",") + value;
		}
		return result;
	}

	private Map<String, PossibleValuesInfo> preparePossibleValuesInfoMap(HandlerMethod handlerMethod) {
		Map<String, PossibleValuesInfo> possibleValuesInfoMap = new HashMap<String, PossibleValuesInfo>();
		Annotation[] annotationArray = handlerMethod.getMethod().getAnnotations();
		if (annotationArray != null) {
			for (Annotation annotation : annotationArray) {
				if (annotation instanceof PossibleValuesHolder) {
					for (PossibleValues possibleValues : ((PossibleValuesHolder) annotation).value()) {
						PossibleValuesInfo possibleValuesInfo = new PossibleValuesInfo();
						possibleValuesInfo.values = possibleValues.values();
						possibleValuesInfo.dependOn = possibleValues.dependentOn();
						possibleValuesInfo.valueDescriptions = possibleValues.valueDescriptions();
						possibleValuesInfoMap.put(possibleValues.name(), possibleValuesInfo);
					}
				}
			}
		}
		return possibleValuesInfoMap;
	}

	private void processMethodParameterAnnotations(HandlerMethod handlerMethod, Method method) {
		int i = 0;
		Class<?>[] paramTypes = handlerMethod.getMethod().getParameterTypes();
		Annotation[][] annotations = handlerMethod.getMethod().getParameterAnnotations();
		Map<String, PossibleValuesInfo> possibleValuesInfoMap = preparePossibleValuesInfoMap(handlerMethod);
		for (Annotation[] annotation : annotations) {
			Class<?> paramType = paramTypes[i++];
			for (Annotation annotation2 : annotation) {
				if (annotation2 instanceof RequestParam) {
					RequestParam param2 = (RequestParam) annotation2;
					Param param = new Param();
					String type = convertJavaToXMLType(paramType);
					param.setName(param2.value());
					param.setStyle(QUERY);
					param.setRequired(param2.required());
					if (!param2.required()) {
						String defaultValue = param2.defaultValue();
						defaultValue = defaultValue.equals(ValueConstants.DEFAULT_NONE) ? NOT_USED : defaultValue;
						param.setDefaultValue(defaultValue);
					}
					param.setType(type);
					param.setXmlnsXs(HTTP_WWW_W3_ORG_2001_XMLSCHEMA);
					setPossibleValues(param, possibleValuesInfoMap);
					method.getRequest().getParamList().add(param);
				} else if (annotation2 instanceof PathVariable) {
					PathVariable param2 = (PathVariable) annotation2;
					String type = convertJavaToXMLType(paramType);
					Param param = new Param();
					param.setName(param2.value());
					param.setStyle(TEMPLATE);
					param.setRequired(true);
					param.setType(type);
					param.setXmlnsXs(HTTP_WWW_W3_ORG_2001_XMLSCHEMA);
					setPossibleValues(param, possibleValuesInfoMap);
					method.getRequest().getParamList().add(param);
				}
			}
		}

	}

	private void processMethodAnnotations(HandlerMethod handlerMethod, Method method) {
		EnumBasedParametersHolder enumBasedParametersHolder = handlerMethod.getMethod()
				.getAnnotation(EnumBasedParametersHolder.class);
		EnumBasedParameters[] enumBasedParameters = enumBasedParametersHolder != null
				? enumBasedParametersHolder.value()
				: null;
		if (enumBasedParameters != null && enumBasedParameters.length > 0) {
			for (EnumBasedParameters enumBasedParameter : enumBasedParameters) {
				Class<? extends BaseEnum> enumClass = enumBasedParameter.enumClass();
				boolean useDefaultValues = enumBasedParameter.useDefaultValues();
				for (BaseEnum baseEnum : (List<BaseEnum>) enumClass.getEnumConstants()[0].asArray()) {
					String type = convertJavaToXMLType(String.class);
					Param param = new Param();
					param.setName(baseEnum.getName());
					param.setStyle(QUERY);
					param.setRequired(baseEnum.isRequired());
					param.setType(type);
					param.setDefaultValue(useDefaultValues ? baseEnum.getDefaultValue() : NOT_USED);
					param.setXmlnsXs(HTTP_WWW_W3_ORG_2001_XMLSCHEMA);
					if (baseEnum.getType() == StudyConfigurationParameterType.SELECT
							|| baseEnum.getType() == StudyConfigurationParameterType.RADIO) {
						Values values = new Values();
						values.setValue(getValues(baseEnum.getValues()));
						param.getValuesList().add(values);
					}
					method.getRequest().getParamList().add(param);
				}
			}
		}
	}

	protected Application prepareWadl(HttpServletRequest request) {
		Application result = new Application();
		result.setDoc(new Doc());
		result.setResources(new Resources());
		result.getResources().setBase(getBaseUrl(request));
		result.setXmlns(HTTP_WADL_DEV_JAVA_NET_2009_02);
		result.getDoc().setTitle(messageSource.getMessage("rest.wadl.title", null, LocaleResolver.getLocale()));
		RequestMappingHandlerMapping handlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
		Map<RequestMappingInfo, HandlerMethod> handletMethods = handlerMapping.getHandlerMethods();
		for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handletMethods.entrySet()) {
			HandlerMethod handlerMethod = entry.getValue();
			Object object = handlerMethod.getBean();
			Object bean = applicationContext.getBean(object.toString());
			boolean isRestContoller = bean instanceof BaseService;
			if (!isRestContoller) {
				continue;
			}
			RequestMappingInfo mappingInfo = entry.getKey();
			Set<String> pattern = mappingInfo.getPatternsCondition().getPatterns();
			Set<RequestMethod> httpMethods = mappingInfo.getMethodsCondition().getMethods();
			for (RequestMethod httpMethod : httpMethods) {
				Method method = new Method();
				for (String uri : pattern) {
					Resource resource = createOrFind(uri, result.getResources());
					resource.setPath(uri);
					resource.setMethod(method);
				}
				method.setName(httpMethod.name());
				method.setId(handlerMethod.getMethod().getName());
				method.setDoc(new Doc());
				method.getDoc().setTitle(handlerMethod.getMethod().getDeclaringClass().getSimpleName() + "."
						+ handlerMethod.getMethod().getName());
				method.setRequest(new Request());
				processMethodParameterAnnotations(handlerMethod, method);
				processMethodAnnotations(handlerMethod, method);
			}
		}
		return result;
	}
}
