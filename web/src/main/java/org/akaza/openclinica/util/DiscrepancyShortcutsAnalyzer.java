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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.service.DiscrepancyNoteThread;

/**
 * Class for building and containing discrepancy notes analyzing objects that will be placed on data entry UX.
 */
public class DiscrepancyShortcutsAnalyzer {

	public static final String DISCREPANCY_SHORTCUTS_ANALYZER = "discrepancyShortcutsAnalyzer";
	public static final String INTERVIEWER_NAME = "interviewer_name";
	public static final String DATE_INTERVIEWED = "date_interviewed";
	public static final String FIRST_NEW_DN = "#newDn_1";
	public static final String FIRST_UPDATED_DN = "#updatedDn_1";
	public static final String FIRST_RESOLUTION_PROPOSED = "#resolutionProposedDn_1";
	public static final String FIRST_CLOSED_DN = "#closedDn_1";
	public static final String FIRST_ANNOTATION = "#annotationDn_1";
	public static final String FIRST_ITEM_TO_SDV = "#itemToSDV_1";
	public static final String SERVLET_PATH = "servletPath";
	public static final String SECTION_ID = "sectionId";
	public static final String TAB_ID = "tabId";
	public static final String DOMAIN_NAME = "domain_name";

	public static final int RES_STATUS_OPEN = 1;
	public static final int RES_STATUS_UPDATED = 2;
	public static final int RES_STATUS_RESOLVED = 3;
	public static final int RES_STATUS_CLOSED = 4;
	public static final int RES_STATUS_NOT_APPLICABLE = 5;
	public static final String MIN_LEFT_D = "_minLeftD";
	public static final String MIN_RIGHT_D = "_minRightD";

	private boolean hasNotes;

	private int totalNew;
	private int totalUpdated;
	private int totalResolutionProposed;
	private int totalClosed;
	private int totalAnnotations;
	private int totalItemsToSDV;

	private int sectionTotalNew;
	private int sectionTotalUpdated;
	private int sectionTotalResolutionProposed;
	private int sectionTotalClosed;
	private int sectionTotalAnnotations;
	private int sectionTotalItemsToSDV;

	private String nextNewDnLink = FIRST_NEW_DN;
	private String nextUpdatedDnLink = FIRST_UPDATED_DN;
	private String nextResolutionProposedLink = FIRST_RESOLUTION_PROPOSED;
	private String nextClosedDnLink = FIRST_CLOSED_DN;
	private String nextAnnotationLink = FIRST_ANNOTATION;
	private String nextItemToSDVLink = FIRST_ITEM_TO_SDV;

	private boolean userIsAbleToSDVItems;

	private DisplayItemBean interviewerDisplayItemBean = new DisplayItemBean();

	private DisplayItemBean interviewDateDisplayItemBean = new DisplayItemBean();

	public boolean isUserIsAbleToSDVItems() {
		return userIsAbleToSDVItems;
	}

	public void setUserIsAbleToSDVItems(boolean userIsAbleToSDVItems) {
		this.userIsAbleToSDVItems = userIsAbleToSDVItems;
	}

	public DisplayItemBean getInterviewDateDisplayItemBean() {
		return interviewDateDisplayItemBean;
	}

	public void setInterviewDateDisplayItemBean(DisplayItemBean interviewDateDisplayItemBean) {
		this.interviewDateDisplayItemBean = interviewDateDisplayItemBean;
	}

	public DisplayItemBean getInterviewerDisplayItemBean() {
		return interviewerDisplayItemBean;
	}

	public void setInterviewerDisplayItemBean(DisplayItemBean interviewerDisplayItemBean) {
		this.interviewerDisplayItemBean = interviewerDisplayItemBean;
	}

	public int getTotalNew() {
		return totalNew;
	}

	/**
	 * Increases total number of new notes.
	 */
	public void incTotalNew() {
		totalNew++;
	}

