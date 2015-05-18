package com.clinovo.rest.service;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * WadlService.
 */
@Controller("restWadlService")
@RequestMapping("/wadl")
public class WadlService {

	public static final String XS_NAMESPACE = "http://www.w3.org/2001/XMLSchema";

	@Autowired
	private ApplicationContext applicationContext;

	@RequestMapping
	@ResponseBody
	public Application main(HttpServletRequest request) {
		Application result = new Application();
		Doc doc = new Doc();
		doc.setTitle("ClinCapture REST API Service WADL");
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

				// Request
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
							String defaultValue = cleanDefault(param2.defaultValue());
							if (!param2.required()) {
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

				// Response
				if (!mediaTypes.isEmpty()) {
					Response wadlResponse = new Response();
					// Class methodReturn = handlerMethod.getReturnType().getClass();
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

	private QName convertJavaToXMLType(Class<?> type) {
		QName nm = new QName("");
		String className = type.toString();
		if (className.contains("String")) {
			nm = new QName(XS_NAMESPACE, "string", "xs");
		} else if (className.contains("Integer")) {
			nm = new QName(XS_NAMESPACE, "integer", "xs");
		} else if (className.contains("int")) {
			nm = new QName(XS_NAMESPACE, "int", "xs");
		} else if (className.contains("boolean")) {
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

	private String cleanDefault(String value) {
		value = value.replaceAll("\t", "");
		value = value.replaceAll("\n", "");
		return value;
	}
}