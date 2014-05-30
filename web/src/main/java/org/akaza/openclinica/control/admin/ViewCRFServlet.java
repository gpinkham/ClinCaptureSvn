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
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.table.sdv.SDVUtil;
import org.jmesa.facade.TableFacade;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.component.HtmlRow;
import org.jmesa.view.html.component.HtmlTable;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.jmesa.facade.TableFacadeFactory.createTableFacade;

/**
 * @author jxu
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style -
 *         Code Templates
 */
@SuppressWarnings({ "unchecked", "serial" })
@Component
public class ViewCRFServlet extends Controller {

	private static final String CRF = "crf";
	private static final String CRF_ID = "crfId";
	private static final String ASSOCIATED_EVENT_LIST = "associatedEventList";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);
		panel.setSubmitDataModule(false);
		panel.setExtractData(false);
		panel.setCreateDataset(false);

		setToPanel(resword.getString("create_CRF"), respage.getString("br_create_new_CRF_entering"), request);
		setToPanel(resword.getString("create_CRF_version"), respage.getString("br_create_new_CRF_uploading"), request);
		setToPanel(resword.getString("revise_CRF_version"), respage.getString("br_if_you_owner_CRF_version"), request);
		setToPanel(resword.getString("CRF_spreadsheet_template"),
				respage.getString("br_download_blank_CRF_spreadsheet_from"), request);
		setToPanel(resword.getString("example_CRF_br_spreadsheets"),
				respage.getString("br_download_example_CRF_instructions_from"), request);

		FormProcessor fp = new FormProcessor(request);

		int crfId = fp.getInt(CRF_ID);
		List<StudyBean> studyBeans;
		if (crfId == 0) {
			addPageMessage(respage.getString("please_choose_a_CRF_to_view"), request);
			forwardPage(Page.CRF_LIST, request, response);
		} else {
			CRFDAO cdao = getCRFDAO();
			CRFVersionDAO vdao = getCRFVersionDAO();
			CRFBean crf = (CRFBean) cdao.findByPK(crfId);
			request.setAttribute("crfName", crf.getName());
			ArrayList<CRFVersionBean> versions = (ArrayList<CRFVersionBean>) vdao.findAllByCRF(crfId);
			crf.setVersions(versions);
			if (ub.isSysAdmin()) {
				// Generate a table showing a list of studies associated with the CRF>>
				StudyDAO studyDAO = getStudyDAO();

				studyBeans = findStudiesForCRFId(crfId, studyDAO);
				// Create the Jmesa table for the studies associated with the CRF
				String studyHtml = renderStudiesTable(studyBeans, request);
				request.setAttribute("studiesTableHTML", studyHtml);
			}

			EventDefinitionCRFDAO eventDefinitionCrfDao = getEventDefinitionCRFDAO();
			List<StudyEventDefinitionBean> studyEventDefList = studyEventDefinitionListFilter((List<EventDefinitionCRFBean>) eventDefinitionCrfDao
					.findAllByCRF(crfId));
			request.setAttribute(ASSOCIATED_EVENT_LIST, studyEventDefList);

			request.setAttribute(CRF, crf);
			forwardPage(Page.VIEW_CRF, request, response);

			populate(currentStudy, crf, versions);
		}
	}

	private Collection<TableColumnHolder> populate(StudyBean currentStudy, CRFBean crf,
			ArrayList<CRFVersionBean> versions) {
		HashMap<CRFVersionBean, ArrayList<TableColumnHolder>> hm = new HashMap<CRFVersionBean, ArrayList<TableColumnHolder>>();
		List<TableColumnHolder> tableColumnHolders = new ArrayList<TableColumnHolder>();
		for (CRFVersionBean versionBean : versions) {
			hm.put(versionBean, new ArrayList<TableColumnHolder>());
		}
		List<RuleSetBean> ruleSets = getRuleSetService().getRuleSetsByCrfAndStudy(crf, currentStudy);
		ruleSets = getRuleSetService().filterByStatusEqualsAvailable(ruleSets);
		for (RuleSetBean ruleSetBean : ruleSets) {
			if (ruleSetBean.getCrfVersion() == null) {
				for (CRFVersionBean key : hm.keySet()) {
					hm.get(key).addAll(createFromRuleSet(ruleSetBean, key));
				}
			}
			if (ruleSetBean.getCrfVersion() != null) {
				try {
					hm.get(ruleSetBean.getCrfVersion()).addAll(
							createFromRuleSet(ruleSetBean, ruleSetBean.getCrfVersion()));
				} catch (NullPointerException e) {
					// i18n support, need to catch a NPE every once in a while
					System.out.println("found NPE");
					// no logger?
				}
			}
		}
		for (ArrayList<TableColumnHolder> list : hm.values()) {
			tableColumnHolders.addAll(list);
		}
		return tableColumnHolders;
	}

	private List<TableColumnHolder> createFromRuleSet(RuleSetBean ruleSet, CRFVersionBean crfVersion) {
		List<TableColumnHolder> tchs = new ArrayList<TableColumnHolder>();
		for (RuleSetRuleBean ruleSetRule : ruleSet.getRuleSetRules()) {
			String ruleExpression = ruleSetRule.getRuleBean().getExpression().getValue();
			String ruleName = ruleSetRule.getRuleBean().getName();
			TableColumnHolder tch = new TableColumnHolder(crfVersion.getName(), crfVersion.getId(), ruleName,
					ruleExpression, ruleSetRule.getActions(), ruleSetRule.getId());
			tchs.add(tch);

		}
		return tchs;
	}

	private String renderStudiesTable(List<StudyBean> studyBeans, HttpServletRequest request) {
		Collection<StudyRowContainer> items = getStudyRows(studyBeans);
		TableFacade tableFacade = createTableFacade("studies", request);
		tableFacade.setColumnProperties("name", "uniqueProtocolid", "actions");

		tableFacade.setItems(items);
		// Fix column titles
		HtmlTable table = (HtmlTable) tableFacade.getTable();

		HtmlRow row = table.getRow();
		SDVUtil sDVUtil = new SDVUtil();

		String[] colNames = new String[] { "name", "uniqueProtocolid", "actions" };
		sDVUtil.setHtmlCellEditors(tableFacade, colNames, true);

		HtmlColumn firstName = row.getColumn("name");
		firstName.setTitle("Study Name");

		HtmlColumn protocol = row.getColumn("uniqueProtocolid");
		protocol.setTitle("Unique Protocol Id");

		HtmlColumn actions = row.getColumn("actions");
		actions.setTitle("Actions");

		return tableFacade.render();
	}

	/*
	 * Generate the rows for the study table. Each row represents a StudyBean domain object.
	 */
	private Collection<StudyRowContainer> getStudyRows(List<StudyBean> studyBeans) {

		Collection<StudyRowContainer> allRows = new ArrayList<StudyRowContainer>();
		StudyRowContainer tempBean;
		StringBuilder actions = new StringBuilder("");
		for (StudyBean studBean : studyBeans) {
			tempBean = new StudyRowContainer();
			tempBean.setName(studBean.getName());
			tempBean.setUniqueProtocolid(studBean.getIdentifier());
			tempBean.setStudyBean(studBean);
			actions.append(StudyRowContainer.VIEW_STUDY_DETAILS_URL).append(studBean.getId())
					.append(StudyRowContainer.VIEW_STUDY_DETAILS_SUFFIX);
			tempBean.setActions(actions.toString());
			allRows.add(tempBean);

			actions = new StringBuilder("");
		}

		return allRows;
	}

	/*
	 * Fetch the studies associated with a CRF, via an event definition that uses the CRF.
	 */
	private List<StudyBean> findStudiesForCRFId(int crfId, StudyDAO studyDao) {
		List<StudyBean> studyBeans = new ArrayList<StudyBean>();
		if (crfId == 0 || studyDao == null) {
			return studyBeans;
		}

		ArrayList<Integer> studyIds = studyDao.getStudyIdsByCRF(crfId);
		StudyBean tempBean;

		for (Integer id : studyIds) {
			tempBean = (StudyBean) studyDao.findByPK(id);
			studyBeans.add(tempBean);

		}
		return studyBeans;
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return SecureController.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

	private List<StudyEventDefinitionBean> studyEventDefinitionListFilter(
			List<EventDefinitionCRFBean> eventDefinitionCrfList) {

		List<StudyEventDefinitionBean> studyEventDefinitionListFiltered = new ArrayList<StudyEventDefinitionBean>();
		StudyEventDefinitionDAO studyEventDefinitionDao = getStudyEventDefinitionDAO();
		UserAccountDAO userAccountDao = getUserAccountDAO();

		for (EventDefinitionCRFBean eventDefCrfBean : eventDefinitionCrfList) {
			if (!eventDefCrfBean.getStatus().isDeleted()) {

				StudyEventDefinitionBean studyEventDefinition = (StudyEventDefinitionBean) studyEventDefinitionDao
						.findByPK(eventDefCrfBean.getStudyEventDefinitionId());
				UserAccountBean userAccountBean = (UserAccountBean) userAccountDao.findByPK(studyEventDefinition
						.getOwnerId());
				studyEventDefinition.setOwner(userAccountBean);

				studyEventDefinitionListFiltered.add(studyEventDefinition);
			}
		}

		return studyEventDefinitionListFiltered;
	}

}
