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

package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.PrintCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.DataEntryServlet;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.display.DisplaySectionBeanHandler;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({"rawtypes", "unchecked",  "serial"})
@Component
public class PrintSubjectCaseBookServlet extends DataEntryServlet {

	@Override
	public void mayProceed(HttpServletRequest request,
			HttpServletResponse response)
			throws InsufficientPermissionException {

		UserAccountBean ub = (UserAccountBean) request.getSession()
				.getAttribute(USER_BEAN_NAME);
		StudyUserRoleBean currentRole = (StudyUserRoleBean) request
				.getSession().getAttribute("userRole");

		if (ub.isSysAdmin()) {
			return;
		}

		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"),
				request);

		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				resexception.getString("not_director"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int subjectId = fp.getInt("subjectId", true);

		System.out.println("Printing casebook for subject #" + subjectId);

        SessionManager sm = (SessionManager) getSessionManager(request);
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		SectionBean sb = (SectionBean) request.getAttribute(SECTION_BEAN);
		String age = "";
		StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
		EventCRFDAO edcdao = new EventCRFDAO(sm.getDataSource());
		CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
		CRFDAO cdao = new CRFDAO(sm.getDataSource());
		SectionDAO sdao = new SectionDAO(sm.getDataSource());
		ItemGroupDAO itemGroupDao = new ItemGroupDAO(sm.getDataSource());
		StudySubjectDAO ssdao = new StudySubjectDAO(getDataSource());
		SubjectDAO subjectDao = new SubjectDAO(getDataSource());
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(getDataSource());
		StudySubjectBean sub = (StudySubjectBean) ssdao.findByPK(subjectId);
		SubjectBean subject = (SubjectBean) subjectDao.findByPK(subjectId);
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		ArrayList<SectionBean> allSectionBeans = new ArrayList<SectionBean>();
		ArrayList<EventCRFBean> edcs = new ArrayList();
		ArrayList sectionBeans = new ArrayList();

		edcs.addAll(edcdao.getEventCRFsByStudySubjectExceptInvalid(subjectId));

		if (currentStudy.getStudyParameterConfig().getCollectDob().equals("1")) {
			age = Utils.getInstance().processAge(sub.getEnrollmentDate(),
					subject.getDateOfBirth());
		}

		String isIE = fp.getString("ie");

		if ("y".equalsIgnoreCase(isIE)) {
			request.setAttribute("isInternetExplorer", "true");
		}

		StudyDAO studydao = new StudyDAO(getDataSource());
		StudyBean study = (StudyBean) studydao.findByPK(sub.getStudyId());

		if (study.getParentStudyId() > 0) {

			// this is a site,find parent
			StudyBean parentStudy = (StudyBean) studydao.findByPK(study
					.getParentStudyId());

			request.setAttribute("studyTitle", parentStudy.getName() + " - "
					+ study.getName());
		} else {
			request.setAttribute("studyTitle", study.getName());
		}

		boolean isSubmitted = false;
		Map sedCrfBeans = null;
		CRFVersionBean crfverBean;

		ecb = new EventCRFBean();

		for (int i = 0; i < edcs.size(); i++) {
			ecb = edcs.get(i);

			if (sedCrfBeans == null) {
				sedCrfBeans = new LinkedHashMap();
			}

			crfverBean = (CRFVersionBean) cvdao.findByPK(ecb.getCRFVersionId());

			String crfStatus = resword.getString(ecb.getStage().getNameRaw());

			System.out.println("I have found eCRF with status: " + crfStatus);

			CRFBean crfBean = cdao.findByVersionId(ecb.getCRFVersionId());
			List<ItemGroupBean> itemGroupBeans = itemGroupDao
					.findOnlyGroupsByCRFVersionID(ecb.getCRFVersionId());
			ArrayList sects = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());

			allSectionBeans = new ArrayList<SectionBean>();

			for (int h = 0; h < sects.size(); h++) {
				sb = (SectionBean) sects.get(h);

				int sectId = sb.getId();

				if (sectId > 0) {
					allSectionBeans.add((SectionBean) sdao.findByPK(sectId));
				}
			}

			if (itemGroupBeans.size() > 0) {
				StudyEventBean sedBean = (StudyEventBean) sedao.findByPK(ecb
						.getStudyEventId());
				StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
						.findByPK(sedBean.getStudyEventDefinitionId());

				sedBean.setStudyEventDefinition(sed);

				DisplaySectionBeanHandler handler = new DisplaySectionBeanHandler(
						true, getDataSource(), getServletContext());

				handler.setCrfVersionId(crfverBean.getId());
				handler.setEventCRFId(ecb.getId());
				crfverBean = (CRFVersionBean) cvdao
						.findByPK(crfverBean.getId());
				crfBean = cdao.findByVersionId(ecb.getCRFVersionId());
				request.setAttribute("dataInvolved", "true");

				List<DisplaySectionBean> displaySectionBeans = handler
						.getDisplaySectionBeans();
				PrintCRFBean printCrfBean = new PrintCRFBean();

				printCrfBean.setDisplaySectionBeans(displaySectionBeans);
				printCrfBean.setCrfVersionBean(crfverBean);
				printCrfBean.setCrfBean(crfBean);
				printCrfBean.setEventCrfBean(ecb);
				printCrfBean.setStudyEventBean(sedBean);
				printCrfBean.setGrouped(true);

				List list = (ArrayList) sedCrfBeans.get(sedBean);

				if (list == null) {
					list = new ArrayList();
				}

				list.add(printCrfBean);
				sedCrfBeans.put(sedBean, list);

				continue;
			} else {
				StudyEventBean sedBean = (StudyEventBean) sedao.findByPK(ecb
						.getStudyEventId());
				StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
						.findByPK(sedBean.getStudyEventDefinitionId());

				sedBean.setStudyEventDefinition(sed);
				request.setAttribute("studyEvent", sedBean);
				request.setAttribute(INPUT_EVENT_CRF, ecb);
				request.setAttribute(SECTION_BEAN, sb);
				request.setAttribute(ALL_SECTION_BEANS, allSectionBeans);
				sectionBeans = super.getAllDisplayBeans(request);

				DisplaySectionBean dsb = super.getDisplayBean(false, false,
						request, isSubmitted);
				PrintCRFBean printCrfBean = new PrintCRFBean();

				printCrfBean.setAllSections(sectionBeans);
				printCrfBean.setDisplaySectionBean(dsb);
				printCrfBean.setEventCrfBean(ecb);
				printCrfBean.setCrfVersionBean(crfverBean);
				printCrfBean.setCrfBean(crfBean);
				printCrfBean.setStudyEventBean(sedBean);
				printCrfBean.setGrouped(false);

				List list = (ArrayList) sedCrfBeans.get(sedBean);

				if (list == null) {
					list = new ArrayList();
				}

				list.add(printCrfBean);
				sedCrfBeans.put(sedBean, list);
			}
		}

