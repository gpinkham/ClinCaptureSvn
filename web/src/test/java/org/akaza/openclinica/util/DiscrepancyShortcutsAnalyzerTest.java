package org.akaza.openclinica.util;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import javax.servlet.http.HttpServletRequest;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.service.DiscrepancyNoteThread;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

public class DiscrepancyShortcutsAnalyzerTest {

	private ItemBean itemBean;
	private ItemDataBean itemDataBean;
	private HttpServletRequest request;
	private DisplayItemBean displayItemBean;
	private DiscrepancyNoteBean discrepancyNoteBean;
	private DiscrepancyNoteThread discrepancyNoteThread;
	@SuppressWarnings("rawtypes")
	private ArrayList discrepancyNotes;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		itemDataBean = Mockito.mock(ItemDataBean.class);
		itemDataBean.setId(1);
		displayItemBean = new DisplayItemBean();
		displayItemBean.setData(itemDataBean);
		displayItemBean.setDbData(itemDataBean);
		itemBean = new ItemBean();
		itemBean.setId(1);
		displayItemBean.setItem(itemBean);
		discrepancyNotes = new ArrayList();
		discrepancyNoteBean = new DiscrepancyNoteBean();
		discrepancyNoteBean.setItemId(1);
		discrepancyNoteBean.setEntityType("itemData");
		discrepancyNotes.add(discrepancyNoteBean);
		discrepancyNoteThread = new DiscrepancyNoteThread();
		discrepancyNoteThread.setLinkedNoteList(new LinkedList(discrepancyNotes));
		displayItemBean.setDiscrepancyNotes(discrepancyNotes);
		request.setAttribute("discrepancyShortcutsAnalyzer", new DiscrepancyShortcutsAnalyzer());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testThatIsFirstNewDnReturnsCorrectValue() throws Exception {
		discrepancyNoteBean.setResolutionStatusId(1);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean, discrepancyNotes);
		assertTrue(displayItemBean.isFirstNewDn());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testThatIsFirstUpdatedDnReturnsCorrectValue() throws Exception {
		discrepancyNoteBean.setResolutionStatusId(2);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean, discrepancyNotes);
		assertTrue(displayItemBean.isFirstUpdatedDn());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testThatIsFirstResolutionProposedReturnsCorrectValue() throws Exception {
		discrepancyNoteBean.setResolutionStatusId(3);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean, discrepancyNotes);
		assertTrue(displayItemBean.isFirstResolutionProposed());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testThatIsFirstClosedDnReturnsCorrectValue() throws Exception {
		discrepancyNoteBean.setResolutionStatusId(4);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean, discrepancyNotes);
		assertTrue(displayItemBean.isFirstClosedDn());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testThatIsFirstAnnotationReturnsCorrectValue() throws Exception {
		discrepancyNoteBean.setResolutionStatusId(5);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean, discrepancyNotes);
		assertTrue(displayItemBean.isFirstAnnotation());
	}
}
