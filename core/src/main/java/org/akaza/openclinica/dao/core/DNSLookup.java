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

package org.akaza.openclinica.dao.core;

import java.net.HttpURLConnection;
import java.net.URL;

import org.akaza.openclinica.core.form.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DNSLookup, a class which will find out if there is a 301 redirect and give us the 'true' URL instead, end-slashes and
 * all.
 *
 * @author Tom
 *
 */
public class DNSLookup {

	private static final Logger LOGGER = LoggerFactory.getLogger(DNSLookup.class);

	public static final int DEEP_MAX = 2;

	/**
	 * Default constructor.
	 */
	public DNSLookup() {
		//
	}

	private String getTrueSystemURL(String url, int deep) {
		String trueUrl = "";
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.connect();
			int responseCode = con.getResponseCode();
			String location = con.getHeaderField("Location");
			if (deep < DEEP_MAX && responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
				return getTrueSystemURL(location, ++deep);
			}
			trueUrl = location.replaceAll("/pages/login/login.*", "/");
		} catch (Exception e) {
			LOGGER.error("Error has occurred.", e);
		}
		trueUrl = StringUtil.isBlank(trueUrl) ? url.trim() : trueUrl.trim();
		return trueUrl.endsWith("/") ? trueUrl : trueUrl.concat("/");
	}

	/**
	 * Returns real system URL.
	 *
	 * @param url
	 *            getTrueSystemURL
	 * @return getTrueSystemURL
	 */
	public String getTrueSystemURL(String url) {
		return getTrueSystemURL(url, 1);
	}
}
