/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.rest.service.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import org.jvnet.ws.wadl.Application;
import org.jvnet.ws.wadl.Doc;
import org.jvnet.ws.wadl.Param;
import org.jvnet.ws.wadl.ParamStyle;
import org.jvnet.ws.wadl.Representation;
import org.jvnet.ws.wadl.Request;
import org.jvnet.ws.wadl.Resource;
import org.jvnet.ws.wadl.Resources;
import org.jvnet.ws.wadl.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.clinovo.i18n.LocaleResolver;

/**
 * BaseWadlService.
 */
public abstract class BaseWadlService extends BaseService {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private MessageSource messageSource;

	private QName convertJavaToXMLType(Class<?> type) {
		QName nm = new QName("");
		String className = type.toString();
		if (className.contains("String")) {
			nm = new QName(XS_NAMESPACE, "string", "xs");
		} else if (className.contains("Integer")) {
			nm = new QName(XS_NAMESPACE, "integer", "xs");
		} else if (className.contains("int")) {
			nm = new QName(XS_NAMESPACE, "int", "xs");
		} else if (className.contains("boolean") || className.contains("Boolean")) {
			nm = new QName(XS_NAMESPACE, "boolean", "xs");
		}
		return nm;
	}

	private Resource createOrFind(String uri, Resources wadResources) {
		List<Resource> current = wadResources.getResource();
		for (Resource resource : current) {
			if (resource.getPath().equalsIgnoreCase(uri)) {
				return resource;
			}
		}
		Resource wadlResource = new Resource();
		current.add(wadlResource);
		return wadlResource;
	}

	private String getBaseUrl(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		return request.getScheme().concat("://").concat(request.getServerName()).concat(":")
				.concat(Integer.toString(request.getServerPort())).concat(requestUri.replaceAll("/wadl.*", ""));
	}

	protected Application prepareWadl(HttpServletRequest request) {
		Application result = new Application();
		Doc doc = new Doc();
		doc.setTitle(messageSource.getMessage("rest.wadl.title", null, LocaleResolver.getLocale()));
		result.getDoc().add(doc);
		Resources wadResources = new Resources();
		wadResources.setBase(getBaseUrl(request));
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
			ProducesRequestCondition producesRequestCondition = mappingInfo.getProducesCondition();
			Set<MediaType> mediaTypes = producesRequestCondition.getProducibleMediaTypes();
			Resource wadlResource = null;
			for (RequestMethod httpMethod : httpMethods) {
				org.jvnet.ws.wadl.Method wadlMethod = new org.jvnet.ws.wadl.Method();
				for (String uri : pattern) {
					wadlResource = createOrFind(uri, wadResources);
					wadlResource.setPath(uri);
				}
				wadlMethod.setName(httpMethod.name());
				Method javaMethod = handlerMethod.getMethod();
				wadlMethod.setId(javaMethod.getName());
				Doc wadlDocMethod = new Doc();
				wadlDocMethod.setTitle(javaMethod.getDeclaringClass().getSimpleName() + "." + javaMethod.getName());
				wadlMethod.getDoc().add(wadlDocMethod);
				Request wadlRequest = new Request();
				Annotation[][] annotations = javaMethod.getParameterAnnotations();
				Class<?>[] paramTypes = javaMethod.getParameterTypes();
				int i = 0;
				for (Annotation[] annotation : annotations) {
					Class<?> paramType = paramTypes[i];
					i++;
					for (Annotation annotation2 : annotation) {

						if (annotation2 instanceof RequestParam) {
							RequestParam param2 = (RequestParam) annotation2;
							Param waldParam = new Param();
							QName nm = convertJavaToXMLType(paramType);
							waldParam.setName(param2.value());
							waldParam.setStyle(ParamStyle.QUERY);
							waldParam.setRequired(param2.required());
							if (!param2.required()) {
								String defaultValue = param2.defaultValue();
								defaultValue = defaultValue.equals(ValueConstants.DEFAULT_NONE)
										? "[not used]"
										: defaultValue;
								waldParam.setDefault(defaultValue);
							}
							waldParam.setType(nm);
							wadlRequest.getParam().add(waldParam);
						} else if (annotation2 instanceof PathVariable) {
							PathVariable param2 = (PathVariable) annotation2;
							QName nm = convertJavaToXMLType(paramType);
							Param waldParam = new Param();
							waldParam.setName(param2.value());
							waldParam.setStyle(ParamStyle.TEMPLATE);
							waldParam.setRequired(true);
							wadlRequest.getParam().add(waldParam);
							waldParam.setType(nm);
						}
					}
				}
				if (!wadlRequest.getParam().isEmpty()) {
					wadlMethod.setRequest(wadlRequest);
				}
				if (!mediaTypes.isEmpty()) {
					Response wadlResponse = new Response();
					ResponseStatus status = handlerMethod.getMethodAnnotation(ResponseStatus.class);
					if (status == null) {
						wadlResponse.getStatus().add((long) (HttpStatus.OK.value()));
					} else {
						HttpStatus httpcode = status.value();
						wadlResponse.getStatus().add((long) httpcode.value());
					}

					for (MediaType mediaType : mediaTypes) {
						Representation wadlRepresentation = new Representation();
						wadlRepresentation.setMediaType(mediaType.toString());
						wadlResponse.getRepresentation().add(wadlRepresentation);
					}
					wadlMethod.getResponse().add(wadlResponse);
				}
				if (wadlResource != null) {
					wadlResource.getMethodOrResource().add(wadlMethod);
				}
			}

		}
		result.getResources().add(wadResources);
		return result;
	}
}
