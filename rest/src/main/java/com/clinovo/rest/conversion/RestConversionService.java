package com.clinovo.rest.conversion;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.RequestParam;

import com.clinovo.rest.exception.RestException;

/**
 * RestConversionService.
 */
public class RestConversionService extends DefaultConversionService {

	@Autowired
	private MessageSource messageSource;

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		try {
			return super.convert(source, sourceType, targetType);
		} catch (ConversionFailedException ex) {
			throw new RestException(messageSource, "rest.wrongParameterType", new Object[]{
					targetType.getAnnotation(RequestParam.class).value(), targetType.getType().getName()},
					HttpServletResponse.SC_BAD_REQUEST);
		}
	}
}
