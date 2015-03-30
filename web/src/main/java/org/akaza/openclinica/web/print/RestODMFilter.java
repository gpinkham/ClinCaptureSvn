package org.akaza.openclinica.web.print;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.clinovo.i18n.LocaleResolver;

/**
 * User role validator.
 */
public class RestODMFilter extends HandlerInterceptorAdapter {

	@Autowired
	public DataSource dataSource;
	private ResourceBundle respage;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		LocaleResolver.resolveLocale();
		respage = ResourceBundleProvider.getPageMessagesBundle();

		List<String> uri = Arrays.asList(request.getRequestURI().toString().split("/"));
		String studySubject = "";
		for (String parameter : uri) {
			if (parameter.indexOf("SS_") == 0) {
				studySubject = parameter;
			} else if (parameter.indexOf("Pdf") > 0) {
				return true;
			}
		}
		StudySubjectDAO studySubjectDAO = getStudySubjectDAO();
		StudyDAO studyDAO = getStudyDAO();

		StudySubjectBean studySubjectBean = studySubjectDAO.findByOid(studySubject);
		StudyBean siteBean = studyDAO.findByStudySubjectId(studySubjectBean.getId());
		UserAccountBean userAccountBean = (UserAccountBean) request.getSession().getAttribute(
				BaseController.USER_BEAN_NAME);
		StudyUserRoleBean currentUserRole = userAccountBean.getRoleByStudy(siteBean);
		if (siteBean.isSite(siteBean.getParentStudyId()) && currentUserRole.getId() == 0) {
			StudyBean parentStudy = (StudyBean) studyDAO.findByPK(siteBean.getParentStudyId());
			currentUserRole = userAccountBean.getRoleByStudy(parentStudy);
		}
		if (currentUserRole.getId() == 0) {
			request.getSession().setAttribute("casebook_exception",
					respage.getString("does_not_have_access_for_subject_casebook"));
			response.sendRedirect(request.getContextPath() + "/MainMenu");
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Returns StudyDAO object for study table.
	 *
	 * @return the StudyDAO object.
	 */
	public StudyDAO getStudyDAO() {
		return new StudyDAO(getDataSource());
	}

	/**
	 * Returns StudySubjectDAO object for study subject table.
	 *
	 * @return the StudySubjectDAO object.
	 */
	public StudySubjectDAO getStudySubjectDAO() {
		return new StudySubjectDAO(getDataSource());
	}

	/**
	 * Returns DataSource object with database connection properties.
	 *
	 * @return the DataSource object.
	 */
	public DataSource getDataSource() {
		return dataSource;
	}
}
