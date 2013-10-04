package com.clinovo.clincapture.control.submit;

import java.util.Arrays;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import org.akaza.openclinica.AbstractContextSentiveTest;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.control.submit.UpdateDNShortcutAnchorsServlet;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.util.DiscrepancyShortcutsAnalyzer;
import org.akaza.openclinica.view.Page;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class UpdateDNShortcutAnchorsServletTest extends AbstractContextSentiveTest {

	private ServletContext servletContext;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private RequestDispatcher requestDispatcher;
	private UpdateDNShortcutAnchorsServlet servlet;
	private FormDiscrepancyNotes formDiscrepancyNotes;
	private DiscrepancyShortcutsAnalyzer discrepancyShortcutsAnalyzer;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		servletContext = Mockito.mock(ServletContext.class);
		requestDispatcher = Mockito.mock(RequestDispatcher.class);
		servlet = new UpdateDNShortcutAnchorsServlet();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setPreferredLocales(Arrays.asList(new Locale[] { new Locale("en") }));
		ResourceBundleProvider.updateLocale(request.getLocale());
		formDiscrepancyNotes = new FormDiscrepancyNotes();
		request.getSession().setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, formDiscrepancyNotes);
		Whitebox.setInternalState(servlet, "servletContext", servletContext);
		Mockito.when(servletContext.getRequestDispatcher(Page.UPDATE_DN_SHORTCUT_ANCHORS_PAGE.getFileName()))
				.thenReturn(requestDispatcher);
		servlet.processRequest(request, response);
		discrepancyShortcutsAnalyzer = (DiscrepancyShortcutsAnalyzer) request
				.getAttribute(DiscrepancyShortcutsAnalyzer.DISCREPANCY_SHORTCUTS_ANALYZER);
	}

	@Test
	public void testThatGetTotalNewReturnsCorrect() throws Exception {
		assertEquals(discrepancyShortcutsAnalyzer.getTotalNew(), 0);
	}

	@Test
	public void testThatGetTotalClosedReturnsCorrect() throws Exception {
		assertEquals(discrepancyShortcutsAnalyzer.getTotalClosed(), 0);
	}

	@Test
	public void testThatGetTotalUpdatedReturnsCorrect() throws Exception {
		assertEquals(discrepancyShortcutsAnalyzer.getTotalUpdated(), 0);
	}

	@Test
	public void testThatGetTotalAnnotationsReturnsCorrect() throws Exception {
		assertEquals(discrepancyShortcutsAnalyzer.getTotalAnnotations(), 0);
	}

	@Test
	public void testThatGetTotalResolutionProposedReturnsCorrect() throws Exception {
		assertEquals(discrepancyShortcutsAnalyzer.getTotalResolutionProposed(), 0);
	}
}
