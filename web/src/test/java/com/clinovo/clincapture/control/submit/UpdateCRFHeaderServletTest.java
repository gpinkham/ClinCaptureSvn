package com.clinovo.clincapture.control.submit;

import com.clinovo.util.SessionUtil;
import org.akaza.openclinica.AbstractContextSentiveTest;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.control.submit.UpdateCRFHeaderServlet;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.util.DiscrepancyShortcutsAnalyzer;
import org.akaza.openclinica.view.Page;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import java.util.Locale;

public class UpdateCRFHeaderServletTest extends AbstractContextSentiveTest {

	private ServletContext servletContext;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private RequestDispatcher requestDispatcher;
	private UpdateCRFHeaderServlet servlet;
	private FormDiscrepancyNotes formDiscrepancyNotes;
	private DiscrepancyShortcutsAnalyzer discrepancyShortcutsAnalyzer;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		servletContext = Mockito.mock(ServletContext.class);
		requestDispatcher = Mockito.mock(RequestDispatcher.class);
		servlet = new UpdateCRFHeaderServlet();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setParameter("sectionId", "1");
		SessionUtil.updateLocale(request.getSession(), Locale.ENGLISH);
		ResourceBundleProvider.updateLocale(SessionUtil.getLocale(request));
		formDiscrepancyNotes = new FormDiscrepancyNotes();
		request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, formDiscrepancyNotes);
		Whitebox.setInternalState(servlet, "dataSource", dataSource);
		Whitebox.setInternalState(servlet, "servletContext", servletContext);
		Mockito.when(servletContext.getRequestDispatcher(Page.UPDATE_CRF_HEADER_PAGE.getFileName())).thenReturn(
				requestDispatcher);
		servlet.processRequest(request, response);
		discrepancyShortcutsAnalyzer = (DiscrepancyShortcutsAnalyzer) request
				.getAttribute(DiscrepancyShortcutsAnalyzer.DISCREPANCY_SHORTCUTS_ANALYZER);
	}

	@Test
	public void testThatGetNextNewDnLinkReturnsCorrectValue() throws Exception {
		assertEquals(discrepancyShortcutsAnalyzer.getNextNewDnLink(), DiscrepancyShortcutsAnalyzer.FIRST_NEW_DN);
	}

	@Test
	public void testThatGetNextClosedDnLinkReturnsCorrectValue() throws Exception {
		assertEquals(discrepancyShortcutsAnalyzer.getNextClosedDnLink(), DiscrepancyShortcutsAnalyzer.FIRST_CLOSED_DN);
	}

	@Test
	public void testThatGetNextUpdatedDnLinkReturnsCorrectValue() throws Exception {
		assertEquals(discrepancyShortcutsAnalyzer.getNextUpdatedDnLink(), DiscrepancyShortcutsAnalyzer.FIRST_UPDATED_DN);
	}

	@Test
	public void testThatGetNextAnnotationLinkReturnsCorrectValue() throws Exception {
		assertEquals(discrepancyShortcutsAnalyzer.getNextAnnotationLink(),
				DiscrepancyShortcutsAnalyzer.FIRST_ANNOTATION);
	}

	@Test
	public void testThatGetNextResolutionProposedLinkReturnsCorrectValue() throws Exception {
		assertEquals(discrepancyShortcutsAnalyzer.getNextResolutionProposedLink(),
				DiscrepancyShortcutsAnalyzer.FIRST_RESOLUTION_PROPOSED);
	}
}
