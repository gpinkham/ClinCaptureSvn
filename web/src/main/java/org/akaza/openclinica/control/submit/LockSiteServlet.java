package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.RememberLastPage;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

@SuppressWarnings({"rawtypes", "unchecked",  "serial"})
public class LockSiteServlet extends SecureController {

	public static final String REFERER_URL = "refererUrl";
	public static final String REFERER = "referer";
	public static final String LOCK_SITE = "LockSite";

	@Override
	protected void mayProceed() throws InsufficientPermissionException {
		request.getLocale();

		if (ub.isSysAdmin()) {
			return;
		}

		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	@Override
	protected void processRequest() throws Exception {
		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
		StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
		EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());

		String referer = request.getHeader(REFERER);
		if (referer != null && !referer.contains(LOCK_SITE)) {
			request.getSession().setAttribute(REFERER_URL, referer);
		}

		String action = request.getParameter("action");
		int siteId = Integer.parseInt(request.getParameter("id"));
		StudyBean studyBean = (StudyBean) sdao.findByPK(siteId);

		checkRoleByUserAndStudy(ub, studyBean.getParentStudyId(), studyBean.getId());

		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
		ArrayList configs = spvdao.findParamConfigByStudy(studyBean);
		studyBean.setStudyParameters(configs);

		String parentStudyName = "";
		if (studyBean.getParentStudyId() > 0) {
			StudyBean parent = (StudyBean) sdao.findByPK(studyBean.getParentStudyId());
			parentStudyName = parent.getName();
		}

		request.setAttribute("parentName", parentStudyName);
		request.setAttribute("siteToView", studyBean);
		request.setAttribute("studyBean", studyBean);
		request.setAttribute("action", action);

		if (request.getParameter("Submit") != null) {
			String message = "";
			List<StudySubjectBean> studySubjectBeanList = ssdao.findAllByStudyId(studyBean.getId());
			if (action.equalsIgnoreCase("lock")) {
				message = resword.getString("lockSiteStudySubjectsResultMsg");
				for (StudySubjectBean studySubjectBean : studySubjectBeanList) {
					List<StudyEventBean> studyEventBeanList = sedao.findAllByStudySubject(studySubjectBean);
					for (StudyEventBean studyEventBean : studyEventBeanList) {
						if (studyEventBean.getSubjectEventStatus() != SubjectEventStatus.LOCKED) {
							studyEventBean.setPrevSubjectEventStatus(studyEventBean.getSubjectEventStatus());
							studyEventBean.setSubjectEventStatus(SubjectEventStatus.LOCKED);
							studyEventBean.setUpdater(ub);
							studyEventBean.setUpdatedDate(new Date());
							ArrayList<EventCRFBean> eventCRFs = ecdao.findAllByStudyEvent(studyEventBean);
							for (EventCRFBean eventCRFBean : eventCRFs) {
								eventCRFBean.setUpdater(ub);
								eventCRFBean.setUpdatedDate(new Date());
								ecdao.update(eventCRFBean);
							}
							sedao.update(studyEventBean);
						}
					}
					studySubjectBean.setStatus(Status.LOCKED);
					ssdao.update(studySubjectBean);
				}
			} else if (action.equalsIgnoreCase("unlock")) {
				message = resword.getString("unlockSiteStudySubjectsResultMsg");
				for (StudySubjectBean studySubjectBean : studySubjectBeanList) {
					List<StudyEventBean> studyEventBeanList = sedao.findAllByStudySubject(studySubjectBean);
					for (StudyEventBean studyEventBean : studyEventBeanList) {
						if (studyEventBean.getSubjectEventStatus() == SubjectEventStatus.LOCKED) {
							studyEventBean.setSubjectEventStatus(studyEventBean.getPrevSubjectEventStatus());
							studyEventBean.setUpdater(ub);
							studyEventBean.setUpdatedDate(new Date());
							ArrayList<EventCRFBean> eventCRFs = ecdao.findAllByStudyEvent(studyEventBean);
							for (EventCRFBean eventCRFBean : eventCRFs) {
								eventCRFBean.setUpdater(ub);
								eventCRFBean.setUpdatedDate(new Date());
								ecdao.update(eventCRFBean);
							}
							sedao.update(studyEventBean);
						}
					}
					studySubjectBean.setStatus(Status.AVAILABLE);
					ssdao.update(studySubjectBean);
				}
			}
			showResultMessage(studyBean, message);
			response.sendRedirect((String) request.getSession().getAttribute(REFERER_URL));
		} else {
			forwardPage(Page.LOCK_SITE);
		}
	}

	private void showResultMessage(StudyBean studyBean, String message) {
		addPageMessage(message.replace("{0}", studyBean.getName()));
		Map storedAttributes = new HashMap();
		storedAttributes.put(SecureController.PAGE_MESSAGE, request.getAttribute(SecureController.PAGE_MESSAGE));
		request.getSession().setAttribute(RememberLastPage.STORED_ATTRIBUTES, storedAttributes);
	}
}