	public void setTotalNew(int totalNew) {
		this.totalNew = totalNew;
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

	public void setTotalUpdated(int totalUpdated) {
		this.totalUpdated = totalUpdated;
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

	public void setTotalResolutionProposed(int totalResolutionProposed) {
		this.totalResolutionProposed = totalResolutionProposed;
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

	public void setTotalClosed(int totalClosed) {
		this.totalClosed = totalClosed;
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

	public void setTotalAnnotations(int totalAnnotations) {
		this.totalAnnotations = totalAnnotations;
	}

	public int getTotalItemsToSDV() {
		return totalItemsToSDV;
	}

	/**
	 * Increases total number of item to SDV notes.
	 */
	public void incTotalItemsToSDV() {
		totalItemsToSDV++;
	}

	public void setTotalItemsToSDV(int totalItemsToSDV) {
		this.totalItemsToSDV = totalItemsToSDV;
	}

	public int getSectionTotalNew() {
		return sectionTotalNew;
	}

	public void setSectionTotalItemsToSDV(int sectionTotalItemsToSDV) {
		this.sectionTotalItemsToSDV = sectionTotalItemsToSDV;
	}

	public void setSectionTotalAnnotations(int sectionTotalAnnotations) {
		this.sectionTotalAnnotations = sectionTotalAnnotations;
	}

	public void setSectionTotalNew(int sectionTotalNew) {
		this.sectionTotalNew = sectionTotalNew;
	}

	public void setSectionTotalUpdated(int sectionTotalUpdated) {
		this.sectionTotalUpdated = sectionTotalUpdated;
	}

	public void setSectionTotalResolutionProposed(int sectionTotalResolutionProposed) {
		this.sectionTotalResolutionProposed = sectionTotalResolutionProposed;
	}

	public void setSectionTotalClosed(int sectionTotalClosed) {
		this.sectionTotalClosed = sectionTotalClosed;
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

	public int getSectionTotalItemsToSDV() {
		return sectionTotalItemsToSDV;
	}

	/**
	 * Increases number of items To SDV section notes.
	 */
	public void incSectionTotalItemsToSDV() {
		sectionTotalItemsToSDV++;
	}

	public String getNextNewDnLink() {
		return nextNewDnLink;
	}

	public void setNextNewDnLink(String nextNewDnLink) {
		this.nextNewDnLink = nextNewDnLink;
	}

	public String getNextUpdatedDnLink() {
		return nextUpdatedDnLink;
	}

	public void setNextUpdatedDnLink(String nextUpdatedDnLink) {
		this.nextUpdatedDnLink = nextUpdatedDnLink;
	}

	public String getNextResolutionProposedLink() {
		return nextResolutionProposedLink;
	}

	public void setNextResolutionProposedLink(String nextResolutionProposedLink) {
		this.nextResolutionProposedLink = nextResolutionProposedLink;
	}

	public String getNextClosedDnLink() {
		return nextClosedDnLink;
	}

	public void setNextClosedDnLink(String nextClosedDnLink) {
		this.nextClosedDnLink = nextClosedDnLink;
	}

	public String getNextAnnotationLink() {
		return nextAnnotationLink;
	}

	public void setNextAnnotationLink(String nextAnnotationLink) {
		this.nextAnnotationLink = nextAnnotationLink;
	}

	public String getNextItemToSDVLink() {
		return nextItemToSDVLink;
	}

	public void setNextItemToSDVLink(String nextItemToSDVLink) {
		this.nextItemToSDVLink = nextItemToSDVLink;
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

	private static final class CurrentSectionInfo {

		private int currentTabId;
		private int currentSectionId;

		private CurrentSectionInfo(FormProcessor fp, List<SectionBean> sections) {
			SectionBean currentSection = (SectionBean) fp.getRequest().getAttribute("section");
			if (fp.getRequest().getMethod().equalsIgnoreCase("POST")) {
				currentSectionId = currentSection.getId();
			} else {
				currentSectionId = fp.getInt(SECTION_ID, true) == 0 ? (sections != null && sections.size() > 0
						? sections.get(0).getId()
						: 0) : fp.getInt(SECTION_ID, true);
			}
			currentTabId = getTabNum(sections, currentSectionId);
		}
	}

	private static String buildLink(FormProcessor fp, int sectionId, EventCRFBean eventCrfBean,
			int eventDefinitionCRFId, List<SectionBean> sections) {
		String link;
		int tabNum = getTabNum(sections, sectionId);
		String servletPath = fp.getString(SERVLET_PATH).isEmpty() ? fp.getRequest().getServletPath() : fp
				.getString("servletPath");
		CurrentSectionInfo currentSectionInfo = new CurrentSectionInfo(fp, sections);
		String cw = fp.getRequest().getParameter("cw");
		String closeWindowParameter = cw != null ? "&cw=1" : "";
		if (servletPath.equalsIgnoreCase("/ResolveDiscrepancy")
				|| servletPath.equalsIgnoreCase("/ViewSectionDataEntry")
				|| servletPath.equalsIgnoreCase("/ViewSectionDataEntryRESTUrlServlet")) {
			link = currentSectionInfo.currentSectionId == sectionId ? "" : fp.getRequest().getScheme()
					+ "://"
					+ fp.getRequest().getSession().getAttribute(DOMAIN_NAME)
					+ fp.getRequest().getRequestURI()
							.replaceAll(fp.getRequest().getServletPath(), "/ViewSectionDataEntry") + "?eventCRFId="
					+ eventCrfBean.getId() + closeWindowParameter + "&crfVersionId=" + eventCrfBean.getCRFVersionId()
					+ "&sectionId=" + sectionId + "&tabId=" + tabNum + "&studySubjectId="
					+ eventCrfBean.getStudySubjectId() + "&eventDefinitionCRFId=" + eventDefinitionCRFId
					+ (fp.getString("exitTo", true).isEmpty() ? "" : "&exitTo=" + fp.getString("exitTo", true));
		} else {
			link = currentSectionInfo.currentTabId == tabNum ? "" : fp.getRequest().getScheme() + "://"
					+ fp.getRequest().getSession().getAttribute(DOMAIN_NAME)
					+ fp.getRequest().getRequestURI().replaceAll(fp.getRequest().getServletPath(), servletPath)
					+ "?eventCRFId=" + eventCrfBean.getId() + closeWindowParameter + "&sectionId=" + sectionId
					+ "&tabId=" + tabNum
					+ (fp.getString("exitTo", true).isEmpty() ? "" : "&exitTo=" + fp.getString("exitTo", true));
		}
		return link;
	}
	private static void prepareItemsToSDVShortcutLink(HttpServletRequest request, EventCRFBean eventCrfBean,
			int eventDefinitionCRFId, List<SectionBean> allSections, ItemDataDAO itemDataDao) {
		List<DisplayItemBean> displayItemBeanList = itemDataDao.getMapItemsToSDV(eventCrfBean.getId());
		Map<String, Integer> deltaMap = new HashMap<String, Integer>();
		for (DisplayItemBean dib : displayItemBeanList) {
			prepareItemsToSDVShortcutLink(request, dib, eventCrfBean, eventDefinitionCRFId, allSections, deltaMap);
		}
	}

	/**
	 * Method that generates itemToSDV urls for jumping between sections.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param dib
	 *            DisplayItemBean
	 * @param eventCrfBean
	 *            EventCRFBean
	 * @param eventDefinitionCRFId
	 *            int
	 * @param sections
	 *            List<SectionBean>
	 * @param deltaMap
	 *            Map<String, Integer>
	 */
	public static void prepareItemsToSDVShortcutLink(HttpServletRequest request, DisplayItemBean dib,
			EventCRFBean eventCrfBean, int eventDefinitionCRFId, List<SectionBean> sections,
			Map<String, Integer> deltaMap) {
		if (dib.getMetadata() != null && dib.getMetadata().getId() > 0 && dib.getMetadata().isSdvRequired()) {
			FormProcessor fp = new FormProcessor(request);
			DiscrepancyShortcutsAnalyzer discrepancyShortcutsAnalyzer = getDiscrepancyShortcutsAnalyzer(request);
			if (request.getMethod().equalsIgnoreCase("POST") && request.getAttribute("section") == null) {
				return;
			}
			CurrentSectionInfo currentSectionInfo = new CurrentSectionInfo(fp, sections);
			String link = buildLink(fp, dib.getMetadata().getSectionId(), eventCrfBean, eventDefinitionCRFId, sections);
			analyze(discrepancyShortcutsAnalyzer, currentSectionInfo, deltaMap, link, FIRST_ITEM_TO_SDV, dib
					.getMetadata().getSectionId());
			discrepancyShortcutsAnalyzer.incTotalItemsToSDV();
		}
	}

	/**
	 * Method that generates discrepancy note urls for jumping between sections.
	 * 
	 * @param request
	 *            the incoming request.
	 * @param eventCrfBean
	 *            the event crf bean for current crf.
	 * @param itemDataDao
	 *            ItemDataDAO
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
			ItemDataDAO itemDataDao, ItemFormMetadataDAO ifmdao, int eventDefinitionCRFId, List<SectionBean> sections,
			List<DiscrepancyNoteThread> noteThreads) {
		DiscrepancyNoteBean tempBean;
		FormProcessor fp = new FormProcessor(request);
		Map<String, Integer> deltaMap = new HashMap<String, Integer>();
		request.removeAttribute(DISCREPANCY_SHORTCUTS_ANALYZER);
		DiscrepancyShortcutsAnalyzer discrepancyShortcutsAnalyzer = getDiscrepancyShortcutsAnalyzer(request);
		if (request.getMethod().equalsIgnoreCase("POST") && request.getAttribute("section") == null) {
			return;
		}
		CurrentSectionInfo currentSectionInfo = new CurrentSectionInfo(fp, sections);
		StudyUserRoleBean studyUserRoleBean = (StudyUserRoleBean) request.getSession().getAttribute("userRole");
		discrepancyShortcutsAnalyzer.setUserIsAbleToSDVItems(eventCrfBean.getStage().isDoubleDE_Complete()
				&& (studyUserRoleBean.isStudyAdministrator() || studyUserRoleBean.isMonitor()));
		prepareItemsToSDVShortcutLink(request, eventCrfBean, eventDefinitionCRFId, sections, itemDataDao);
		for (DiscrepancyNoteThread dnThread : noteThreads) {
			tempBean = dnThread.getLinkedNoteList().getLast();
			if (tempBean != null
					&& (tempBean.getEntityType().equalsIgnoreCase("itemData") || tempBean.getEntityType()
							.equalsIgnoreCase("eventCrf")) && tempBean.getParentDnId() == 0) {
				discrepancyShortcutsAnalyzer.setHasNotes(true);
				ItemFormMetadataBean ifmbean = ifmdao.findByItemIdAndCRFVersionId(tempBean.getItemId(),
						eventCrfBean.getCRFVersionId());
				String link = tempBean.getEntityType().equalsIgnoreCase("eventCrf") ? "" : buildLink(fp,
						ifmbean.getSectionId(), eventCrfBean, eventDefinitionCRFId, sections);
				String key = null;
				if (ResolutionStatus.UPDATED.equals(tempBean.getResStatus())) {
					discrepancyShortcutsAnalyzer.incTotalUpdated();
					key = FIRST_UPDATED_DN;
				} else if (ResolutionStatus.OPEN.equals(tempBean.getResStatus())) {
					key = FIRST_NEW_DN;
					discrepancyShortcutsAnalyzer.incTotalNew();
				} else if (ResolutionStatus.CLOSED.equals(tempBean.getResStatus())) {
					key = FIRST_CLOSED_DN;
					discrepancyShortcutsAnalyzer.incTotalClosed();
				} else if (ResolutionStatus.RESOLVED.equals(tempBean.getResStatus())) {
					key = FIRST_RESOLUTION_PROPOSED;
					discrepancyShortcutsAnalyzer.incTotalResolutionProposed();
				} else if (ResolutionStatus.NOT_APPLICABLE.equals(tempBean.getResStatus())) {
					key = FIRST_ANNOTATION;
					discrepancyShortcutsAnalyzer.incTotalAnnotations();
				}
				if (key != null && !tempBean.getEntityType().equalsIgnoreCase("eventCrf")) {
					analyze(discrepancyShortcutsAnalyzer, currentSectionInfo, deltaMap, link, key,
							ifmbean.getSectionId());
				}
			}
		}
	}

	/**
	 * Method that analyzes data to build dn shortcut link.
	 * 
	 * @param discrepancyShortcutsAnalyzer
	 *            DiscrepancyShortcutsAnalyzer
	 * @param currentSectionInfo
	 *            CurrentSectionInfo
	 * @param deltaMap
	 *            Map<String, Integer>
	 * @param link
	 *            String
	 * @param key
	 *            String
	 * @param itemSectionId
	 *            int
	 */
	public static void analyze(DiscrepancyShortcutsAnalyzer discrepancyShortcutsAnalyzer,
			CurrentSectionInfo currentSectionInfo, Map<String, Integer> deltaMap, String link, String key,
			int itemSectionId) {
		int d = itemSectionId - currentSectionInfo.currentSectionId;
		Integer minLeftD = deltaMap.get(key.concat(MIN_LEFT_D));
		Integer minRightD = deltaMap.get(key.concat(MIN_RIGHT_D));
		if (d <= 0 && minRightD == null) {
			minLeftD = minLeftD == null ? d : Math.min(d, minLeftD);
			if (minLeftD == d) {
				deltaMap.put(key.concat(MIN_LEFT_D), minLeftD);
				setNextLink(discrepancyShortcutsAnalyzer, link, key);
			}
		} else if (d > 0) {
			minRightD = minRightD == null ? d : Math.min(d, minRightD);
			if (minRightD == d) {
				deltaMap.put(key.concat(MIN_RIGHT_D), minRightD);
				setNextLink(discrepancyShortcutsAnalyzer, link, key);
			}
		}
	}

	/**
	 * Method that sets next dn link for certain resolution.
	 *
	 * @param discrepancyShortcutsAnalyzer
	 *            DiscrepancyShortcutsAnalyzer
	 * @param link
	 *            String
	 * @param key
	 *            String
	 */
	public static void setNextLink(DiscrepancyShortcutsAnalyzer discrepancyShortcutsAnalyzer, String link, String key) {
		if (key.equals(FIRST_UPDATED_DN)) {
			discrepancyShortcutsAnalyzer.setNextUpdatedDnLink(link + FIRST_UPDATED_DN);
		} else if (key.equals(FIRST_NEW_DN)) {
			discrepancyShortcutsAnalyzer.setNextNewDnLink(link + FIRST_NEW_DN);
		} else if (key.equals(FIRST_CLOSED_DN)) {
			discrepancyShortcutsAnalyzer.setNextClosedDnLink(link + FIRST_CLOSED_DN);
		} else if (key.equals(FIRST_RESOLUTION_PROPOSED)) {
			discrepancyShortcutsAnalyzer.setNextResolutionProposedLink(link + FIRST_RESOLUTION_PROPOSED);
		} else if (key.equals(FIRST_ANNOTATION)) {
			discrepancyShortcutsAnalyzer.setNextAnnotationLink(link + FIRST_ANNOTATION);
		} else if (key.equals(FIRST_ITEM_TO_SDV)) {
			discrepancyShortcutsAnalyzer.setNextItemToSDVLink(link + FIRST_ITEM_TO_SDV);
		}
	}

	/**
	 * Generates url anchors suffixes for current note threads.
	 * 
	 * @param request
	 *            the incoming request.
	 * @param dib
	 *            the crf item that should be highlighted.
	 * @param additionalCheck
	 *            boolean
	 * @param noteThreads
	 *            the list of discrepancy notes threads.
	 */
	public static void prepareDnShortcutAnchors(HttpServletRequest request, DisplayItemBean dib,
			List<DiscrepancyNoteThread> noteThreads, boolean additionalCheck) {
		DiscrepancyShortcutsAnalyzer discrepancyShortcutsAnalyzer = getDiscrepancyShortcutsAnalyzer(request);
		if (dib.getMetadata() != null && dib.getData() != null && dib.getData().getId() > 0
				&& dib.getMetadata().getId() > 0 && dib.getMetadata().isSdvRequired() && !dib.getData().isSdv()) {
			discrepancyShortcutsAnalyzer.incSectionTotalItemsToSDV();
			dib.getItemToSDV().add(
					"itemToSDV_".concat(Integer.toString(discrepancyShortcutsAnalyzer.getSectionTotalItemsToSDV())));
		}
		for (DiscrepancyNoteThread dnThread : noteThreads) {
			DiscrepancyNoteBean tempBean = dnThread.getLinkedNoteList().getLast();
			if (tempBean != null
					&& tempBean.getParentDnId() == 0
					&& (tempBean.getEntityType().equalsIgnoreCase("itemData") || tempBean.getEntityType()
							.equalsIgnoreCase("eventCrf"))) {
				if (additionalCheck) {
					if (tempBean.getEntityType().equalsIgnoreCase("itemData")
							&& !(((tempBean.getId() > 0 && tempBean.getItemId() == dib.getDbData().getItemId() && tempBean
									.getItemDataOrdinal() == dib.getDbData().getOrdinal()) || (tempBean.getId() == 0 && tempBean
									.getField().equalsIgnoreCase(dib.getField()))))) {
						continue;
					}
					if (tempBean.getEntityType().equalsIgnoreCase("eventCrf")
							&& !tempBean.getColumn().equalsIgnoreCase(dib.getField())) {
						continue;
					}
				}
				switch (tempBean.getResolutionStatusId()) {
					case RES_STATUS_OPEN :
						discrepancyShortcutsAnalyzer.incSectionTotalNew();
						dib.getNewDn().add(
								"newDn_".concat(Integer.toString(discrepancyShortcutsAnalyzer.getSectionTotalNew())));
						break;
					case RES_STATUS_UPDATED :
						discrepancyShortcutsAnalyzer.incSectionTotalUpdated();
						dib.getUpdatedDn().add(
								"updatedDn_".concat(Integer.toString(discrepancyShortcutsAnalyzer
										.getSectionTotalUpdated())));
						break;
					case RES_STATUS_RESOLVED :
						discrepancyShortcutsAnalyzer.incSectionTotalResolutionProposed();
						dib.getResolutionProposedDn().add(
								"resolutionProposedDn_".concat(Integer.toString(discrepancyShortcutsAnalyzer
										.getSectionTotalResolutionProposed())));
						break;
					case RES_STATUS_CLOSED :
						discrepancyShortcutsAnalyzer.incSectionTotalClosed();
						dib.getClosedDn().add(
								"closedDn_".concat(Integer.toString(discrepancyShortcutsAnalyzer
										.getSectionTotalClosed())));
						break;
					case RES_STATUS_NOT_APPLICABLE :
						discrepancyShortcutsAnalyzer.incSectionTotalAnnotations();
						dib.getAnnotationDn().add(
								"annotationDn_".concat(Integer.toString(discrepancyShortcutsAnalyzer
										.getSectionTotalAnnotations())));
						break;
					default :
						break;
				}
			}
		}
	}

	/**
	 * Returns DiscrepancyShortcutsAnalyzer (it's unique per request).
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return DiscrepancyShortcutsAnalyzer
	 */
	public static DiscrepancyShortcutsAnalyzer getDiscrepancyShortcutsAnalyzer(HttpServletRequest request) {
		DiscrepancyShortcutsAnalyzer discrepancyShortcutsAnalyzer = (DiscrepancyShortcutsAnalyzer) request
				.getAttribute(DISCREPANCY_SHORTCUTS_ANALYZER);
		if (discrepancyShortcutsAnalyzer == null) {
			discrepancyShortcutsAnalyzer = new DiscrepancyShortcutsAnalyzer();
			discrepancyShortcutsAnalyzer.getInterviewerDisplayItemBean().setField(INTERVIEWER_NAME);
			discrepancyShortcutsAnalyzer.getInterviewDateDisplayItemBean().setField(DATE_INTERVIEWED);
			request.setAttribute(DISCREPANCY_SHORTCUTS_ANALYZER, discrepancyShortcutsAnalyzer);
		}
		return discrepancyShortcutsAnalyzer;
	}
}
