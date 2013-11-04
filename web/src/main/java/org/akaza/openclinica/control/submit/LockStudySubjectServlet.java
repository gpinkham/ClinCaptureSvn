package org.akaza.openclinica.control.submit;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.RememberLastPage;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
@Component
public class LockStudySubjectServlet extends Controller {

	public static final String REFERER_URL = "refererUrl";
	public static final String REFERER = "referer";
	public static final String LOCK_STUDY_SUBJECT = "LockStudySubject";

	public final static String HAS_UNIQUE_ID_NOTE = "hasUniqueIDNote";
	public final static String HAS_DOB_NOTE = "hasDOBNote";
	public final static String HAS_GENDER_NOTE = "hasGenderNote";
	public final static String HAS_ENROLLMENT_NOTE = "hasEnrollmentNote";
	public final static String UNIQUE_ID_NOTE = "uniqueIDNote";
	public final static String DOB_NOTE = "dOBNote";
	public final static String GENDER_NOTE = "genderNote";
	public final static String ENROLLMENT_NOTE = "enrollmentNote";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserAccountBean ub = getUserAccountBean(request);
        StudyBean currentStudy = getCurrentStudy(request);

		StudySubjectDAO ssdao = getStudySubjectDAO();
		SubjectDAO sdao = getSubjectDAO();
		StudyDAO studyDao = getStudyDAO();
		StudyParameterValueDAO spvdao = getStudyParameterValueDAO();
		StudyEventDAO sedao = getStudyEventDAO();
		EventCRFDAO ecdao = getEventCRFDAO();

		String referer = request.getHeader(REFERER);
		if (referer != null && !referer.contains(LOCK_STUDY_SUBJECT)) {
			request.getSession().setAttribute(REFERER_URL, referer);
		}

		String action = request.getParameter("action");
		int studySubjectId = Integer.parseInt(request.getParameter("id"));
		StudySubjectBean studySubjectBean = (StudySubjectBean) ssdao.findByPK(studySubjectId);
		SubjectBean subjectBean = (SubjectBean) sdao.findByPK(studySubjectBean.getSubjectId());
		StudyBean studyBean = (StudyBean) studyDao.findByPK(studySubjectBean.getStudyId());
		StudyBean parentStudyBean = studyBean.getParentStudyId() > 0 ? (StudyBean) studyDao.findByPK(studyBean
				.getParentStudyId()) : null;

		studyBean.getStudyParameterConfig().setCollectDob(
				spvdao.findByHandleAndStudy(studyBean.getId(), "collectDob").getValue());

		boolean subjectStudyIsCurrentStudy = studySubjectBean.getStudyId() == currentStudy.getId();
		boolean isParentStudy = studyBean.getParentStudyId() < 1;

		// Get any disc notes for this subject : studySubId
		DiscrepancyNoteDAO discrepancyNoteDAO = getDiscrepancyNoteDAO();
		List<DiscrepancyNoteBean> allNotesforSubject = new ArrayList<DiscrepancyNoteBean>();

		// These methods return only parent disc notes
		if (subjectStudyIsCurrentStudy && isParentStudy) {
			allNotesforSubject = discrepancyNoteDAO.findAllSubjectByStudyAndId(studyBean,
					studySubjectBean.getSubjectId());
			allNotesforSubject.addAll(discrepancyNoteDAO.findAllStudySubjectByStudyAndId(studyBean,
					studySubjectBean.getSubjectId()));
		} else {
			if (!isParentStudy) {
				StudyBean stParent = (StudyBean) studyDao.findByPK(studyBean.getParentStudyId());
				allNotesforSubject = discrepancyNoteDAO.findAllSubjectByStudiesAndSubjectId(stParent, studyBean,
						studySubjectBean.getSubjectId());

				allNotesforSubject.addAll(discrepancyNoteDAO.findAllStudySubjectByStudiesAndStudySubjectId(stParent,
						studyBean, studySubjectBean.getSubjectId()));

			} else {
				allNotesforSubject = discrepancyNoteDAO.findAllSubjectByStudiesAndSubjectId(currentStudy, studyBean,
						studySubjectBean.getSubjectId());

				allNotesforSubject.addAll(discrepancyNoteDAO.findAllStudySubjectByStudiesAndStudySubjectId(
						currentStudy, studyBean, studySubjectId));
			}

		}

		if (!allNotesforSubject.isEmpty()) {
			setRequestAttributesForNotes(request, allNotesforSubject);
		}

		SubjectBean subject = (SubjectBean) sdao.findByPK(studySubjectBean.getSubjectId());
		if (currentStudy.getStudyParameterConfig().getCollectDob().equals("2")) {
			Date dob = subject.getDateOfBirth();
			if (dob != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dob);
				int year = cal.get(Calendar.YEAR);
				request.setAttribute("yearOfBirth", new Integer(year));
			} else {
				request.setAttribute("yearOfBirth", "");
			}
		}

		request.setAttribute("studySubjectBean", studySubjectBean);
		request.setAttribute("parentStudy", parentStudyBean);
		request.setAttribute("subjectStudy", studyBean);
		request.setAttribute("subject", subjectBean);
		request.setAttribute("action", action);
		request.setAttribute("study", currentStudy);

		if (request.getParameter("Submit") != null) {
			String message = "";
			List<StudyEventBean> studyEventBeanList = sedao.findAllByStudySubject(studySubjectBean);
			if (action.equalsIgnoreCase("lock")) {
				message = resword.getString("lockStudySubjectResultMsg");
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
			} else if (action.equalsIgnoreCase("unlock")) {
				message = resword.getString("unlockStudySubjectResultMsg");
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
			showResultMessage(request, studySubjectBean, message);
			response.sendRedirect((String) request.getSession().getAttribute(REFERER_URL));
		} else {
			forwardPage(Page.LOCK_STUDY_SUBJECT, request, response);
		}
	}

	private void setRequestAttributesForNotes(HttpServletRequest request, List<DiscrepancyNoteBean> discBeans) {
		for (DiscrepancyNoteBean discrepancyNoteBean : discBeans) {
			if ("unique_identifier".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
				request.setAttribute(HAS_UNIQUE_ID_NOTE, "yes");
				request.setAttribute(UNIQUE_ID_NOTE, discrepancyNoteBean);
			} else if ("date_of_birth".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
				request.setAttribute(HAS_DOB_NOTE, "yes");
				request.setAttribute(DOB_NOTE, discrepancyNoteBean);
			} else if ("enrollment_date".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
				request.setAttribute(HAS_ENROLLMENT_NOTE, "yes");
				request.setAttribute(ENROLLMENT_NOTE, discrepancyNoteBean);
			} else if ("gender".equalsIgnoreCase(discrepancyNoteBean.getColumn())) {
				request.setAttribute(HAS_GENDER_NOTE, "yes");
				request.setAttribute(GENDER_NOTE, discrepancyNoteBean);
			}
		}
	}

	private void showResultMessage(HttpServletRequest request, StudySubjectBean studySubjectBean, String message) {
		addPageMessage(message.replace("{0}", studySubjectBean.getName()), request);
		Map storedAttributes = new HashMap();
		storedAttributes.put(Controller.PAGE_MESSAGE, request.getAttribute(Controller.PAGE_MESSAGE));
		request.getSession().setAttribute(RememberLastPage.STORED_ATTRIBUTES, storedAttributes);
	}
}
