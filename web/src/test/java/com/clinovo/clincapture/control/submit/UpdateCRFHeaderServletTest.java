package com.clinovo.clincapture.control.submit;

import java.util.Arrays;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
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
		request.setPreferredLocales(Arrays.asList(new Locale[] { new Locale("en") }));
		ResourceBundleProvider.updateLocale(request.getLocale());
		formDiscrepancyNotes = new FormDiscrepancyNotes();
		request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, formDiscrepancyNotes);
		Whitebox.setInternalState(servlet, "servletContext", servletContext);
		Mockito.when(servletContext.getRequestDispatcher(Page.UPDATE_CRF_HEADER_PAGE.getFileName())).thenReturn(
                requestDispatcher);
		servlet.processRequest(request, response);
		discrepancyShortcutsAnalyzer = (DiscrepancyShortcutsAnalyzer) request
				.getAttribute(DiscrepancyShortcutsAnalyzer.DISCREPANCY_SHORTCUTS_ANALYZER);
	}

	@Test
	public void testThatGetFirstNewDnLinkReturnsCorrectValue() throws Exception {
		assertEquals(discrepancyShortcutsAnalyzer.getFirstNewDnLink(), DiscrepancyShortcutsAnalyzer.FIRST_NEW_DN);
	}

	@Test
	public void testThatGetFirstClosedDnLinkReturnsCorrectValue() throws Exception {
		assertEquals(discrepancyShortcutsAnalyzer.getFirstClosedDnLink(), DiscrepancyShortcutsAnalyzer.FIRST_CLOSED_DN);
	}

	@Test
	public void testThatGetFirstUpdatedDnLinkReturnsCorrectValue() throws Exception {
		assertEquals(discrepancyShortcutsAnalyzer.getFirstUpdatedDnLink(),
				DiscrepancyShortcutsAnalyzer.FIRST_UPDATED_DN);
	}

	@Test
	public void testThatGetFirstAnnotationLinkReturnsCorrectValue() throws Exception {
		assertEquals(discrepancyShortcutsAnalyzer.getFirstAnnotationLink(),
				DiscrepancyShortcutsAnalyzer.FIRST_ANNOTATION);
	}

	@Test
	public void testThatGetFirstResolutionProposedLinkReturnsCorrectValue() throws Exception {
		assertEquals(discrepancyShortcutsAnalyzer.getFirstResolutionProposedLink(),
				DiscrepancyShortcutsAnalyzer.FIRST_RESOLUTION_PROPOSED);
	}
}