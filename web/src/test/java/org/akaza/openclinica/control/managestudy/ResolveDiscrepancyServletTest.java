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
package org.akaza.openclinica.control.managestudy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Locale;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.clinovo.i18n.LocaleResolver;

public class ResolveDiscrepancyServletTest {

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private ResolveDiscrepancyServlet servlet;
	private UserAccountBean currentUser;
	private StudyUserRoleBean currentRole;
	private StudyBean currentStudy;
	private StudyBean currentSite;
	private DiscrepancyNoteBean note;
	private ItemDataBean itemData;
	private EventCRFBean ecb;
	private StudySubjectBean ssb;
	private CRFVersionBean crfvb;
	private StudyEventBean seb;
	private EventDefinitionCRFBean edcb;
	private ItemDataDAO iddao;
	private EventCRFDAO ecrfdao;
	private StudySubjectDAO ssdao;
	private CRFVersionDAO crfvdao;
	private StudyEventDAO sedao;
	private EventDefinitionCRFDAO edcdao;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		servlet = Mockito.mock(ResolveDiscrepancyServlet.class);
		currentUser = new UserAccountBean();
		currentRole = new StudyUserRoleBean();
		currentStudy = new StudyBean();
		currentSite = new StudyBean();
		note = new DiscrepancyNoteBean();
		itemData = new ItemDataBean();
		ecb = new EventCRFBean();
		ssb = new StudySubjectBean();
		crfvb = new CRFVersionBean();
		seb = new StudyEventBean();
		edcb = new EventDefinitionCRFBean();
		iddao = Mockito.mock(ItemDataDAO.class);
		ecrfdao = Mockito.mock(EventCRFDAO.class);
		ssdao = Mockito.mock(StudySubjectDAO.class);
		crfvdao = Mockito.mock(CRFVersionDAO.class);
		sedao = Mockito.mock(StudyEventDAO.class);
		edcdao = Mockito.mock(EventDefinitionCRFDAO.class);

		Mockito.when(servlet.getCurrentRole(request)).thenReturn(currentRole);
		Mockito.when(servlet.getUserAccountBean(request)).thenReturn(currentUser);
		Mockito.when(servlet.getCurrentStudy(request)).thenReturn(currentStudy);
		Mockito.when(servlet.getItemDataDAO()).thenReturn(iddao);
		Mockito.when(servlet.getEventCRFDAO()).thenReturn(ecrfdao);
		Mockito.when(servlet.getStudySubjectDAO()).thenReturn(ssdao);
		Mockito.when(servlet.getCRFVersionDAO()).thenReturn(crfvdao);
		Mockito.when(servlet.getStudyEventDAO()).thenReturn(sedao);
		Mockito.when(servlet.getEventDefinitionCRFDAO()).thenReturn(edcdao);
		Mockito.when(iddao.findByPK(1)).thenReturn(itemData);
		Mockito.when(ecrfdao.findByPK(1)).thenReturn(ecb);
		Mockito.when(ssdao.findByPK(1)).thenReturn(ssb);
		Mockito.when(crfvdao.findByPK(1)).thenReturn(crfvb);
		Mockito.when(sedao.findByPK(1)).thenReturn(seb);
		Mockito.when(edcdao.findByStudyEventDefinitionIdAndCRFId(1, 1)).thenReturn(edcb);
		Mockito.doCallRealMethod().when(servlet).mayProceed(request, response);
		Mockito.doCallRealMethod().when(servlet).getPageForForwarding(request, note);

		Locale locale = new Locale("en");
		LocaleResolver.updateLocale(request, locale);
		ResourceBundleProvider.updateLocale(locale);
		ResourceBundle respage = ResourceBundleProvider.getPageMessagesBundle(locale);
		ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle(locale);
		Whitebox.setInternalState(servlet, "respage", respage);
		Whitebox.setInternalState(servlet, "resexception", resexception);

