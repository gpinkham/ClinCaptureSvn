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

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;

/**
 * DeleteCrfService.
 */
public interface DeleteCrfService {

	/**
	 * Deletes crf.
	 * 
	 * @param crfBean
	 *            CRFBean
	 * @param userAccountBean
	 *            UserAccountBean
	 * @param locale
	 *            Locale
	 * @param force
	 *            boolean if it is true then crf will be deleted immediately with all related data
	 * @throws Exception
	 *             an Exception
	 */
	void deleteCrf(CRFBean crfBean, UserAccountBean userAccountBean, Locale locale, boolean force) throws Exception;

	/**
	 * Deletes crf version.
	 * 
	 * @param crfVersionBean
	 *            CRFVersionBean
	 * @param locale
	 *            Locale
	 * @param force
	 *            boolean if it is true then crf version will be deleted immediately with all related data
	 * @throws Exception
	 *             an Exception
	 */
	void deleteCrfVersion(CRFVersionBean crfVersionBean, Locale locale, boolean force) throws Exception;
}
