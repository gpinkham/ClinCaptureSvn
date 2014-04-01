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
 *
 * Copyright 2003-2008 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.action.DiscrepancyNoteActionBean;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.expression.Context;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionObjectWrapper;
import org.akaza.openclinica.domain.rule.expression.ExpressionProcessor;
import org.akaza.openclinica.domain.rule.expression.ExpressionProcessorFactory;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.clinovo.util.ValidatorHelper;
import com.google.gson.Gson;

/**
 * Verify the Rule import , show records that have Errors as well as records that will be saved.
 * 
 * @author Krikor krumlian
 */
@SuppressWarnings({"rawtypes","unchecked"})
@Component
public class TestRuleServlet extends Controller {

	private static final long serialVersionUID = 9116068126651934226L;
	protected final Logger log = LoggerFactory.getLogger(TestRuleServlet.class);

	private final String TARGET = "target";
	private final String RULE = "rule";
	private final String RULE_SET_RULE_ID = "ruleSetRuleId";
	
	void putDummyActionInSession(HttpServletRequest request) {
		ArrayList<RuleActionBean> actions = new ArrayList<RuleActionBean>();
		DiscrepancyNoteActionBean discNoteAction = new DiscrepancyNoteActionBean();
		discNoteAction.setExpressionEvaluatesTo(true);
		discNoteAction.setMessage("TEST DISCREPANCY");
		actions.add(discNoteAction);
		request.getSession().setAttribute("testRuleActions", actions);
	}

