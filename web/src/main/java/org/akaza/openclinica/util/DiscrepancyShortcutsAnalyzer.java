package org.akaza.openclinica.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.service.DiscrepancyNoteThread;

public class DiscrepancyShortcutsAnalyzer {

	public static final String DISCREPANCY_SHORTCUTS_ANALYZER = "discrepancyShortcutsAnalyzer";

	public static final String FIRST_NEW_DN = "#firstNewDn";
	public static final String FIRST_UPDATED_DN = "#firstUpdatedDn";
	public static final String FIRST_RESOLUTION_PROPOSED = "#firstResolutionProposed";
	public static final String FIRST_CLOSED_DN = "#firstClosedDn";
	public static final String FIRST_ANNOTATION = "#firstAnnotation";

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

	public void incTotalNew() {
		totalNew++;
	}

	public void setTotalNew(int totalNew) {
		this.totalNew = totalNew;
	}

	public int getTotalUpdated() {
		return totalUpdated;
	}

	public void incTotalUpdated() {
		totalUpdated++;
	}

	public void setTotalUpdated(int totalUpdated) {
		this.totalUpdated = totalUpdated;
	}

	public int getTotalResolutionProposed() {
		return totalResolutionProposed;
	}

	public void incTotalResolutionProposed() {
		totalResolutionProposed++;
	}

	public void setTotalResolutionProposed(int totalResolutionProposed) {
		this.totalResolutionProposed = totalResolutionProposed;
	}

	public int getTotalClosed() {
		return totalClosed;
	}

	public void incTotalClosed() {
		totalClosed++;
	}

	public void setTotalClosed(int totalClosed) {
		this.totalClosed = totalClosed;
	}

	public int getTotalAnnotations() {
		return totalAnnotations;
	}

	public void incTotalAnnotations() {
		totalAnnotations++;
	}

	public void setTotalAnnotations(int totalAnnotations) {
		this.totalAnnotations = totalAnnotations;
	}

	public int getSectionTotalNew() {
		return sectionTotalNew;
	}

	public void incSectionTotalNew() {
		sectionTotalNew++;
	}

	public void setSectionTotalNew(int sectionTotalNew) {
		this.sectionTotalNew = sectionTotalNew;
	}

	public int getSectionTotalUpdated() {
		return sectionTotalUpdated;
	}

	public void incSectionTotalUpdated() {
		sectionTotalUpdated++;
	}

	public void setSectionTotalUpdated(int sectionTotalUpdated) {
		this.sectionTotalUpdated = sectionTotalUpdated;
	}

	public int getSectionTotalResolutionProposed() {
		return sectionTotalResolutionProposed;
	}

	public void incSectionTotalResolutionProposed() {
		sectionTotalResolutionProposed++;
	}

	public void setSectionTotalResolutionProposed(int sectionTotalResolutionProposed) {
		this.sectionTotalResolutionProposed = sectionTotalResolutionProposed;
	}

	public int getSectionTotalClosed() {
		return sectionTotalClosed;
	}

	public void incSectionTotalClosed() {
		sectionTotalClosed++;
	}

	public void setSectionTotalClosed(int sectionTotalClosed) {
		this.sectionTotalClosed = sectionTotalClosed;
	}

	public int getSectionTotalAnnotations() {
		return sectionTotalAnnotations;
	}

	public void incSectionTotalAnnotations() {
		sectionTotalAnnotations++;
	}

	public void setSectionTotalAnnotations(int sectionTotalAnnotations) {
		this.sectionTotalAnnotations = sectionTotalAnnotations;
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

	private static int getTabNum(List<SectionBean> sections, int sectionId) {
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void prepareDnShortcutLinks(HttpServletRequest request, EventCRFBean eventCrfBean, SectionDAO sdao,
			ItemFormMetadataDAO ifmdao, List<DiscrepancyNoteThread> noteThreads) {
		DiscrepancyNoteBean tempBean;
		FormProcessor fp = new FormProcessor(request);
		Map<String, Integer> linkMap = new HashMap<String, Integer>();
		List<SectionBean> sections = sdao.findAllByCRFVersionId(eventCrfBean.getCRFVersionId());
		DiscrepancyShortcutsAnalyzer discrepancyShortcutsAnalyzer = new DiscrepancyShortcutsAnalyzer();
		request.setAttribute("discrepancyShortcutsAnalyzer", discrepancyShortcutsAnalyzer);
		for (DiscrepancyNoteThread dnThread : noteThreads) {
			tempBean = dnThread.getLinkedNoteList().getLast();
			if (tempBean != null && tempBean.getEntityType().equalsIgnoreCase("itemData")) {
				ItemFormMetadataBean ifmbean = ifmdao.findByItemIdAndCRFVersionId(tempBean.getItemId(),
						eventCrfBean.getCRFVersionId());
				int tabNum = getTabNum(sections, ifmbean.getSectionId());
				String link = request.getServletPath().replaceAll("/", "") + "?eventCRFId=" + eventCrfBean.getId()
						+ "&sectionId=" + ifmbean.getSectionId() + "&tab=" + tabNum
						+ (fp.getString("exitTo").isEmpty() ? "" : "&exitTo=" + fp.getString("exitTo"));
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

	public static void prepareDnShortcutAnchors(HttpServletRequest request, DisplayItemBean dib) {
		DiscrepancyShortcutsAnalyzer discrepancyShortcutsAnalyzer = (DiscrepancyShortcutsAnalyzer) request
				.getAttribute(DISCREPANCY_SHORTCUTS_ANALYZER);
		int status = dib.getDiscrepancyNoteStatus();
		switch (status) {
		case 1: {
			discrepancyShortcutsAnalyzer.incSectionTotalNew();
			if (discrepancyShortcutsAnalyzer.getSectionTotalNew() == 1) {
				dib.setFirstNewDn(true);
			}
			break;
		}
		case 2: {
			discrepancyShortcutsAnalyzer.incSectionTotalUpdated();
			if (discrepancyShortcutsAnalyzer.getSectionTotalUpdated() == 1) {
				dib.setFirstUpdatedDn(true);
			}
			break;
		}
		case 3: {
			discrepancyShortcutsAnalyzer.incSectionTotalResolutionProposed();
			if (discrepancyShortcutsAnalyzer.getSectionTotalResolutionProposed() == 1) {
				dib.setFirstResolutionProposed(true);
			}
			break;
		}
		case 4: {
			discrepancyShortcutsAnalyzer.incSectionTotalClosed();
			if (discrepancyShortcutsAnalyzer.getSectionTotalClosed() == 1) {
				dib.setFirstClosedDn(true);
			}
			break;
		}
		case 5: {
			discrepancyShortcutsAnalyzer.incSectionTotalAnnotations();
			if (discrepancyShortcutsAnalyzer.getSectionTotalAnnotations() == 1) {
				dib.setFirstAnnotation(true);
			}
			break;
		}
		}
	}
}