		currentStudy.setId(1);
		currentStudy.setStatus(Status.AVAILABLE);
		currentSite.setId(2);
		currentSite.setParentStudyId(1);
		currentSite.setStatus(Status.AVAILABLE);
		currentRole.setStudyId(1);
		note.setEntityType("itemdata");
		note.setEntityId(1);
		itemData.setId(1);
		itemData.setEventCRFId(1);
		ecb.setId(1);
		ecb.setStudyEventId(1);
		ecb.setStudySubjectId(1);
		ecb.setCRFVersionId(1);
		ecb.setStatus(Status.AVAILABLE);
		ecb.setNotStarted(false);
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY);
		ecb.setOwner(new UserAccountBean());
		ecb.getOwner().setId(2);
		ssb.setId(1);
		ssb.setStatus(Status.AVAILABLE);
		crfvb.setId(1);
		crfvb.setCrfId(1);
		seb.setId(1);
		seb.setStudyEventDefinitionId(1);
	}

	@Test
	public void testThatStudyAdministratorCanProceed() throws InsufficientPermissionException {
		currentRole.setRole(Role.STUDY_ADMINISTRATOR);
		servlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatSystemAdministratorCanProceed() throws InsufficientPermissionException {
		currentRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		servlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatStudyCoderCanProceed() throws InsufficientPermissionException {
		currentRole.setRole(Role.STUDY_CODER);
		servlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatStudyDirectorCanProceed() throws InsufficientPermissionException {
		currentRole.setRole(Role.STUDY_DIRECTOR);
		servlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatStudyMonitorCanProceed() throws InsufficientPermissionException {
		currentRole.setRole(Role.STUDY_MONITOR);
		servlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatStudyEvaluatorCanProceed() throws InsufficientPermissionException {
		currentRole.setRole(Role.STUDY_EVALUATOR);
		servlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatSiteMonitorCanProceed() throws InsufficientPermissionException {
		currentRole.setRole(Role.SITE_MONITOR);
		servlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatClinicalResearchCoordinatorCanProceed() throws InsufficientPermissionException {
		currentRole.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		servlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatInvestigatorCanProceed() throws InsufficientPermissionException {
		currentRole.setRole(Role.INVESTIGATOR);
		servlet.mayProceed(request, response);
		assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatStudyAdministratorGetsDataEntryPageWhenInStudyAndCrfInIDE() {
		currentRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentStudy.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.INITIAL_DATA_ENTRY_SERVLET, page);
	}

	@Test
	public void testThatStudyAdministratorGetsDataEntryPageWhenInStudyAndDdeEnabledCrfIsIdeCompleted() {
		currentRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentStudy.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
		edcb.setDoubleEntry(true);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.DOUBLE_DATA_ENTRY_SERVLET, page);
	}

	@Test
	public void testThatStudyAdministratorGetsDataEntryPageWhenInStudyAndCrfIsIdeCompleted() {
		currentRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentStudy.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
		edcb.setDoubleEntry(false);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.ADMIN_EDIT_SERVLET, page);
	}

	@Test
	public void testThatStudyAdministratorGetsDataEntryPageWhenInStudyAndCrfIsDdeCompleted() {
		currentRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentStudy.getId());
		ecb.setStage(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE);
		edcb.setDoubleEntry(true);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.ADMIN_EDIT_SERVLET, page);
	}

	@Test
	public void testThatStudyEvaluatorGetsViewPageWhenInStudyAndCrfInIDE() {
		currentRole.setRole(Role.STUDY_EVALUATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentStudy.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.VIEW_SECTION_DATA_ENTRY_SERVLET, page);
	}

	@Test
	public void testThatStudyEvaluatorGetsAdminEditPageWhenInStudyAndNonEvaluableCrfIsIDECompleted() {
		currentRole.setRole(Role.STUDY_EVALUATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentStudy.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
		edcb.setEvaluatedCRF(false);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.ADMIN_EDIT_SERVLET, page);
	}

	@Test
	public void testThatStudyEvaluatorGetsCrfEditPageWhenInStudyAndEvaluableCrfIsIDECompleted() {
		currentRole.setRole(Role.STUDY_EVALUATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentStudy.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
		edcb.setEvaluatedCRF(true);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.DOUBLE_DATA_ENTRY_SERVLET, page);
	}

	@Test
	public void testThatStudyEvaluatorGetsAdminEditPageWhenInStudyAndNonEvaluableCrfIsEvaluationCompleted() {
		currentRole.setRole(Role.STUDY_EVALUATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentStudy.getId());
		ecb.setStage(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE);
		edcb.setEvaluatedCRF(true);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.ADMIN_EDIT_SERVLET, page);
	}

	@Test
	public void testThatStudyMonitorGetsViewCRFPageWhenInStudy() {
		currentRole.setRole(Role.STUDY_MONITOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentStudy.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.VIEW_SECTION_DATA_ENTRY_SERVLET, page);
	}

	@Test
	public void testThatStudyAdministratorGetsDataEntryPageWhenInSiteAndCrfInIDE() {
		currentRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentSite.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.INITIAL_DATA_ENTRY_SERVLET, page);
	}

	@Test
	public void testThatStudyAdministratorGetsDataEntryPageWhenInSiteAndDdeEnabledCrfIsIdeCompleted() {
		currentRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentSite.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
		edcb.setDoubleEntry(true);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.DOUBLE_DATA_ENTRY_SERVLET, page);
	}

	@Test
	public void testThatStudyAdministratorGetsDataEntryPageWhenInSiteAndCrfIsIdeCompleted() {
		currentRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentSite.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
		edcb.setDoubleEntry(false);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.ADMIN_EDIT_SERVLET, page);
	}

	@Test
	public void testThatStudyEvaluatorGetsViewPageWhenInSiteAndCrfInIDE() {
		currentRole.setRole(Role.STUDY_EVALUATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentSite.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY);
		Mockito.when(servlet.getCurrentStudy(request)).thenReturn(currentSite);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.VIEW_SECTION_DATA_ENTRY_SERVLET, page);
	}

	@Test
	public void testThatStudyEvaluatorGetsAdminEditPageWhenInSiteAndNonEvaluableCrfIsIDECompleted() {
		currentRole.setRole(Role.STUDY_EVALUATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentSite.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
		edcb.setEvaluatedCRF(false);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.ADMIN_EDIT_SERVLET, page);
	}

	@Test
	public void testThatStudyEvaluatorGetsCrfEditPageWhenInSiteAndEvaluableCrfIsIDECompleted() {
		currentRole.setRole(Role.STUDY_EVALUATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentSite.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
		edcb.setEvaluatedCRF(true);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.DOUBLE_DATA_ENTRY_SERVLET, page);
	}

	@Test
	public void testThatStudyMonitorGetsViewPageWhenInSite() {
		currentRole.setRole(Role.STUDY_MONITOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentSite.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY);
		Mockito.when(servlet.getCurrentStudy(request)).thenReturn(currentSite);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.VIEW_SECTION_DATA_ENTRY_SERVLET, page);
	}

	@Test
	public void testThatSiteMonitorGetsViewPage() {
		currentRole.setRole(Role.SITE_MONITOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentSite.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY);
		Mockito.when(servlet.getCurrentStudy(request)).thenReturn(currentSite);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.VIEW_SECTION_DATA_ENTRY_SERVLET, page);
	}

	@Test
	public void testThatClinicalResearchCoordinatorGetsAdminEditPageWhenCrfIsIDECompleted() {
		currentRole.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentSite.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.ADMIN_EDIT_SERVLET, page);
	}

	@Test
	public void testThatClinicalResearchCoordinatorGetsDdePageWhenDdeCrfIsIDECompleted() {
		currentRole.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentSite.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
		edcb.setDoubleEntry(true);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.DOUBLE_DATA_ENTRY_SERVLET, page);
	}

	@Test
	public void testThatClinicalResearchCoordinatorGetsIdePageWhenCrfStillInIDE() {
		currentRole.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentSite.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY);
		Mockito.when(servlet.getCurrentStudy(request)).thenReturn(currentSite);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.INITIAL_DATA_ENTRY_SERVLET, page);
	}

	@Test
	public void testThatInvestigatorGetsAdminEditPageWhenCrfIsIDECompleted() {
		currentRole.setRole(Role.INVESTIGATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentSite.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.ADMIN_EDIT_SERVLET, page);
	}

	@Test
	public void testThatInvestigatorGetsDdePageWhenDdeCrfIsIDECompleted() {
		currentRole.setRole(Role.INVESTIGATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentSite.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
		edcb.setDoubleEntry(true);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.DOUBLE_DATA_ENTRY_SERVLET, page);
	}

	@Test
	public void testThatInvestigatorGetsIdePageWhenCrfStillInIDE() {
		currentRole.setRole(Role.INVESTIGATOR);
		currentUser.addRole(currentRole);
		currentUser.setActiveStudyId(currentSite.getId());
		ecb.setStage(DataEntryStage.INITIAL_DATA_ENTRY);
		Mockito.when(servlet.getCurrentStudy(request)).thenReturn(currentSite);
		Page page = servlet.getPageForForwarding(request, note);
		assertEquals(Page.INITIAL_DATA_ENTRY_SERVLET, page);
	}
}
