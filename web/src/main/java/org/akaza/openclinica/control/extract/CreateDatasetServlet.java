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
package org.akaza.openclinica.control.extract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DatasetItemStatus;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.TermType;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.FilterBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.control.core.SecureController;
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


/**
 * Creates a dataset by building a query based on study events, CRFs and items
 * 
 * @author jxu
 * @author thickerson
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class CreateDatasetServlet extends SecureController {

    private static Set xyz;

	public static final String BEAN_YEARS = "years";

	public static final String BEAN_MONTHS = "months";

	public static final String EVENTS_FOR_CREATE_DATASET = "eventsForCreateDataset";

	public static final String SAVE_BUTTON = "save";

	public static final String SAVE_CONTINUE_BUTTON = "saveContinue";

	public static final String DOB = "dob";

	public static final String GENDER = "gender";

	public static final String EVENT_LOCATION = "location";

	public static final String EVENT_START = "start";

	public static final String EVENT_END = "end";
	//

	// adding: subj_status, unique_identifier, age_at_event
	public static final String SUBJ_STATUS = "subj_status";
	public static final String UNIQUE_ID = "unique_identifier";
	public static final String AGE_AT_EVENT = "age_at_event";
	public static final String SUBJ_SECONDARY_ID = "subj_secondary_id";

	public static final String GROUP_INFORMATION = "group_information";

	public static final String EVENT_STATUS = "event_status";

	public static final String DISCREPANCY_INFORMATION = "disc";

	public static final String CRF_STATUS = "crf_status";
	public static final String CRF_VERSION = "crf_version";
	public static final String INTERVIEWER_NAME = "interviewer";
	public static final String INTERVIEWER_DATE = "interviewer_date";

    @Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR) || currentRole.getRole().equals(Role.STUDY_MONITOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO

	}

	public ArrayList setUpStudyGroups() {
		StudyDAO studydao = new StudyDAO(sm.getDataSource());
		StudyGroupClassDAO sgclassdao = new StudyGroupClassDAO(sm.getDataSource());
		StudyBean theStudy = (StudyBean) studydao.findByPK(sm.getUserBean().getActiveStudyId());
		ArrayList sgclasses = sgclassdao.findAllActiveByStudy(theStudy);
		
		return sgclasses;
	}

	@Override
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		String action = fp.getString("action");
        Date ddate = new SimpleDateFormat("MM/dd/yyyy").parse("01/01/1900");
        request.setAttribute("defaultStart", local_df.parse(local_df.format(ddate)));
        request.setAttribute("defaultEnd", getLastDayOfMonth(2100, 12));
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
			forwardPage(Page.CREATE_DATASET_1);

		} else {
			DatasetBean dsb = (DatasetBean) session.getAttribute("newDataset");
			if (dsb == null) {
				response.sendRedirect(request.getContextPath() + "/CreateDataset");
			} else if (action.equalsIgnoreCase("back_to_begin")) {
                if (dsb.getId() > 0) {
                    response.sendRedirect(request.getContextPath() + "/ViewDatasets");
                } else {
                    forwardPage(Page.CREATE_DATASET_1);
                }
			} else if (action.equalsIgnoreCase("back_to_viewselected")) {
				response.sendRedirect(request.getContextPath() + "/ViewSelected");
			} else if (action.equalsIgnoreCase("back_to_beginsubmit")) {
				forwardPage(Page.CREATE_DATASET_3);
			} else if (action.equalsIgnoreCase("back_to_scopesubmit")) {
				forwardPage(Page.CREATE_DATASET_4);
			} else if ("begin".equalsIgnoreCase(action)) {
				// step 2 -- select study events/crfs

				StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
				StudyBean studyWithEventDefinitions = currentStudy;
				if (currentStudy.getParentStudyId() > 0) {
					studyWithEventDefinitions = new StudyBean();
					studyWithEventDefinitions.setId(currentStudy.getParentStudyId());

				}
				ArrayList seds = seddao.findAllActiveByStudy(studyWithEventDefinitions);

				CRFDAO crfdao = new CRFDAO(sm.getDataSource());
				HashMap events = new LinkedHashMap();
				for (int i = 0; i < seds.size(); i++) {
					StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seds.get(i);
					ArrayList<CRFBean> crfs = (ArrayList<CRFBean>) crfdao.findAllActiveByDefinition(sed);

					if (currentStudy.getParentStudyId() > 0) {
						// sift through these CRFs and see which ones are hidden
						HideCRFManager hideCRFs = HideCRFManager.createHideCRFManager();
						crfs = hideCRFs.removeHiddenCRFBeans(studyWithEventDefinitions, sed, crfs, sm.getDataSource());
					}

					if (!crfs.isEmpty()) {
						events.put(sed, crfs);
					}
				}
				if (events.isEmpty()) {
					addPageMessage(respage.getString("not_have_study_definitions_assigned"));
					forwardPage(Page.CREATE_DATASET_1);
				} else {
					crfdao = new CRFDAO(sm.getDataSource());
					ItemDAO idao = new ItemDAO(sm.getDataSource());
					ArrayList sedItemIds = CreateDatasetServlet.allSedItemIdsInStudy(events, crfdao, idao);

					request.setAttribute("eventlist", events);
					session.setAttribute("numberOfStudyItems", new HashSet(sedItemIds).size());
					session.setAttribute(EVENTS_FOR_CREATE_DATASET, events);

					// forwardPage(Page.CREATE_DATASET_2);
					forwardPage(Page.CREATE_DATASET_VIEW_SELECTED);
				}

			} else if ("beginsubmit".equalsIgnoreCase(action)) {
				String saveItems = fp.getString(SAVE_BUTTON);
				extractIdsFromForm(dsb);
				extractEventIds(dsb);
                if (xyz == null) {
                    xyz = dsb.getItemMap().keySet();
                } else {
                    for (String key : (Set<String>)dsb.getItemMap().keySet()) {
                        if (!xyz.contains(key)) {
                        }
                    }
                }
				if (!StringUtil.isBlank(saveItems)) {
					request.setAttribute("eventlist", session.getAttribute(EVENTS_FOR_CREATE_DATASET));
					String summary = respage.getString("you_have_selected") + " " + dsb.getItemMap().size() + " "
							+ respage.getString("items_so_far");
					summary += genAttMsg(dsb);
					addPageMessage(summary);

					int crfId = fp.getInt("crfId");
					if (crfId > 0) {
                        request.setAttribute("allItems", dsb.getItemDefCrf());
						// user choose a crf and select items
						forwardPage(Page.CREATE_DATASET_2);
					} else {
						ArrayList sgclasses = dsb.getAllSelectedGroups();
						if (sgclasses == null || sgclasses.size() == 0) {
							sgclasses = setUpStudyGroups();
						}
						// TODO push out list of subject groups here???
						// form submitted from "view selected item ' or
						// attribute page, so
						// forward back to "view selected item " page
						forwardPage(Page.CREATE_DATASET_VIEW_SELECTED);
					}
				} else {
					if (dsb.getItemIds().size() == 0) {
                        request.setAttribute("allItems", dsb.getItemDefCrf());
						request.setAttribute("eventlist", session.getAttribute(EVENTS_FOR_CREATE_DATASET));
						addPageMessage(respage.getString("should_select_one_item_to_create_dataset"));
						forwardPage(Page.CREATE_DATASET_2);
					} else {

						String summary = respage.getString("you_have_selected") + " " + dsb.getItemMap().size() + " "
								+ respage.getString("items_totally_for_this_dataset");

						summary += genAttMsg(dsb);
						addPageMessage(summary);

						fp.addPresetValue("firstmonth", 0);// 0 means using
						// default month
						fp.addPresetValue("firstyear", 1900);
						fp.addPresetValue("lastmonth", 0);
						fp.addPresetValue("lastyear", 2100);
						setPresetValues(fp.getPresetValues());

						logger.warn("found preset values while setting date: " + fp.getPresetValues().toString());

						forwardPage(Page.CREATE_DATASET_3);
					}
				}

			} else if ("scopesubmit".equalsIgnoreCase(action)) {
				dsb.setFirstMonth(fp.getInt("firstmonth"));
				dsb.setFirstYear(fp.getInt("firstyear"));
				dsb.setLastMonth(fp.getInt("lastmonth"));
				dsb.setLastYear(fp.getInt("lastyear"));

				errors = new HashMap();
				if (dsb.getFirstMonth() > 0 && dsb.getFirstYear() == 1900) {
					Validator.addError(errors, "firstmonth", restext.getString("if_specify_month_also_specify_year"));

				}
				if (dsb.getLastMonth() > 0 && dsb.getLastYear() == 2100) {
					Validator.addError(errors, "lastmonth", restext.getString("if_specify_month_also_specify_year"));
				}
				if (dsb.getFirstYear() > 1900 && dsb.getFirstMonth() == 0) {
					Validator.addError(errors, "firstmonth", restext.getString("if_specify_year_also_specify_month"));

				}
				if (dsb.getLastYear() < 2100 && dsb.getLastMonth() == 0) {
					Validator.addError(errors, "lastmonth", restext.getString("if_specify_year_also_specify_month"));
				}
				Date dateStart = dsb.getFirstYear() > 1900 && dsb.getFirstMonth() > 0 ? getFirstDayOfMonth(
						dsb.getFirstYear(), dsb.getFirstMonth()) : null;
				Date dateEnd = dsb.getLastYear() < 2100 && dsb.getLastMonth() > 0 ? getLastDayOfMonth(
						dsb.getLastYear(), dsb.getLastMonth()) : null;

				if (dateEnd != null && dateStart != null && dateEnd.compareTo(dateStart) < 0) {
					Validator.addError(errors, "firstmonth", restext.getString("the_from_should_be_come_before_to"));
				}

				if (!errors.isEmpty()) {
					setInputMessages(errors);
					addPageMessage(respage.getString("errors_in_submission_see_below"));
					setPresetValues(fp.getPresetValues());

					forwardPage(Page.CREATE_DATASET_3);
				} else {
					// session.setAttribute("firstmonth", new Integer(firstMonth).toString());
					dsb.setDateStart(dateStart);
					// dateStart.getMonth();
					// GregorianCalendar gregCal = new GregorianCalendar(dateStart);
					// gregCal.get(Calendar.MONTH);
					// gregCal.get(Calendar.YEAR);
					dsb.setDateEnd(dateEnd);

					if (fp.getString("submit").equals(resword.getString("continue_to_apply_filter"))) {
						// you got here by mistake.
						forwardPage(Page.MENU);
					} else {
						forwardPage(Page.CREATE_DATASET_4);
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

				Validator v = new Validator(request);

				v.addValidation("dsName", Validator.NO_BLANKS);
				v.addValidation("dsName", Validator.NO_SEMI_COLONS_OR_COLONS);
				v.addValidation("dsDesc", Validator.NO_BLANKS);
				v.addValidation("dsStatus", Validator.IS_VALID_TERM, TermType.STATUS);

				v.addValidation("dsName", Validator.LENGTH_NUMERIC_COMPARISON,
						NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
				v.addValidation("dsDesc", Validator.LENGTH_NUMERIC_COMPARISON,
						NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);

				if (dsb.getOdmPriorStudyOid() != null && dsb.getOdmPriorStudyOid().length() > 0) {
					v.addValidation("mdvPrevOID", Validator.NO_BLANKS);
				}
				errors = v.validate();

				String dsName = fp.getString("dsName");
				if (!StringUtil.isBlank(dsName)) {
					if (dsName.contains("/") || dsName.contains("\\")) {
						Validator.addError(errors, "dsName", restext.getString("slash_not_allowed"));
					}
					Matcher matcher = Pattern.compile("[^\\w_\\d ]").matcher(dsName);
					boolean isContainSpecialSymbol = matcher.find();
					if (isContainSpecialSymbol) {
						Validator.addError(errors, "dsName",
								resexception.getString("dataset_should_not_contain_any_special"));
					}
					if (dsb.getId() <= 0) {
						DatasetDAO dsdao = new DatasetDAO(sm.getDataSource());
						DatasetBean dsBean = (DatasetBean) dsdao.findByNameAndStudy(fp.getString("dsName").trim(),
								currentStudy);
						if (dsBean.getId() > 0) {
							Validator.addError(errors, "dsName",
									restext.getString("dataset_name_used_by_another_choose_unique"));
						}
					}
				}

				if (!errors.isEmpty()) {
					addPageMessage(respage.getString("errors_in_submission_see_below"));
					setInputMessages(errors);
					setPresetValues(fp.getPresetValues());

					forwardPage(Page.CREATE_DATASET_4);
				} else {
					if (dsb.getOdmPriorMetaDataVersionOid() != null && dsb.getOdmPriorMetaDataVersionOid().length() > 0
							&& (dsb.getOdmPriorStudyOid() == null || dsb.getOdmPriorStudyOid().length() <= 0)) {
						dsb.setOdmPriorStudyOid(currentStudy.getId() + "");
					}
					dsb.setSQLStatement(dsb.generateQuery());
					String dbName = SQLInitServlet.getField("dataBase");
					if ("oracle".equals(dbName)) {
						dsb.setSQLStatement(dsb.generateOracleQuery());
					}
					// TODO set up oracle syntax for the query, grab the
					// database
					// from the session manager and feed it to the dataset bean
					// possibly done, tbh 1/4/2005

					// TODO look for the filter here, re-create the sql
					// statement
					// and put it in here
					// possibly done need to test, tbh 1/7/2005
					FilterBean fb = (FilterBean) session.getAttribute("newFilter");
					if (fb != null) {
						// FilterDAO fDAO = new FilterDAO(sm.getDataSource());
						dsb.setSQLStatement(dsb.getSQLStatement() + " " + fb.getSQLStatement());
					}

					String submit = fp.getString("btnSubmit");
					if (!resword.getString("continue").equalsIgnoreCase(submit)) {
						forwardPage(Page.CREATE_DATASET_3);
					}

					// done to remove the set up of going to get the filter, tbh
					// set up dataset here, grab primary key???!!!???
					// added by jxu

					forwardPage(Page.CONFIRM_DATASET);
				}

			} else if ("confirmall".equalsIgnoreCase(action)) {
				String submit = fp.getString("btnSubmit");
				logger.info("reached confirm all");
				if (!resword.getString("submit_for_dataset").equalsIgnoreCase(submit)) {
					// we're going back, so we should not destroy the
					// data we've created, tbh
					// session.removeAttribute("newDataset");
					// session.removeAttribute("newFilter");
					forwardPage(Page.CREATE_DATASET_4);
				} else {
					DatasetDAO ddao = new DatasetDAO(sm.getDataSource());

					dsb.setStudyId(this.currentStudy.getId());

					dsb.setOwner(ub);
					// dsb.setOwnerId(ub.getId());
					//
					// at this point, dataset itemId will still be kept
					// uniquely.
					dsb = finalUpateDatasetBean(dsb);

					if (dsb.getId() == 0) {
						// if the bean hasn't been created already that is...
						logger.info("*** about to create the dataset bean");
						dsb = (DatasetBean) ddao.create(dsb);
						logger.info("created dataset bean: " + dsb.getId() + ", name: " + dsb.getName());
						if (!dsb.isActive()) {
							addPageMessage(restext.getString("problem_creating_dataset_try_again"));
							forwardPage(Page.EXTRACT_DATASETS_MAIN);
						}
					}
					// YW, 2-20-2008 << for editing existing dataset
					else if (dsb.getId() > 0) {
						dsb = (DatasetBean) ddao.updateAll(dsb);
						if (!dsb.isActive()) {
							addPageMessage(restext.getString("problem_creating_dataset_try_again"));
							forwardPage(Page.EXTRACT_DATASETS_MAIN);
						}
						dsb = (DatasetBean) ddao.updateGroupMap(dsb);
						if (!dsb.isActive()) {
							addPageMessage(restext
									.getString("problem_updating_subject_group_class_when_updating_dataset"));
							forwardPage(Page.EXTRACT_DATASETS_MAIN);
						}
					}
					logger.info("setting data set id here");
					// may be easier to just set the dataset bean
					// back into the session?

					request.setAttribute("dataset", dsb);

					forwardPage(Page.EXPORT_DATASETS);
				}
			} else {
				// refine this bit to catch errors, hopefully
				addPageMessage(restext.getString("creating_new_dataset_cancelled"));
				forwardPage(Page.CREATE_DATASET_1);
			}
		}
	}

	public void extractIdsFromForm(DatasetBean db) {
		FormProcessor fp = new FormProcessor(request);
		int crfId = fp.getInt("crfId");
		int defId = fp.getInt("defId");
		boolean eventAttr = fp.getBoolean("eventAttr");
		boolean subAttr = fp.getBoolean("subAttr");
		boolean CRFAttr = fp.getBoolean("CRFAttr");
		boolean groupAttr = fp.getBoolean("groupAttr");
		// ArrayList allGroups = setUpStudyGroups();//new ArrayList();
		// possible function call here
		// ArrayList allSelectedGroups = new ArrayList();
		// possible session call here
		// boolean discAttr = fp.getBoolean("discAttr");
		// we decide not to touch groups here, except in call from 'view
		// selected'

		if (defId > 0 && !db.getEventIds().contains(new Integer(defId))) {
			db.getEventIds().add(new Integer(defId));
		}

		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
		String defName = "";
		if (defId > 0 && crfId != -1) {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(defId);
			defName = sed.getName();
		}

        if (crfId > 0) {
            db.setItemDefCrf((ArrayList)session.getAttribute("allCrfItems"));
        }

        db.getItemIds().clear();
        db.getItemMap().clear();

        CRFDAO cdao = new CRFDAO(sm.getDataSource());
        CRFBean crf = (CRFBean) cdao.findByPK(crfId);

        int i = 0;
        Iterator<ItemBean> iterator = db.getItemDefCrf().iterator();
        while (iterator.hasNext()) {
            ItemBean selectedItem = iterator.next();
            String checked = fp.getString("itemSelected" + i);
            String itemCrfName = fp.getString("itemCrfName" + i);
            String itemDefName = fp.getString("itemDefName" + i);
            selectedItem.setSelected(!StringUtil.isBlank(checked) && "yes".equalsIgnoreCase(checked.trim()));
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

                if (!db.getItemMap().containsKey(selectedItem.getDatasetItemMapKey())) {
                    // logger.info("one item selected");
                    logger.info("one item selected");
                    db.getItemIds().add(new Integer(selectedItem.getId()));
                    if (selectedItem.getDefId() == 0) {
                        db.getItemMap().put(defId + "_" + selectedItem.getId(), selectedItem);
                    } else {
                        db.getItemMap().put(selectedItem.getDefId() + "_" + selectedItem.getId(), selectedItem);
                    }
                }
            }
            i++;
        }

        if (crfId == -1) {// from view selected page
            getSubAttr(fp, db);
            getEventAttr(fp, db);
            getGroupAttr(fp, db);
            getCRFAttr(fp, db);
        }

		if (crfId == 0) {// event or subject attribute page
			if (subAttr) {
				getSubAttr(fp, db);
			} else if (eventAttr) {
				getEventAttr(fp, db);
			} else if (groupAttr) {
				getGroupAttr(fp, db);
			} else if (CRFAttr) {
				getCRFAttr(fp, db);
			}
		}

	}

	/**
	 * @param year
	 *            The year part of the date.
	 * @param month
	 *            The month part of the date.
	 * @return A Date object corresponding to the first day of the specified year and month.
	 */
	private Date getFirstDayOfMonth(int year, int month) {
		// scale month down to 0 .. 11 range
		month--;

		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(year, month, 1, 0, 0, 0);
		return new Date(c.getTimeInMillis());
	}

	/**
	 * @param year
	 *            The year part of the date.
	 * @param month
	 *            The month part of the date.
	 * @return A Date object corresponding to the last day of the specified year and month.
	 */
	private Date getLastDayOfMonth(int year, int month) {
		// scale month down to 0 .. 11 range
		month--;

		// the idea is to get the first day of the next month
		// then subtract a day and let the Calendar class do the real work

		Calendar c = Calendar.getInstance();

		c.clear();

		// get the first day of the current month and year, e.g.:
		// a. Apr. 1 2001
		// b. Feb. 1 2000
		// c. Feb. 1 2001
		// d. Dec. 1 1999
		c.set(year, month, 1, 23, 59, 59);

		// get the first day of the next month, e.g.:
		// a. May 1 2001
		// b. Mar. 1 2000
		// c. Mar. 1 2001
		// d. Jan. 1 2000 (note roll-over to next year)
		c.add(Calendar.MONTH, 1);

		// get the immediately preceding date, e.g.:
		// a. Apr. 30 2001 (note that Calendar knows April is only 30 days)
		// b. Feb. 29 2000 (note sensitivity to the leap year)
		// c. Feb. 28 2001
		// d. Dec. 31 1999 (note roll-back to the previous year)
		c.add(Calendar.DATE, -1);

		return new Date(c.getTimeInMillis());
	}

	private ArrayList getMonths() {
		ArrayList answer = new ArrayList();

		answer.add(resword.getString("January"));
		answer.add(resword.getString("February"));
		answer.add(resword.getString("March"));
		answer.add(resword.getString("April"));
		answer.add(resword.getString("May"));
		answer.add(resword.getString("June"));
		answer.add(resword.getString("July"));
		answer.add(resword.getString("August"));
		answer.add(resword.getString("September"));
		answer.add(resword.getString("October"));
		answer.add(resword.getString("November"));
		answer.add(resword.getString("December"));

		return answer;
	}

	private ArrayList getYears() {
		ArrayList answer = new ArrayList();

		Calendar currTime = Calendar.getInstance();
		int currYear = currTime.get(Calendar.YEAR);

		for (int i = currYear; i >= 1980; i--) {
			answer.add(String.valueOf(i));
		}

		return answer;
	}


	private String genAttMsg(DatasetBean db) {
		String summary = "";

		if (db.isShowEventEnd() || db.isShowEventLocation() || db.isShowEventStart() || db.isShowEventStatus()
				|| db.isShowSubjectAgeAtEvent()) {
			summary = summary + respage.getString("you_choose_to_show_event_attributes");
			if (db.isShowEventLocation()) {
				summary = summary + resword.getString("location") + ", ";
			}
			if (db.isShowEventStart()) {
				summary = summary + resword.getString("start_date") + ", ";
			}
			if (db.isShowEventEnd()) {
				summary = summary + resword.getString("end_date") + ", ";
			}
			if (db.isShowEventStatus()) {
				summary = summary + resword.getString("status") + ", ";
			}
			if (db.isShowSubjectAgeAtEvent()) {
				summary = summary + " " + resword.getString("age_at_event") + ", ";
			}
		}

		if (db.isShowSubjectDob() || db.isShowSubjectGender() || db.isShowSubjectStatus()
				|| db.isShowSubjectUniqueIdentifier() || db.isShowSubjectSecondaryId()) {
			summary = summary.trim();
			summary = summary.endsWith(",") ? summary.substring(0, summary.length() - 1) : summary;
			summary += summary.length() > 0 ? ". " : " ";
			summary += respage.getString("you_choose_to_show_subject_attributes");
			if (db.isShowSubjectDob()) {
				summary = summary + resword.getString("date_year_of_birth") + ", ";
			}
			if (db.isShowSubjectGender()
					&& (currentStudy == null || currentStudy.getStudyParameterConfig().getGenderRequired()
							.equalsIgnoreCase("true"))) {
				summary = summary
						+ (currentStudy == null ? resword.getString("gender") : currentStudy.getStudyParameterConfig()
								.getSecondaryIdLabel()) + ", ";
			}
			if (db.isShowSubjectStatus()) {
				summary = summary + " " + resword.getString("status") + ", ";
			}
			if (db.isShowSubjectUniqueIdentifier()) {
				summary = summary + " " + resword.getString("person_ID") + ", ";
			}
			if (db.isShowSubjectSecondaryId()
					&& (currentStudy == null || !currentStudy.getStudyParameterConfig().getSecondaryIdRequired()
							.equalsIgnoreCase("not_used"))) {
				summary = summary
						+ " "
						+ (currentStudy == null ? resword.getString("secondary_ID") : currentStudy
								.getStudyParameterConfig().getSecondaryIdLabel()) + ", ";
			}
		}
		// newly added tbh
		if (db.isShowCRFcompletionDate() || db.isShowCRFinterviewerDate() || db.isShowCRFinterviewerName()
				|| db.isShowCRFstatus() || db.isShowCRFversion()) {
			summary = summary.trim();
			summary = summary.endsWith(",") ? summary.substring(0, summary.length() - 1) : summary;
			summary += summary.length() > 0 ? ". " : " ";
			summary += resword.getString("you_choose_to_show_CRF") + ": ";
			if (db.isShowCRFcompletionDate()) {
				summary = summary + " " + resword.getString("completion_date") + ", ";
			}
			if (db.isShowCRFinterviewerDate()) {
				summary = summary + " " + resword.getString("interview_date") + ", ";
			}
			if (db.isShowCRFinterviewerName()) {
				summary = summary + " " + resword.getString("interviewer_name") + ", ";
			}
			if (db.isShowCRFstatus()) {
				summary = summary + " " + resword.getString("CRF_status") + ", ";
			}
			if (db.isShowCRFversion()) {
				summary = summary + " " + resword.getString("CRF_version") + ", ";
			}
		}
		summary = summary.trim();
		summary = summary.endsWith(",") ? summary.substring(0, summary.length() - 1) : summary;
		summary += summary.length() > 0 ? ". " : " ";
	
		if (db.isShowSubjectGroupInformation()) {
			summary += resword.getString("you_choose_to_show_subject_group");
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

		String unique_id = fp.getString(UNIQUE_ID);
		if (!StringUtil.isBlank(unique_id) && "yes".equalsIgnoreCase(unique_id.trim())) {
			db.setShowSubjectUniqueIdentifier(true);
			logger.info("added unique id");
		} else if (db.isShowSubjectUniqueIdentifier()) {
			db.setShowSubjectUniqueIdentifier(false);
		}

		String secondary_id = fp.getString(SUBJ_SECONDARY_ID);
		if (!StringUtil.isBlank(secondary_id) && "yes".equalsIgnoreCase(secondary_id.trim())) {
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
			// user unchecked event location on page
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
		// add here; event status
		String status = fp.getString(EVENT_STATUS);
		if (!StringUtil.isBlank(status) && "yes".equalsIgnoreCase(status.trim())) {
			db.setShowEventStatus(true);
			logger.info("added event status");
		} else if (db.isShowEventStatus()) {
			db.setShowEventStatus(false);
		}

		String ageatevent = fp.getString(AGE_AT_EVENT);
		if (!StringUtil.isBlank(ageatevent) && "yes".equalsIgnoreCase(ageatevent.trim())) {
			db.setShowSubjectAgeAtEvent(true);
			logger.info("added age at event");
		} else if (db.isShowSubjectAgeAtEvent()) {
			db.setShowSubjectAgeAtEvent(false);
		}
	}

	private void getGroupAttr(FormProcessor fp, DatasetBean db) {
        db.setAllSelectedGroups(new ArrayList());
		ArrayList allGroups = setUpStudyGroups();

		for (int j = 0; j < allGroups.size(); j++) {
			StudyGroupClassBean sgclass = (StudyGroupClassBean) allGroups.get(j);
			String checked = fp.getString("groupSelected" + sgclass.getId());
			if (!StringUtil.isBlank(checked) && "yes".equalsIgnoreCase(checked.trim())) {
				db.setShowSubjectGroupInformation(true);
				// were they all checked? yes or no, we need to set this flag
				sgclass.setSelected(true);
				logger.info("just set a group to true: " + sgclass.getName());
				// Let subjectGroupIds contain only selected
				// StudyGroupClass_id <<
				if (db.getSubjectGroupIds() != null && !db.getSubjectGroupIds().contains(sgclass.getId())) {
					db.getSubjectGroupIds().add(new Integer(sgclass.getId()));
				}
			} else {
				sgclass.setSelected(false);
				// Delete StudyGroupClass_id from
				// subjectGroupIds if appliable<<
				if (db.getSubjectGroupIds() != null && db.getSubjectGroupIds().contains(sgclass.getId())) {
					db.getSubjectGroupIds().remove(new Integer(sgclass.getId()));
				}
			}
            db.getAllSelectedGroups().add(sgclass);
			// db.getSubjectGroupIds().add(new Integer(sgclass.getId()));
			logger.info("just added subject group ids: " + sgclass.getId());
		}
		// above really necessary? tbh
		logger.info("added SUBJECT group info");
		// if it's been set previously and we've unchecked everything, run this
		if (db.isShowSubjectGroupInformation() && db.getAllSelectedGroups().size() == 0) {
			db.setShowSubjectGroupInformation(false);
			logger.info("show subject group info was TRUE, set to FALSE");
		}
		// } else if (db.isShowSubjectGroupInformation()) {
		// db.setShowSubjectGroupInformation(false);
		// }
	}

	/*
	 * private void getDiscrepancyAttr(FormProcessor fp, DatasetBean db) { String disc =
	 * fp.getString(DISCREPANCY_INFORMATION); if (!StringUtil.isBlank(disc) && "yes".equalsIgnoreCase(disc.trim())) {
	 * db.setShowDiscrepancyInformation(true); logger.info("added disc info"); } else if
	 * (db.isShowDiscrepancyInformation()) { db.setShowDiscrepancyInformation(false); } }
	 */
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
		String idate = fp.getString(INTERVIEWER_DATE);
		if (!StringUtil.isBlank(idate) && "yes".equalsIgnoreCase(idate.trim())) {
			db.setShowCRFinterviewerDate(true);
			logger.info("added interviewer date");
		} else if (db.isShowCRFinterviewerDate()) {
			db.setShowCRFinterviewerDate(false);
		}
		String iname = fp.getString(INTERVIEWER_NAME);
		if (!StringUtil.isBlank(iname) && "yes".equalsIgnoreCase(iname.trim())) {
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

	public static ArrayList<String> allSedItemIdsInStudy(HashMap events, CRFDAO crfdao, ItemDAO idao) {
		ArrayList<String> sedItemIds = new ArrayList<String>();
		Iterator it = events.keySet().iterator();
		while (it.hasNext()) {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) it.next();
			ArrayList crfs = (ArrayList) crfdao.findAllActiveByDefinition(sed);
			for (int i = 0; i < crfs.size(); i++) {
				CRFBean crf = (CRFBean) crfs.get(i);
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

	/*
	 * Make sure item ids are unique and match SQLStatement for them
	 */
	protected DatasetBean finalUpateDatasetBean(DatasetBean db) {
		DatasetBean dsb = db;
		ArrayList<Integer> itemIds = new ArrayList<Integer>();
		Set<Integer> ids = new HashSet<Integer>();
		String idList = "item_id in (";
		for (String key : (Set<String>) dsb.getItemMap().keySet()) {
			ItemBean ib = (ItemBean) dsb.getItemMap().get(key);
			if (!ids.contains(ib.getId())) {
				ids.add(ib.getId());
				itemIds.add(ib.getId());
				idList += ib.getId() + ", ";
			}
		}
		idList = idList.length() > 12 ? idList.substring(0, idList.length() - 2) : idList;
		dsb.getItemIds().clear();
		dsb.setItemIds(itemIds);
		dsb.setSQLStatement(dsb.sqlWithUniqeItemIds(idList));
		return dsb;
	}
}
