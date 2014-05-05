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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
@Component
public class CompleteCRFDeleteServlet extends Controller {

	private static String CRF_ID = "crfId";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {

		UserAccountBean ub = getUserAccountBean(request);

		if (!ub.isSysAdmin()) {
			throw new InsufficientPermissionException(Page.MENU,
					resexception.getString("you_may_not_perform_administrative_functions"), "1");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		FormProcessor fp = new FormProcessor(request);
		int crfId = fp.getInt(CRF_ID);

		EventCRFDAO eventCRFDao = getEventCRFDAO();
		EventDefinitionCRFDAO eventDefinitionCrfDao = getEventDefinitionCRFDAO();

		List<EventCRFBean> eventCrfBeanList = eventCRFDao.findAllByCRF(crfId);
		if (eventCrfBeanList.size() > 0) {
			addPageMessage(respage.getString("some_subjects_have_already_opened_DE_stage"), request);
		}

		List<DiscrepancyNoteBean> crfDiscrepancyNotes = collectAllDnFromEventCrfList(eventCrfBeanList);
		if (crfDiscrepancyNotes.size() > 0) {
			addPageMessage(respage.getString("some_dn_have_already_opened"), request);
		}

		List<EventDefinitionCRFBean> eventDefinitionCrfListFiltered = eventDefinitionCrfListFilter((List<EventDefinitionCRFBean>) eventDefinitionCrfDao.findAllByCRF(crfId));
		if (eventDefinitionCrfListFiltered.size() > 0) {
			addPageMessage(respage.getString("some_study_event_already_contain"), request);
		}

		List<RuleSetBean> crfInTargetRuleSetList = ruleSetListFilter(crfId);
		boolean crfUsedInRuleExpression = false;

		if (crfInTargetRuleSetList.size() > 0) {
			addPageMessage(respage.getString("some_rules_have_already_created"), request);
		} else {
			crfUsedInRuleExpression = checkCrfInRuleExpressions(crfId);
			if (crfUsedInRuleExpression) {
				addPageMessage(respage.getString("some_rules_have_already_created"), request);
			}
		}

		if (eventCrfBeanList.size() > 0 || crfDiscrepancyNotes.size() > 0 || eventDefinitionCrfListFiltered.size() > 0 || crfInTargetRuleSetList.size() > 0 || crfUsedInRuleExpression) {

			String keyValue = (String) request.getSession().getAttribute("savedListCRFsUrl");

			if (keyValue != null) {
				Map storedAttributes = new HashMap();
				storedAttributes.put(Controller.PAGE_MESSAGE, request.getAttribute(Controller.PAGE_MESSAGE));
				request.getSession().setAttribute(STORED_ATTRIBUTES, storedAttributes);
				try {
					response.sendRedirect(response.encodeRedirectURL(keyValue));
				} catch (IOException e) {
					logger.error("Redirect: " + e.getMessage());
				}
			} else {
				forwardPage(Page.CRF_LIST_SERVLET, request, response);
			}
		} else {

			CRFDAO crfDao = getCRFDAO();
			crfDao.deleteCrfById(crfId);

			addPageMessage(respage.getString("the_crf_has_been_removed"), request);
			forwardPage(Page.CRF_LIST_SERVLET, request, response);
		}

	}

	@SuppressWarnings("unchecked")
	private boolean checkCrfInRuleExpressions(int crfId) {

		CRFVersionDAO crfVersionDao = getCRFVersionDAO();
		ItemDAO itemDao = getItemDAO();
		RuleSetDao ruleSetDao = getRuleSetDao();
		StudyDAO studyDao = getStudyDAO();

		List<CRFVersionBean> crfVersionList = (List<CRFVersionBean>) crfVersionDao.findAllByCRF(crfId);
		List<String> itemOidList = new ArrayList<String>();

		for (CRFVersionBean crfVersionBean : crfVersionList) {
			List<ItemBean> crfVersionItemList = itemDao.findAllItemsByVersionId(crfVersionBean.getId());
			if (crfVersionItemList.size() > 0) {
				for (ItemBean item : crfVersionItemList) {
					itemOidList.add(item.getOid());
				}
			}
		}

		List<RuleSetBean> ruleSetBeanListFromStudies = new ArrayList<RuleSetBean>();
		List<StudyBean> studyList = (List<StudyBean>) studyDao.findAll();

		for (StudyBean study : studyList) {
			List<RuleSetBean> studyRuleSetBeanList = ruleSetDao.findAllByStudy(study);
			if (studyRuleSetBeanList.size() > 0) {
				ruleSetBeanListFromStudies.addAll(studyRuleSetBeanList);
			}
		}

		boolean crfItemUsedInExpression = false;
		for (RuleSetBean ruleSetBean : ruleSetBeanListFromStudies) {
			for (RuleSetRuleBean ruleSetRuleBean : ruleSetBean.getRuleSetRules()) {
				String expression = ruleSetRuleBean.getRuleBean().getExpression().getValue();
				for (String itemOid : itemOidList) {
					if (expression.indexOf(itemOid) > 0) {
						crfItemUsedInExpression = true;
						return crfItemUsedInExpression;
					}
				}
			}
		}

		return crfItemUsedInExpression;
	}

	@SuppressWarnings("unchecked")
	private List<RuleSetBean> ruleSetListFilter(int crfId) {

		RuleSetDao ruleSetDao = getRuleSetDao();
		StudyDAO studyDao = getStudyDAO();
		CRFDAO crfDao = getCRFDAO();

		List<RuleSetBean> ruleSetBeanList = new ArrayList<RuleSetBean>();
		List<StudyBean> studyList = (List<StudyBean>) studyDao.findAll();
		CRFBean crfBean = (CRFBean) crfDao.findByPK(crfId);

		for (StudyBean study : studyList) {
			List<RuleSetBean> studyRuleSetBeanList = ruleSetDao.findByCrf(crfBean, study);
			if (studyRuleSetBeanList.size() > 0) {
				ruleSetBeanList.addAll(studyRuleSetBeanList);
			}
		}
		return ruleSetBeanList;
	}

	private List<EventDefinitionCRFBean> eventDefinitionCrfListFilter(List<EventDefinitionCRFBean> eventDefinitionCrfList) {

		List<EventDefinitionCRFBean> eventDefinitionCrfListFiltered = new ArrayList<EventDefinitionCRFBean>();

		for (EventDefinitionCRFBean eventDefCrfBean : eventDefinitionCrfList) {
			if (!eventDefCrfBean.getStatus().isDeleted()) {
				eventDefinitionCrfListFiltered.add(eventDefCrfBean);
			}
		}

		return eventDefinitionCrfListFiltered;
	}

	private List<DiscrepancyNoteBean> collectAllDnFromEventCrfList(List<EventCRFBean> eventCrfBeans) {

		DiscrepancyNoteDAO discrepancyNoteDao = getDiscrepancyNoteDAO();
		List<DiscrepancyNoteBean> dnList = new ArrayList<DiscrepancyNoteBean>();

		for (EventCRFBean eventCrfBean : eventCrfBeans) {
			dnList.addAll(discrepancyNoteDao.findAllItemNotesByEventCRF(eventCrfBean.getId()));
		}

		return dnList;
	}
}
