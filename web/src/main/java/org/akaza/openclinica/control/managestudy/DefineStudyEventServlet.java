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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.akaza.openclinica.bean.admin.CRFBean;
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
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.CRFRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.springframework.stereotype.Component;

import com.clinovo.validator.EventDefinitionValidator;

/**
 * The servlet for creating event definition of user's current active study.
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
@Component
public class DefineStudyEventServlet extends Controller {

	public static final int FIVE = 5;
	public static final int INT_2000 = 2000;
	public static final int INT_3 = 3;
	public static final String DEFINE_UPDATE_STUDY_EVENT_PAGE_2_URL = "defineUpdateStudyEventPage2Url";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.LIST_DEFINITION_SERVLET, respage.getString("current_study_locked"), request, response);

		if (currentStudy.getParentStudyId() > 0) {
			addPageMessage(respage.getString("SED_may_only_added_top_level")
					+ respage.getString("please_contact_sysadmin_questions"), request);
			throw new InsufficientPermissionException(Page.STUDY_EVENT_DEFINITION_LIST,
					resexception.getString("not_top_study"), "1");
		}

		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_persmission_add_SED_to_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.STUDY_EVENT_DEFINITION_LIST,
				resexception.getString("not_study_director"), "1");
	}

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
			crfsWithVersion = (ArrayList) getCRFDAO().findAllActiveCrfs();
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
								submitDefinition(request);
								StudyEventDefinitionBean sed = new StudyEventDefinitionBean();
								sed.setStudyId(currentStudy.getId());
								session.setAttribute("definition", sed);
								forwardPage(Page.DEFINE_STUDY_EVENT1, request, response);
							}
						}
					} catch (NumberFormatException e) {
						logger.error(e.getMessage());
						addPageMessage(respage.getString("the_new_event_definition_creation_cancelled"), request);
						forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
					} catch (NullPointerException e) {
						logger.error(e.getMessage());
						addPageMessage(respage.getString("the_new_event_definition_creation_cancelled"), request);
						forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
					}

				} else if ("next".equalsIgnoreCase(actionName)) {
					Integer pageNumber = Integer.valueOf(request.getParameter("pageNum"));
					if (pageNumber != null) {
						if (pageNumber == 2) {
							String nextListPage = request.getParameter("next_list_page");
							if (nextListPage != null && nextListPage.equalsIgnoreCase("true")) {
								logger.trace("confirmDefinition1 step 1");
								confirmDefinition1(request, response);
							} else {
								logger.trace("confirmDefinition2 step 2");
								confirmDefinition2(request, response, false);
							}
						} else {
							logger.trace("confirmDefinition1 step 3");
							confirmDefinition1(request, response);
						}
					} else {
						logger.trace("forwardPage step 4");
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

	private void confirmDefinition1(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.getSession().setAttribute("definition", createStudyEventDefinition(request));

		HashMap errors = EventDefinitionValidator.validate(getConfigurationDao(), getUserAccountDAO(),
				getCurrentStudy());

		if (errors.isEmpty()) {
			logger.info("no errors in the first section");
			prepareServletForStepTwo(new FormProcessor(request), response, true);
		} else {
			logger.trace("has validation errors in the first section");
			request.setAttribute("formMessages", errors);
			setOrGetDefinition(request);
			forwardPage(Page.DEFINE_STUDY_EVENT1, request, response);
		}
	}

	private boolean shouldRedirect(FormProcessor fp, HttpServletResponse response) throws IOException {
		boolean result = false;
		int pageNum = fp.getInt("pageNum");
		String action = fp.getString("actionName");
		String url = (String) fp.getRequest().getSession().getAttribute(DEFINE_UPDATE_STUDY_EVENT_PAGE_2_URL);
		if (url != null && fp.getRequest().getQueryString() == null) {
			if (action.equals("next") && pageNum == 1) {
				String type = fp.getString("type");
				boolean changed = url.contains("formWithStateFlag=changed");
				String tail = url.replaceAll(".*&ebl_page=", "&ebl_page=");
				url = "submitted=1".concat(changed ? "&formWithStateFlag=changed" : "").concat("&name=")
						.concat(fp.getString("name")).concat("&actionName=next").concat("&description=")
						.concat(fp.getString("description")).concat("&type=").concat(fp.getString("type"))
						.concat(!type.equalsIgnoreCase("calendared_visit")
								? "&repeating=".concat(fp.getString("repeating"))
								: "")
						.concat("&category=").concat(fp.getString("category"));
				if (type.equalsIgnoreCase("calendared_visit")) {
					String isReference = fp.getString("isReference");
					url = url.concat("&isReference=").concat(isReference).concat("&schDay=")
							.concat(fp.getString("schDay")).concat("&maxDay=").concat(fp.getString("maxDay"))
							.concat("&minDay=").concat(fp.getString("minDay")).concat("&emailDay=")
							.concat(fp.getString("emailDay")).concat(!isReference.equalsIgnoreCase("true")
									? "&emailUser=".concat(fp.getString("emailUser"))
									: "");
				}
				url = url.concat("&pageNum=1").concat(tail);
			}
			response.sendRedirect(fp.getRequest().getContextPath().concat("/DefineStudyEvent?").concat(url));
			result = true;
		}
		return result;
	}

	private void prepareServletForStepTwo(FormProcessor fp, HttpServletResponse response, boolean checkForm)
			throws IOException {
		if (!shouldRedirect(fp, response)) {
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
						// Removing the elements from session which has been deselected.
						if (tmpCRFIdMap.containsKey(id)) {
							tmpCRFIdMap.remove(id);
						}
					}
				}
			}
			logger.trace("about to set tmpCRFIdMap " + tmpCRFIdMap.toString());
			fp.getRequest().getSession().setAttribute("tmpCRFIdMap", tmpCRFIdMap);

			EntityBeanTable table = getEntityBeanTable();
			ArrayList allRows = CRFRow.generateRowsFromBeans(crfsWithVersion);
			String[] columns = {resword.getString("CRF_name"), resword.getString("date_created"),
					resword.getString("owner"), resword.getString("date_updated"), resword.getString("last_updated_by"),
					resword.getString("selected")};
			table.setColumns(new ArrayList(Arrays.asList(columns)));
			table.hideColumnLink(FIVE);
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
			if (def1.getType().equalsIgnoreCase("calendared_visit")) {
				args.put("schDay", Integer.toString(def1.getScheduleDay()));
				args.put("maxDay", Integer.toString(def1.getMaxDay()));
				args.put("minDay", Integer.toString(def1.getMinDay()));
				args.put("emailDay", Integer.toString(def1.getEmailDay()));
				if ("true".equals(Boolean.toString(def1.getReferenceVisit()))) {
					args.put("isReference", "true");
				} else {
					args.put("isReference", "");
					args.put("emailUser", userBean.getName());
				}
			}
			args.put("formWithStateFlag", fp.getRequest().getParameter("formWithStateFlag"));
			table.setQuery("DefineStudyEvent", args);
			table.setRows(allRows);
			table.computeDisplay();

			fp.getRequest().setAttribute("table", table);
			String queryString = fp.getRequest().getQueryString();
			if (queryString != null) {
				String filterKeyword = fp.getRequest().getParameter("ebl_filterKeyword");
				fp.getRequest().getSession().setAttribute(DEFINE_UPDATE_STUDY_EVENT_PAGE_2_URL,
						queryString.concat(filterKeyword != null ? "&ebl_filterKeyword=".concat(filterKeyword) : ""));
			}
			forwardPage(Page.DEFINE_STUDY_EVENT2, fp.getRequest(), response);
		}
	}

	/**
	 * Validates the entire definition.
	 * 
	 * @param request
	 *            the incoming request
	 * @param response
	 *            the response to redirect to
	 * @throws Exception
	 *             an Exception
	 */
	private void confirmWholeDefinition(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		FormProcessor fp = new FormProcessor(request);
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) request.getSession().getAttribute("definition");
		ArrayList eventDefinitionCRFs = new ArrayList();
		CRFVersionDAO cvdao = getCRFVersionDAO();
		Map crfNameToEdcMap = (HashMap) request.getSession().getAttribute("crfNameToEdcMap");
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
			String acceptNewCrfVersions = fp.getString("acceptNewCrfVersions" + i);
			String deQuality = fp.getString("deQuality" + i);
			String decisionCondition = fp.getString("decisionCondition" + i);
			String electronicSignature = fp.getString("electronicSignature" + i);
			String emailCRFTo = fp.getString("mailTo" + i);
			String emailOnStep = fp.getString("emailOnStep" + i);
			String tabbingMode = fp.getString("tabbingMode" + i);

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
			if (!StringUtil.isBlank(acceptNewCrfVersions) && "yes".equalsIgnoreCase(acceptNewCrfVersions.trim())) {
				edcBean.setAcceptNewCrfVersions(true);
			} else {
				edcBean.setAcceptNewCrfVersions(false);
			}
			if (!StringUtil.isBlank(requiredCRF) && "yes".equalsIgnoreCase(requiredCRF.trim())) {
				edcBean.setRequiredCRF(true);
			} else {
				edcBean.setRequiredCRF(false);
			}
			if (!StringUtil.isBlank(deQuality) && "dde".equalsIgnoreCase(deQuality.trim())) {
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
			if (!StringUtil.isBlank(emailCRFTo)) {
				edcBean.setEmailTo(emailCRFTo);
			} else {
				edcBean.setEmailTo("");
			}
			if (!StringUtil.isBlank(emailOnStep)) {
				edcBean.setEmailStep(emailOnStep);
			} else {
				edcBean.setEmailStep("");
			}
			if (!StringUtil.isBlank(deQuality) && "evaluation".equalsIgnoreCase(deQuality.trim())) {
				edcBean.setEvaluatedCRF(true);
			} else {
				edcBean.setEvaluatedCRF(false);
			}
			if (!StringUtil.isBlank(tabbingMode) && ("leftToRight".equalsIgnoreCase(tabbingMode.trim())
					|| "topToBottom".equalsIgnoreCase(tabbingMode.trim()))) {
				edcBean.setTabbingMode(tabbingMode);
			} else {
				edcBean.setTabbingMode("leftToRight");
			}

			edcBean.setNullValues("");
			edcBean.setStudyId(ub.getActiveStudyId());
			eventDefinitionCRFs.add(edcBean);
			crfNameToEdcMap.put(edcBean.getCrfName(), edcBean);
		}
		request.setAttribute("eventDefinitionCRFs", eventDefinitionCRFs);
		request.getSession().setAttribute("edCRFs", eventDefinitionCRFs);
		forwardPage(Page.DEFINE_STUDY_EVENT_CONFIRM, request, response);

	}

	/**
	 * Constructs study event definition bean from request.
	 * 
	 * @param request
	 *            the incoming request
	 * @return <code>StudyEventDefinitionBean</code> bean that will be displayed on UX
	 */
	private StudyEventDefinitionBean createStudyEventDefinition(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) request.getSession().getAttribute("definition");
		sed.setName(fp.getString("name"));
		sed.setRepeating("true".equalsIgnoreCase(fp.getString("repeating")));
		sed.setCategory(fp.getString("category"));
		sed.setDescription(fp.getString("description"));
		sed.setType(fp.getString("type"));
		sed.setMaxDay(fp.getInt("maxDay"));
		sed.setMinDay(fp.getInt("minDay"));
		sed.setScheduleDay(fp.getInt("schDay"));
		int userId = getIdByUserName(fp.getString("emailUser"));
		sed.setUserEmailId(userId != 0 ? userId : 1);
		sed.setEmailDay(fp.getInt("emailDay"));
		sed.setReferenceVisit("true".equalsIgnoreCase(fp.getString("isReference")));
		return sed;
	}

	private void confirmDefinition2(HttpServletRequest request, HttpServletResponse response, boolean isBack)
			throws Exception {

		FormProcessor fp = new FormProcessor(request);
		CRFVersionDAO vdao = getCRFVersionDAO();
		ArrayList crfArray = new ArrayList();
		Map tmpCRFIdMap = (HashMap) request.getSession().getAttribute("tmpCRFIdMap");
		if (tmpCRFIdMap == null) {
			tmpCRFIdMap = new HashMap();
		}
		Map crfNameToEdcMap = (HashMap) request.getSession().getAttribute("crfNameToEdcMap");
		if (crfNameToEdcMap == null) {
			crfNameToEdcMap = new HashMap();
			request.getSession().setAttribute("crfNameToEdcMap", crfNameToEdcMap);
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
				// only find active versions
				ArrayList versions = (ArrayList) vdao.findAllActiveByCRF(cb.getId());
				cb.setVersions(versions);
				SourceDataVerification.fillSDVStatuses(cb.getSdvOptions());
				crfArray.add(cb);
				if (crfNameToEdcMap.get(cb.getName()) == null) {
					crfNameToEdcMap.put(cb.getName(), new EventDefinitionCRFBean());
				}
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

				ArrayList versions = (ArrayList) vdao.findAllActiveByCRF(cb.getId());
				cb.setVersions(versions);
				SourceDataVerification.fillSDVStatuses(cb.getSdvOptions());
				crfArray.add(cb);
				if (crfNameToEdcMap.get(cb.getName()) == null) {
					crfNameToEdcMap.put(cb.getName(), new EventDefinitionCRFBean());
				}
			}
		}
		logger.info("crf array after *second* pass: " + crfArray.toString());
		logger.trace("about to set tmpCRFIdMap " + tmpCRFIdMap.toString());
		request.getSession().setAttribute("tmpCRFIdMap", tmpCRFIdMap);

		if (crfArray.size() == 0 && !isBack) {
			addPageMessage(respage.getString("no_CRF_selected_for_definition_add_later"), request);
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) request.getSession().getAttribute("definition");
			sed.setCrfs(new ArrayList());
			request.getSession().setAttribute("definition", sed);
			forwardPage(Page.DEFINE_STUDY_EVENT_CONFIRM, request, response);
		} else {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) request.getSession().getAttribute("definition");

			logger.info("setting crfs into defintion to review: " + crfArray.toString());
			sed.setCrfs(crfArray);

			request.getSession().setAttribute("definition", sed);

			logger.info("forwarding to defineStudyEvent3.jsp");
			forwardPage(Page.DEFINE_STUDY_EVENT3, request, response);
		}
	}

	private void submitDefinition(HttpServletRequest request) {
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
			createChildEdcs(edc, currentStudy);
		}

		request.removeAttribute("formWithStateFlag");
		request.getSession().removeAttribute("definition");
		request.getSession().removeAttribute("edCRFs");
		request.getSession().removeAttribute("crfsWithVersion");
		request.getSession().removeAttribute("tmpCRFIdMap");
		request.getSession().removeAttribute("referenceVisitAlreadyExist");
		request.getSession().removeAttribute("maxDay");
		request.getSession().removeAttribute("minDay");
		request.getSession().removeAttribute("schDay");
		request.getSession().removeAttribute("emailUser");
		request.getSession().removeAttribute("emailDay");
		request.getSession().removeAttribute("isReference");
		request.getSession().removeAttribute("crfNameToEdcMap");
		request.getSession().removeAttribute(DEFINE_UPDATE_STUDY_EVENT_PAGE_2_URL);
		checkReferenceVisit(request);
		addPageMessage(respage.getString("the_new_event_definition_created_succesfully"), request);

	}

	private void createChildEdcs(EventDefinitionCRFBean createdEdc, StudyBean currentStudy) {
		StudyDAO studyDao = new StudyDAO(getDataSource());
		EventDefinitionCRFDAO cdao = new EventDefinitionCRFDAO(getDataSource());
		Collection<Integer> siteIds = studyDao.findAllSiteIdsByStudy(currentStudy);
		siteIds.remove(currentStudy.getId());
		int parentId = createdEdc.getId();

		for (int siteId : siteIds) {
			EventDefinitionCRFBean childEdc = createdEdc;
			childEdc.setStudyId(siteId);
			childEdc.setParentId(parentId);
			cdao.create(childEdc);
		}
	}

	private void checkReferenceVisit(HttpServletRequest request) {
		StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
		ArrayList<StudyEventDefinitionBean> definitions = seddao.findReferenceVisitBeans();
		for (StudyEventDefinitionBean studyEventDefinition : definitions) {
			if (studyEventDefinition.getReferenceVisit()) {
				logger.trace("Reference visit already exist");
				request.getSession().setAttribute("referenceVisitAlreadyExist", true);
				break;
			}
		}
	}

	private int getIdByUserName(String userName) {
		UserAccountDAO uadao = getUserAccountDAO();
		UserAccountBean userBean = (UserAccountBean) uadao.findByUserName(userName);
		return userBean.getId();
	}
}
