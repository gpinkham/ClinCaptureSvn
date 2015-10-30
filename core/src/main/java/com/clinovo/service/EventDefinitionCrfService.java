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
import java.util.Map;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;

/**
 * EventDefinitionCrfService.
 */
public interface EventDefinitionCrfService {

	/**
	 * Method updates chiild event definition crfs for new crf version.
	 *
	 * @param crfVersionBean
	 *            CRFVersionBean
	 * @param updater
	 *            UserAccountBean
	 */
	void updateChildEventDefinitionCrfsForNewCrfVersion(CRFVersionBean crfVersionBean, UserAccountBean updater);

	/**
	 * Method updates child event definition crfs.
	 *
	 * @param childEventDefinitionCRFsToUpdate List<EventDefinitionCRFBean>
	 * @param parentsMap Map<Integer, EventDefinitionCRFBean>
	 * @param updater UserAccountBean
	 * @param parentsBeforeUpdateMap Map<Integer, EventDefinitionCRFBean>
	 */
	void updateChildEventDefinitionCRFs(List<EventDefinitionCRFBean> childEventDefinitionCRFsToUpdate,
			Map<Integer, EventDefinitionCRFBean> parentsMap,
			Map<Integer, EventDefinitionCRFBean> parentsBeforeUpdateMap, UserAccountBean updater);

	/**
	 * Fills info for EventDefinitionCRFBean.
	 *
	 * @param eventDefinitionCRFBean
	 *            EventDefinitionCRFBean
	 * @param studyEventDefinitionBean
	 *            StudyEventDefinitionBean
	 */
	void fillEventDefinitionCrf(EventDefinitionCRFBean eventDefinitionCRFBean,
			StudyEventDefinitionBean studyEventDefinitionBean);

	/**
	 * Method that removes study event definition crf.
	 *
	 * @param eventDefinitionCRFBean
	 *            EventDefinitionCRFBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void removeEventDefinitionCrf(EventDefinitionCRFBean eventDefinitionCRFBean, UserAccountBean updater)
			throws Exception;

	/**
	 * Method that restores event definition crf.
	 *
	 * @param eventDefinitionCRFBean
	 *            EventDefinitionCRFBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void restoreEventDefinitionCrf(EventDefinitionCRFBean eventDefinitionCRFBean, UserAccountBean updater)
			throws Exception;

	/**
	 * This method will check if current default version is available and set another one if not.
	 * @param edcBean EventDefinitionCRFBean
	 * @param versionList List<CRFVersionBean>
	 * @param updater UserAccountBean
	 */
	void updateDefaultVersionOfEventDefinitionCRF(EventDefinitionCRFBean edcBean, List<CRFVersionBean> versionList,
												  UserAccountBean updater);

	/**
	 * Delete crf from study event definition.
	 * @param eventDefinitionCRFBean EventDefinitionCRFBean
	 * @throws Exception in case if some item data is present for this Event Definition CRF.
	 */
	void deleteEventDefinitionCrf(EventDefinitionCRFBean eventDefinitionCRFBean) throws Exception;
}
