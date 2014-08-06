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
package org.akaza.openclinica.util;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.service.DiscrepancyNoteThread;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DiscrepancyShortcutsAnalyzerTest extends DefaultAppContextTest {

	public static final int THREE = 3;
	public static final int FOUR = 4;
	public static final int FIVE = 5;
	public static final int SIX = 6;
	private MockHttpServletRequest request;
	private DisplayItemBean displayItemBean;
	private DiscrepancyNoteBean discrepancyNoteBean;
	private List<DiscrepancyNoteThread> noteThreads;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		ItemDataBean itemDataBean = Mockito.mock(ItemDataBean.class);
		itemDataBean.setId(1);
		displayItemBean = new DisplayItemBean();
		displayItemBean.setData(itemDataBean);
		displayItemBean.setDbData(itemDataBean);
		ItemBean itemBean = new ItemBean();
		itemBean.setId(1);
		displayItemBean.setItem(itemBean);
		noteThreads = new ArrayList<DiscrepancyNoteThread>();
		ArrayList<DiscrepancyNoteBean> discrepancyNotes = new ArrayList<DiscrepancyNoteBean>();
		discrepancyNoteBean = new DiscrepancyNoteBean();
		discrepancyNoteBean.setItemId(1);
		discrepancyNoteBean.setEntityType("itemData");
		discrepancyNoteBean.setParentDnId(0);
		discrepancyNotes.add(discrepancyNoteBean);
		DiscrepancyNoteThread discrepancyNoteThread = new DiscrepancyNoteThread();
		discrepancyNoteThread.setLinkedNoteList(new LinkedList<DiscrepancyNoteBean>(discrepancyNotes));
		displayItemBean.setDiscrepancyNotes(discrepancyNotes);
		noteThreads.add(discrepancyNoteThread);
		request.setAttribute("discrepancyShortcutsAnalyzer", new DiscrepancyShortcutsAnalyzer());
	}

	@Test
	public void testThatIsFirstNewDnReturnsCorrectValue() throws Exception {
		discrepancyNoteBean.setResolutionStatusId(1);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean, noteThreads);
		assertTrue(displayItemBean.isFirstNewDn());
	}

	@Test
	public void testThatIsFirstUpdatedDnReturnsCorrectValue() throws Exception {
		discrepancyNoteBean.setResolutionStatusId(2);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean, noteThreads);
		assertTrue(displayItemBean.isFirstUpdatedDn());
	}

	@Test
	public void testThatIsFirstResolutionProposedReturnsCorrectValue() throws Exception {
		discrepancyNoteBean.setResolutionStatusId(THREE);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean, noteThreads);
		assertTrue(displayItemBean.isFirstResolutionProposed());
	}

	@Test
	public void testThatIsFirstClosedDnReturnsCorrectValue() throws Exception {
		discrepancyNoteBean.setResolutionStatusId(FOUR);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean, noteThreads);
		assertTrue(displayItemBean.isFirstClosedDn());
	}

	@Test
	public void testThatIsFirstAnnotationReturnsCorrectValue() throws Exception {
		discrepancyNoteBean.setResolutionStatusId(FIVE);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean, noteThreads);
		assertTrue(displayItemBean.isFirstAnnotation());
	}

	@Test
	public void testThatPrepareDnShortcutLinksBuildCorrectUrlForNonPopupPage() throws Exception {
		buildAnalyzerUrl(false);
		DiscrepancyShortcutsAnalyzer analyzer = (DiscrepancyShortcutsAnalyzer) request
				.getAttribute("discrepancyShortcutsAnalyzer");
		assertEquals("http://clincapture.com?eventCRFId=2&sectionId=0&tabId=1#firstNewDn", analyzer.getFirstNewDnLink());

	}

	@Test
	public void testThatPrepareDnShortcutLinksBuildCorrectUrlForPopupPage() throws Exception {
		buildAnalyzerUrl(true);
		DiscrepancyShortcutsAnalyzer analyzer = (DiscrepancyShortcutsAnalyzer) request
				.getAttribute("discrepancyShortcutsAnalyzer");
		assertEquals("http://clincapture.com?eventCRFId=2&cw=1&sectionId=0&tabId=1#firstNewDn",
				analyzer.getFirstNewDnLink());

	}

	private void buildAnalyzerUrl(boolean isPopup) {

		EventCRFBean eventCRFBean = new EventCRFBean();
		eventCRFBean.setId(2);
		ItemFormMetadataDAO itemFormMetadataDAO = Mockito.mock(ItemFormMetadataDAO.class);
		ItemFormMetadataBean itemFormMetadataBean = new ItemFormMetadataBean();
		itemFormMetadataBean.setId(FOUR);
		Mockito.when(itemFormMetadataDAO.findByItemIdAndCRFVersionId(Mockito.anyInt(), Mockito.anyInt())).thenReturn(
				itemFormMetadataBean);
		SectionBean section = new SectionBean();
		section.setId(FOUR);
		List<SectionBean> sectionBeans = new ArrayList<SectionBean>();
		sectionBeans.add(section);
		if (isPopup) {
			request.setParameter("cw", "1");
		}
		request.setParameter("tabId", "3");
		request.getSession().setAttribute("domain_name", "clincapture.com");
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutLinks(request, eventCRFBean, itemFormMetadataDAO, SIX,
				sectionBeans, noteThreads);
	}
}
