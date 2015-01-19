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

package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockRequestDispatcher;
import org.springframework.mock.web.MockServletContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

@RunWith(PowerMockRunner.class)
public class ViewSectionDataEntryServletTest {

	@Spy
	private ViewSectionDataEntryServlet viewSectionDataEntryServlet = new ViewSectionDataEntryServlet();
	@Mock
	private MockHttpServletResponse response;
	@Mock
	private StudyUserRoleBean currentRole;
	@Mock
	private SectionDAO sectionDAO;
	@Mock
	private ItemGroupDAO itemGroupDAO;

	private MockHttpServletRequest request;
	private MockRequestDispatcher requestDispatcher;
	private MockServletContext servletContext;

	@Before
	public void setUp() throws Exception {
		// Objects
		SectionBean section = new SectionBean();
		currentRole = new StudyUserRoleBean();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		UserAccountBean currentUser = new UserAccountBean();
		CRFVersionBean crfVersionBean = new CRFVersionBean();
		ArrayList<SectionBean> sections = new ArrayList<SectionBean>();
		ArrayList<ItemGroupBean> itemGroups = new ArrayList<ItemGroupBean>();
		EventDefinitionCRFBean eventDefinitionCRF = new EventDefinitionCRFBean();
		DisplayTableOfContentsBean displayTableOfContentsBean = new DisplayTableOfContentsBean();
		request.getSession().setAttribute("current.session.locale", Locale.ENGLISH);

		section.setId(1);
		crfVersionBean.setCrfId(1);
		eventDefinitionCRF.setId(0);
		currentUser.addUserType(UserType.USER);
		currentRole.setRole(Role.STUDY_ADMINISTRATOR);
		displayTableOfContentsBean.setSections(sections);
		servletContext = Mockito.mock(MockServletContext.class);
		requestDispatcher = Mockito.mock(MockRequestDispatcher.class);

		request.getSession().setAttribute("userRole", currentRole);
		request.getSession().setAttribute(ViewSectionDataEntryServlet.USER_BEAN_NAME, currentUser);

		EventDefinitionCRFDAO eventDefinitionCRFDAO = Mockito.mock(EventDefinitionCRFDAO.class);
		CRFVersionDAO crfVersionDAO = Mockito.mock(CRFVersionDAO.class);

		Mockito.doReturn(eventDefinitionCRFDAO).when(viewSectionDataEntryServlet).getEventDefinitionCRFDAO();
		Mockito.doReturn(crfVersionDAO).when(viewSectionDataEntryServlet).getCRFVersionDAO();
		Mockito.doReturn(displayTableOfContentsBean).when(viewSectionDataEntryServlet).getDisplayBeanByCrfVersionId(
				Mockito.anyInt());
		Mockito.doReturn(sectionDAO).when(viewSectionDataEntryServlet).getSectionDAO();
		Mockito.doReturn(itemGroupDAO).when(viewSectionDataEntryServlet).getItemGroupDAO();
		Mockito.doReturn(servletContext).when(viewSectionDataEntryServlet).getServletContext();

		PowerMockito.doNothing().when(viewSectionDataEntryServlet).mayAccess(request);
		Mockito.when(servletContext.getRequestDispatcher(Mockito.any(String.class))).thenReturn(requestDispatcher);
		Mockito.doReturn(eventDefinitionCRF).when(eventDefinitionCRFDAO).findByPK(Mockito.anyInt());
		Mockito.doReturn(crfVersionBean).when(crfVersionDAO).findByPK(Mockito.anyInt());
		Mockito.doReturn(section).when(sectionDAO).findByPK(Mockito.anyInt());
		Mockito.doReturn(itemGroups).when(itemGroupDAO).findLegitGroupBySectionId(Mockito.anyInt());

		Locale locale = new Locale("en");
		request.setPreferredLocales(Arrays.asList(locale));
		ResourceBundleProvider.updateLocale(locale);
		ResourceBundle resformat = ResourceBundleProvider.getFormatBundle(locale);
		ResourceBundle respage = ResourceBundleProvider.getPageMessagesBundle(locale);
		Whitebox.setInternalState(viewSectionDataEntryServlet, "resformat", resformat);
		Whitebox.setInternalState(viewSectionDataEntryServlet, "respage", respage);
	}

	@Test
	public void testThatViewSectionDataEntryServletGrantAccessToStudyAdministrator()
			throws InsufficientPermissionException {
		viewSectionDataEntryServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatViewSectionDataEntryServletGrantAccessToStudyCoder() throws InsufficientPermissionException {
		currentRole.setRole(Role.STUDY_CODER);
		viewSectionDataEntryServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatViewSectionDataEntryServletGrantAccessToStudyMonitor() throws InsufficientPermissionException {
		currentRole.setRole(Role.STUDY_MONITOR);
		viewSectionDataEntryServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatViewSectionDataEntryServletGrantAccessToCRC() throws InsufficientPermissionException {
		currentRole.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		viewSectionDataEntryServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatViewSectionDataEntryServletGrantAccessToInvestigator() throws InsufficientPermissionException {
		currentRole.setRole(Role.INVESTIGATOR);
		viewSectionDataEntryServlet.mayProceed(request, response);
		Assert.assertNull(request.getAttribute("pageMessages"));
	}

	@Test
	public void testThatViewSectionDataEntryReturnsListStudySubjectsIfAttributesAreEmpty() throws Exception {
		viewSectionDataEntryServlet.processRequest(request, response);
		Mockito.verify(servletContext).getRequestDispatcher(Page.LIST_STUDY_SUBJECTS_SERVLET.getFileName());
		Mockito.verify(requestDispatcher).forward(request, response);
	}

	@Test
	public void testThatCRFListWillBeReturnedIfAttributesAreValid() throws Exception {
		request.setParameter("eventCRFId", "0");
		request.setParameter("crfVersionId", "1");
		viewSectionDataEntryServlet.processRequest(request, response);
		Mockito.verify(servletContext).getRequestDispatcher(Page.CRF_LIST_SERVLET.getFileName());
		Mockito.verify(requestDispatcher).forward(request, response);
	}
}