	void populteFormFields(FormProcessor fp) {
		String targetForm = fp.getString(TARGET).trim().replaceAll("(\n|\t|\r)", "");
		String testRulesTarget = (String) fp.getRequest().getSession().getAttribute("testRulesTarget");
		if (testRulesTarget != null) {
			String targetSess = testRulesTarget.trim().replaceAll("(\n|\t|\r)", "");
			if (!targetForm.equals(targetSess)) {
				putDummyActionInSession(fp.getRequest());
                fp.getRequest().getSession().removeAttribute("testRulesTarget");
			}
		}
		String textFields[] = { TARGET, RULE, RULE_SET_RULE_ID };
		fp.setCurrentStringValuesAsPreset(textFields);
		HashMap presetValues = fp.getPresetValues();
		setPresetValues(presetValues, fp.getRequest());

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// Reset the study if RS is testing a rule for a different study
		if ((request.getParameter("study") != null && !request.getParameter("study").isEmpty()) && getCurrentStudy(request).getId() != Integer.parseInt(request.getParameter("study"))) {
			originalScope = (StudyBean) request.getSession().getAttribute(STUDY);
			request.getSession().setAttribute(STUDY, getStudyDAO().findByPK(Integer.valueOf(request.getParameter("study"))));
		}

		StudyBean currentStudy = getCurrentStudy(request);
		FormProcessor fp = new FormProcessor(request);
		String action = request.getParameter("action");
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		if (StringUtil.isBlank(action)) {
			request.setAttribute("result", resword.getString("test_rule_default_result"));
			Integer ruleSetRuleId = fp.getInt("ruleSetRuleId");

			if (ruleSetRuleId != 0) { // If testing an existing ruleSetRule
				
				RuleSetRuleBean rsr = getRuleSetRuleDao().findById(ruleSetRuleId);
				rsr.getActions().size();
				HashMap presetValues = new HashMap();
				presetValues.put(TARGET, rsr.getRuleSetBean().getTarget().getValue());
				presetValues.put(RULE, rsr.getRuleBean().getExpression().getValue());
				presetValues.put(RULE_SET_RULE_ID, String.valueOf(ruleSetRuleId));
				fp.setPresetValues(presetValues);
				setPresetValues(presetValues, request);
				request.getSession().setAttribute("testRuleActions", rsr.getActions());
                request.getSession().setAttribute("testRulesTarget", rsr.getRuleSetBean().getTarget().getValue());
				request.setAttribute("ruleSetRuleId", ruleSetRuleId);
				request.setAttribute("ruleSetId", rsr.getRuleSetBean().getId());
				ItemBean item = getExpressionService(currentStudy).getItemBeanFromExpression(rsr.getRuleSetBean().getTarget().getValue());
				request.setAttribute("itemName", item.getName());
				request.setAttribute("itemDefinition", item.getDescription());
				request.setAttribute("ruleSetRuleAvailable", true);

			} else { // free form testing
				putDummyActionInSession(request);
			}
			request.getSession().removeAttribute("testValues");
			request.setAttribute("action", "validate");
			forwardPage(Page.TEST_RULES, request, response);

		} else if (action.equals("validate")) {
			try {
				HashMap<String, String> result = validate(request, v, currentStudy);
				// do not modify
				Map serialResult = new HashMap(result);
				if (result.get("ruleValidation").equals("rule_valid")) {
					addPageMessage(resword.getString("test_rules_message_valid"), request);
				} else {
					addPageMessage(resword.getString("test_rules_message_invalid"), request);
				}
				request.setAttribute("ruleValidation", result.get("ruleValidation"));
				request.setAttribute("validate", "on");
				request.setAttribute("ruleEvaluatesTo", resword.getString("test_rules_validate_message"));
				request.setAttribute("ruleValidationFailMessage", result.get("ruleValidationFailMessage"));
				request.setAttribute("action", result.get("ruleValidation").equals("rule_valid") ? "test" : "validate");
				result.remove("result");
				result.remove("ruleValidation");
				result.remove("ruleEvaluatesTo");
				result.remove("ruleValidationFailMessage");
				populateTooltip(request, result);
				request.getSession().setAttribute("testValues", result);
				populteFormFields(fp);
				
				if (request.getParameter("rs") != null && request.getParameter("rs").equals("true")) {
					response.getWriter().write(new Gson().toJson(serialResult));
				} else {
					forwardPage(Page.TEST_RULES, request, response);
				}
			} catch (Exception ex) {
				
				response.sendError(500, ex.getMessage());
			}

		} else if (action.equals("test")) {

			HashMap<String, String> result = validate(request, v, currentStudy);
			HashMap errors = v.validate();

			if (!errors.isEmpty()) {
				setInputMessages(errors, request);
				if (result.get("ruleValidation").equals("rule_valid")) {
					addPageMessage(resword.getString("test_rules_message_valid"), request);
				} else {
					addPageMessage(resword.getString("test_rules_message_invalid"), request);
				}
				request.setAttribute("ruleValidation", result.get("ruleValidation"));
				request.setAttribute("validate", "on");
				request.setAttribute("ruleEvaluatesTo", resword.getString("test_rules_rule_fail_invalid_data_type")
						+ " " + resword.getString("test_rules_rule_fail_invalid_data_type_desc"));
				request.setAttribute("ruleValidationFailMessage", result.get("ruleValidationFailMessage"));
				request.setAttribute("action", "test");

			} else {

				if (result.get("ruleValidation").equals("rule_valid")) {
					addPageMessage(resword.getString("test_rules_message_valid"), request);
				} else {
					addPageMessage(resword.getString("test_rules_message_invalid"), request);
				}
				request.setAttribute("action", result.get("ruleValidation").equals("rule_valid") ? "test" : "validate");
				request.setAttribute("ruleValidation", result.get("ruleValidation"));
				request.setAttribute("ruleEvaluatesTo", result.get("ruleEvaluatesTo"));
				request.setAttribute("ruleValidationFailMessage", result.get("ruleValidationFailMessage"));
			}

			if (result.get("ruleValidation").equals("rule_invalid")) {
				request.getSession().setAttribute("testValues", new HashMap<String, String>());
			} else {
                request.getSession().setAttribute("testValues", result);
			}

			result.remove("result");
			result.remove("ruleValidation");
			result.remove("ruleEvaluatesTo");
			result.remove("ruleValidationFailMessage");
			populateTooltip(request, result);

			populteFormFields(fp);

			forwardPage(Page.TEST_RULES, request, response);
		}
		// reset back to original scope 
		if (originalScope != null) {
			request.getSession().setAttribute(STUDY, originalScope);
		}
	}

