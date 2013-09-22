package org.akaza.openclinica.util;

import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletRequest;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

public class DiscrepancyShortcutsAnalyzerTest {

	private ItemDataBean itemDataBean;
	private HttpServletRequest request;
	private DisplayItemBean displayItemBean;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		itemDataBean = Mockito.mock(ItemDataBean.class);
		itemDataBean.setId(1);
		displayItemBean = new DisplayItemBean();
		displayItemBean.setData(itemDataBean);
        request.setAttribute("discrepancyShortcutsAnalyzer", new DiscrepancyShortcutsAnalyzer());
	}

	@Test
	public void testThatIsFirstNewDnReturnsCorrectValue() throws Exception {
		displayItemBean.setDiscrepancyNoteStatus(1);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean);
		assertTrue(displayItemBean.isFirstNewDn());
	}

	@Test
	public void testThatIsFirstUpdatedDnReturnsCorrectValue() throws Exception {
		displayItemBean.setDiscrepancyNoteStatus(2);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean);
		assertTrue(displayItemBean.isFirstUpdatedDn());
	}

	@Test
	public void testThatIsFirstResolutionProposedReturnsCorrectValue() throws Exception {
		displayItemBean.setDiscrepancyNoteStatus(3);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean);
		assertTrue(displayItemBean.isFirstResolutionProposed());
	}

	@Test
	public void testThatIsFirstClosedDnReturnsCorrectValue() throws Exception {
		displayItemBean.setDiscrepancyNoteStatus(4);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean);
		assertTrue(displayItemBean.isFirstClosedDn());
	}

	@Test
	public void testThatIsFirstAnnotationReturnsCorrectValue() throws Exception {
		displayItemBean.setDiscrepancyNoteStatus(5);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean);
		assertTrue(displayItemBean.isFirstAnnotation());
	}
}
