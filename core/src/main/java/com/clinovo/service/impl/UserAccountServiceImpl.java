/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVOâ€™S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityAction;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.service.UserAccountService;

@Service("userAccountService")
public class UserAccountServiceImpl implements UserAccountService {

	@Autowired
	private DataSource dataSource;

	private UserAccountDAO userAccountDAO;

	private StudyDAO studyDAO;

	private MessageFormat messageFormat = new MessageFormat("");

	public UserAccountDAO getUserAccountDAO() {
		return userAccountDAO == null ? new UserAccountDAO(dataSource) : userAccountDAO;
	}

	public StudyDAO getStudyDAO() {
		return studyDAO == null ? new StudyDAO(dataSource) : studyDAO;
	}

	public MessageFormat getMessageFormat() {
		return messageFormat;
	}

	public boolean doesUserHaveRoleInStydies(UserAccountBean user, List<StudyBean> studyList) throws Exception {

		List<StudyBean> fullStudyList = new ArrayList<StudyBean>();
		List<StudyUserRoleBean> rolesList = user.getRoles();
		boolean isStudyLevelUser = rolesList.get(0).isStudyLevelRole();

		if (isStudyLevelUser) {
			fullStudyList = studyList;
		} else {
			for (StudyBean study : studyList) {
				fullStudyList.addAll(getStudyDAO().findAllByParentAndActive(study.getId()));
			}
		}

		for (StudyUserRoleBean role : rolesList) {
			for (StudyBean study : fullStudyList) {
				if (role.getStudyId() == study.getId()) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean performActionOnStudyUserRole(int userId, int studyId, int actionId, UserAccountBean currentUser,
			StringBuilder message, ResourceBundle respage) throws Exception {

		boolean operationSucceeded = false;

		if (EntityAction.contains(actionId)) {

			EntityAction actionSpecified = EntityAction.get(actionId);

			if (actionSpecified.equals(EntityAction.DELETE)) {

				operationSucceeded = deleteStudyUserRole(userId, studyId, currentUser, message, respage);

			} else if (actionSpecified.equals(EntityAction.REMOVE)) {

				operationSucceeded = removeStudyUserRole(userId, studyId, currentUser, message, respage);

			} else if (actionSpecified.equals(EntityAction.RESTORE)) {

				operationSucceeded = restoreStudyUserRole(userId, studyId, currentUser, message, respage);

			}

		}

		return operationSucceeded;
	}

	public boolean deleteStudyUserRole(int userId, int studyId, UserAccountBean currentUser, StringBuilder message,
			ResourceBundle respage) throws Exception {

		boolean operationSucceeded = false;
		UserAccountBean user = (UserAccountBean) getUserAccountDAO().findByPK(userId);

		if (user.isActive() && !user.getName().equalsIgnoreCase(UserAccountBean.ROOT)) {

			StudyUserRoleBean studyUserRole = getUserAccountDAO().findRoleByUserNameAndStudyId(user.getName(), studyId);

			if (studyUserRole.isActive()
					&& !(currentUser.getId() == user.getId() && currentUser.getActiveStudyId() == studyUserRole.getStudyId())) {

				getUserAccountDAO().deleteUserRole(studyUserRole);

				if (user.getActiveStudyId() == studyUserRole.getStudyId()) {
					updateUserAcocunt(user.getId(), currentUser);
				}

				Object argsForMessageFormat[] = { studyUserRole.getRoleName(), user.getName(),
						((StudyBean) getStudyDAO().findByPK(studyId)).getName() };

				getMessageFormat().applyPattern(respage.getString("the_study_user_role_deleted"));
				message.append(getMessageFormat().format(argsForMessageFormat));
				operationSucceeded = true;
			}
		}

		return operationSucceeded;
	}

	public boolean removeStudyUserRole(int userId, int studyId, UserAccountBean currentUser, StringBuilder message,
			ResourceBundle respage) throws Exception {

		boolean operationSucceeded = false;
		UserAccountBean user = (UserAccountBean) getUserAccountDAO().findByPK(userId);

		if (user.isActive() && !user.getName().equalsIgnoreCase(UserAccountBean.ROOT)) {

			StudyUserRoleBean studyUserRole = getUserAccountDAO().findRoleByUserNameAndStudyId(user.getName(), studyId);

			if (studyUserRole.isActive()
					&& !(currentUser.getId() == user.getId() && currentUser.getActiveStudyId() == studyUserRole.getStudyId())) {

				if (removeRole(studyUserRole, currentUser, false)) {

					Object argsForMessageFormat[] = { studyUserRole.getRoleName(), user.getName(),
							((StudyBean) getStudyDAO().findByPK(studyId)).getName() };

					getMessageFormat().applyPattern(respage.getString("the_study_user_role_removed"));
					message.append(getMessageFormat().format(argsForMessageFormat));
					operationSucceeded = true;

				}
			}
		}

		return operationSucceeded;
	}

	public void autoRemoveStudyUserRole(StudyUserRoleBean studyUserRole, UserAccountBean currentUser) throws Exception {

		removeRole(studyUserRole, currentUser, true);
	}

	private boolean removeRole(StudyUserRoleBean studyUserRole, UserAccountBean currentUser, boolean autoRemove)
			throws Exception {

		boolean operationSucceeded = false;

		if (!studyUserRole.getStatus().equals(Status.DELETED) && !studyUserRole.getStatus().equals(Status.AUTO_DELETED)) {

			UserAccountBean user = (UserAccountBean) getUserAccountDAO().findByUserName(studyUserRole.getUserName());

			if (autoRemove) {
				studyUserRole.setStatus(Status.AUTO_DELETED);
			} else {
				studyUserRole.setStatus(Status.DELETED);
			}

			studyUserRole.setUpdater(currentUser);
			studyUserRole.setUpdatedDate(new Date());
			getUserAccountDAO().updateStudyUserRole(studyUserRole, user.getName());

			if (user.getActiveStudyId() == studyUserRole.getStudyId()) {
				updateUserAcocunt(user.getId(), currentUser);
			}

			operationSucceeded = true;
		}

		return operationSucceeded;
	}

	public boolean restoreStudyUserRole(int userId, int studyId, UserAccountBean currentUser, StringBuilder message,
			ResourceBundle respage) throws Exception {

		boolean operationSucceeded = false;
		UserAccountBean user = (UserAccountBean) getUserAccountDAO().findByPK(userId);

		if (user.isActive() && !user.getName().equalsIgnoreCase(UserAccountBean.ROOT)) {

			StudyUserRoleBean studyUserRole = getUserAccountDAO().findRoleByUserNameAndStudyId(user.getName(), studyId);

			if (studyUserRole.isActive()
					&& !(currentUser.getId() == user.getId() && currentUser.getActiveStudyId() == studyUserRole.getStudyId())) {

				if (restoreRole(studyUserRole, currentUser, message, respage, false)) {

					Object argsForMessageFormat[] = { studyUserRole.getRoleName(), user.getName(),
							((StudyBean) getStudyDAO().findByPK(studyId)).getName() };

					getMessageFormat().applyPattern(respage.getString("the_study_user_role_restored"));
					message.append(getMessageFormat().format(argsForMessageFormat));
					operationSucceeded = true;
				}
			}

		}

		return operationSucceeded;
	}

	public void autoRestoreStudyUserRole(StudyUserRoleBean studyUserRole, UserAccountBean currentUser) throws Exception {

		restoreRole(studyUserRole, currentUser, null, null, true);
	}

	private boolean restoreRole(StudyUserRoleBean studyUserRole, UserAccountBean currentUser, StringBuilder message,
			ResourceBundle respage, boolean autoRestore) throws Exception {

		boolean operationSucceeded = false;
		
		boolean checkRoleStatus = autoRestore ? studyUserRole.getStatus().equals(Status.AUTO_DELETED) : (studyUserRole.getStatus().equals(Status.AUTO_DELETED) 
				|| studyUserRole.getStatus().equals(Status.DELETED));
		
		if (checkRoleStatus) {

			StudyBean study = (StudyBean) getStudyDAO().findByPK(studyUserRole.getStudyId());

			if (study.getStatus().equals(Status.DELETED) || study.getStatus().equals(Status.AUTO_DELETED)) {

				if (!autoRestore && message != null && respage != null) {
					messageFormat.applyPattern(respage.getString("the_role_cannot_be_restored_since_study_deleted"));
					message.append(messageFormat.format(new Object[] { study.getName() }));
				}

			} else {

				UserAccountBean user = (UserAccountBean) getUserAccountDAO().findByUserName(studyUserRole.getUserName());

				if (!user.getStatus().equals(Status.DELETED)) {

					studyUserRole.setStatus(Status.AVAILABLE);
					studyUserRole.setUpdater(currentUser);
					studyUserRole.setUpdatedDate(new Date());
					getUserAccountDAO().updateStudyUserRole(studyUserRole, user.getName());

					setActiveStudyId(user, studyUserRole.getStudyId());
					
					operationSucceeded = true;
				}
			}
		}

		return operationSucceeded;
	}

	private void updateUserAcocunt(int userId, UserAccountBean currentUser) throws Exception {

		boolean doesUserHaveActiveRole = false;
		UserAccountBean user = (UserAccountBean) getUserAccountDAO().findByPK(userId);

		for (StudyUserRoleBean studyUserRole : user.getRoles()) {
			if (studyUserRole.getStatus().equals(Status.AVAILABLE)) {
				setActiveStudyId(user, studyUserRole.getStudyId());
				doesUserHaveActiveRole = true;
				break;
			}
		}

		if (!doesUserHaveActiveRole) {
			getUserAccountDAO().lockUser(user.getId());
		}

	}

	public boolean doesUserHaveAvailableRole(int userId) throws Exception {

		UserAccountBean user = (UserAccountBean) getUserAccountDAO().findByPK(userId);

		for (StudyUserRoleBean studyUserRole : user.getRoles()) {
			if (studyUserRole.getStatus().equals(Status.AVAILABLE)) {
				return true;
			}
		}
		return false;
	}

	public void setActiveStudyId(UserAccountBean user, int studyId) throws Exception {

		user.setActiveStudyId(studyId);
		getUserAccountDAO().update(user);
	}

}
