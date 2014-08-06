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

import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.service.DiscrepancyNoteThread;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for building and containing discrepancy notes analyzing objects that will be placed on data entry UX.
 */
public class DiscrepancyShortcutsAnalyzer {

	public static final String DISCREPANCY_SHORTCUTS_ANALYZER = "discrepancyShortcutsAnalyzer";

	public static final String FIRST_NEW_DN = "#firstNewDn";
	public static final String FIRST_UPDATED_DN = "#firstUpdatedDn";
	public static final String FIRST_RESOLUTION_PROPOSED = "#firstResolutionProposed";
	public static final String FIRST_CLOSED_DN = "#firstClosedDn";
	public static final String FIRST_ANNOTATION = "#firstAnnotation";
	public static final String SERVLET_PATH = "servletPath";
	public static final String SECTION_ID = "sectionId";
	public static final String TAB_ID = "tabId";
	public static final String DOMAIN_NAME = "domain_name";

	public static final int RES_STATUS_OPEN = 1;
	public static final int RES_STATUS_UPDATED = 2;
	public static final int RES_STATUS_RESOLVED = 3;
	public static final int RES_STATUS_CLOSED = 4;
	public static final int RES_STATUS_NOT_APPLICABLE = 5;

	private boolean hasNotes;

	private int totalNew;
	private int totalUpdated;
	private int totalResolutionProposed;
	private int totalClosed;
	private int totalAnnotations;

	private int sectionTotalNew;
	private int sectionTotalUpdated;
	private int sectionTotalResolutionProposed;
	private int sectionTotalClosed;
	private int sectionTotalAnnotations;

	private String firstNewDnLink = FIRST_NEW_DN;
	private String firstUpdatedDnLink = FIRST_UPDATED_DN;
	private String firstResolutionProposedLink = FIRST_RESOLUTION_PROPOSED;
	private String firstClosedDnLink = FIRST_CLOSED_DN;
	private String firstAnnotationLink = FIRST_ANNOTATION;

	public int getTotalNew() {
		return totalNew;
	}

	/**
	 * Increases total number of new notes.
	 */
	public void incTotalNew() {
		totalNew++;
	}

	public int getTotalUpdated() {
		return totalUpdated;
	}

	/**
	 * Increases total number of updated notes.
	 */
	public void incTotalUpdated() {
		totalUpdated++;
	}

	public int getTotalResolutionProposed() {
		return totalResolutionProposed;
	}

	/**
	 * Increases total number of resolution proposed notes.
	 */
	public void incTotalResolutionProposed() {
		totalResolutionProposed++;
	}

	public int getTotalClosed() {
		return totalClosed;
	}

	/**
	 * Increases total number of closed notes.
	 */
	public void incTotalClosed() {
		totalClosed++;
	}

	public int getTotalAnnotations() {
		return totalAnnotations;
	}

	/**
	 * Increases total number of annotation notes.
	 */
	public void incTotalAnnotations() {
		totalAnnotations++;
	}

	public int getSectionTotalNew() {
		return sectionTotalNew;
	}

	/**
	 * Increases number of total section notes.
	 */
	public void incSectionTotalNew() {
		sectionTotalNew++;
	}

	public int getSectionTotalUpdated() {
		return sectionTotalUpdated;
	}

	/**
	 * Increases number of updated section notes.
	 */
	public void incSectionTotalUpdated() {
		sectionTotalUpdated++;
	}

	public int getSectionTotalResolutionProposed() {
		return sectionTotalResolutionProposed;
	}

	/**
	 * Increases number of resolution proposed section notes.
	 */
	public void incSectionTotalResolutionProposed() {
		sectionTotalResolutionProposed++;
	}

	public int getSectionTotalClosed() {
		return sectionTotalClosed;
	}

	/**
	 * Increases number of closed section notes.
	 */
	public void incSectionTotalClosed() {
		sectionTotalClosed++;
	}

	public int getSectionTotalAnnotations() {
		return sectionTotalAnnotations;
	}

	/**
	 * Increases number of annotation section notes.
	 */
	public void incSectionTotalAnnotations() {
		sectionTotalAnnotations++;
	}

