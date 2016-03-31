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

package com.clinovo.rest.conversion;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.RequestParam;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.rest.exception.RestException;
import com.clinovo.util.DateUtil;

/**
 * RestConversionService.
 */
public class RestConversionService extends DefaultConversionService {

	@Autowired
	private MessageSource messageSource;

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		String targetTypeName = targetType.getType().getName();
		try {
			if (targetTypeName.equals("java.util.Date")) {
				return convertDate(source, sourceType, targetType);
			} else {
				return super.convert(source, sourceType, targetType);
			}
		} catch (ConversionFailedException ex) {
			Object[] args;
			String errorMessageKey = "rest.wrongParameterType";
			targetTypeName = targetTypeName.equals("java.lang.Boolean") ? "boolean" : targetTypeName;
			targetTypeName = targetTypeName.equals("java.lang.Integer") ? "int" : targetTypeName;
			targetTypeName = targetTypeName.equals("[Ljava.lang.Integer;") ? "Integer[]" : targetTypeName;
			if (targetTypeName.equals("java.util.Date")) {
				errorMessageKey = "rest.wrongDateParameterType";
				args = new Object[]{targetType.getAnnotation(RequestParam.class).value(), DateUtil.ISO_DATE};
			} else {
				args = new Object[]{targetType.getAnnotation(RequestParam.class).value(), targetTypeName};
			}
			throw new RestException(messageSource, errorMessageKey, args, HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private Date convertDate(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		Date result;
		try {
			result = DateUtil.parseDateString(source.toString(), DateUtil.DatePattern.ISO_DATE,
					LocaleResolver.getLocale());
		} catch (Exception ex) {
			throw new ConversionFailedException(sourceType, targetType, source, ex);
		}
		return result;
	}
}
