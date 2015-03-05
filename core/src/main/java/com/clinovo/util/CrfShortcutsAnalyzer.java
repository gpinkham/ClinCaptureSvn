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
package com.clinovo.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.service.DiscrepancyNoteThread;

import com.clinovo.service.ItemSDVService;

/**
 * Class for building and containing discrepancy notes analyzing objects that will be placed on data entry UX.
 */
public class CrfShortcutsAnalyzer {

	public static final String CRF_SHORTCUTS_ANALYZER = "crfShortcutsAnalyzer";
	public static final String INTERVIEWER_NAME = "interviewer_name";
	public static final String DATE_INTERVIEWED = "date_interviewed";
	public static final String FIRST_NEW_DN = "#newDn_1";
	public static final String FIRST_UPDATED_DN = "#updatedDn_1";
	public static final String FIRST_RESOLUTION_PROPOSED = "#resolutionProposedDn_1";
	public static final String FIRST_CLOSED_DN = "#closedDn_1";
	public static final String FIRST_ANNOTATION = "#annotationDn_1";
	public static final String FIRST_ITEM_TO_SDV = "#itemToSDV_1";
	public static final String SERVLET_PATH = "servletPath";
	public static final String SECTION = "section";
	public static final String POST = "POST";
	public static final String CW = "cw";
	public static final String EXIT_TO = "exitTo";
	public static final String SECTION_ID = "sectionId";
	public static final String TAB_ID = "tabId";
	public static final String DOMAIN_NAME = "domain_name";
	public static final String MIN_LEFT_D = "_minLeftD";
	public static final String MIN_RIGHT_D = "_minRightD";
	public static final String USER_ROLE = "userRole";
	public static final String ITEM_DATA = "itemData";
	public static final String EVENT_CRF = "eventCrf";
	public static final String ITEM_TO_SDV = "itemToSDV_";
	public static final String NEW_DN = "newDn_";
	public static final String UPDATED_DN = "updatedDn_";
	public static final String RESOLUTION_PROPOSED_DN = "resolutionProposedDn_";
	public static final String CLOSED_DN = "closedDn_";
	public static final String ANNOTATION_DN = "annotationDn_";

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
	private int totalItemsToSDV;

	private int sectionTotalNew;
	private int sectionTotalUpdated;
	private int sectionTotalResolutionProposed;
	private int sectionTotalClosed;
	private int sectionTotalAnnotations;
	private int sectionTotalItemsToSDV;

	private int itemsToSDVAnchorCounter;

	private String nextNewDnLink = FIRST_NEW_DN;
	private String nextUpdatedDnLink = FIRST_UPDATED_DN;
	private String nextResolutionProposedLink = FIRST_RESOLUTION_PROPOSED;
	private String nextClosedDnLink = FIRST_CLOSED_DN;
	private String nextAnnotationLink = FIRST_ANNOTATION;
	private String nextItemToSDVLink = FIRST_ITEM_TO_SDV;

	private boolean userIsAbleToSDVItems;

	private String scheme;
	private String requestURI;
	private String domainName;
	private String servletPath;
	private String requestMethod;
	private Map<String, Object> attributes;

	private ItemSDVService itemSDVService;

	public CrfShortcutsAnalyzer(String scheme, String requestMethod, String requestURI, String servletPath,
			String domainName, Map<String, Object> attributes, ItemSDVService itemSDVService) {
		this.scheme = scheme;
		this.requestURI = requestURI;
		this.domainName = domainName;
		this.attributes = attributes;
		this.servletPath = servletPath;
		this.requestMethod = requestMethod;
		this.itemSDVService = itemSDVService;
	}

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

	/**
	 * Increases the items to SDV anchor counter.
	 */
	public void incItemsToSDVAnchorCounter() {
		itemsToSDVAnchorCounter++;
	}

