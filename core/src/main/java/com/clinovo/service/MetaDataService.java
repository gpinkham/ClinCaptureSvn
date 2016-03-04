/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.clinovo.service;

import java.util.Locale;

import org.akaza.openclinica.bean.login.UserAccountBean;

/**
 * MetaDataService.
 */
public interface MetaDataService {

	/**
	 * Returns odm in xml format.
	 * 
	 * @param currentUser
	 *            UserAccountBean
	 * @param studyOID
	 *            String
	 * @param subjectIdentifier
	 *            String
	 * @param studyEventOID
	 *            String
	 * @param formVersionOID
	 *            String
	 * @param includeDN
	 *            boolean
	 * @param includeAudit
	 *            boolean
	 * @param localizeDatesToUserTZ
	 *            boolean
	 * @param locale
	 *            Locale
	 * @return String
	 */
	String getXML(UserAccountBean currentUser, String studyOID, String subjectIdentifier, String studyEventOID,
			String formVersionOID, boolean includeDN, boolean includeAudit, boolean localizeDatesToUserTZ,
			Locale locale);

	/**
	 * Returns odm in html format.
	 *
	 * @param contextPath
	 *            String
	 * @param currentUser
	 *            UserAccountBean
	 * @param studyOID
	 *            String
	 * @param subjectIdentifier
	 *            String
	 * @param studyEventOID
	 *            String
	 * @param formVersionOID
	 *            String
	 * @param includeDNs
	 *            boolean
	 * @param includeAudits
	 *            boolean
	 * @param localizeDatesToUserTZ
	 *            boolean
	 * @param locale
	 *            Locale
	 * @return String
	 */
	String getHTML(String contextPath, UserAccountBean currentUser, String studyOID, String subjectIdentifier,
			String studyEventOID, String formVersionOID, boolean includeDNs, boolean includeAudits,
			boolean localizeDatesToUserTZ, Locale locale);
}