	public String getFirstNewDnLink() {
		return firstNewDnLink;
	}

	public void setFirstNewDnLink(String firstNewDnLink) {
		this.firstNewDnLink = firstNewDnLink;
	}

	public String getFirstUpdatedDnLink() {
		return firstUpdatedDnLink;
	}

	public void setFirstUpdatedDnLink(String firstUpdatedDnLink) {
		this.firstUpdatedDnLink = firstUpdatedDnLink;
	}

	public String getFirstResolutionProposedLink() {
		return firstResolutionProposedLink;
	}

	public void setFirstResolutionProposedLink(String firstResolutionProposedLink) {
		this.firstResolutionProposedLink = firstResolutionProposedLink;
	}

	public String getFirstClosedDnLink() {
		return firstClosedDnLink;
	}

	public void setFirstClosedDnLink(String firstClosedDnLink) {
		this.firstClosedDnLink = firstClosedDnLink;
	}

	public String getFirstAnnotationLink() {
		return firstAnnotationLink;
	}

	public void setFirstAnnotationLink(String firstAnnotationLink) {
		this.firstAnnotationLink = firstAnnotationLink;
	}

	public boolean isHasNotes() {
		return hasNotes;
	}

	public void setHasNotes(boolean hasNotes) {
		this.hasNotes = hasNotes;
	}

	/**
	 * Select section bean from the list by section id.
	 * 
	 * @param sections
	 *            the list with eCRF sections.
	 * @param sectionId
	 *            the current section id.
	 * @return the current section bean.
	 */
	public static int getTabNum(List<SectionBean> sections, int sectionId) {
		int tabNum = 1;
		if (sections != null && sections.size() > 0) {
			for (SectionBean sectionBean : sections) {
				if (sectionBean.getId() == sectionId) {
					tabNum = sections.indexOf(sectionBean) + 1;
					break;
				}
			}
		}
		return tabNum;
	}

	private static String buildLink(FormProcessor fp, ItemFormMetadataBean ifmbean, EventCRFBean eventCrfBean,
			int eventDefinitionCRFId, List<SectionBean> sections) {
		String link;
		int currentTabId;
		int currentSectionId;
		int tabNum = getTabNum(sections, ifmbean.getSectionId());
		String servletPath = fp.getString(SERVLET_PATH).isEmpty() ? fp.getRequest().getServletPath() : fp
				.getString("servletPath");
		SectionBean currentSection = (SectionBean) fp.getRequest().getAttribute("section");
		if (fp.getRequest().getMethod().equalsIgnoreCase("POST")) {
			currentSectionId = currentSection.getId();
			currentTabId = getTabNum(sections, currentSectionId);
		} else {
			currentTabId = fp.getInt(TAB_ID) == 0 ? 1 : fp.getInt(TAB_ID);
			currentSectionId = fp.getInt(SECTION_ID, true) == 0 ? (sections != null && sections.size() > 0 ? sections
					.get(0).getId() : 0) : fp.getInt(SECTION_ID, true);
		}

		String cw = fp.getRequest().getParameter("cw");
		String closeWindowParameter = cw != null ? "&cw=1" : "";
		if (servletPath.equalsIgnoreCase("/ResolveDiscrepancy")
				|| servletPath.equalsIgnoreCase("/ViewSectionDataEntry")
				|| servletPath.equalsIgnoreCase("/ViewSectionDataEntryRESTUrlServlet")) {
			link = currentSectionId == ifmbean.getSectionId() ? "" : fp.getRequest().getScheme()
					+ "://"
					+ fp.getRequest().getSession().getAttribute(DOMAIN_NAME)
					+ fp.getRequest().getRequestURI()
							.replaceAll(fp.getRequest().getServletPath(), "/ViewSectionDataEntry") + "?eventCRFId="
					+ eventCrfBean.getId() + closeWindowParameter + "&crfVersionId=" + eventCrfBean.getCRFVersionId()
					+ "&sectionId=" + ifmbean.getSectionId() + "&tabId=" + tabNum + "&studySubjectId="
					+ eventCrfBean.getStudySubjectId() + "&eventDefinitionCRFId=" + eventDefinitionCRFId
					+ (fp.getString("exitTo", true).isEmpty() ? "" : "&exitTo=" + fp.getString("exitTo", true));
		} else {
			link = currentTabId == tabNum ? "" : fp.getRequest().getScheme() + "://"
					+ fp.getRequest().getSession().getAttribute(DOMAIN_NAME)
					+ fp.getRequest().getRequestURI().replaceAll(fp.getRequest().getServletPath(), servletPath)
					+ "?eventCRFId=" + eventCrfBean.getId() + closeWindowParameter + "&sectionId="
					+ ifmbean.getSectionId() + "&tabId=" + tabNum
					+ (fp.getString("exitTo", true).isEmpty() ? "" : "&exitTo=" + fp.getString("exitTo", true));
		}
		return link;
	}

