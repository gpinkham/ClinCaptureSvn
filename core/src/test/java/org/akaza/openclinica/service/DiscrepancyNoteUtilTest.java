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
package org.akaza.openclinica.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DiscrepancyNoteUtilTest {

	private ArrayList<StudyUserRoleBean> userAccounts;
	private UserAccountDAO udao;
	private StudyDAO studyDAO;
	private EventDefinitionCRFDAO edcdao;
	private StudyBean study;
	private EventCRFBean ecb;
	private EventDefinitionCRFBean edcb;
	private UserAccountBean rootUserAccount;

	@Before
	public void setUp() {
		udao = Mockito.mock(UserAccountDAO.class);
		studyDAO = Mockito.mock(StudyDAO.class);
		edcdao = Mockito.mock(EventDefinitionCRFDAO.class);
		study = new StudyBean();
		study.setId(1);
		study.setParentStudyId(0);
		ecb = new EventCRFBean();
		ecb.setId(1);
		ecb.setStudyEventId(1);
		ecb.setCRFVersionId(1);
		edcb = new EventDefinitionCRFBean();
		rootUserAccount = new UserAccountBean();
		rootUserAccount.setId(1);
		rootUserAccount.setStatus(Status.AVAILABLE);
		StudyUserRoleBean rootRole = new StudyUserRoleBean();
		rootRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		rootUserAccount.addRole(rootRole);

		userAccounts = new ArrayList<StudyUserRoleBean>();
		generateUserAccounts(0, Role.STUDY_ADMINISTRATOR);
		generateUserAccounts(1, Role.STUDY_MONITOR);
		generateUserAccounts(2, Role.STUDY_EVALUATOR);

		Mockito.when(udao.findAllUsersByStudyOrSite(1, 0, 1)).thenReturn(userAccounts);
		Mockito.when(studyDAO.findByStudySubjectId(1)).thenReturn(study);
		Mockito.when(udao.findByPK(1)).thenReturn(rootUserAccount);
		Mockito.when(edcdao.findForStudyByStudyEventIdAndCRFVersionId(1, 1)).thenReturn(edcb);
	}

	private void generateUserAccounts(int index, Role role) {
		userAccounts.add(new StudyUserRoleBean());
		userAccounts.get(index).setId(index + 1);
		userAccounts.get(index).setRole(role);
	}

	@Test
	public void testThatEvaluatorRoleIsIncludedWhenCRFIsIdeComplete() {
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
		ecb.setNotStarted(false);
		edcb.setEvaluatedCRF(true);
		List<StudyUserRoleBean> surbs = DiscrepancyNoteUtil.generateUserAccounts(1, study, udao, studyDAO, ecb, edcdao);
		assertEquals(4, surbs.size());
	}

	@Test
	public void testThatEvaluatorRoleIsNotIncludedWhenCRFInIDE() {
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY);
		List<StudyUserRoleBean> surbs = DiscrepancyNoteUtil.generateUserAccounts(1, study, udao, studyDAO, ecb, edcdao);
		assertEquals(3, surbs.size());
	}
	
	@Test
	public void testThatEvaluatorRoleIsNotIncludedWhenCRFIsNotStarted() {
		ecb.setStage(DataEntryStage.UNCOMPLETED);
		ecb.setNotStarted(true);
		List<StudyUserRoleBean> surbs = DiscrepancyNoteUtil.generateUserAccounts(1, study, udao, studyDAO, ecb, edcdao);
		assertEquals(3, surbs.size());
	}
	
	@Test
	public void testThatEvaluatorRoleIsNotIncludedWhenCRFIsNotEvaluable() {
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
		ecb.setNotStarted(true);
		edcb.setEvaluatedCRF(false);
		List<StudyUserRoleBean> surbs = DiscrepancyNoteUtil.generateUserAccounts(1, study, udao, studyDAO, ecb, edcdao);
		assertEquals(3, surbs.size());
	}
}
