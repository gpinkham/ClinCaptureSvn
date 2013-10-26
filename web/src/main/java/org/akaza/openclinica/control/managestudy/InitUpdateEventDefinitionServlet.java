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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * Prepares to update study event definition
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class InitUpdateEventDefinitionServlet extends SecureController {

	/**
	 * Checks whether the user has the correct privilege
	 */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		checkStudyLocked(Page.LIST_DEFINITION_SERVLET, respage.getString("current_study_locked"));
		if (ub.isSysAdmin()) {
			return;
		}

		StudyEventDAO sdao = new StudyEventDAO(sm.getDataSource());
		// get current studyid
		int studyId = currentStudy.getId();

		if (ub.hasRoleInStudy(studyId)) {
			Role r = ub.getRoleByStudy(studyId).getRole();
			if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR)) {
				return;
			} else {
				addPageMessage(respage.getString("no_have_permission_to_update_study_event_definition")
						+ respage.getString("please_contact_sysadmin_questions"));
				throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET,
						resexception.getString("not_study_director"), "1");

			}
		}

		// To Do: the following code doesn't apply to admin for now
		String idString = request.getParameter("id");
		int defId = Integer.valueOf(idString.trim()).intValue();
		logger.info("defId" + defId);
		ArrayList events = (ArrayList) sdao.findAllByDefinition(defId);
		if (events != null && events.size() > 0) {
			logger.info("has events");
			for (int i = 0; i < events.size(); i++) {
				StudyEventBean sb = (StudyEventBean) events.get(i);
				if (!sb.getStatus().equals(Status.DELETED) && !sb.getStatus().equals(Status.AUTO_DELETED)) {
					logger.info("found one event");
					addPageMessage(respage.getString("sorry_but_at_this_time_may_not_modufy_SED"));
					throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET,
							resexception.getString("not_unpopulated"), "1");
				}
			}
		}

	}

	@Override
	public void processRequest() throws Exception {
		setUserNameInsteadEmail();
		StudyEventDefinitionDAO sdao = new StudyEventDefinitionDAO(sm.getDataSource());
		String idString = request.getParameter("id");
		logger.info("definition id: " + idString);
		if (StringUtil.isBlank(idString)) {
			addPageMessage(respage.getString("please_choose_a_definition_to_edit"));
			forwardPage(Page.LIST_DEFINITION_SERVLET);
		} else {
			// definition id
			int defId = Integer.valueOf(idString.trim()).intValue();
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) sdao.findByPK(defId);

			if (currentStudy.getId() != sed.getStudyId()) {
				addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
						+ respage.getString("change_active_study_or_contact"));
				forwardPage(Page.MENU_SERVLET);
				return;
			}

			EventDefinitionCRFDAO edao = new EventDefinitionCRFDAO(sm.getDataSource());
			ArrayList eventDefinitionCRFs = (ArrayList) edao.findAllParentsByDefinition(defId);

			CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
			CRFDAO cdao = new CRFDAO(sm.getDataSource());
			ArrayList newEventDefinitionCRFs = new ArrayList();
			for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
				EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
				ArrayList versions = (ArrayList) cvdao.findAllActiveByCRF(edc.getCrfId());
				edc.setVersions(versions);
				CRFBean crf = (CRFBean) cdao.findByPK(edc.getCrfId());
				edc.setCrfName(crf.getName());
				edc.setCrf(crf);
				// TO DO: use a better way on JSP page,eg.function tag
				edc.setNullFlags(processNullValues(edc));

				CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(edc.getDefaultVersionId());
				edc.setDefaultVersionName(defaultVersion.getName());
				newEventDefinitionCRFs.add(edc);
			}

			session.setAttribute("definition", sed);
			session.setAttribute("eventDefinitionCRFs", newEventDefinitionCRFs);
			// changed above to new list because static, in-place updating is updating all EDCs

			addSDVstatuses();

			forwardPage(Page.UPDATE_EVENT_DEFINITION1);
		}

	}

	private HashMap processNullValues(EventDefinitionCRFBean edc) {
		HashMap flags = new LinkedHashMap();
		String s = "";// edc.getNullValues();
		for (int j = 0; j < edc.getNullValuesList().size(); j++) {
			NullValue nv1 = (NullValue) edc.getNullValuesList().get(j);
			s = s + nv1.getName().toUpperCase() + ",";
		}
		logger.info("********:" + s);
		if (s != null) {
			for (int i = 1; i <= NullValue.toArrayList().size(); i++) {
				String nv = NullValue.get(i).getName().toUpperCase();
				// if (s.indexOf(nv) >= 0) {
				// indexOf won't save us
				// because NA and NASK will come back both positive, for example
				// rather, we need a regexp here
				Pattern p = Pattern.compile(nv + "\\W");
				// find our word with a non-word character after it (,)
				Matcher m = p.matcher(s);
				if (m.find()) {
					flags.put(nv, "1");
					logger.info("********1:" + nv + " found at " + m.start() + ", " + m.end());
				} else {
					flags.put(nv, "0");
					logger.info("********0:" + nv);
				}

			}
		}

		return flags;
	}
	
	private void setUserNameInsteadEmail() {
		String sedId = request.getParameter("id");
		int eventId = Integer.valueOf(sedId).intValue();
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
		StudyEventDefinitionBean sedBean = (StudyEventDefinitionBean) seddao.findByPK(eventId);
		int userId = sedBean.getUserEmailId();
		UserAccountDAO uadao = new UserAccountDAO(sm.getDataSource());
		UserAccountBean userBean = (UserAccountBean) uadao.findByPK(userId);
		if(userBean.getName() != null) {
			session.setAttribute("userNameInsteadEmail", userBean.getName());	
		} else {
			session.setAttribute("userNameInsteadEmail", "Not found in the db");	
		}
	}
	
	private void addSDVstatuses(){
		ArrayList<String> sdvOptions = new ArrayList<String>();
		sdvOptions.add(SourceDataVerification.AllREQUIRED.toString());
		sdvOptions.add(SourceDataVerification.PARTIALREQUIRED.toString());
		sdvOptions.add(SourceDataVerification.NOTREQUIRED.toString());
		sdvOptions.add(SourceDataVerification.NOTAPPLICABLE.toString());
		session.setAttribute("sdvOptions", sdvOptions);
	}
}
