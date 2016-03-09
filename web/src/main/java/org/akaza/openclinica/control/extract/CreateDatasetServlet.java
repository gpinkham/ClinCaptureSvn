/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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
package org.akaza.openclinica.control.extract;

import com.clinovo.util.ValidatorHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DatasetItemStatus;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.TermType;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.FilterBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.service.crfdata.HideCRFManager;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;

/**
 * Creates a dataset by building a query based on study events, CRFs and items.
 */
@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
@Component
public class CreateDatasetServlet extends SpringServlet {

	public static final String BEAN_YEARS = "years";
	public static final String BEAN_MONTHS = "months";
	public static final String EVENTS_FOR_CREATE_DATASET = "eventsForCreateDataset";
	public static final String SAVE_BUTTON = "save";
	public static final String DOB = "dob";
	public static final String GENDER = "gender";
	public static final String EVENT_LOCATION = "location";
	public static final String EVENT_START = "start";
	public static final String EVENT_END = "end";
	public static final String SUBJ_STATUS = "subj_status";
	public static final String UNIQUE_ID = "unique_identifier";
	public static final String AGE_AT_EVENT = "age_at_event";
	public static final String SUBJ_SECONDARY_ID = "subj_secondary_id";
	public static final String EVENT_STATUS = "event_status";
	public static final String CRF_STATUS = "crf_status";
	public static final String CRF_VERSION = "crf_version";
	public static final String INTERVIEWER_NAME = "interviewer";
	public static final String INTERVIEWER_DATE = "interviewer_date";
	public static final int MIN_YEAR = 1980;
	public static final int TWO_T_ONE_H = 2100;
	public static final int TWELVE = 12;
	public static final int FIRST_YEAR = 1900;
	public static final int TWO_H_FF = 255;
	public static final int TWO_T = 2000;

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		if (haveAccess(getUserAccountBean(request), getCurrentRole(request))){
			return;
		}

