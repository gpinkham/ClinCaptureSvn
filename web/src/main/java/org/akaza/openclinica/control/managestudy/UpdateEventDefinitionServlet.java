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

import com.clinovo.model.EDCItemMetadata;
import com.clinovo.util.RequestUtil;
import com.clinovo.util.SignStateRestorer;
import com.clinovo.validator.EventDefinitionValidator;
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
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet handles update requests on study event definition bean properties and update/remove/restore requests on event
 * definition CRF beans, owned by study event definition bean.
 *
 */
@SuppressWarnings({"rawtypes", "serial", "unchecked"})
@Component
public class UpdateEventDefinitionServlet extends Controller {

	public static final int VALIDATION_MAX_CHARACTERS_NUMBER = 2000;
	public static final int VALIDATION_MAX_DIGIT_NUMBER = 3;

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);
		checkStudyLocked(Page.LIST_DEFINITION_SERVLET, getResPage().getString("current_study_locked"), request, response);
		if (ub.isSysAdmin() || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}
		addPageMessage(getResPage().getString("no_have_permission_to_update_study_event_definition") + "<br>"
				+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET,
				getResException().getString("not_study_director"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		checkReferenceVisit(request);
		String action = request.getParameter("action");
		if (StringUtil.isBlank(action)) {
			forwardPage(Page.UPDATE_EVENT_DEFINITION1, request, response);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				confirmDefinition(request, response);
			} else if ("submit".equalsIgnoreCase(action)) {
				submitDefinition(request, response);
			} else if ("addCrfs".equalsIgnoreCase(action)) {
				FormProcessor fp = new FormProcessor(request);
				StudyEventDefinitionBean sed = (StudyEventDefinitionBean) request.getSession()
						.getAttribute("definition");
				saveEventDefinitionToSession(sed, fp);
				saveEventDefinitionCRFsToSession(fp);
				String url = (String) fp.getRequest().getSession()
						.getAttribute(DefineStudyEventServlet.DEFINE_UPDATE_STUDY_EVENT_PAGE_2_URL);
				if (url != null && fp.getRequest().getQueryString() == null) {
					response.sendRedirect(fp.getRequest().getContextPath().concat("/AddCRFToDefinition?").concat(url));
				} else {
					forwardPage(Page.ADD_CRFTO_DEFINITION_SERVLET, request, response);
				}
			} else if ("configureItemLevelSDV".equalsIgnoreCase(action)) {
				FormProcessor fp = new FormProcessor(request);
				StudyEventDefinitionBean sed = (StudyEventDefinitionBean) request.getSession()
						.getAttribute("definition");
				saveEventDefinitionToSession(sed, fp);
				saveEventDefinitionCRFsToSession(fp);
				int edcId = fp.getInt("edcToConfigure");
				response.sendRedirect(fp.getRequest().getContextPath().concat("/pages/configureItemLevelSDV?").concat("edcId=" + edcId));
			} else {
				addPageMessage(getResPage().getString("updating_ED_is_cancelled"), request);
				clearSession(request.getSession());
				forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
			}
		}

	}

	private void confirmDefinition(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) request.getSession().getAttribute("definition");

		HashMap errors = EventDefinitionValidator.validate(getConfigurationDao(), getUserAccountDAO(),
				getCurrentStudy());

		if (!errors.isEmpty()) {
			StudyEventDefinitionBean sedForErrors = new StudyEventDefinitionBean();
			sedForErrors.setName(fp.getString("name"));
			sedForErrors.setDescription(fp.getString("description"));
			sedForErrors.setCategory(fp.getString("category"));
			sedForErrors.setType(fp.getString("type"));
			sedForErrors.setRepeating("true".equalsIgnoreCase(fp.getString("repeating")));
			sedForErrors.setMaxDay(fp.getInt("maxDay"));
			sedForErrors.setMinDay(fp.getInt("minDay"));
			sedForErrors.setScheduleDay(fp.getInt("schDay"));
			sedForErrors.setEmailDay(fp.getInt("emailDay"));
			sedForErrors.setReferenceVisit("true".equalsIgnoreCase(fp.getString("isReference")));
			request.getSession().setAttribute("userNameInsteadEmail", fp.getString("emailUser"));
			request.setAttribute("definition", sedForErrors);
			logger.info("has errors");
			request.setAttribute("formMessages", errors);
			forwardPage(Page.UPDATE_EVENT_DEFINITION1, request, response);
			return;
		} else {
			logger.info("no errors");
			saveEventDefinitionToSession(sed, fp);
		}

		saveEventDefinitionCRFsToSession(fp);

		forwardPage(Page.UPDATE_EVENT_DEFINITION_CONFIRM, request, response);
	}

	private void submitDefinition(HttpServletRequest request, HttpServletResponse response) throws Exception {

		StudyBean currentStudy = getCurrentStudy(request);
		UserAccountBean updater = getUserAccountBean(request);
		List<EventDefinitionCRFBean> eventDefinitionCRFsToUpdate = (List<EventDefinitionCRFBean>) request.getSession()
				.getAttribute("eventDefinitionCRFs");
		List<EventDefinitionCRFBean> childEventDefinitionCRFsToUpdate = (List<EventDefinitionCRFBean>) request
				.getSession().getAttribute("childEventDefCRFs");
		List<EventDefinitionCRFBean> oldEventDefinitionCRFs = (List<EventDefinitionCRFBean>) request.getSession()
				.getAttribute("oldEventDefinitionCRFs");

		Map<Integer, SignStateRestorer> signStateRestorerMap = (Map<Integer, SignStateRestorer>) request.getSession()
				.getAttribute("signStateRestorerMap");
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) request.getSession()
				.getAttribute("definition");

		HashMap<Integer, ArrayList<EDCItemMetadata>>  edcItemMetadataMap = (HashMap<Integer, ArrayList<EDCItemMetadata>>)
				request.getSession().getAttribute("edcItemMetadataMap");

		getEventDefinitionService().updateTheWholeStudyEventDefinition(currentStudy, updater, studyEventDefinitionBean,
				eventDefinitionCRFsToUpdate, childEventDefinitionCRFsToUpdate, oldEventDefinitionCRFs,
				signStateRestorerMap, edcItemMetadataMap);

		clearSession(request.getSession());
		addPageMessage(getResPage().getString("the_ED_has_been_updated_succesfully"), request);
		forwardPage(Page.LIST_DEFINITION_SERVLET, request, response);
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

		UserAccountBean userBean = (UserAccountBean) getUserAccountDAO().findByUserName(userName);
		return userBean.getId();
	}

	private void saveEventDefinitionCRFsToSession(FormProcessor fp) {

		ArrayList edcs = (ArrayList) fp.getRequest().getSession().getAttribute("eventDefinitionCRFs");
		CRFVersionDAO cvdao = new CRFVersionDAO(getDataSource());

		for (int i = 0; i < edcs.size(); i++) {
			EventDefinitionCRFBean edcBean = (EventDefinitionCRFBean) edcs.get(i);
			if (!edcBean.getStatus().equals(Status.DELETED) && !edcBean.getStatus().equals(Status.AUTO_DELETED)) {
				int defaultVersionId = fp.getInt("defaultVersionId" + i);
				edcBean.setDefaultVersionId(defaultVersionId);
				CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(edcBean.getDefaultVersionId());
				edcBean.setDefaultVersionName(defaultVersion.getName());

				String requiredCRF = fp.getString("requiredCRF" + i);
				String deQuality = fp.getString("deQuality" + i);
				String decisionCondition = fp.getString("decisionCondition" + i);
				String electronicSignature = fp.getString("electronicSignature" + i);
				String hideCRF = fp.getString("hideCRF" + i);
				String acceptNewCrfVersions = fp.getString("acceptNewCrfVersions" + i);
				int sdvId = fp.getInt("sdvOption" + i);
				String emailStep = fp.getString("emailOnStep" + i);
				String emailTo = fp.getString("mailTo" + i);
				String tabbingMode = fp.getString("tabbingMode" + i);
				int propagateChange = fp.getInt("propagateChange" + i);

				if (!StringUtil.isBlank(tabbingMode) && ("leftToRight".equalsIgnoreCase(tabbingMode.trim())
						|| "topToBottom".equalsIgnoreCase(tabbingMode.trim()))) {
					edcBean.setTabbingMode(tabbingMode);
				} else {
					edcBean.setTabbingMode("leftToRight");
				}

				if (!StringUtil.isBlank(acceptNewCrfVersions) && "yes".equalsIgnoreCase(acceptNewCrfVersions.trim())) {
					edcBean.setAcceptNewCrfVersions(true);
				} else {
					edcBean.setAcceptNewCrfVersions(false);
				}
				if (!StringUtil.isBlank(hideCRF) && "yes".equalsIgnoreCase(hideCRF.trim())) {
					edcBean.setHideCrf(true);
				} else {
					edcBean.setHideCrf(false);
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

				if (!StringUtil.isBlank(electronicSignature) && "yes".equalsIgnoreCase(electronicSignature.trim())) {
					edcBean.setElectronicSignature(true);
				} else {
					edcBean.setElectronicSignature(false);
				}

				if (!StringUtil.isBlank(decisionCondition) && "yes".equalsIgnoreCase(decisionCondition.trim())) {
					edcBean.setDecisionCondition(true);
				} else {
					edcBean.setDecisionCondition(false);
				}

				if (!StringUtil.isBlank(deQuality) && "evaluation".equalsIgnoreCase(deQuality.trim())) {
					edcBean.setEvaluatedCRF(true);
				} else {
					edcBean.setEvaluatedCRF(false);
				}

				if (sdvId > 0 && (edcBean.getSourceDataVerification() == null
						|| sdvId != edcBean.getSourceDataVerification().getCode())) {
					edcBean.setSourceDataVerification(SourceDataVerification.getByCode(sdvId));
				}

				if (!StringUtil.isBlank(emailTo)) {
					edcBean.setEmailTo(emailTo);
				} else {
					edcBean.setEmailTo("");
				}

				if (!StringUtil.isBlank(emailStep)) {
					edcBean.setEmailStep(emailStep);
				} else {
					edcBean.setEmailStep("");
				}

				edcBean.setPropagateChange(propagateChange);
			}

		}
		fp.getRequest().getSession().setAttribute("eventDefinitionCRFs", edcs);
	}

	private void saveEventDefinitionToSession(StudyEventDefinitionBean sed, FormProcessor fp) {

		sed.setName(fp.getString("name"));
		sed.setRepeating("true".equalsIgnoreCase(fp.getString("repeating")));
		sed.setCategory(fp.getString("category"));
		sed.setDescription(fp.getString("description"));
		sed.setType(fp.getString("type"));
		sed.setMaxDay(fp.getInt("maxDay"));
		sed.setMinDay(fp.getInt("minDay"));
		sed.setScheduleDay(fp.getInt("schDay"));
		fp.getRequest().getSession().setAttribute("userNameInsteadEmail", fp.getString("emailUser"));
		int userId = getIdByUserName(fp.getString("emailUser"));
		sed.setUserEmailId(userId != 0 ? userId : 1);
		sed.setEmailDay(fp.getInt("emailDay"));
		sed.setReferenceVisit("true".equalsIgnoreCase(fp.getString("isReference")));
		fp.getRequest().getSession().setAttribute("definition", sed);
	}

	/**
	 * Clears session bean after study event definition bean update is finished.
	 *
	 * @param session
	 *            HttpSession current user session bean.
	 */
	public static void clearSession(HttpSession session) {

		session.removeAttribute("definition");
		session.removeAttribute("eventDefinitionCRFs");
		session.removeAttribute("oldEventDefinitionCRFs");
		session.removeAttribute("childEventDefCRFs");
		session.removeAttribute("tmpCRFIdMap");
		session.removeAttribute("crfsWithVersion");
		session.removeAttribute("showCalendaredVisitBox");
		session.removeAttribute("changedReference");
		session.removeAttribute("userNameInsteadEmail");
		session.removeAttribute("sdvOptions");
		session.removeAttribute("crfNameToEdcMap");
		session.removeAttribute(DefineStudyEventServlet.DEFINE_UPDATE_STUDY_EVENT_PAGE_2_URL);
		session.removeAttribute("userNameInsteadEmail");
		session.removeAttribute("edcItemMetadataMap");
		session.removeAttribute("edcSDVMap");
		RequestUtil.getRequest().removeAttribute("formWithStateFlag");
	}

}
