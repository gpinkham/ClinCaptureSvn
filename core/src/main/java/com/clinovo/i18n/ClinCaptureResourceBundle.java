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
import java.util.ResourceBundle;
import java.util.Set;

/**
 * ClinCaptureResourceBundle.
 */
public class ClinCaptureResourceBundle extends ResourceBundle {

	public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

	public static final String QQQ = "???";

	private ResourceBundle resourceBundle;
	private ResourceBundle defaultResourceBundle;

	private boolean useDefaultMessageMethod;

	/**
	 * Constructor.
	 *
	 * @param clinCaptureResourceBundleLoader
	 *            ClinCaptureResourceBundleLoader
	 * @param useDefaultMessageMethod
	 *            boolean
	 * @param baseName
	 *            String
	 * @param locale
	 *            Locale
	 */
	public ClinCaptureResourceBundle(ClinCaptureResourceBundleLoader clinCaptureResourceBundleLoader,
			boolean useDefaultMessageMethod, String baseName, Locale locale) {
		this(clinCaptureResourceBundleLoader, baseName, locale);
		this.useDefaultMessageMethod = useDefaultMessageMethod;
	}

	/**
	 * Constructor.
	 * 
	 * @param clinCaptureResourceBundleLoader
	 *            ClinCaptureResourceBundleLoader
	 * @param baseName
	 *            String
	 * @param locale
	 *            Locale
	 */
	public ClinCaptureResourceBundle(ClinCaptureResourceBundleLoader clinCaptureResourceBundleLoader, String baseName,
			Locale locale) {
		resourceBundle = clinCaptureResourceBundleLoader.getResourceBundle(baseName, locale);
		defaultResourceBundle = clinCaptureResourceBundleLoader.getResourceBundle(baseName, DEFAULT_LOCALE);
	}

	@Override
	protected Object handleGetObject(String key) {
		Object value = useDefaultMessageMethod ? null : QQQ.concat(key).concat(QQQ);
		if (resourceBundle != null && resourceBundle.containsKey(key)) {
			value = resourceBundle.getObject(key);
		} else if (defaultResourceBundle != null && defaultResourceBundle.containsKey(key)) {
			value = defaultResourceBundle.getObject(key);
		}
		return value;
	}

	@Override
	public Enumeration<String> getKeys() {
		Set<String> keys = new HashSet<String>();
		if (resourceBundle != null) {
			keys.addAll(resourceBundle.keySet());
		}
		if (defaultResourceBundle != null) {
			keys.addAll(defaultResourceBundle.keySet());
		}
		return Collections.enumeration(keys);
	}
}
