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

import com.clinovo.util.ValidatorHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.CRFRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.springframework.stereotype.Component;

/**
 * @author jxu
 * 
 *         Defines a new study event
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
@Component
public class DefineStudyEventServlet extends Controller {

	/**
	 * Checks whether the user has the correct privilege
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.LIST_DEFINITION_SERVLET, respage.getString("current_study_locked"), request, response);

		if (currentStudy.getParentStudyId() > 0) {
			addPageMessage(
					respage.getString("SED_may_only_added_top_level")
							+ respage.getString("please_contact_sysadmin_questions"), request);
			throw new InsufficientPermissionException(Page.STUDY_EVENT_DEFINITION_LIST,
					resexception.getString("not_top_study"), "1");
		}

		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_persmission_add_SED_to_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.STUDY_EVENT_DEFINITION_LIST,
				resexception.getString("not_study_director"), "1");

	}

	/**
	 * Processes the 'define study event' request
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		StudyBean currentStudy = getCurrentStudy(request);
		FormProcessor fpr = new FormProcessor(request);
		logger.trace("actionName*******" + fpr.getString("actionName"));
		logger.trace("pageNum*******" + fpr.getString("pageNum"));
		checkReferenceVisit(request);
		String actionName = request.getParameter("actionName");
		ArrayList crfsWithVersion = (ArrayList) request.getSession().getAttribute("crfsWithVersion");
		if (crfsWithVersion == null) {
			crfsWithVersion = new ArrayList();
			CRFDAO cdao = getCRFDAO();
			CRFVersionDAO cvdao = getCRFVersionDAO();
			ArrayList crfs = (ArrayList) cdao.findAllByStatus(Status.AVAILABLE);

			for (Object crf1 : crfs) {
				CRFBean crf = (CRFBean) crf1;
				ArrayList versions = cvdao.findAllByCRFId(crf.getId());
				if (!versions.isEmpty()) {
					crfsWithVersion.add(crf);
				}

			}
			request.getSession().setAttribute("crfsWithVersion", crfsWithVersion);
		}
		if (StringUtil.isBlank(actionName)) {
			setOrGetDefinition(request);
			forwardPage(Page.DEFINE_STUDY_EVENT1, request, response);
		} else {
			// When this page is called first time - session need to be cleaned
			if ("init".equalsIgnoreCase(actionName)) {
				UpdateEventDefinitionServlet.clearSession(request.getSession());
				forwardPage(Page.DEFINE_STUDY_EVENT1, request, response);
			} else {
				if ("confirm".equalsIgnoreCase(actionName)) {
					confirmWholeDefinition(request, response);
				} else if ("submit".equalsIgnoreCase(actionName)) {
					try {
						Integer nextAction = Integer.valueOf(request.getParameter("nextAction"));
						if (nextAction != null) {
							if (nextAction == 1) {
								session.removeAttribute("definition");
								addPageMessage(respage.getString("the_new_event_definition_creation_cancelled"),
										request);
								forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
							} else if (nextAction == 2) {
								submitDefinition(request);
								ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);
								session.setAttribute("pageMessages", pageMessages);
								forwardPage(Page.DEFINE_STUDY_EVENT1, request, response);
							} else {
								logger.info("actionName ==> 3");
								submitDefinition(request);
								StudyEventDefinitionBean sed = new StudyEventDefinitionBean();
								sed.setStudyId(currentStudy.getId());
								session.setAttribute("definition", sed);
								forwardPage(Page.DEFINE_STUDY_EVENT1, request, response);
							}
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
						addPageMessage(respage.getString("the_new_event_definition_creation_cancelled"), request);
						forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
					} catch (NullPointerException e) {
						e.printStackTrace();
						addPageMessage(respage.getString("the_new_event_definition_creation_cancelled"), request);
						forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
					}

				} else if ("next".equalsIgnoreCase(actionName)) {
					Integer pageNumber = Integer.valueOf(request.getParameter("pageNum"));
					if (pageNumber != null) {
						if (pageNumber == 2) {
							String nextListPage = request.getParameter("next_list_page");
							if (nextListPage != null && nextListPage.equalsIgnoreCase("true")) {
								logger.trace("+++ step 1");
								confirmDefinition1(request, response);
							} else {
								logger.trace("+++ step 2");
								confirmDefinition2(request, response, false);
							}
						} else {
							logger.trace("+++ step 3");
							confirmDefinition1(request, response);
						}
					} else {
						logger.trace("+++ step 4");
						setOrGetDefinition(request);
						forwardPage(Page.DEFINE_STUDY_EVENT1, request, response);
					}
				} else if ("back1".equalsIgnoreCase(actionName)) {
					FormProcessor fp = new FormProcessor(request);
					prepareServletForStepTwo(fp, response, false);
				} else if ("back2".equalsIgnoreCase(actionName)) {
					confirmDefinition2(request, response, true);
				}
			}
		}
	}

	private void setOrGetDefinition(HttpServletRequest request) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);
		HttpSession session = request.getSession();

		if (session.getAttribute("definition") == null) {
			logger.trace("did not find definition");
			StudyEventDefinitionBean sed = new StudyEventDefinitionBean();
			sed.setStudyId(currentStudy.getId());
			session.setAttribute("definition", sed);
		} else {
			logger.trace("found definition");
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");
			session.setAttribute("definition", sed);
		}
	}

	/**
	 * Validates the first section of definition inputs
	 * 
	 * @throws Exception
	 */
	private void confirmDefinition1(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		FormProcessor fp = new FormProcessor(request);
        StudyBean currentStudy = getCurrentStudy(fp.getRequest());
		v.addValidation("name", Validator.NO_BLANKS);
		v.addValidation("type", Validator.NO_BLANKS);
		v.addValidation("name", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				2000);
		v.addValidation("description", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);
		v.addValidation("category", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);
		//Clinovo start #134 start
		String calendaredVisitType = fp.getString("type");
		if ("calendared_visit".equalsIgnoreCase(calendaredVisitType)) {
			v.addValidation("maxDay", Validator.IS_REQUIRED);
			v.addValidation("maxDay", Validator.IS_A_FLOAT,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 3);
			v.addValidation("minDay", Validator.IS_REQUIRED);
			v.addValidation("minDay", Validator.IS_A_FLOAT,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 3);
			v.addValidation("schDay", Validator.IS_REQUIRED);
			v.addValidation("schDay", Validator.IS_A_FLOAT,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 3);
			if("".equalsIgnoreCase(fp.getString("isReference"))) {
					v.addValidation("emailUser", Validator.NO_BLANKS);
			}
			v.addValidation("emailDay", Validator.IS_REQUIRED);
			v.addValidation("emailDay", Validator.IS_A_FLOAT,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 3);
            request.getSession().setAttribute("maxDay", fp.getString("maxDay"));
            request.getSession().setAttribute("minDay", fp.getString("minDay"));
            request.getSession().setAttribute("schDay", fp.getString("schDay"));
            request.getSession().setAttribute("emailUser", fp.getString("emailUser"));
            request.getSession().setAttribute("emailDay", fp.getString("emailDay"));
            request.getSession().setAttribute("isReference", fp.getString("isReference"));

		}
		HashMap errors = v.validate();
		int minDay = fp.getInt("minDay");
		int maxDay = fp.getInt("maxDay");
		int schDay = fp.getInt("schDay");
		int emailDay = fp.getInt("emailDay");
		String emailUser = fp.getString("emailUser");
		if (!(maxDay >= schDay)) {
			Validator.addError(errors, "maxDay",resexception.getString("daymax_greate_or_equal_dayschedule"));
		}
		if(!(minDay<=schDay)) {
			Validator.addError(errors, "minDay",resexception.getString("daymin_less_or_equal_dayschedule"));
		}
		if (!(minDay<=maxDay)) {
			Validator.addError(errors, "minDay",resexception.getString("daymin_less_or_equal_daymax"));
		}
		if(!(emailDay <= schDay)) {
			Validator.addError(errors, "emailDay",resexception.getString("dayemail_less_or_equal_dayschedule"));
		}
		if (!checkUserName(currentStudy, emailUser) && "calendared_visit".equalsIgnoreCase(calendaredVisitType) && "".equalsIgnoreCase(fp.getString("isReference"))) {
			Validator.addError(errors, "emailUser", resexception.getString("this_user_name_does_not_exist"));
		}
        request.getSession().setAttribute("definition", createStudyEventDefinition(request));
				if (errors.isEmpty()) {
					logger.info("no errors in the first section");
					prepareServletForStepTwo(fp, response, true);
				} else {
					logger.trace("has validation errors in the first section");
					request.setAttribute("formMessages", errors);
					setOrGetDefinition(request);
					forwardPage(Page.DEFINE_STUDY_EVENT1, request, response);
				}	
		
	}
	private void prepareServletForStepTwo(FormProcessor fp, HttpServletResponse response, boolean checkForm) {
		/*
		 * The tmpCRFIdMap will hold all the selected CRFs in the session when the user is navigating through the list.
		 * This has been done so that when the user moves to the next page of CRF list, the selection made in the
		 * previous page doesn't get lost.
		 */
		Map tmpCRFIdMap = (HashMap) fp.getRequest().getSession().getAttribute("tmpCRFIdMap");
		if (tmpCRFIdMap == null) {
			tmpCRFIdMap = new HashMap();
		}
		logger.trace("tmpCRFIdMap " + tmpCRFIdMap.toString());
		ArrayList crfsWithVersion = (ArrayList) fp.getRequest().getSession().getAttribute("crfsWithVersion");
		logger.trace("crf version " + crfsWithVersion.size());
		if (checkForm) {
			for (int i = 0; i < crfsWithVersion.size(); i++) {
				logger.trace("in loop " + i);
				int id = fp.getInt("id" + i);
				String name = fp.getString("name" + i);
				String selected = fp.getString("selected" + i);
				if (!StringUtil.isBlank(selected) && "yes".equalsIgnoreCase(selected.trim())) {
					logger.trace("found id: " + id + " name " + name + " selected " + selected);
					tmpCRFIdMap.put(id, name);
				} else {
					// Removing the elements from session which has been
					// deselected.
					if (tmpCRFIdMap.containsKey(id)) {
						tmpCRFIdMap.remove(id);
					}
				}
			}
		}
		logger.trace("about to set tmpCRFIdMap " + tmpCRFIdMap.toString());
		fp.getRequest().getSession().setAttribute("tmpCRFIdMap", tmpCRFIdMap);

		EntityBeanTable table = fp.getEntityBeanTable();
		ArrayList allRows = CRFRow.generateRowsFromBeans(crfsWithVersion);
		String[] columns = { resword.getString("CRF_name"), resword.getString("date_created"),
				resword.getString("owner"), resword.getString("date_updated"), resword.getString("last_updated_by"),
				resword.getString("selected") };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(5);
		StudyEventDefinitionBean def1 = (StudyEventDefinitionBean) fp.getRequest().getSession()
				.getAttribute("definition");
		UserAccountDAO uadao = getUserAccountDAO();
		UserAccountBean userBean = (UserAccountBean) uadao.findByPK(def1.getUserEmailId());
		HashMap args = new HashMap();
		args.put("actionName", "next");
		args.put("pageNum", "1");
		args.put("name", def1.getName());
		args.put("repeating", Boolean.toString(def1.isRepeating()));
		args.put("category", def1.getCategory());
		args.put("description", def1.getDescription());
		args.put("type", def1.getType());
		args.put("schDay", Integer.toString(def1.getScheduleDay()));
		args.put("maxDay", Integer.toString(def1.getMaxDay()));
		args.put("minDay", Integer.toString(def1.getMinDay()));
		args.put("emailDay", Integer.toString(def1.getEmailDay()));
		args.put("emailUser", userBean.getName());
		if ("true".equals(Boolean.toString(def1.getReferenceVisit()))) {
			args.put("isReference", "true");
		} else {
			args.put("isReference", "");
		}
		table.setQuery("DefineStudyEvent", args);
		table.setRows(allRows);
		table.computeDisplay();

		fp.getRequest().setAttribute("table", table);
		forwardPage(Page.DEFINE_STUDY_EVENT2, fp.getRequest(), response);

	}

	/**
	 * Validates the entire definition
	 * 
	 * @throws Exception
	 */
	private void confirmWholeDefinition(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		FormProcessor fp = new FormProcessor(request);
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) request.getSession().getAttribute("definition");
		ArrayList eventDefinitionCRFs = new ArrayList();
		CRFVersionDAO cvdao = getCRFVersionDAO();
		for (int i = 0; i < sed.getCrfs().size(); i++) {
			EventDefinitionCRFBean edcBean = new EventDefinitionCRFBean();
			int crfId = fp.getInt("crfId" + i);
			int defaultVersionId = fp.getInt("defaultVersionId" + i);
			edcBean.setCrfId(crfId);
			edcBean.setDefaultVersionId(defaultVersionId);
			CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(edcBean.getDefaultVersionId());
			edcBean.setDefaultVersionName(defaultVersion.getName());

			String crfName = fp.getString("crfName" + i);
			edcBean.setCrfName(crfName);

			String requiredCRF = fp.getString("requiredCRF" + i);
			String doubleEntry = fp.getString("doubleEntry" + i);
			String decisionCondition = fp.getString("decisionCondition" + i);
			String electronicSignature = fp.getString("electronicSignature" + i);

			String hiddenCrf = fp.getString("hiddenCrf" + i);
			// hideCRF is false by default in the bean
			if (!StringUtil.isBlank(hiddenCrf) && "yes".equalsIgnoreCase(hiddenCrf.trim())) {
				edcBean.setHideCrf(true);
			}

			String sdvOption = fp.getString("sdvOption" + i);
			if (!StringUtil.isBlank(sdvOption)) {
				int id = Integer.valueOf(sdvOption);
				edcBean.setSourceDataVerification(SourceDataVerification.getByCode(id));
			}
			if (!StringUtil.isBlank(requiredCRF) && "yes".equalsIgnoreCase(requiredCRF.trim())) {
				edcBean.setRequiredCRF(true);
			} else {
				edcBean.setRequiredCRF(false);
			}
			if (!StringUtil.isBlank(doubleEntry) && "yes".equalsIgnoreCase(doubleEntry.trim())) {
				edcBean.setDoubleEntry(true);
			} else {
				edcBean.setDoubleEntry(false);
			}
			if (!StringUtil.isBlank(decisionCondition) && "yes".equalsIgnoreCase(decisionCondition.trim())) {
				edcBean.setDecisionCondition(true);
			} else {
				edcBean.setDecisionCondition(false);
			}

			if (!StringUtil.isBlank(electronicSignature) && "yes".equalsIgnoreCase(electronicSignature.trim())) {
				edcBean.setElectronicSignature(true);
			} else {
				edcBean.setElectronicSignature(false);
			}

			String nullString = "";
			// process null values
			ArrayList nulls = NullValue.toArrayList();
			for (Object aNull : nulls) {
				NullValue n = (NullValue) aNull;
				String myNull = fp.getString(n.getName().toLowerCase() + i);
				if (!StringUtil.isBlank(myNull) && "yes".equalsIgnoreCase(myNull.trim())) {
					nullString = nullString + n.getName().toUpperCase() + ",";
				}

			}
			nullString = (nullString != "") ? nullString.substring(0, nullString.length() - 1) : "";
			
			edcBean.setNullValues(nullString);
			edcBean.setStudyId(ub.getActiveStudyId());
			eventDefinitionCRFs.add(edcBean);
		}
		request.setAttribute("eventDefinitionCRFs", eventDefinitionCRFs);
		request.getSession().setAttribute("edCRFs", eventDefinitionCRFs);// not used on page
		forwardPage(Page.DEFINE_STUDY_EVENT_CONFIRM, request, response);

	}

	/**
	 * Constructs study bean from request-first section
	 * 
	 * @return StudyEventDefinitionBean
	 */
	private StudyEventDefinitionBean createStudyEventDefinition(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) request.getSession().getAttribute("definition");
		sed.setName(fp.getString("name"));
		// YW <<
		String temp = fp.getString("repeating");
		if ("true".equalsIgnoreCase(temp) || "1".equals(temp)) {
			sed.setRepeating(true);
		} else if ("false".equalsIgnoreCase(temp) || "0".equals(temp)) {
			sed.setRepeating(false);
		}
		// YW >>
		sed.setCategory(fp.getString("category"));
		sed.setDescription(fp.getString("description"));
		sed.setType(fp.getString("type"));
		// Clinovo ticket #134 start
		sed.setMaxDay(fp.getInt("maxDay"));
		sed.setMinDay(fp.getInt("minDay"));
		sed.setScheduleDay(fp.getInt("schDay"));
		int userId = getIdByUserName(fp.getString("emailUser"));
		if (userId != 0) {
			sed.setUserEmailId(userId);
		} else {
			sed.setUserEmailId(1);
		}
		sed.setEmailDay(fp.getInt("emailDay"));
		String referenceVisitValue = fp.getString("isReference");
		if ("true".equalsIgnoreCase(referenceVisitValue)) {
			sed.setReferenceVisit(true);
		} else {
			sed.setReferenceVisit(false);
		}
		// end
		return sed;

	}

	private void confirmDefinition2(HttpServletRequest request, HttpServletResponse response, boolean isBack)
			throws Exception {

		FormProcessor fp = new FormProcessor(request);
		CRFVersionDAO vdao = getCRFVersionDAO();
		ArrayList crfArray = new ArrayList();
		Map tmpCRFIdMap = (HashMap) request.getSession().getAttribute("tmpCRFIdMap");
		// trying to avoid NPE not sure why we would get it there ((tmpCRFIdMap.containsKey(id))), tbh
		if (tmpCRFIdMap == null) {
			tmpCRFIdMap = new HashMap();
		}
		logger.trace("confirm definition 2: tmp crf id map " + tmpCRFIdMap.toString());
		ArrayList crfsWithVersion = (ArrayList) request.getSession().getAttribute("crfsWithVersion");
		for (int i = 0; i < crfsWithVersion.size(); i++) {
			int id = fp.getInt("id" + i);
			String name = fp.getString("name" + i);
			String selected = fp.getString("selected" + i);
			if (!StringUtil.isBlank(selected) && "yes".equalsIgnoreCase(selected.trim())) {
				tmpCRFIdMap.put(id, name);
				// for back button compatibility
				logger.info("one crf selected");
				CRFBean cb = new CRFBean();
				cb.setId(id);
				cb.setName(name);
				// only find active verions
				ArrayList versions = (ArrayList) vdao.findAllActiveByCRF(cb.getId());
				cb.setVersions(versions);

				crfArray.add(cb);
			} else {
				if (tmpCRFIdMap.containsKey(id)) {
					tmpCRFIdMap.remove(id);
				}
			}
		}
		logger.info("crf array after first pass: " + crfArray.toString());

		for (Object o : tmpCRFIdMap.keySet()) {
			int id = (Integer) o;
			String name = (String) tmpCRFIdMap.get(id);
			boolean isExists = false;
			for (Object aCrfArray : crfArray) {
				CRFBean cb = (CRFBean) aCrfArray;
				if (id == cb.getId()) {
					isExists = true;
				}
			}
			if (!isExists) {
				CRFBean cb = new CRFBean();
				cb.setId(id);
				cb.setName(name);

				// only find active verions
				ArrayList versions = (ArrayList) vdao.findAllActiveByCRF(cb.getId());
				cb.setVersions(versions);

				crfArray.add(cb);
			}
		}
		logger.info("crf array after *second* pass: " + crfArray.toString());
		logger.trace("about to set tmpCRFIdMap " + tmpCRFIdMap.toString());
		request.getSession().setAttribute("tmpCRFIdMap", tmpCRFIdMap);

		if (crfArray.size() == 0 && !isBack) {// no crf selected
			addPageMessage(respage.getString("no_CRF_selected_for_definition_add_later"), request);
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) request.getSession().getAttribute("definition");
			sed.setCrfs(new ArrayList());
			request.getSession().setAttribute("definition", sed);
			forwardPage(Page.DEFINE_STUDY_EVENT_CONFIRM, request, response);

		} else {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) request.getSession().getAttribute("definition");

			logger.info("setting crfs into defintion to review: " + crfArray.toString());
			sed.setCrfs(crfArray);// crfs selected by user

			request.getSession().setAttribute("definition", sed);

			ArrayList<String> sdvOptions = new ArrayList<String>();
			sdvOptions.add(SourceDataVerification.AllREQUIRED.toString());
			sdvOptions.add(SourceDataVerification.PARTIALREQUIRED.toString());
			sdvOptions.add(SourceDataVerification.NOTREQUIRED.toString());
			sdvOptions.add(SourceDataVerification.NOTAPPLICABLE.toString());
			request.setAttribute("sdvOptions", sdvOptions);

			logger.info("forwarding to defineStudyEvent3.jsp");
			forwardPage(Page.DEFINE_STUDY_EVENT3, request, response);
		}
	}

	/**
	 * Inserts the new study into database NullPointer catch added by tbh 092007, mean to fix task #1642 in Mantis
	 * 
	 */
	private void submitDefinition(HttpServletRequest request) throws NullPointerException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		StudyEventDefinitionDAO edao = getStudyEventDefinitionDAO();
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) request.getSession().getAttribute("definition");
		if (sed.getName().equals("") || sed.getName() == null) {
			throw new NullPointerException();
		}
		logger.info("Definition bean to be created:" + sed.getName() + sed.getStudyId());

		// fine the last one's ordinal
		ArrayList defs = edao.findAllByStudy(currentStudy);
		if (defs == null || defs.isEmpty()) {
			sed.setOrdinal(1);
		} else {
			int lastCount = defs.size() - 1;
			StudyEventDefinitionBean last = (StudyEventDefinitionBean) defs.get(lastCount);
			sed.setOrdinal(last.getOrdinal() + 1);
		}
		sed.setOwner(ub);
		sed.setStudyId(currentStudy.getId());
		// above line added for insurance, tbh
		sed.setCreatedDate(new Date());
		sed.setStatus(Status.AVAILABLE);
		StudyEventDefinitionBean sed1 = (StudyEventDefinitionBean) edao.create(sed);

		EventDefinitionCRFDAO cdao = getEventDefinitionCRFDAO();
		ArrayList eventDefinitionCRFs = new ArrayList();
		if (request.getSession().getAttribute("edCRFs") != null) {
			eventDefinitionCRFs = (ArrayList) request.getSession().getAttribute("edCRFs");
		}
		for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
			EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
			edc.setOwner(ub);
			edc.setCreatedDate(new Date());
			edc.setStatus(Status.AVAILABLE);
			edc.setStudyEventDefinitionId(sed1.getId());
			edc.setOrdinal(i + 1);
			cdao.create(edc);
		}

		request.getSession().removeAttribute("definition");
		request.getSession().removeAttribute("edCRFs");
		request.getSession().removeAttribute("crfsWithVersion");
		request.getSession().removeAttribute("tmpCRFIdMap");
		request.getSession().removeAttribute("referenceVisitAlredyExist");
		request.getSession().removeAttribute("maxDay");
		request.getSession().removeAttribute("minDay");
		request.getSession().removeAttribute("schDay");
		request.getSession().removeAttribute("emailUser");
		request.getSession().removeAttribute("emailDay");
		request.getSession().removeAttribute("isReference");
		checkReferenceVisit(request);
		addPageMessage(respage.getString("the_new_event_definition_created_succesfully"), request);

	}

	private void checkReferenceVisit(HttpServletRequest request) {
		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		ArrayList<StudyEventDefinitionBean> definitions = seddao.findReferenceVisitBeans();
		for (StudyEventDefinitionBean studyEventDefinition : definitions) {
			if (studyEventDefinition.getReferenceVisit()) {
				logger.trace("Reference visit already exist");
				request.getSession().setAttribute("referenceVisitAlredyExist", true);
				break;
			}
		}
	}

	private boolean checkUserName(StudyBean currentStudy, String emailUser) {
		boolean isValid = false;
		UserAccountDAO uadao = getUserAccountDAO();
		ArrayList<StudyUserRoleBean> userBean = uadao.findAllByStudyId(currentStudy.getId());
		for (StudyUserRoleBean userAccountBean : userBean) {
			if (emailUser.equals(userAccountBean.getUserName())) {
				isValid = true;
				break;
			} else {
				isValid = false;
			}
		}
		return isValid;
	}

	private int getIdByUserName(String userName) {
		UserAccountDAO uadao = getUserAccountDAO();
		UserAccountBean userBean = (UserAccountBean) uadao.findByUserName(userName);
		return userBean.getId();
	}

}