	/**
	 * Generates discrepancy note urls for jumping between sections.
	 * 
	 * @param request
	 *            the incoming request.
	 * @param eventCrfBean
	 *            the event crf bean for current crf.
	 * @param ifmdao
	 *            the item metadata bean for current crf.
	 * @param eventDefinitionCRFId
	 *            the event crf definition id.
	 * @param sections
	 *            the list of event crf sections.
	 * @param noteThreads
	 *            the list of discrepancy notes group.
	 */
	public static void prepareDnShortcutLinks(HttpServletRequest request, EventCRFBean eventCrfBean,
			ItemFormMetadataDAO ifmdao, int eventDefinitionCRFId, List<SectionBean> sections,
			List<DiscrepancyNoteThread> noteThreads) {
		DiscrepancyNoteBean tempBean;
		FormProcessor fp = new FormProcessor(request);
		Map<String, Integer> linkMap = new HashMap<String, Integer>();
		DiscrepancyShortcutsAnalyzer discrepancyShortcutsAnalyzer = new DiscrepancyShortcutsAnalyzer();
		request.setAttribute(DISCREPANCY_SHORTCUTS_ANALYZER, discrepancyShortcutsAnalyzer);
		if (request.getMethod().equalsIgnoreCase("POST") && request.getAttribute("section") == null) {
			return;
		}
		for (DiscrepancyNoteThread dnThread : noteThreads) {
			tempBean = dnThread.getLinkedNoteList().getLast();
			if (tempBean != null && tempBean.getEntityType().equalsIgnoreCase("itemData")
					&& tempBean.getParentDnId() == 0) {
				discrepancyShortcutsAnalyzer.setHasNotes(true);
				ItemFormMetadataBean ifmbean = ifmdao.findByItemIdAndCRFVersionId(tempBean.getItemId(),
						eventCrfBean.getCRFVersionId());
				String link = buildLink(fp, ifmbean, eventCrfBean, eventDefinitionCRFId, sections);
				if (ResolutionStatus.UPDATED.equals(tempBean.getResStatus())) {
					discrepancyShortcutsAnalyzer.incTotalUpdated();
					Integer sectionId = linkMap.get(FIRST_UPDATED_DN);
					if (sectionId == null || ifmbean.getSectionId() < sectionId) {
						linkMap.put(FIRST_UPDATED_DN, ifmbean.getSectionId());
						discrepancyShortcutsAnalyzer.setFirstUpdatedDnLink(link + FIRST_UPDATED_DN);
					}
				} else if (ResolutionStatus.OPEN.equals(tempBean.getResStatus())) {
					discrepancyShortcutsAnalyzer.incTotalNew();
					Integer sectionId = linkMap.get(FIRST_NEW_DN);
					if (sectionId == null || ifmbean.getSectionId() < sectionId) {
						linkMap.put(FIRST_NEW_DN, ifmbean.getSectionId());
						discrepancyShortcutsAnalyzer.setFirstNewDnLink(link + FIRST_NEW_DN);
					}
				} else if (ResolutionStatus.CLOSED.equals(tempBean.getResStatus())) {
					discrepancyShortcutsAnalyzer.incTotalClosed();
					Integer sectionId = linkMap.get(FIRST_CLOSED_DN);
					if (sectionId == null || ifmbean.getSectionId() < sectionId) {
						linkMap.put(FIRST_CLOSED_DN, ifmbean.getSectionId());
						discrepancyShortcutsAnalyzer.setFirstClosedDnLink(link + FIRST_CLOSED_DN);
					}
				} else if (ResolutionStatus.RESOLVED.equals(tempBean.getResStatus())) {
					discrepancyShortcutsAnalyzer.incTotalResolutionProposed();
					Integer sectionId = linkMap.get(FIRST_RESOLUTION_PROPOSED);
					if (sectionId == null || ifmbean.getSectionId() < sectionId) {
						linkMap.put(FIRST_RESOLUTION_PROPOSED, ifmbean.getSectionId());
						discrepancyShortcutsAnalyzer.setFirstResolutionProposedLink(link + FIRST_RESOLUTION_PROPOSED);
					}
				} else if (ResolutionStatus.NOT_APPLICABLE.equals(tempBean.getResStatus())) {
					discrepancyShortcutsAnalyzer.incTotalAnnotations();
					Integer sectionId = linkMap.get(FIRST_ANNOTATION);
					if (sectionId == null || ifmbean.getSectionId() < sectionId) {
						linkMap.put(FIRST_ANNOTATION, ifmbean.getSectionId());
						discrepancyShortcutsAnalyzer.setFirstAnnotationLink(link + FIRST_ANNOTATION);
					}
				}
			}

		}
	}

