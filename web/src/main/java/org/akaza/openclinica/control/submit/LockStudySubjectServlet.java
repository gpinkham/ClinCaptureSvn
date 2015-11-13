package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({"rawtypes", "unchecked", "serial"})
@Component
public class LockStudySubjectServlet extends Controller {

	public static final String REFERER_URL = "refererUrl";
	public static final String REFERER = "referer";
	public static final String LOCK_STUDY_SUBJECT = "LockStudySubject";
	public static final String HAS_UNIQUE_ID_NOTE = "hasUniqueIDNote";
	public static final String HAS_DOB_NOTE = "hasDOBNote";
	public static final String HAS_GENDER_NOTE = "hasGenderNote";
	public static final String HAS_ENROLLMENT_NOTE = "hasEnrollmentNote";
	public static final String UNIQUE_ID_NOTE = "uniqueIDNote";
	public static final String DOB_NOTE = "dOBNote";
	public static final String GENDER_NOTE = "genderNote";
	public static final String ENROLLMENT_NOTE = "enrollmentNote";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"),
				"1");
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		StudyDAO studyDao = getStudyDAO();
		SubjectDAO subjectDao = getSubjectDAO();

		String referer = request.getHeader(REFERER);
		if (referer != null && !referer.contains(LOCK_STUDY_SUBJECT)) {
			request.getSession().setAttribute(REFERER_URL, referer);
		}

		String action = request.getParameter("action");
		int studySubjectId = Integer.parseInt(request.getParameter("id"));
		StudySubjectBean studySubjectBean = (StudySubjectBean) getStudySubjectDAO().findByPK(studySubjectId);
		SubjectBean subjectBean = (SubjectBean) subjectDao.findByPK(studySubjectBean.getSubjectId());
		StudyBean studyBean = (StudyBean) studyDao.findByPK(studySubjectBean.getStudyId());
		StudyBean parentStudyBean = studyBean.getParentStudyId() > 0
				? (StudyBean) studyDao.findByPK(studyBean.getParentStudyId())
				: null;

		studyBean.getStudyParameterConfig().setCollectDob(
				getStudyParameterValueDAO().findByHandleAndStudy(studyBean.getId(), "collectDob").getValue());

		boolean subjectStudyIsCurrentStudy = studySubjectBean.getStudyId() == currentStudy.getId();
		boolean isParentStudy = studyBean.getParentStudyId() < 1;

		// Get any disc notes for this subject : studySubId
		DiscrepancyNoteDAO discrepancyNoteDAO = getDiscrepancyNoteDAO();
		List<DiscrepancyNoteBean> allNotesforSubject = new ArrayList<DiscrepancyNoteBean>();

		// These methods return only parent disc notes
		if (subjectStudyIsCurrentStudy && isParentStudy) {
			allNotesforSubject = discrepancyNoteDAO.findAllSubjectByStudyAndId(studyBean,
					studySubjectBean.getSubjectId());
			allNotesforSubject.addAll(
					discrepancyNoteDAO.findAllStudySubjectByStudyAndId(studyBean, studySubjectBean.getSubjectId()));
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

				allNotesforSubject.addAll(discrepancyNoteDAO.findAllStudySubjectByStudiesAndStudySubjectId(currentStudy,
						studyBean, studySubjectId));
			}

		}

		if (!allNotesforSubject.isEmpty()) {
			setRequestAttributesForNotes(request, allNotesforSubject);
		}

		SubjectBean subject = (SubjectBean) subjectDao.findByPK(studySubjectBean.getSubjectId());
		if (currentStudy.getStudyParameterConfig().getCollectDob().equals("2")) {
			Date dob = subject.getDateOfBirth();
			if (dob != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dob);
				int year = cal.get(Calendar.YEAR);
				request.setAttribute("yearOfBirth", year);
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
			if (action.equalsIgnoreCase("lock")) {
				message = resword.getString("lockStudySubjectResultMsg");
				getStudySubjectService().lockStudySubject(studySubjectBean, ub);
			} else if (action.equalsIgnoreCase("unlock")) {
				message = resword.getString("unlockStudySubjectResultMsg");
				getStudySubjectService().unlockStudySubject(studySubjectBean, ub);
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
		request.getSession().setAttribute(BaseController.STORED_ATTRIBUTES, storedAttributes);
	}
}