	public int getItemsToSDVAnchorCounter() {
		return itemsToSDVAnchorCounter;
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

	private final class CurrentSectionInfo {
		private int currentTabId;
		private int currentSectionId;
		public CurrentSectionInfo(List<SectionBean> sections) {
			SectionBean currentSection = (SectionBean) attributes.get(SECTION);
			if (requestMethod.equalsIgnoreCase(POST)) {
				currentSectionId = currentSection.getId();
			} else {
				currentSectionId = (Integer) attributes.get(SECTION_ID) == 0 ? (sections != null && sections.size() > 0
						? sections.get(0).getId()
						: 0) : (Integer) attributes.get(SECTION_ID);
			}
			currentTabId = getTabNum(sections, currentSectionId);
		}
	}

	private String buildLink(int sectionId, EventCRFBean eventCrfBean, int eventDefinitionCRFId,
			List<SectionBean> sections) {
		String link;
		int tabNum = getTabNum(sections, sectionId);
		String exitTo = (String) attributes.get(EXIT_TO);
		String servletPathAttr = (String) attributes.get(SERVLET_PATH);
		CurrentSectionInfo currentSectionInfo = new CurrentSectionInfo(sections);
		String cw = (String) attributes.get(CW);
		String closeWindowParameter = cw != null ? "&cw=1" : "";
		if (servletPathAttr.equalsIgnoreCase("/ResolveDiscrepancy")
				|| servletPathAttr.equalsIgnoreCase("/ViewSectionDataEntry")
				|| servletPathAttr.equalsIgnoreCase("/ViewSectionDataEntryRESTUrlServlet")) {
			link = currentSectionInfo.currentSectionId == sectionId ? "" : scheme.concat("://").concat(domainName)
					.concat(requestURI.replaceAll(servletPath, "/ViewSectionDataEntry")).concat("?eventCRFId=")
					.concat(Integer.toString(eventCrfBean.getId())).concat(closeWindowParameter)
					.concat("&crfVersionId=").concat(Integer.toString(eventCrfBean.getCRFVersionId()))
					.concat("&sectionId=").concat(Integer.toString(sectionId)).concat("&tabId=")
					.concat(Integer.toString(tabNum)).concat("&studySubjectId=")
					.concat(Integer.toString(eventCrfBean.getStudySubjectId())).concat("&eventDefinitionCRFId=")
					.concat(Integer.toString(eventDefinitionCRFId))
					.concat(exitTo.isEmpty() ? "" : "&exitTo=".concat(exitTo));
		} else {
			link = currentSectionInfo.currentTabId == tabNum ? "" : scheme.concat("://").concat(domainName)
					.concat(requestURI.replaceAll(servletPath, servletPathAttr)).concat("?eventCRFId=")
					.concat(Integer.toString(eventCrfBean.getId())).concat(closeWindowParameter).concat("&sectionId=")
					.concat(Integer.toString(sectionId)).concat("&tabId=").concat(Integer.toString(tabNum))
					.concat(exitTo.isEmpty() ? "" : "&exitTo=".concat(exitTo));
		}
		return link;
	}

	private void prepareItemsToSDVShortcutLink(EventCRFBean eventCrfBean, int eventDefinitionCRFId,
			List<SectionBean> allSections) {
		List<DisplayItemBean> displayItemBeanList = itemSDVService.getListOfItemsToSDV(eventCrfBean.getId());
		Map<String, Integer> deltaMap = new HashMap<String, Integer>();
		for (DisplayItemBean dib : displayItemBeanList) {
			prepareItemsToSDVShortcutLink(dib, eventCrfBean, eventDefinitionCRFId, allSections, deltaMap);
		}
	}

	/**
	 * Method that generates itemToSDV urls for jumping between sections.
	 * 
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
	public void prepareItemsToSDVShortcutLink(DisplayItemBean dib, EventCRFBean eventCrfBean, int eventDefinitionCRFId,
			List<SectionBean> sections, Map<String, Integer> deltaMap) {
		if (dib.getMetadata() != null && dib.getMetadata().getId() > 0 && dib.getMetadata().isSdvRequired()) {
			CurrentSectionInfo currentSectionInfo = new CurrentSectionInfo(sections);
			String link = buildLink(dib.getMetadata().getSectionId(), eventCrfBean, eventDefinitionCRFId, sections);
			analyze(currentSectionInfo, deltaMap, link, FIRST_ITEM_TO_SDV, dib.getMetadata().getSectionId());
			incTotalItemsToSDV();
			if (currentSectionInfo.currentSectionId == dib.getMetadata().getSectionId()) {
				incSectionTotalItemsToSDV();
			}
		}
	}

	/**
	 * Method that generates discrepancy note urls for jumping between sections.
	 * 
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
	public void prepareDnShortcutLinks(EventCRFBean eventCrfBean, ItemFormMetadataDAO ifmdao, int eventDefinitionCRFId,
			List<SectionBean> sections, List<DiscrepancyNoteThread> noteThreads) {
		DiscrepancyNoteBean tempBean;
		Map<String, Integer> deltaMap = new HashMap<String, Integer>();
		if (requestMethod.equalsIgnoreCase(POST) && attributes.get(SECTION) == null) {
			return;
		}
		CurrentSectionInfo currentSectionInfo = new CurrentSectionInfo(sections);
		StudyUserRoleBean studyUserRoleBean = (StudyUserRoleBean) attributes.get(USER_ROLE);
		setUserIsAbleToSDVItems(eventCrfBean.getStage().isDoubleDE_Complete()
				&& (studyUserRoleBean.isStudyAdministrator() || studyUserRoleBean.isMonitor()));
		prepareItemsToSDVShortcutLink(eventCrfBean, eventDefinitionCRFId, sections);
		for (DiscrepancyNoteThread dnThread : noteThreads) {
			tempBean = dnThread.getLinkedNoteList().getLast();
			if (tempBean != null
					&& (tempBean.getEntityType().equalsIgnoreCase(ITEM_DATA) || tempBean.getEntityType()
							.equalsIgnoreCase(EVENT_CRF)) && tempBean.getParentDnId() == 0) {
				setHasNotes(true);
				ItemFormMetadataBean ifmbean = ifmdao.findByItemIdAndCRFVersionId(tempBean.getItemId(),
						eventCrfBean.getCRFVersionId());
				String link = tempBean.getEntityType().equalsIgnoreCase(EVENT_CRF) ? "" : buildLink(
						ifmbean.getSectionId(), eventCrfBean, eventDefinitionCRFId, sections);
				String key = null;
				if (ResolutionStatus.UPDATED.equals(tempBean.getResStatus())) {
					incTotalUpdated();
					key = FIRST_UPDATED_DN;
				} else if (ResolutionStatus.OPEN.equals(tempBean.getResStatus())) {
					key = FIRST_NEW_DN;
					incTotalNew();
				} else if (ResolutionStatus.CLOSED.equals(tempBean.getResStatus())) {
					key = FIRST_CLOSED_DN;
					incTotalClosed();
				} else if (ResolutionStatus.RESOLVED.equals(tempBean.getResStatus())) {
					key = FIRST_RESOLUTION_PROPOSED;
					incTotalResolutionProposed();
				} else if (ResolutionStatus.NOT_APPLICABLE.equals(tempBean.getResStatus())) {
					key = FIRST_ANNOTATION;
					incTotalAnnotations();
				}
				if (key != null && !tempBean.getEntityType().equalsIgnoreCase(EVENT_CRF)) {
					analyze(currentSectionInfo, deltaMap, link, key, ifmbean.getSectionId());
				}
			}
		}
	}

	/**
	 * Method that analyzes data to build dn shortcut link.
	 * 
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
	public void analyze(CurrentSectionInfo currentSectionInfo, Map<String, Integer> deltaMap, String link, String key,
			int itemSectionId) {
		int d = itemSectionId - currentSectionInfo.currentSectionId;
		Integer minLeftD = deltaMap.get(key.concat(MIN_LEFT_D));
		Integer minRightD = deltaMap.get(key.concat(MIN_RIGHT_D));
		if (d <= 0 && minRightD == null) {
			minLeftD = minLeftD == null ? d : Math.min(d, minLeftD);
			if (minLeftD == d) {
				deltaMap.put(key.concat(MIN_LEFT_D), minLeftD);
				setNextLink(link, key);
			}
		} else if (d > 0) {
			minRightD = minRightD == null ? d : Math.min(d, minRightD);
			if (minRightD == d) {
				deltaMap.put(key.concat(MIN_RIGHT_D), minRightD);
				setNextLink(link, key);
			}
		}
	}

	/**
	 * Method that sets next dn link for certain resolution.
	 *
	 * @param link
	 *            String
	 * @param key
	 *            String
	 */
	public void setNextLink(String link, String key) {
		if (key.equals(FIRST_UPDATED_DN)) {
			setNextUpdatedDnLink(link.concat(FIRST_UPDATED_DN));
		} else if (key.equals(FIRST_NEW_DN)) {
			setNextNewDnLink(link.concat(FIRST_NEW_DN));
		} else if (key.equals(FIRST_CLOSED_DN)) {
			setNextClosedDnLink(link.concat(FIRST_CLOSED_DN));
		} else if (key.equals(FIRST_RESOLUTION_PROPOSED)) {
			setNextResolutionProposedLink(link.concat(FIRST_RESOLUTION_PROPOSED));
		} else if (key.equals(FIRST_ANNOTATION)) {
			setNextAnnotationLink(link.concat(FIRST_ANNOTATION));
		} else if (key.equals(FIRST_ITEM_TO_SDV)) {
			setNextItemToSDVLink(link.concat(FIRST_ITEM_TO_SDV));
		}
	}

	/**
	 * Generates url anchors suffixes for current note threads.
	 *
	 * @param dib
	 *            the crf item that should be highlighted.
	 * @param additionalCheck
	 *            boolean
	 * @param noteThreads
	 *            the list of discrepancy notes threads.
	 */
	public void prepareDnShortcutAnchors(DisplayItemBean dib, List<DiscrepancyNoteThread> noteThreads,
			boolean additionalCheck) {
		if (dib.getMetadata() != null && dib.getData() != null && dib.getData().getId() > 0
				&& dib.getMetadata().getId() > 0 && dib.getMetadata().isSdvRequired() && !dib.getData().isSdv()) {
			incItemsToSDVAnchorCounter();
			dib.getItemToSDV().add(ITEM_TO_SDV.concat(Integer.toString(getItemsToSDVAnchorCounter())));
		}
		for (DiscrepancyNoteThread dnThread : noteThreads) {
			DiscrepancyNoteBean tempBean = dnThread.getLinkedNoteList().getLast();
			if (tempBean != null
					&& tempBean.getParentDnId() == 0
					&& (tempBean.getEntityType().equalsIgnoreCase(ITEM_DATA) || tempBean.getEntityType()
							.equalsIgnoreCase(EVENT_CRF))) {
				if (additionalCheck) {
					if (tempBean.getEntityType().equalsIgnoreCase(ITEM_DATA)
							&& !(((tempBean.getId() > 0 && tempBean.getItemId() == dib.getDbData().getItemId() && tempBean
									.getItemDataOrdinal() == dib.getDbData().getOrdinal()) || (tempBean.getId() == 0 && tempBean
									.getField().equalsIgnoreCase(dib.getField()))))) {
						continue;
					}
					if (tempBean.getEntityType().equalsIgnoreCase(EVENT_CRF)
							&& !tempBean.getColumn().equalsIgnoreCase(dib.getField())) {
						continue;
					}
				}
				switch (tempBean.getResolutionStatusId()) {
					case RES_STATUS_OPEN :
						incSectionTotalNew();
						dib.getNewDn().add(NEW_DN.concat(Integer.toString(getSectionTotalNew())));
						break;
					case RES_STATUS_UPDATED :
						incSectionTotalUpdated();
						dib.getUpdatedDn().add(UPDATED_DN.concat(Integer.toString(getSectionTotalUpdated())));
						break;
					case RES_STATUS_RESOLVED :
						incSectionTotalResolutionProposed();
						dib.getResolutionProposedDn().add(
								RESOLUTION_PROPOSED_DN.concat(Integer.toString(getSectionTotalResolutionProposed())));
						break;
					case RES_STATUS_CLOSED :
						incSectionTotalClosed();
						dib.getClosedDn().add(CLOSED_DN.concat(Integer.toString(getSectionTotalClosed())));
						break;
					case RES_STATUS_NOT_APPLICABLE :
						incSectionTotalAnnotations();
						dib.getAnnotationDn().add(ANNOTATION_DN.concat(Integer.toString(getSectionTotalAnnotations())));
						break;
					default :
						break;
				}
			}
		}
	}
}
