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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;

import com.clinovo.exception.CodeException;
import com.clinovo.model.DiscrepancyDescription;
import com.clinovo.util.DateUtil;

/**
 * StudyService.
 */
public interface StudyService {

	/**
	 * Removes study.
	 *
	 * @param study
	 *            StudyBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void removeStudy(StudyBean study, UserAccountBean updater) throws Exception;

	/**
	 * Restores study.
	 *
	 * @param study
	 *            StudyBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void restoreStudy(StudyBean study, UserAccountBean updater) throws Exception;

	/**
	 * Removes site.
	 * 
	 * @param studyBean
	 *            StudyBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void removeSite(StudyBean studyBean, UserAccountBean updater) throws Exception;

	/**
	 * Restores site.
	 *
	 * @param studyBean
	 *            StudyBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void restoreSite(StudyBean studyBean, UserAccountBean updater) throws Exception;

	/**
	 * Locks site.
	 *
	 * @param studyBean
	 *            StudyBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void lockSite(StudyBean studyBean, UserAccountBean updater) throws Exception;

	/**
	 * Unlocks site.
	 *
	 * @param studyBean
	 *            StudyBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void unlockSite(StudyBean studyBean, UserAccountBean updater) throws Exception;

	/**
	 * Returns study bean, where specified study subject was created.
	 *
	 * @param currentStudy
	 *            StudyBean represents current scope.
	 * @param studySubject
	 *            StudySubjectBean represents target study subject.
	 * @return StudyBean
	 */
	StudyBean getSubjectStudy(StudyBean currentStudy, StudySubjectBean studySubject);

	/**
	 * Prepares study bean and fills it.
	 * 
	 * @param studyBean
	 *            StudyBean
	 * @param currentUser
	 *            UserAccountBean
	 * @param parametersMap
	 *            Map
	 * @param featuresMap
	 *            map
	 * @param datePattern
	 *            DateUtil.DatePattern
	 * @param locale
	 *            Locale
	 * @return StudyBean
	 */
	StudyBean prepareStudyBean(StudyBean studyBean, UserAccountBean currentUser, Map<String, String> parametersMap,
			Map<String, String> featuresMap, DateUtil.DatePattern datePattern, Locale locale);

	/**
	 * Prepares study bean configuration parameters.
	 *
	 * @param studyBean
	 *            StudyBean
	 * @return StudyBean
	 */
	StudyBean prepareStudyBeanConfiguration(StudyBean studyBean);

	/**
	 * Prepares study bean configuration parameters and fills it.
	 * 
	 * @param studyBean
	 *            StudyBean
	 * @param configurationParametersMap
	 *            Map
	 * @return StudyBean
	 */
	StudyBean prepareStudyBeanConfiguration(StudyBean studyBean, Map<String, String> configurationParametersMap);

	/**
	 * Saves study bean.
	 *
	 * @param userId
	 *            int
	 * @param studyBean
	 *            StudyBean
	 * @param currentUser
	 *            UserAccountBean
	 * @param pageMessagesBundle
	 *            ResourceBundle
	 * @return StudyBean
	 */
	StudyBean saveStudyBean(int userId, StudyBean studyBean, UserAccountBean currentUser,
			ResourceBundle pageMessagesBundle);

	/**
	 * Updates study bean.
	 * 
	 * @param studyBean
	 *            StudyBean
	 * @param dDescriptionsMap
	 *            Map
	 * @param currentUser
	 *            UserAccountBean
	 * @return StudyBean
	 * @throws CodeException
	 *             the CodeException
	 */
	StudyBean updateStudy(StudyBean studyBean, Map<String, List<DiscrepancyDescription>> dDescriptionsMap,
			UserAccountBean currentUser) throws CodeException;
}
