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

package com.clinovo.rest.wrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * RestRequestWrapper.
 */
@SuppressWarnings("unchecked")
public class RestRequestWrapper extends HttpServletRequestWrapper {

	private Map<String, String[]> allParameters = null;

	/**
	 * Constructs a request object wrapping the given request.
	 *
	 * @param request
	 *            HttpServletRequest
	 * @throws IllegalArgumentException
	 *             if the request is null
	 */
	public RestRequestWrapper(HttpServletRequest request) {
		super(request);
		allParameters = new TreeMap<String, String[]>();
		allParameters.putAll(super.getParameterMap());
	}

	/**
	 * Adds new parameter.
	 * 
	 * @param name
	 *            String
	 * @param value
	 *            String
	 */
	public void addParameter(String name, String value) {
		Map<String, String[]> newAllParameters = new TreeMap<String, String[]>();
		newAllParameters.putAll(allParameters);
		newAllParameters.put(name, new String[]{value});
		allParameters = new TreeMap<String, String[]>();
		allParameters.putAll(newAllParameters);
	}

	@Override
	public String getParameter(final String name) {
		String[] strings = getParameterMap().get(name);
		if (strings != null) {
			return strings[0];
		}
		return super.getParameter(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return Collections.unmodifiableMap(allParameters);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(getParameterMap().keySet());
	}

	@Override
	public String[] getParameterValues(final String name) {
		return getParameterMap().get(name);
	}
}