		request.setAttribute("studySubject", sub);
		request.setAttribute("subject", subject);
		request.setAttribute("age", age);
		request.setAttribute("sedCrfBeans", sedCrfBeans);
		request.setAttribute("studyName", currentStudy.getName());
		forwardPage(Page.VIEW_SUBJECT_CASE_BOOK_PRINT, request, response);
	}

	@Override
	protected Status getBlankItemStatus() {
		return Status.AVAILABLE;
	}

	@Override
	protected Status getNonBlankItemStatus(HttpServletRequest request) {
		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) request
				.getAttribute(EVENT_DEF_CRF_BEAN);

		return edcb.isDoubleEntry() ? Status.PENDING : Status.UNAVAILABLE;
	}

	@Override
	protected String getEventCRFAnnotations(HttpServletRequest request) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		return ecb.getAnnotations();
	}

	@Override
	protected void setEventCRFAnnotations(String annotations,
			HttpServletRequest request) {
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
	protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v,
			DisplayItemBean dib, String inputName, HttpServletRequest request) {
		org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata()
				.getResponseSet().getResponseType();

		// note that this step sets us up both for
		// displaying the data on the form again, in the event of an error
		// and sending the data to the database, in the event of no error
		dib = loadFormValue(dib, request);

		// types TEL and ED are not supported yet
		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)) {
			dib = validateDisplayItemBeanText(v, dib, inputName, request);
		} else if (rt
				.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
			dib = validateDisplayItemBeanSingleCV(v, dib, inputName);
		} else if (rt
				.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
			dib = validateDisplayItemBeanMultipleCV(v, dib, inputName);
		} else if (rt
				.equals(org.akaza.openclinica.bean.core.ResponseType.CALCULATION)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.GROUP_CALCULATION)) {

			// for now, treat calculation like any other text input --
			// eventually this might need to be customized
			dib = validateDisplayItemBeanText(v, dib, inputName, request);
		}

		return dib;
	}

	@Override
	protected List<DisplayItemGroupBean> validateDisplayItemGroupBean(
			DiscrepancyValidator v, DisplayItemGroupBean digb,
			List<DisplayItemGroupBean> digbs,
			List<DisplayItemGroupBean> formGroups, HttpServletRequest request,
			HttpServletResponse response) {
		return formGroups;
	}

	@Override
	protected boolean shouldLoadDBValues(DisplayItemBean dib) {
		return true;
	}

	@Override
	protected boolean shouldRunRules() {
		return false;
	}

	@Override
	protected boolean isAdministrativeEditing() {
		return false; // To change body of implemented methods use File |
						// Settings | File Templates.
	}

	@Override
	protected boolean isAdminForcedReasonForChange(HttpServletRequest request) {
		return false; // To change body of implemented methods use File |
						// Settings | File Templates.
	}
}
