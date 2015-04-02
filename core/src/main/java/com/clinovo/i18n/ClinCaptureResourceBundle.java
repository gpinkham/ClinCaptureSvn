/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/
package com.clinovo.i18n;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * ClinCaptureResourceBundle.
 */
public class ClinCaptureResourceBundle extends ResourceBundle {

	public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
	public static final String QQQ = "???";

	private static final int START_INDEX = 5;

	private Set<String> keys;
	private Properties properties;

	private boolean wasCalledFromResourceBundleMessageSource() {
		boolean found = false;
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		if (stackTraceElements.length >= START_INDEX) {
			StackTraceElement stackTraceElement = stackTraceElements[START_INDEX];
			if (stackTraceElement.getClassName().equals(ResourceBundleMessageSource.class.getName())) {
				found = true;
			}
		}
		return found;
	}

	public ClinCaptureResourceBundle(String baseName, Locale locale) {
		keys = new HashSet<String>();
		properties = new Properties();
		Enumeration<String> enumeration;
		ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
		if (resourceBundle.getLocale().equals(locale)) {
			enumeration = resourceBundle.getKeys();
			while (enumeration.hasMoreElements()) {
				String key = enumeration.nextElement();
				keys.add(key);
				properties.setProperty(key, resourceBundle.getString(key));
			}
		}
		ResourceBundle defaultResourceBundle = ResourceBundle.getBundle(baseName, DEFAULT_LOCALE);
		enumeration = defaultResourceBundle.getKeys();
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			if (!properties.containsKey(key)) {
				keys.add(key);
				properties.setProperty(key, defaultResourceBundle.getString(key));
			}
		}
	}

	@Override
	protected Object handleGetObject(String key) {
		Object value = properties.get(key);
		return value != null
				? value
				: (wasCalledFromResourceBundleMessageSource() ? null : QQQ.concat(key).concat(QQQ));
	}

	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(keys);
	}
}
