package com.clinovo.clincapture.control.submit;

import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.akaza.openclinica.AbstractContextSentiveTest;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.control.submit.UpdateCRFHeaderServlet;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.CrfShortcutsAnalyzer;

public class UpdateCRFHeaderServletTest extends AbstractContextSentiveTest {

	private CrfShortcutsAnalyzer crfShortcutsAnalyzer;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		ServletContext servletContext = Mockito.mock(ServletContext.class);
		RequestDispatcher requestDispatcher = Mockito.mock(RequestDispatcher.class);
		UpdateCRFHeaderServlet servlet = new UpdateCRFHeaderServlet();
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.setParameter("sectionId", "1");
		request.setParameter("eventCRFId", "1");
		LocaleResolver.updateLocale(request.getSession(), Locale.ENGLISH);
		ResourceBundleProvider.updateLocale(LocaleResolver.getLocale(request));
		FormDiscrepancyNotes formDiscrepancyNotes = new FormDiscrepancyNotes();
		request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, formDiscrepancyNotes);
		request.getSession().setAttribute("userRole", new StudyUserRoleBean());
		Whitebox.setInternalState(servlet, "dataSource", dataSource);
		Whitebox.setInternalState(servlet, "servletContext", servletContext);
		Whitebox.setInternalState(servlet, "itemSDVService", itemSDVService);
		Mockito.when(servletContext.getRequestDispatcher(Page.UPDATE_CRF_HEADER_PAGE.getFileName())).thenReturn(
				requestDispatcher);
		servlet.processRequest(request, response);
		crfShortcutsAnalyzer = (CrfShortcutsAnalyzer) request.getAttribute(CrfShortcutsAnalyzer.CRF_SHORTCUTS_ANALYZER);
	}

	@Test
	public void testThatGetNextNewDnLinkReturnsCorrectValue() throws Exception {
		assertEquals(crfShortcutsAnalyzer.getNextNewDnLink(), CrfShortcutsAnalyzer.FIRST_NEW_DN);
	}

	@Test
	public void testThatGetNextClosedDnLinkReturnsCorrectValue() throws Exception {
		assertEquals(crfShortcutsAnalyzer.getNextClosedDnLink(), CrfShortcutsAnalyzer.FIRST_CLOSED_DN);
	}

	@Test
	public void testThatGetNextUpdatedDnLinkReturnsCorrectValue() throws Exception {
		assertEquals(crfShortcutsAnalyzer.getNextUpdatedDnLink(), CrfShortcutsAnalyzer.FIRST_UPDATED_DN);
	}

	@Test
	public void testThatGetNextAnnotationLinkReturnsCorrectValue() throws Exception {
		assertEquals(crfShortcutsAnalyzer.getNextAnnotationLink(), CrfShortcutsAnalyzer.FIRST_ANNOTATION);
	}

	@Test
	public void testThatGetNextResolutionProposedLinkReturnsCorrectValue() throws Exception {
		assertEquals(crfShortcutsAnalyzer.getNextResolutionProposedLink(),
				CrfShortcutsAnalyzer.FIRST_RESOLUTION_PROPOSED);
	}
}
