package com.clinovo.rest.service;

import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * BaseService.
 */
public abstract class BaseService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MessageSource messageSource;

	/**
	 * Method that checks role / scope consistency.
	 *
	 * @param roleId
	 *            int
	 * @param studyId
	 *            int
	 * @throws RestException
	 *             the RestException
	 */
	public void checkRoleScopeConsistency(int roleId, int studyId) throws RestException {
		Role role = null;
		try {
			role = Role.get(roleId);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		StudyBean studyBean = (StudyBean) new StudyDAO(dataSource).findByPK(studyId);
		if (role == null) {
			throw new RestException(messageSource, "rest.createUser.roleDoesNotExist", new Object[] { roleId },
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (studyBean.getId() == 0) {
			throw new RestException(messageSource, "rest.createUser.studyDoesNotExist", new Object[] { studyId },
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (studyBean.getParentStudyId() == 0
				&& (role.getCode().equals(Role.CLINICAL_RESEARCH_COORDINATOR.getCode())
						|| role.getCode().equals(Role.INVESTIGATOR.getCode()) || role.getCode().equals(
						Role.SITE_MONITOR.getCode()))) {
			throw new RestException(messageSource, "rest.createUser.itsForbiddenToAssignSiteLevelRoleToStudy", null,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (studyBean.getParentStudyId() > 0
				&& (role.getCode().equals(Role.STUDY_ADMINISTRATOR.getCode())
						|| role.getCode().equals(Role.STUDY_DIRECTOR.getCode())
						|| role.getCode().equals(Role.STUDY_MONITOR.getCode())
						|| role.getCode().equals(Role.STUDY_CODER.getCode()) || role.getCode().equals(
						Role.STUDY_EVALUATOR.getCode()))) {
			throw new RestException(messageSource, "rest.createUser.itsForbiddenToAssignStudyLevelRoleToSite", null,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Method that checks study access for authorised user.
	 *
	 * @param studyId
	 *            int
	 * @throws RestException
	 *             the RestException
	 */
	public void checkStudyAccess(int studyId) throws RestException {
		UserDetails userDetails = UserDetails.getCurrentUserDetails();
		if (userDetails != null && !userDetails.getRoleCode().equals(Role.SYSTEM_ADMINISTRATOR.getCode())) {
			StudyUserRoleBean studyUserRoleBean = new UserAccountDAO(dataSource).findRoleByUserNameAndStudyId(
					userDetails.getUserName(), studyId);
			if (studyUserRoleBean.getId() == 0) {
				throw new RestException(messageSource, "rest.createUser.authorisedUserIsNotAssignedToStudy",
						new Object[] { new StudyDAO(dataSource).findByPK(studyId).getName() },
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
	}

	/**
	 * Method that checks username existence.
	 * 
	 * @param userName
	 *            String
	 * @throws RestException
	 *             the RestException
	 */
	public void checkUsernameExistence(String userName) throws RestException {
		if (new UserAccountDAO(dataSource).findByUserName(userName).getId() > 0) {
			throw new RestException(messageSource, "rest.createUser.usernameHasBeenTaken",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
