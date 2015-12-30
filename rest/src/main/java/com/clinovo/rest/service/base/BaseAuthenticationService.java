package com.clinovo.rest.service.base;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.core.OpenClinicaPasswordEncoder;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.clinovo.rest.exception.RestException;

/**
 * BaseAuthenticationService.
 */
public abstract class BaseAuthenticationService extends BaseService {

	@Autowired
	private OpenClinicaPasswordEncoder passwordEncoder;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private DataSource dataSource;

	protected UserAccountBean authenticateUser(String userName, String password) {
		UserAccountBean userAccountBean = (UserAccountBean) new UserAccountDAO(dataSource).findByUserName(userName);
		if (userAccountBean != null && userAccountBean.getId() > 0) {
			if (!passwordEncoder.isPasswordValid(userAccountBean.getPasswd(), password, null)) {
				throw new RestException(messageSource, "rest.authentication.wrongUserNameOrPassword",
						HttpServletResponse.SC_UNAUTHORIZED);
			}
		} else {
			throw new RestException(messageSource, "rest.authentication.noUserFound",
					HttpServletResponse.SC_UNAUTHORIZED);
		}
		return userAccountBean;
	}

	protected StudyUserRoleBean getStudyUserRole(StudyBean studyBean, UserAccountBean userAccountBean, int errorCode) {
		StudyUserRoleBean result;
		if (studyBean != null && studyBean.getId() > 0) {
			if (studyBean.getParentStudyId() > 0) {
				throw new RestException(messageSource, "rest.authentication.studyMustBeStudy", errorCode);
			} else {
				StudyUserRoleBean surBean = userAccountBean.getSysAdminRole();
				if (surBean == null) {
					surBean = new UserAccountDAO(dataSource).findRoleByUserNameAndStudyId(userAccountBean.getName(),
							studyBean.getId());
				}
				if (surBean != null && surBean.getId() > 0) {
					if (surBean.getRole().getId() == Role.STUDY_ADMINISTRATOR.getId()
							|| surBean.getRole().getId() == Role.SYSTEM_ADMINISTRATOR.getId()) {
						if (userAccountBean.hasUserType(UserType.SYSADMIN)) {
							result = surBean;
						} else {
							throw new RestException(messageSource,
									"rest.authentication.onlyUsersWithTypeAdministratorCanBeAuthenticated", errorCode);
						}
					} else {
						throw new RestException(messageSource,
								"rest.authentication.onlyRootOrStudyAdministratorCanBeAuthenticated", errorCode);
					}
				} else {
					throw new RestException(messageSource, "rest.authentication.userIsNotAssignedToStudy", errorCode);
				}
			}
		} else {
			throw new RestException(messageSource, "rest.authentication.wrongStudyName", errorCode);
		}
		return result;
	}
}