		addPageMessage(getResPage().getString("no_have_correct_privilege_current_study")
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU,
				getResException().getString("not_allowed_access_extract_data_servlet"), "1");
	}

	public static boolean haveAccess(UserAccountBean ub, StudyUserRoleBean currentRole) {
		if (ub.isSysAdmin() || Role.STUDY_ADMINISTRATOR.equals(currentRole.getRole()) || Role.INVESTIGATOR.equals(currentRole.getRole())
				|| Role.isMonitor(currentRole.getRole()) || Role.STUDY_SPONSOR.equals(currentRole.getRole())) {
			return true;
		}
		
		return false;
	}

	/**
	 * Set up study groups.
	 * @param ub UserAccountBean
	 * @return ArrayList
	 */
	public ArrayList setUpStudyGroups(UserAccountBean ub) {

		StudyDAO studydao = getStudyDAO();
		StudyGroupClassDAO sgclassdao = getStudyGroupClassDAO();
		StudyBean theStudy = (StudyBean) studydao.findByPK(ub.getActiveStudyId());
		return sgclassdao.findAllActiveByStudy(theStudy);
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		SimpleDateFormat localDateFormat = getLocalDf(request);
		FormProcessor fp = new FormProcessor(request);
		String action = fp.getString("action");
		Date ddate = new SimpleDateFormat("MM/dd/yyyy").parse("01/01/1900");
		request.setAttribute("defaultStart", localDateFormat.parse(localDateFormat.format(ddate)));
		request.setAttribute("defaultEnd", getLastDayOfMonth(TWO_T_ONE_H, TWELVE));
		request.setAttribute("statuses", Status.toActiveArrayList());
		request.setAttribute(BEAN_MONTHS, getMonths());
		request.setAttribute(BEAN_YEARS, getYears());
		if (StringUtil.isBlank(action)) {

			GregorianCalendar.getInstance();
			DatasetBean dsb = new DatasetBean();
			String temp = dsb.getOdmMetaDataVersionOid();
			dsb.setName("");
			dsb.setDescription("");
			dsb.setOdmPriorStudyOid("");
			dsb.setOdmPriorMetaDataVersionOid("");
			dsb.setDatasetItemStatus(DatasetItemStatus.get(1));
			dsb.setOdmMetaDataVersionOid(temp != null && temp.length() > 0 ? temp : "v1.0.0");
			dsb.setOdmMetaDataVersionName(temp != null && temp.length() > 0 ? temp : "MetaDataVersion_v1.0.0");
			request.getSession().setAttribute("newDataset", dsb);
			request.getSession().setAttribute("crf", new CRFBean());
			forwardPage(Page.CREATE_DATASET_1, request, response);

		} else {

			DatasetBean dsb = (DatasetBean) request.getSession().getAttribute("newDataset");
			if (dsb == null) {
				response.sendRedirect(request.getContextPath() + "/CreateDataset");
			} else if (action.equalsIgnoreCase("back_to_begin")) {

				if (dsb.getId() > 0) {
					response.sendRedirect(request.getContextPath() + "/ViewDatasets");
				} else {
					forwardPage(Page.CREATE_DATASET_1, request, response);
				}
			} else if (action.equalsIgnoreCase("back_to_viewselected")) {
				response.sendRedirect(request.getContextPath() + "/ViewSelected");
			} else if (action.equalsIgnoreCase("back_to_beginsubmit")) {
				forwardPage(Page.CREATE_DATASET_3, request, response);
			} else if (action.equalsIgnoreCase("back_to_scopesubmit")) {
				forwardPage(Page.CREATE_DATASET_4, request, response);
			} else if ("begin".equalsIgnoreCase(action)) {

				// step 2 -- select study events/crfs
				StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
				StudyBean studyWithEventDefinitions = currentStudy;
				if (currentStudy.getParentStudyId() > 0) {

					studyWithEventDefinitions = new StudyBean();
					studyWithEventDefinitions.setId(currentStudy.getParentStudyId());
				}
				ArrayList seds = seddao.findAllActiveByStudy(studyWithEventDefinitions);

				CRFDAO crfdao = getCRFDAO();
				HashMap events = new LinkedHashMap();
				for (Object sed1 : seds) {

					StudyEventDefinitionBean sed = (StudyEventDefinitionBean) sed1;
					ArrayList<CRFBean> crfs = (ArrayList<CRFBean>) crfdao.findAllActiveUnmaskedByDefinition(sed, ub);

					if (currentStudy.getParentStudyId() > 0) {
						// sift through these CRFs and see which ones are hidden
						HideCRFManager hideCRFs = HideCRFManager.createHideCRFManager();
						crfs = hideCRFs.removeHiddenCRFBeans(studyWithEventDefinitions, sed, crfs, getDataSource());
					}
					if (!crfs.isEmpty()) {
						events.put(sed, crfs);
					}
				}
				if (events.isEmpty()) {
					addPageMessage(getResPage().getString("not_have_study_definitions_assigned"), request);
					forwardPage(Page.CREATE_DATASET_1, request, response);
				} else {
					crfdao = getCRFDAO();
					ItemDAO idao = getItemDAO();
					ArrayList sedItemIds = CreateDatasetServlet.allSedItemIdsInStudy(events, crfdao, idao, ub);
					request.setAttribute("eventlist", events);
					request.getSession().setAttribute("totalNumberOfStudyItems", sedItemIds.size());
					request.getSession().setAttribute(EVENTS_FOR_CREATE_DATASET, events);

					forwardPage(Page.CREATE_DATASET_VIEW_SELECTED, request, response);
				}
			} else if ("beginsubmit".equalsIgnoreCase(action)) {
				String saveItems = fp.getString(SAVE_BUTTON);
				extractIdsFromForm(request, dsb);
				extractEventIds(dsb);
				if (!StringUtil.isBlank(saveItems)) {
					request.setAttribute("eventlist", request.getSession().getAttribute(EVENTS_FOR_CREATE_DATASET));
					String summary = getResPage().getString("you_have_selected") + " " + dsb.getItemIds().size() + " "
							+ getResPage().getString("items_so_far");
					summary += genAttMsg(currentStudy, dsb);
					addPageMessage(summary, request);
					int crfId = fp.getInt("crfId");
					if (crfId > 0) {

						request.setAttribute("allItems", dsb.getItemDefCrf());
						forwardPage(Page.CREATE_DATASET_2, request, response);
					} else {
						ArrayList sgclasses = dsb.getAllSelectedGroups();
						if (sgclasses == null || sgclasses.size() == 0) {
							sgclasses = setUpStudyGroups(ub);
						}
						forwardPage(Page.CREATE_DATASET_VIEW_SELECTED, request, response);
					}
				} else {

					if (dsb.getItemIds().size() == 0) {

						request.setAttribute("allItems", dsb.getItemDefCrf());
						request.setAttribute("eventlist", request.getSession().getAttribute(EVENTS_FOR_CREATE_DATASET));
						addPageMessage(getResPage().getString("should_select_one_item_to_create_dataset"), request);
						forwardPage(Page.CREATE_DATASET_2, request, response);
					} else {

						String summary = getResPage().getString("you_have_selected") + " " + dsb.getItemIds().size() + " "
								+ getResPage().getString("items_totally_for_this_dataset");

						summary += genAttMsg(currentStudy, dsb);
						addPageMessage(summary, request);

						fp.addPresetValue("firstmonth", 0);    // 0 means using

						fp.addPresetValue("firstyear", FIRST_YEAR);
						fp.addPresetValue("lastmonth", 0);
						fp.addPresetValue("lastyear", TWO_T_ONE_H);
						setPresetValues(fp.getPresetValues(), request);

						logger.warn("found preset values while setting date: " + fp.getPresetValues().toString());

						forwardPage(Page.CREATE_DATASET_3, request, response);
					}
				}

			} else if ("scopesubmit".equalsIgnoreCase(action)) {

				dsb.setFirstMonth(fp.getInt("firstmonth"));
				dsb.setFirstYear(fp.getInt("firstyear"));
				dsb.setLastMonth(fp.getInt("lastmonth"));
				dsb.setLastYear(fp.getInt("lastyear"));

				HashMap errors = new HashMap();
				if (dsb.getFirstMonth() > 0 && dsb.getFirstYear() == FIRST_YEAR) {
					Validator.addError(errors, "firstmonth", getResText().getString("if_specify_month_also_specify_year"));
				}

				if (dsb.getLastMonth() > 0 && dsb.getLastYear() == TWO_T_ONE_H) {
					Validator.addError(errors, "lastmonth", getResText().getString("if_specify_month_also_specify_year"));
				}

				if (dsb.getFirstYear() > FIRST_YEAR && dsb.getFirstMonth() == 0) {
					Validator.addError(errors, "firstmonth", getResText().getString("if_specify_year_also_specify_month"));
				}

				if (dsb.getLastYear() < TWO_T_ONE_H && dsb.getLastMonth() == 0) {
					Validator.addError(errors, "lastmonth", getResText().getString("if_specify_year_also_specify_month"));
				}
				Date dateStart = dsb.getFirstYear() > FIRST_YEAR && dsb.getFirstMonth() > 0
						? getFirstDayOfMonth(dsb.getFirstYear(), dsb.getFirstMonth()) : null;
				Date dateEnd = dsb.getLastYear() < TWO_T_ONE_H && dsb.getLastMonth() > 0
						? getLastDayOfMonth(dsb.getLastYear(), dsb.getLastMonth()) : null;

				if (dateEnd != null && dateStart != null && dateEnd.compareTo(dateStart) < 0) {
					Validator.addError(errors, "firstmonth", getResText().getString("the_from_should_be_come_before_to"));
				}
				if (!errors.isEmpty()) {
					setInputMessages(errors, request);
					addPageMessage(getResPage().getString("errors_in_submission_see_below"), request);
					setPresetValues(fp.getPresetValues(), request);
					forwardPage(Page.CREATE_DATASET_3, request, response);
				} else {
					dsb.setDateStart(dateStart);
					dsb.setDateEnd(dateEnd);

					if (fp.getString("submit").equals(getResWord().getString("continue_to_apply_filter"))) {
						forwardPage(Page.MENU, request, response);
					} else {
						forwardPage(Page.CREATE_DATASET_4, request, response);
					}
				}
			} else if ("specifysubmit".equalsIgnoreCase(action)) {
				dsb.setOdmMetaDataVersionName(fp.getString("mdvName"));
				dsb.setOdmMetaDataVersionOid(fp.getString("mdvOID"));
				dsb.setOdmPriorMetaDataVersionOid(fp.getString("mdvPrevOID"));
				dsb.setOdmPriorStudyOid(fp.getString("mdvPrevStudy"));
				dsb.setName(fp.getString("dsName"));
				dsb.setDescription(fp.getString("dsDesc"));
				dsb.setStatus(Status.get(fp.getInt("dsStatus")));
				dsb.setDatasetItemStatus(DatasetItemStatus.get(fp.getInt("itemStatus")));

				Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));

				v.addValidation("dsName", Validator.NO_BLANKS);
				v.addValidation("dsName", Validator.NO_SEMI_COLONS_OR_COLONS);
				v.addValidation("dsDesc", Validator.NO_BLANKS);
				v.addValidation("dsStatus", Validator.IS_VALID_TERM, TermType.STATUS);

				v.addValidation("dsName", Validator.LENGTH_NUMERIC_COMPARISON,
						NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, TWO_H_FF);
				v.addValidation("dsDesc", Validator.LENGTH_NUMERIC_COMPARISON,
						NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, TWO_T);

				if (dsb.getOdmPriorStudyOid() != null && dsb.getOdmPriorStudyOid().length() > 0) {
					v.addValidation("mdvPrevOID", Validator.NO_BLANKS);
				}
				HashMap errors = v.validate();

				String dsName = fp.getString("dsName");
				if (!StringUtil.isBlank(dsName)) {

					if (dsName.contains("/") || dsName.contains("\\")) {
						Validator.addError(errors, "dsName", getResText().getString("slash_not_allowed"));
					}

					Matcher matcher = Pattern.compile("[^\\w_\\d ]").matcher(dsName);
					boolean isContainSpecialSymbol = matcher.find();
					if (isContainSpecialSymbol) {
						Validator.addError(errors, "dsName",
								getResException().getString("dataset_should_not_contain_any_special"));
					}

					if (dsb.getId() <= 0) {

						DatasetDAO dsdao = getDatasetDAO();
						DatasetBean dsBean = (DatasetBean) dsdao.findByNameAndStudy(fp.getString("dsName").trim(), currentStudy);
						if (dsBean.getId() > 0) {
							Validator.addError(errors, "dsName",
									getResText().getString("dataset_name_used_by_another_choose_unique"));
						}
					}
				}

				if (!errors.isEmpty()) {

					addPageMessage(getResPage().getString("errors_in_submission_see_below"), request);
					setInputMessages(errors, request);
					setPresetValues(fp.getPresetValues(), request);
					forwardPage(Page.CREATE_DATASET_4, request, response);
				} else {

					if (dsb.getOdmPriorMetaDataVersionOid() != null && dsb.getOdmPriorMetaDataVersionOid().length() > 0
							&& (dsb.getOdmPriorStudyOid() == null || dsb.getOdmPriorStudyOid().length() <= 0)) {
						dsb.setOdmPriorStudyOid(currentStudy.getId() + "");
					}
					dsb.setSQLStatement(dsb.generateQuery());
					String dbType = SQLInitServlet.getField("dbType");
					if ("oracle".equals(dbType)) {
						dsb.setSQLStatement(dsb.generateOracleQuery());
					}

					FilterBean fb = (FilterBean) request.getSession().getAttribute("newFilter");
					if (fb != null) {
						dsb.setSQLStatement(dsb.getSQLStatement() + " " + fb.getSQLStatement());
					}
					forwardPage(Page.CONFIRM_DATASET, request, response);
				}

			} else if ("confirmall".equalsIgnoreCase(action)) {

				String submit = fp.getString("btnSubmit");
				logger.info("reached confirm all");
				if (!getResWord().getString("submit_for_dataset").equalsIgnoreCase(submit)) {
					forwardPage(Page.CREATE_DATASET_4, request, response);
				} else {

					DatasetDAO ddao = getDatasetDAO();

					if (dsb.getStudyId() == 0) {
						dsb.setStudyId(currentStudy.getId());
					}

					dsb.setOwner(ub);
					dsb = finalUpateDatasetBean(dsb);

					if (dsb.getId() == 0) {

						logger.info("*** about to create the dataset bean");
						dsb = (DatasetBean) ddao.create(dsb);
						logger.info("created dataset bean: " + dsb.getId() + ", name: " + dsb.getName());
						if (!dsb.isActive()) {
							addPageMessage(getResText().getString("problem_creating_dataset_try_again"), request);
							forwardPage(Page.VIEW_DATASETS_SERVLET, request, response);
							return;
						}
					} else if (dsb.getId() > 0) {

						dsb = (DatasetBean) ddao.updateAll(dsb);
						if (!dsb.isActive()) {
							addPageMessage(getResText().getString("problem_creating_dataset_try_again"), request);
							forwardPage(Page.EXTRACT_DATASETS_MAIN, request, response);
						}

						dsb = (DatasetBean) ddao.updateGroupMap(dsb);
						if (!dsb.isActive()) {
							addPageMessage(getResText().getString("problem_updating_subject_group_class_when_updating_dataset"),
									request);
							forwardPage(Page.EXTRACT_DATASETS_MAIN, request, response);
						}
					}
					logger.info("setting data set id here");

					request.setAttribute("dataset", dsb);

					forwardPage(Page.EXPORT_DATASETS, request, response);
				}
			} else {

				addPageMessage(getResText().getString("creating_new_dataset_cancelled"), request);
				forwardPage(Page.CREATE_DATASET_1, request, response);
			}
		}
	}

	/**
	 * Extract IDs from Form and put in to DatasetBean.
	 * @param request HttpServletRequest
	 * @param db DatasetBean
	 */
	public void extractIdsFromForm(HttpServletRequest request, DatasetBean db) {

		FormProcessor fp = new FormProcessor(request);
		int crfId = fp.getInt("crfId");
		int defId = fp.getInt("defId");
		boolean eventAttr = fp.getBoolean("eventAttr");
		boolean subAttr = fp.getBoolean("subAttr");
		boolean crfAttr = fp.getBoolean("CRFAttr");
		boolean groupAttr = fp.getBoolean("groupAttr");

		if (defId > 0 && !db.getEventIds().contains(new Integer(defId))) {
			db.getEventIds().add(defId);
		}

		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		String defName = "";
		if (defId > 0 && crfId != -1) {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(defId);
			defName = sed.getName();
		}

		CRFBean crf = (CRFBean) getCRFDAO().findByPK(crfId);

		int i = 0;
		Iterator<ItemBean> iterator;
		if (crfId > 0) {
			iterator = ((ArrayList) request.getSession().getAttribute("allCrfItems")).iterator();
		} else {
			iterator = db.getItemDefCrf().iterator();
		}

		while (iterator.hasNext()) {

			ItemBean selectedItem = iterator.next();
			String checked = fp.getString("itemSelected" + i);
			String itemCrfName = fp.getString("itemCrfName" + i);
			String itemDefName = fp.getString("itemDefName" + i);
			if (crfId > 0 || (!itemCrfName.isEmpty() && !itemDefName.isEmpty())) {

				selectedItem.setSelected(!StringUtil.isBlank(checked) && "yes".equalsIgnoreCase(checked.trim()));
				String selectedItemKey = selectedItem.getDefId() == 0 ? (defId + "_"
						+ selectedItem.getItemMeta().getCrfVersionId() + "_" + selectedItem.getId()) : (selectedItem
						.getDefId() + "_" + selectedItem.getItemMeta().getCrfVersionId() + "_" + selectedItem.getId());
				if (selectedItem.isSelected()) {
					if (!"".equals(crf.getName())) {
						selectedItem.setCrfName(crf.getName());
					} else {
						selectedItem.setCrfName(itemCrfName);
					}
					if (!"".equals(defName)) {
						selectedItem.setDefName(defName);
					} else {
						selectedItem.setDefName(itemDefName);
					}
				}
				db.getItemMap().put(selectedItemKey, selectedItem);
			}
			i++;
		}

		db.getItemIds().clear();
		db.getItemDefCrf().clear();
		db.getItemDefCrf().addAll(db.getItemMap().values());
		Collections.sort(db.getItemDefCrf(), new ItemBean.ItemBeanComparator());

		for (ItemBean itemBean : (List<ItemBean>) db.getItemDefCrf()) {
			if (itemBean.isSelected()) {
				db.getItemIds().add(itemBean.getId());
			}
		}

		if (crfId == -1) {    // from view selected page

			getSubAttr(fp, db);
			getEventAttr(fp, db);
			getGroupAttr(fp, db);
			getCRFAttr(fp, db);
		}

		if (crfId == 0) {    // event or subject attribute page

			if (subAttr) {
				getSubAttr(fp, db);
			} else if (eventAttr) {
				getEventAttr(fp, db);
			} else if (groupAttr) {
				getGroupAttr(fp, db);
			} else if (crfAttr) {
				getCRFAttr(fp, db);
			}
		}

	}

	/**
	 * @param year  The year part of the date.
	 * @param month The month part of the date.
	 * @return A Date object corresponding to the first day of the specified year and month.
	 */
	private Date getFirstDayOfMonth(int year, int month) {

		DateTimeZone userTimeZone = DateTimeZone.forID(getUserAccountBean().getUserTimeZoneId());
		DateTime userLocaleStartDate = new DateTime(year, month, 1, 0, 0, 0).withZoneRetainFields(userTimeZone);
		return userLocaleStartDate.withZone(DateTimeZone.getDefault()).toDate();
	}

	/**
	 * @param year  The year part of the date.
	 * @param month The month part of the date.
	 * @return A Date object corresponding to the last day of the specified year and month.
	 */
	private Date getLastDayOfMonth(int year, int month) {

		DateTimeZone userTimeZone = DateTimeZone.forID(getUserAccountBean().getUserTimeZoneId());
		DateTime userLocaleEndDate = new DateTime(year, month, 1, 23, 59, 59).plusMonths(1).minusDays(1)
				.withZoneRetainFields(userTimeZone);
		return userLocaleEndDate.withZone(DateTimeZone.getDefault()).toDate();
	}

	private ArrayList getMonths() {

		ArrayList answer = new ArrayList();

		answer.add(getResWord().getString("January"));
		answer.add(getResWord().getString("February"));
		answer.add(getResWord().getString("March"));
		answer.add(getResWord().getString("April"));
		answer.add(getResWord().getString("May"));
		answer.add(getResWord().getString("June"));
		answer.add(getResWord().getString("July"));
		answer.add(getResWord().getString("August"));
		answer.add(getResWord().getString("September"));
		answer.add(getResWord().getString("October"));
		answer.add(getResWord().getString("November"));
		answer.add(getResWord().getString("December"));

		return answer;
	}

	private ArrayList getYears() {

		ArrayList answer = new ArrayList();

		Calendar currTime = Calendar.getInstance();
		int currYear = currTime.get(Calendar.YEAR);

		for (int i = currYear; i >= MIN_YEAR; i--) {
			answer.add(String.valueOf(i));
		}

		return answer;
	}

	private String genAttMsg(StudyBean currentStudy, DatasetBean db) {

		String summary = "";
		if (db.isShowEventEnd() || db.isShowEventLocation() || db.isShowEventStart() || db.isShowEventStatus()
				|| db.isShowSubjectAgeAtEvent()) {
			summary = summary + getResPage().getString("you_choose_to_show_event_attributes");
			if (db.isShowEventLocation()) {
				summary = summary + getResWord().getString("location") + ", ";
			}
			if (db.isShowEventStart()) {
				summary = summary + getResWord().getString("start_date") + ", ";
			}
			if (db.isShowEventEnd()) {
				summary = summary + getResWord().getString("end_date") + ", ";
			}
			if (db.isShowEventStatus()) {
				summary = summary + getResWord().getString("status") + ", ";
			}
			if (db.isShowSubjectAgeAtEvent()) {
				summary = summary + " " + getResWord().getString("age_at_event") + ", ";
			}
		}

		if (db.isShowSubjectDob() || db.isShowSubjectGender() || db.isShowSubjectStatus()
				|| db.isShowSubjectUniqueIdentifier() || db.isShowSubjectSecondaryId()) {
			summary = summary.trim();
			summary = summary.endsWith(",") ? summary.substring(0, summary.length() - 1) : summary;
			summary += summary.length() > 0 ? ". " : " ";
			summary += getResPage().getString("you_choose_to_show_subject_attributes");
			if (db.isShowSubjectDob()) {
				summary = summary + getResWord().getString("date_year_of_birth") + ", ";
			}
			if (db.isShowSubjectGender()
					&& (currentStudy == null
					|| currentStudy.getStudyParameterConfig().getGenderRequired().equalsIgnoreCase("true"))) {

				summary = summary
						+ (currentStudy == null ? getResWord().getString("gender")
						: currentStudy.getStudyParameterConfig().getSecondaryIdLabel()) + ", ";
			}
			if (db.isShowSubjectStatus()) {
				summary = summary + " " + getResWord().getString("status") + ", ";
			}
			if (db.isShowSubjectUniqueIdentifier()) {
				summary = summary + " " + getResWord().getString("person_ID") + ", ";
			}
			if (db.isShowSubjectSecondaryId()
					&& (currentStudy == null
					|| !currentStudy.getStudyParameterConfig().getSecondaryIdRequired().equalsIgnoreCase("not_used"))) {

				summary = summary + " "
						+ (currentStudy == null ? getResWord().getString("secondary_ID")
						: currentStudy.getStudyParameterConfig().getSecondaryIdLabel()) + ", ";
			}
		}

		if (db.isShowCRFcompletionDate() || db.isShowCRFinterviewerDate() || db.isShowCRFinterviewerName()
				|| db.isShowCRFstatus() || db.isShowCRFversion()) {

			summary = summary.trim();
			summary = summary.endsWith(",") ? summary.substring(0, summary.length() - 1) : summary;
			summary += summary.length() > 0 ? ". " : " ";
			summary += getResWord().getString("you_choose_to_show_CRF") + ": ";
			if (db.isShowCRFcompletionDate()) {
				summary = summary + " " + getResWord().getString("completion_date") + ", ";
			}
			if (db.isShowCRFinterviewerDate()) {
				summary = summary + " " + getResWord().getString("interview_date") + ", ";
			}
			if (db.isShowCRFinterviewerName()) {
				summary = summary + " " + getResWord().getString("interviewer_name") + ", ";
			}
			if (db.isShowCRFstatus()) {
				summary = summary + " " + getResWord().getString("CRF_status") + ", ";
			}
			if (db.isShowCRFversion()) {
				summary = summary + " " + getResWord().getString("CRF_version") + ", ";
			}
		}
		summary = summary.trim();
		summary = summary.endsWith(",") ? summary.substring(0, summary.length() - 1) : summary;
		summary += summary.length() > 0 ? ". " : " ";

		if (db.isShowSubjectGroupInformation()) {
			summary += getResWord().getString("you_choose_to_show_subject_group");
		}
		return summary;
	}

	private void getSubAttr(FormProcessor fp, DatasetBean db) {

		String dob = fp.getString(DOB);
		if (!StringUtil.isBlank(dob) && "yes".equalsIgnoreCase(dob.trim())) {
			db.setShowSubjectDob(true);
		} else if (db.isShowSubjectDob()) {
			db.setShowSubjectDob(false);
		}

		String gender = fp.getString(GENDER);
		if (!StringUtil.isBlank(gender) && "yes".equalsIgnoreCase(gender.trim())) {
			db.setShowSubjectGender(true);
		} else if (db.isShowSubjectGender()) {
			db.setShowSubjectGender(false);
		}

		String status = fp.getString(SUBJ_STATUS);
		if (!StringUtil.isBlank(status) && "yes".equalsIgnoreCase(status.trim())) {
			db.setShowSubjectStatus(true);
			logger.info("added subject status");
		} else if (db.isShowSubjectStatus()) {
			db.setShowSubjectStatus(false);
		}

		String uniqueId = fp.getString(UNIQUE_ID);
		if (!StringUtil.isBlank(uniqueId) && "yes".equalsIgnoreCase(uniqueId.trim())) {
			db.setShowSubjectUniqueIdentifier(true);
			logger.info("added unique id");
		} else if (db.isShowSubjectUniqueIdentifier()) {
			db.setShowSubjectUniqueIdentifier(false);
		}

		String secondaryId = fp.getString(SUBJ_SECONDARY_ID);
		if (!StringUtil.isBlank(secondaryId) && "yes".equalsIgnoreCase(secondaryId.trim())) {
			db.setShowSubjectSecondaryId(true);
			logger.info("added secondary id");
		} else if (db.isShowSubjectSecondaryId()) {
			db.setShowSubjectSecondaryId(false);
		}
	}

	private void getEventAttr(FormProcessor fp, DatasetBean db) {
		String location = fp.getString(EVENT_LOCATION);
		if (!StringUtil.isBlank(location) && "yes".equalsIgnoreCase(location.trim())) {
			db.setShowEventLocation(true);
		} else if (db.isShowEventLocation()) {
			db.setShowEventLocation(false);
		}
		String start = fp.getString(EVENT_START);
		if (!StringUtil.isBlank(start) && "yes".equalsIgnoreCase(start.trim())) {
			db.setShowEventStart(true);
		} else if (db.isShowEventStart()) {
			db.setShowEventStart(false);
		}
		String end = fp.getString(EVENT_END);
		if (!StringUtil.isBlank(end) && "yes".equalsIgnoreCase(end.trim())) {
			db.setShowEventEnd(true);
		} else if (db.isShowEventEnd()) {
			db.setShowEventEnd(false);
		}
		String status = fp.getString(EVENT_STATUS);
		if (!StringUtil.isBlank(status) && "yes".equalsIgnoreCase(status.trim())) {
			db.setShowEventStatus(true);
			logger.info("added event status");
		} else if (db.isShowEventStatus()) {
			db.setShowEventStatus(false);
		}
		String ageAtEvent = fp.getString(AGE_AT_EVENT);
		if (!StringUtil.isBlank(ageAtEvent) && "yes".equalsIgnoreCase(ageAtEvent.trim())) {
			db.setShowSubjectAgeAtEvent(true);
			logger.info("added age at event");
		} else if (db.isShowSubjectAgeAtEvent()) {
			db.setShowSubjectAgeAtEvent(false);
		}
	}

	private void getGroupAttr(FormProcessor fp, DatasetBean db) {
		UserAccountBean ub = getUserAccountBean(fp.getRequest());
		db.setAllSelectedGroups(new ArrayList());
		List<StudyGroupClassBean> allGroups = setUpStudyGroups(ub);

		for (StudyGroupClassBean studyGroupClass: allGroups) {
			String checked = fp.getString("groupSelected" + studyGroupClass.getId());
			if (!StringUtil.isBlank(checked) && "yes".equalsIgnoreCase(checked.trim())) {
				db.setShowSubjectGroupInformation(true);
				studyGroupClass.setSelected(true);
				logger.info("just set a group to true: " + studyGroupClass.getName());

				if (db.getSubjectGroupIds() != null && !db.getSubjectGroupIds().contains(studyGroupClass.getId())) {
					db.getSubjectGroupIds().add(studyGroupClass.getId());
				}
			} else {
				studyGroupClass.setSelected(false);

				if (db.getSubjectGroupIds() != null && db.getSubjectGroupIds().contains(studyGroupClass.getId())) {
					db.getSubjectGroupIds().remove(new Integer(studyGroupClass.getId()));
				}
			}
			db.getAllSelectedGroups().add(studyGroupClass);
			logger.info("just added subject group ids: " + studyGroupClass.getId());
		}
		logger.info("added SUBJECT group info");
		if (db.isShowSubjectGroupInformation() && db.getAllSelectedGroups().size() == 0) {
			db.setShowSubjectGroupInformation(false);
			logger.info("show subject group info was TRUE, set to FALSE");
		}
	}

	private void getCRFAttr(FormProcessor fp, DatasetBean db) {

		String status = fp.getString(CRF_STATUS);
		if (!StringUtil.isBlank(status) && "yes".equalsIgnoreCase(status.trim())) {
			db.setShowCRFstatus(true);
			logger.info("added crf status");
		} else if (db.isShowCRFstatus()) {
			db.setShowCRFstatus(false);
		}
		String version = fp.getString(CRF_VERSION);
		if (!StringUtil.isBlank(version) && "yes".equalsIgnoreCase(version.trim())) {
			db.setShowCRFversion(true);
			logger.info("added crf version");
		} else if (db.isShowCRFversion()) {
			db.setShowCRFversion(false);
		}
		String interviewerDate = fp.getString(INTERVIEWER_DATE);
		if (!StringUtil.isBlank(interviewerDate) && "yes".equalsIgnoreCase(interviewerDate.trim())) {
			db.setShowCRFinterviewerDate(true);
			logger.info("added interviewer date");
		} else if (db.isShowCRFinterviewerDate()) {
			db.setShowCRFinterviewerDate(false);
		}
		String interviewerName = fp.getString(INTERVIEWER_NAME);
		if (!StringUtil.isBlank(interviewerName) && "yes".equalsIgnoreCase(interviewerName.trim())) {
			db.setShowCRFinterviewerName(true);
			logger.info("added interviewer name");
		} else if (db.isShowCRFinterviewerName()) {
			db.setShowCRFinterviewerName(false);
		}
	}

	private void extractEventIds(DatasetBean db) {

		ArrayList<Integer> selectedSedIds = new ArrayList<Integer>();
		HashMap dbItemMap = db != null ? db.getItemMap() : new HashMap();
		if (dbItemMap.size() > 0) {
			Iterator<String> it = dbItemMap.keySet().iterator();
			while (it.hasNext()) {
				Integer selected = Integer.valueOf(it.next().split("_")[0].trim());
				if (!"0".equals(selected) && !selectedSedIds.contains(selected)) {
					selectedSedIds.add(selected);
				}
			}
		}
		if (selectedSedIds.size() > 0) {
			db.getEventIds().clear();
			db.setEventIds(selectedSedIds);
		}
	}

	/**
	 * Get aff litems in the study.
	 * @param events HashMap
	 * @param crfdao CRFDAO
	 * @param idao ItemDAO
	 * @param ub UserAccountBean
	 * @return ArrayList
	 */
	public static ArrayList<String> allSedItemIdsInStudy(HashMap events, CRFDAO crfdao, ItemDAO idao, UserAccountBean ub) {

		ArrayList<String> sedItemIds = new ArrayList<String>();
		for (Object o : events.keySet()) {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) o;
			ArrayList crfs = (ArrayList) crfdao.findAllActiveUnmaskedByDefinition(sed, ub);
			for (Object crf1 : crfs) {
				CRFBean crf = (CRFBean) crf1;
				ArrayList<ItemBean> items = idao.findAllActiveByCRF(crf);
				for (ItemBean item : items) {
					Integer itemId = item.getId();
					if (!sedItemIds.contains(itemId)) {
						sedItemIds.add(sed.getId() + "-" + item.getId());
					}
				}
			}
		}
		return sedItemIds;
	}

	/**
	 * Update Dataset.
	 * @param datasetBean DatasetBean
	 * @return DatasetBean
	 */
	protected DatasetBean finalUpateDatasetBean(DatasetBean datasetBean) {

		ArrayList<Integer> itemIds = new ArrayList<Integer>();
		Set<Integer> ids = new HashSet<Integer>();
		String idList = "item_id in (";
		for (String key : (Set<String>) datasetBean.getItemMap().keySet()) {
			ItemBean ib = (ItemBean) datasetBean.getItemMap().get(key);
			if (!ids.contains(ib.getId())) {
				ids.add(ib.getId());
				itemIds.add(ib.getId());
				idList += ib.getId() + ", ";
			}
		}
		idList = idList.length() > TWELVE ? idList.substring(0, idList.length() - 2) : idList;
		datasetBean.getItemIds().clear();
		datasetBean.setItemIds(itemIds);
		datasetBean.setSQLStatement(datasetBean.sqlWithUniqeItemIds(idList));
		return datasetBean;
	}
}