	private void itemDataTypeToValidatorId(String key, ItemBean item, Validator v) {
		switch (item.getItemDataTypeId()) {
		case 6:
			v.addValidation(key, Validator.IS_AN_INTEGER);
			break;
		case 7:
			v.addValidation(key, Validator.IS_A_FLOAT);
			break;
		case 9:
			v.addValidation(key, Validator.IS_A_DATE);
			break;

		default:
			break;
		}
	}

	private void populateTooltip(HttpServletRequest request, HashMap<String, String> testVariablesAndValues) {
        StudyBean currentStudy = getCurrentStudy(request);
		if (testVariablesAndValues != null) {
			for (Map.Entry<String, String> entry : testVariablesAndValues.entrySet()) {
				ItemBean item = getExpressionService(currentStudy).getItemBeanFromExpression(entry.getKey());
				DisplayItemBean dib = new DisplayItemBean();
				dib.setItem(item);
				request.setAttribute(entry.getKey() + "-tooltip",
						item.getName() + ": " + ItemDataType.get(item.getItemDataTypeId()).getName());
				request.setAttribute(entry.getKey() + "-dib", dib);
				if (item.getItemDataTypeId() == 9) {
					try {
						SimpleDateFormat sdf = new SimpleDateFormat(resformat.getString("date_format_string"));
						SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
						if (!entry.getValue().isEmpty()) {
							java.util.Date date = sdf2.parse(entry.getValue());
							entry.setValue(sdf.format(date));
						}
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			}
		}

	}

	private HashMap<String, String> validate(HttpServletRequest request, Validator v, StudyBean study) {

		FormProcessor fp = new FormProcessor(request);

		String targetString = fp.getString("target");
		String ruleString = fp.getString("rule");
		ruleString = ruleString.trim().replaceAll("(\n|\t|\r)", " ");
		targetString = targetString.trim().replaceAll("(\n|\t|\r)", "");

		HashMap<String, String> p = request.getSession().getAttribute("testValues") != null ? (HashMap<String, String>) request.getSession()
				.getAttribute("testValues") : new HashMap<String, String>();

		if (p != null) {
			for (Map.Entry<String, String> entry : p.entrySet()) {
				entry.setValue(fp.getString(entry.getKey()));
				ItemBean item = getExpressionService(study).getItemBeanFromExpression(entry.getKey());
				List<ItemFormMetadataBean> itemFormMetadataBeans = getItemFormMetadataDAO().findAllByItemId(item.getId());
				ItemFormMetadataBean itemFormMetadataBean = itemFormMetadataBeans.size() > 0 ? itemFormMetadataBeans
						.get(0) : null;
				if (!entry.getValue().equals("") && NullValue.getByName(entry.getValue()) == NullValue.INVALID) {
					if (itemFormMetadataBean != null) {
						if (itemFormMetadataBean.getResponseSet().getResponseType() == ResponseType.SELECTMULTI
								|| itemFormMetadataBean.getResponseSet().getResponseType() == ResponseType.CHECKBOX) {
							v.addValidation(entry.getKey(), Validator.IN_RESPONSE_SET_COMMA_SEPERATED,
									itemFormMetadataBean.getResponseSet());
						}
						if (itemFormMetadataBean.getResponseSet().getResponseType() == ResponseType.SELECT
								|| itemFormMetadataBean.getResponseSet().getResponseType() == ResponseType.RADIO) {
							v.addValidation(entry.getKey(), Validator.IN_RESPONSE_SET_SINGLE_VALUE,
									itemFormMetadataBean.getResponseSet());
						} else {
							itemDataTypeToValidatorId(entry.getKey(), item, v);
						}
					}
				}

				if (item.getItemDataTypeId() == 9) {
					try {
						SimpleDateFormat sdf = new SimpleDateFormat(resformat.getString("date_format_string"));
						SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
						if (!entry.getValue().isEmpty()) {
							java.util.Date date = sdf.parse(entry.getValue());
							entry.setValue(sdf2.format(date));
						}
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			}
		}

		List<RuleActionBean> actions = request.getSession().getAttribute("testRuleActions") != null ? (List<RuleActionBean>) request.getSession()
				.getAttribute("testRuleActions") : new ArrayList<RuleActionBean>();

		if (actions != null) {
			for (int i = 0; i < actions.size(); i++) {
				actions.get(i).setExpressionEvaluatesTo(fp.getBoolean("actions" + i));
			}
		}

		// Check Target if not valid report and return
		try {
			getExpressionService(study).ruleSetExpressionChecker(targetString);
		} catch (OpenClinicaSystemException e) {
			HashMap<String, String> result = new HashMap<String, String>();
			MessageFormat mf = new MessageFormat("");
			mf.applyPattern(respage.getString(e.getErrorCode()));
			Object[] arguments = e.getErrorParams();
			result.put("ruleValidation", "target_invalid");
			result.put("ruleValidationFailMessage", e.getErrorCode() + " : " + mf.format(arguments));
			result.put("ruleEvaluatesTo", "");
			request.setAttribute("targetFail", "on");
			return result;

		}

		// Auto update itemName & itemDefinition based on target
		ItemBean item = getExpressionService(study).getItemBeanFromExpression(targetString);
		if (item != null) {
			request.setAttribute("itemName", item.getName());
			request.setAttribute("itemDefinition", item.getDescription());
		}

		RuleSetBean ruleSet = new RuleSetBean();
		ExpressionBean target = new ExpressionBean();
		target.setContext(Context.OC_RULES_V1);
		target.setValue(targetString);
		ruleSet.setTarget(target);

		RuleSetBean persistentRuleSet = getRuleSetDao().findByExpressionAndStudy(ruleSet, study.getId());

		if (persistentRuleSet != null) {
			request.setAttribute("ruleSetId", request.getParameter("ruleSetId"));
		}

		ExpressionBean rule = new ExpressionBean();
		rule.setContext(Context.OC_RULES_V1);
		rule.setValue(ruleString);

		ExpressionObjectWrapper eow = new ExpressionObjectWrapper(getDataSource(), study, rule, ruleSet);
		ExpressionProcessor ep = ExpressionProcessorFactory.createExpressionProcessor(eow);
		ep.setRespage(respage);

		// Run expression with populated HashMap
		DateTime start = new DateTime();
		HashMap<String, String> result = ep.testEvaluateExpression(p);
		DateTime end = new DateTime();
		Duration dur = new Duration(start, end);
		PeriodFormatter yearsAndMonths = new PeriodFormatterBuilder().printZeroAlways().appendSecondsWithMillis()
				.appendSuffix(" second", " seconds").toFormatter();
		yearsAndMonths.print(dur.toPeriod());

		// Run expression with empty HashMap to check rule validity, because
		// using illegal test values will cause invalidity
		HashMap<String, String> k = new HashMap<String, String>();
		HashMap<String, String> theResult = ep.testEvaluateExpression(k);
		if (theResult.get("ruleValidation").equals("rule_valid") && result.get("ruleValidation").equals("rule_invalid")) {
			result.put("ruleValidation", "rule_valid");
			result.put("ruleEvaluatesTo",
					resword.getString("test_rules_rule_fail") + " " + result.get("ruleValidationFailMessage"));
			result.remove("ruleValidationFailMessage");

		}
		// Put on screen
		request.setAttribute("duration", yearsAndMonths.print(dur.toPeriod()));
		return result;

	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
        UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return Controller.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}
		Role r = currentRole.getRole();
		if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}
		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	private ExpressionService getExpressionService(StudyBean currentStudy) {
        ExpressionService expressionService = new ExpressionService(new ExpressionObjectWrapper(getDataSource(), currentStudy, null, null));
        expressionService.setExpressionWrapper(new ExpressionObjectWrapper(getDataSource(), currentStudy, null, null));
        return expressionService;
	}

}
