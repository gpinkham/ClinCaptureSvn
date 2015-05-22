/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.DataEntryServlet;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import com.clinovo.util.SpreadsheetPreviewUtil;

/**
 * Preview a CRF version section data entry. This class is based almost entirely on ViewSectionDataEntryServlet except
 * that it's designed to provide a preview of a crf before the crfversion is inserted into the database.
 * 
 * @author Bruce W. Perry
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
@Component
public class ViewSectionDataEntryPreview extends DataEntryServlet {

	public static final String SECTION_TITLE = "section_title";
	public static final String SECTION_LABEL = "section_label";
	public static final String SECTION_SUBTITLE = "subtitle";
	public static final String INSTRUCTIONS = "instructions";
	public static final String BORDERS = "borders";

	/**
	 * Checks whether the user has the correct privilege. This is from ViewSectionDataEntryServlet.
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);
		StudyUserRoleBean currentRole = (StudyUserRoleBean) request.getSession().getAttribute("userRole");
		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)
				|| currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study") + " "
						+ respage.getString("change_active_study_or_contact"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		SectionBean sb = (SectionBean) request.getAttribute(SECTION_BEAN);
		// These numbers will be zero if the
		// params are not present in the URL
		int crfid = fp.getInt("crfId");
		int tabNum = fp.getInt("tabId");
		HttpSession session = request.getSession();
		request.setAttribute("crfId", crfid);
		String crfName = "";
		String verNumber = "";
		// All the data on the uploaded Excel file
		// see org.akaza.openclinica.control.admin.SpreadsheetPreview
		// createCrfMetaObject() method
		Map<String, Map> crfMap = (Map) session.getAttribute("preview_crf");
		if (crfMap == null) {
			// addPageMessage
			String msg = respage.getString("preview_data_has_timed_out");
			this.addPageMessage(msg, request);
			logger.info("The session attribute \"preview_crf\" has expired or gone out of scope in: "
					+ this.getClass().getName());
			this.forwardPage(Page.CRF_LIST_SERVLET, request, response);
		}

		Map<String, String> crfIdnameInfo = null;
		if (crfMap != null) {
			crfIdnameInfo = crfMap.get("crf_info");
		}
		// Get the CRF name and version String
		if (crfIdnameInfo != null) {
			Map.Entry mapEnt = null;
			for (Object element : crfIdnameInfo.entrySet()) {
				mapEnt = (Map.Entry) element;
				if (((String) mapEnt.getKey()).equalsIgnoreCase("crf_name")) {
					crfName = (String) mapEnt.getValue();
				}
				if (((String) mapEnt.getKey()).equalsIgnoreCase("version")) {
					verNumber = (String) mapEnt.getValue();
				}
			}
		}

		// Set up the beans that DisplaySectionBean and the preview
		// depend on
		EventCRFBean ebean = new EventCRFBean();
		CRFVersionBean crfverBean = new CRFVersionBean();
		crfverBean.setName(verNumber);
		CRFBean crfbean = new CRFBean();
		crfbean.setId(crfid);
		crfbean.setName(crfName);
		ebean.setCrf(crfbean);

		// This happens in ViewSectionDataEntry
		// It's an assumption that it has to happen here as well
		ecb = ebean;

		// All the groups data, if it's present in the CRF
		Map<Integer, Map<String, String>> groupsMap = null;
		if (crfMap != null) {
			groupsMap = crfMap.get("groups");
		}
		// Find out whether this CRF involves groups
		// At least one group is involved if the groups Map is not null or
		// empty, and the first group entry (there may be only one) has a
		// valid group label
		boolean hasGroups = false;

		// A SortedMap containing the row number as the key, and the
		// section headers/values (contained in a Map) as the value
		Map<Integer, Map<String, String>> sectionsMap = null;
		if (crfMap != null) {
			sectionsMap = crfMap.get("sections");
		}
		// The itemsMap contains the spreadsheet table items row number as a
		// key,
		// followed by a map of the column names/values; it contains values for
		// display
		// such as 'left item text'
		Map<Integer, Map<String, String>> itemsMap = null;
		if (crfMap != null) {
			itemsMap = crfMap.get("items");
		}

		// Create a list of FormGroupBeans from Maps of groups,
		// items, and sections
		BeanFactory beanFactory = new BeanFactory();
		// FormBeanUtil formUtil = new FormBeanUtil();

		// Set up sections for the preview
		Map.Entry me;
		SectionBean secbean;
		ArrayList<SectionBean> allSectionBeans = new ArrayList<SectionBean>();
		String nameStr;
		String pageNum;
		Map secMap;

		NumberFormat numFormatter = NumberFormat.getInstance();
		numFormatter.setMaximumFractionDigits(0);
		if (sectionsMap != null) {
			for (Object element : sectionsMap.entrySet()) {
				secbean = new SectionBean();
				me = (Map.Entry) element;
				secMap = (Map) me.getValue();
				nameStr = (String) secMap.get("section_label");
				secbean.setName(nameStr);
				secbean.setTitle((String) secMap.get("section_title"));
				secbean.setInstructions((String) secMap.get("instructions"));
				secbean.setSubtitle((String) secMap.get("subtitle"));
				pageNum = (String) secMap.get("page_number");
				// ensure pageNum is an actual number; the user is not required
				// to
				// type a number in that Spreadsheet cell
				try {
					pageNum = numFormatter.format(Double.parseDouble(pageNum));
				} catch (NumberFormatException nfe) {
					pageNum = "";
				}

				secbean.setPageNumberLabel(pageNum);
				// Sift through the items to see if their section label matches
				// the section's section_label column
				secbean.setNumItems(SpreadsheetPreviewUtil.getNumberOfItemsInSection(itemsMap, secbean.getName()));
				allSectionBeans.add(secbean);
			}
		}
		DisplayTableOfContentsBean dtocBean = new DisplayTableOfContentsBean();
		// Methods should just take Lists, the interface, not
		// ArrayList only!
		dtocBean.setSections(allSectionBeans);

		request.setAttribute("toc", dtocBean);
		request.setAttribute("sectionNum", allSectionBeans.size() + "");

		// Assuming that the super class' SectionBean sb variable must be
		// initialized,
		// since it happens in ViewSectionDataEntryServlet. verify this
		sb = allSectionBeans.get(0);
		// This is the StudySubjectBean
		// Not sure if this is needed for a Preview, but leaving
		// it in for safety/consisitency reasons
		request.setAttribute(INPUT_EVENT_CRF, ecb);
		request.setAttribute(SECTION_BEAN, sb);
		setupStudyBean(request);
		// Create a DisplaySectionBean for the SectionBean specified by the
		// tab number.
		tabNum = tabNum == 0 ? 1 : tabNum;
		String sectionTitle = getSectionColumnBySecNum(sectionsMap, tabNum, SECTION_TITLE);
		String secLabel = getSectionColumnBySecNum(sectionsMap, tabNum, SECTION_LABEL);
		String secSubtitle = getSectionColumnBySecNum(sectionsMap, tabNum, SECTION_SUBTITLE);
		String instructions = getSectionColumnBySecNum(sectionsMap, tabNum, INSTRUCTIONS);
		int secBorders = getSectionBordersBySecNum(sectionsMap, tabNum, BORDERS);

		DisplaySectionBean displaySection = beanFactory.createDisplaySectionBean(itemsMap, sectionTitle, secLabel,
				secSubtitle, instructions, crfName, secBorders);

		//
		// the variable hasGroups should only be true if the group appears in
		// this section
		List<DisplayItemBean> disBeans = displaySection.getItems();
		ItemFormMetadataBean metaBean;
		String groupLabel;
		hasGroups = false;
		for (DisplayItemBean diBean : disBeans) {
			metaBean = diBean.getMetadata();
			groupLabel = metaBean.getGroupLabel();
			if (groupLabel != null && groupLabel.length() > 0) {
				hasGroups = true;
				break;
			}

		}
		// Create groups associated with this section
		List<DisplayItemGroupBean> disFormGroupBeans = null;

		if (hasGroups) {
			disFormGroupBeans = beanFactory.createGroupBeans(itemsMap, groupsMap, secLabel, crfName);
			displaySection.setDisplayFormGroups(disFormGroupBeans);
		}

		displaySection.setCrfVersion(crfverBean);
		displaySection.setCrf(crfbean);
		displaySection.setEventCRF(ebean);
		// Not sure if this is needed? The JSPs pull it out
		// as a request attribute
		SectionBean aSecBean = new SectionBean();

		request.setAttribute(BEAN_DISPLAY, displaySection);
		// verify these attributes, from the original servlet, are
		// necessary
		request.setAttribute("sec", aSecBean);
		request.setAttribute("EventCRFBean", ebean);
		try {
			request.setAttribute("tabId", Integer.toString(tabNum));
		} catch (NumberFormatException nfe) {
			request.setAttribute("tabId", new Integer("1"));
		}
		if (hasGroups) {
			logger.info("has group, new_table is true");
			request.setAttribute("new_table", true);
		}
		forwardPage(Page.CREATE_CRF_VERSION_CONFIRM, request, response);

	}

	// Get a Section's title by its key number in the sectionsMap; i.e., what is
	// the title
	// of the first section in the CRF?
	private String getSectionColumnBySecNum(Map sectionsMap, int sectionNum, String sectionColumn) {
		if (sectionsMap == null || sectionColumn == null || sectionColumn.length() < 1) {
			return "";
		}
		Map innerMap = (Map) sectionsMap.get(sectionNum);
		return (String) innerMap.get(sectionColumn);
	}

	private int getSectionBordersBySecNum(Map sectionsMap, int sectionNum, String sectionColumn) {
		if (sectionsMap == null || sectionColumn == null || sectionColumn.length() < 1) {
			return 0;
		}
		Map innerMap = (Map) sectionsMap.get(sectionNum);
		String tempBorder = (String) innerMap.get(sectionColumn);
		// if the section borders property in the CRF template
		// is blank, return 0
		if (tempBorder != null && tempBorder.length() < 1) {
			return 0;
		}
		// if the borders property is null, return 0; otherwise return the value stored
		// in the HashMap
		if (tempBorder != null) {
			return new Integer(tempBorder);

		} else {
			return 0;
		}
	}

	private void setupStudyBean(HttpServletRequest request) {
		String age = "";
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		StudySubjectDAO ssdao = new StudySubjectDAO(getDataSource());
		StudySubjectBean sub = (StudySubjectBean) ssdao.findByPK(ecb.getStudySubjectId());
		// This is the SubjectBean
		SubjectDAO subjectDao = new SubjectDAO(getDataSource());
		int subjectId = sub.getSubjectId();
		int studyId = sub.getStudyId();
		SubjectBean subject = (SubjectBean) subjectDao.findByPK(subjectId);
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		// Let us process the age
		if (currentStudy.getStudyParameterConfig().getCollectDob().equals("1")) {
			StudyEventDAO sedao = new StudyEventDAO(getDataSource());
			StudyEventBean se = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
			StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(getDataSource());
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(se.getStudyEventDefinitionId());
			se.setStudyEventDefinition(sed);
			request.setAttribute("studyEvent", se);
			age = Utils.getInstance().processAge(sub.getEnrollmentDate(), subject.getDateOfBirth());
		}
		// Get the study then the parent study
		StudyDAO studydao = new StudyDAO(getDataSource());
		StudyBean study = (StudyBean) studydao.findByPK(studyId);

		if (study.getParentStudyId() > 0) {
			// this is a site,find parent
			StudyBean parentStudy = (StudyBean) studydao.findByPK(study.getParentStudyId());
			request.setAttribute("studyTitle", parentStudy.getName() + " - " + study.getName());
		} else {
			request.setAttribute("studyTitle", study.getName());
		}

		request.setAttribute("studySubject", sub);
		request.setAttribute("subject", subject);
		request.setAttribute("age", age);
	}

	@Override
	protected Status getBlankItemStatus() {
		return Status.AVAILABLE;
	}

	@Override
	protected Status getNonBlankItemStatus(HttpServletRequest request) {
		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) request.getAttribute(EVENT_DEF_CRF_BEAN);
		return edcb.isDoubleEntry() ? Status.PENDING : Status.UNAVAILABLE;
	}

	@Override
	protected String getEventCRFAnnotations(HttpServletRequest request) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		return ecb.getAnnotations();
	}

	@Override
	protected void setEventCRFAnnotations(String annotations, HttpServletRequest request) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		ecb.setAnnotations(annotations);
	}

	@Override
	protected Page getJSPPage() {
		return Page.VIEW_SECTION_DATA_ENTRY;
	}

	@Override
	protected Page getServletPage(HttpServletRequest request) {
		return Page.VIEW_SECTION_DATA_ENTRY_SERVLET;
	}

	@Override
	protected boolean validateInputOnFirstRound() {
		return true;
	}

	@Override
	protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v, DisplayItemBean dib, String inputName,
			HttpServletRequest request) {
		org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();

		// note that this step sets us up both for
		// displaying the data on the form again, in the event of an error
		// and sending the data to the database, in the event of no error
		dib = loadFormValue(dib, request);

		// types TEL and ED are not supported yet
		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.CALCULATION)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.GROUP_CALCULATION)) {
			dib = validateDisplayItemBeanText(v, dib, inputName, request);
		} else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
			dib = validateDisplayItemBeanSingleCV(v, dib, inputName);
		} else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
			dib = validateDisplayItemBeanMultipleCV(v, dib, inputName);
		}

		return dib;
	}

	@Override
	protected List<DisplayItemGroupBean> validateDisplayItemGroupBean(DiscrepancyValidator v,
			DisplayItemGroupBean digb, List<DisplayItemGroupBean> digbs, List<DisplayItemGroupBean> formGroups,
			HttpServletRequest request, HttpServletResponse response) {

		return formGroups;

	}

	@Override
	protected boolean shouldRunRules() {
		return false;
	}

	protected boolean isAdministrativeEditing() {
		return false;
	}

	protected boolean isAdminForcedReasonForChange(HttpServletRequest request) {
		return false;
	}
}