	/**
	 * Generates url anchors suffixes for current note threads.
	 * 
	 * @param request
	 *            the incoming request.
	 * @param dib
	 *            the crf item that should be highlighted.
	 * @param noteThreads
	 *            the list of discrepancy notes threads.
	 */
	public static void prepareDnShortcutAnchors(HttpServletRequest request, DisplayItemBean dib,
			List<DiscrepancyNoteThread> noteThreads) {
		DiscrepancyShortcutsAnalyzer discrepancyShortcutsAnalyzer = (DiscrepancyShortcutsAnalyzer) request
				.getAttribute(DISCREPANCY_SHORTCUTS_ANALYZER);
		if (discrepancyShortcutsAnalyzer != null) {
			for (DiscrepancyNoteThread dnThread : noteThreads) {
				DiscrepancyNoteBean tempBean = dnThread.getLinkedNoteList().getLast();
				if (tempBean != null && tempBean.getEntityType().equalsIgnoreCase("itemData")
						&& tempBean.getParentDnId() == 0) {
					switch (tempBean.getResolutionStatusId()) {
					case RES_STATUS_OPEN:
						discrepancyShortcutsAnalyzer.incSectionTotalNew();
						if (discrepancyShortcutsAnalyzer.getSectionTotalNew() == 1) {
							dib.setFirstNewDn(true);
						}
						break;
					case RES_STATUS_UPDATED:
						discrepancyShortcutsAnalyzer.incSectionTotalUpdated();
						if (discrepancyShortcutsAnalyzer.getSectionTotalUpdated() == 1) {
							dib.setFirstUpdatedDn(true);
						}
						break;
					case RES_STATUS_RESOLVED:
						discrepancyShortcutsAnalyzer.incSectionTotalResolutionProposed();
						if (discrepancyShortcutsAnalyzer.getSectionTotalResolutionProposed() == 1) {
							dib.setFirstResolutionProposed(true);
						}
						break;
					case RES_STATUS_CLOSED:
						discrepancyShortcutsAnalyzer.incSectionTotalClosed();
						if (discrepancyShortcutsAnalyzer.getSectionTotalClosed() == 1) {
							dib.setFirstClosedDn(true);
							break;
						}
					case RES_STATUS_NOT_APPLICABLE:
						discrepancyShortcutsAnalyzer.incSectionTotalAnnotations();
						if (discrepancyShortcutsAnalyzer.getSectionTotalAnnotations() == 1) {
							dib.setFirstAnnotation(true);
						}
						break;
					default:
						break;
					}
				}
			}
		}
	}
}
