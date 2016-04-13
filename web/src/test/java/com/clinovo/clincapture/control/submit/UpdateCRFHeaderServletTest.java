package com.clinovo.clincapture.control.submit;

import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import com.clinovo.service.ItemSDVService;
import com.clinovo.service.impl.ItemSDVServiceImpl;
import com.clinovo.util.RequestUtil;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterConfig;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.control.submit.UpdateCRFHeaderServlet;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.CrfShortcutsAnalyzer;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RequestUtil.class})
@SuppressWarnings("static-access")
public class UpdateCRFHeaderServletTest {

	private CrfShortcutsAnalyzer crfShortcutsAnalyzer;

	@Before
	public void setUp() throws Exception {

		UpdateCRFHeaderServlet servlet = Mockito.spy(UpdateCRFHeaderServlet.class);
		ServletContext servletContext = Mockito.mock(ServletContext.class);
		RequestDispatcher requestDispatcher = Mockito.mock(RequestDispatcher.class);
		SectionDAO sectionDAO = Mockito.mock(SectionDAO.class);
		EventDefinitionCRFDAO eventDefCRFDAO = Mockito.mock(EventDefinitionCRFDAO.class);
		ItemSDVService itemSDVService = Mockito.mock(ItemSDVServiceImpl.class);
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.setParameter("sectionId", "1");
		request.setParameter("eventCRFId", "0");
		request.setParameter("eventDefinitionCRFId", "1");
		LocaleResolver.updateLocale(request.getSession(), Locale.ENGLISH);
		ResourceBundleProvider.updateLocale(LocaleResolver.getLocale(request));
		FormDiscrepancyNotes formDiscrepancyNotes = new FormDiscrepancyNotes();
		StudyBean currentStudy = new StudyBean();
		currentStudy.setId(1);
		currentStudy.setStudyParameterConfig(new StudyParameterConfig());
		SectionBean section = new SectionBean();
		section.setId(1);
		EventDefinitionCRFBean eventDefCRF = new EventDefinitionCRFBean();
		eventDefCRF.setId(1);
		PowerMockito.mockStatic(RequestUtil.class);
		PowerMockito.when(RequestUtil.getCurrentStudy()).thenReturn(currentStudy);
		PowerMockito.when(servlet.getSectionDAO()).thenReturn(sectionDAO);
		PowerMockito.when(sectionDAO.findByPK(1)).thenReturn(section);
		PowerMockito.when(servlet.getEventDefinitionCRFDAO()).thenReturn(eventDefCRFDAO);
		PowerMockito.when(eventDefCRFDAO.findByPK(1)).thenReturn(eventDefCRF);
		request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, formDiscrepancyNotes);
		request.getSession().setAttribute("userRole", new StudyUserRoleBean());
		Whitebox.setInternalState(servlet, "servletContext", servletContext);
		Whitebox.setInternalState(servlet, "itemSDVService", itemSDVService);
		Mockito.when(servletContext.getRequestDispatcher(Page.UPDATE_CRF_HEADER_PAGE.getFileName())).thenReturn(
				requestDispatcher);
		servlet.processRequest(request, response);
		crfShortcutsAnalyzer = (CrfShortcutsAnalyzer) request.getAttribute(CrfShortcutsAnalyzer.CRF_SHORTCUTS_ANALYZER);
	}

	@Test
	public void testThatGetNextNewDnLinkReturnsCorrectValue() throws Exception {
		Assert.assertEquals(crfShortcutsAnalyzer.getNextNewDnLink(), CrfShortcutsAnalyzer.FIRST_NEW_DN);
	}

	@Test
	public void testThatGetNextClosedDnLinkReturnsCorrectValue() throws Exception {
		Assert.assertEquals(crfShortcutsAnalyzer.getNextClosedDnLink(), CrfShortcutsAnalyzer.FIRST_CLOSED_DN);
	}

	@Test
	public void testThatGetNextUpdatedDnLinkReturnsCorrectValue() throws Exception {
		Assert.assertEquals(crfShortcutsAnalyzer.getNextUpdatedDnLink(), CrfShortcutsAnalyzer.FIRST_UPDATED_DN);
	}

	@Test
	public void testThatGetNextAnnotationLinkReturnsCorrectValue() throws Exception {
		Assert.assertEquals(crfShortcutsAnalyzer.getNextAnnotationLink(), CrfShortcutsAnalyzer.FIRST_ANNOTATION);
	}

	@Test
	public void testThatGetNextResolutionProposedLinkReturnsCorrectValue() throws Exception {
		Assert.assertEquals(crfShortcutsAnalyzer.getNextResolutionProposedLink(),
				CrfShortcutsAnalyzer.FIRST_RESOLUTION_PROPOSED);
	}
}
