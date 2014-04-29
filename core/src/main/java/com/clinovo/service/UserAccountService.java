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

package com.clinovo.service;

import java.util.List;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;

public interface UserAccountService {

	/**
	 * Discovers, if the user has a role, assigned to the one of studies from specified list,
	 * no matter if the role has status REMOVED or AVAILABLE.
	 * 
	 * @param user
	 *				user account, for which search should be performed
	 * @param studyList
	 *				list of studies to search in (must contain only studies, and no sites)
	 * @return TRUE, if the user has a role, assigned to the one of studies from specified list, and FALSE otherwise
	 */
	public boolean doesUserHaveRoleInStydies(UserAccountBean user, List<StudyBean> studyList) throws Exception;
	
	/**
	 * Performs specified action on study user role (remove, restore, delete).
	 * 
	 * @param userId
	 *				the user's account ID, that owns the role to be processed
	 * @param studyId
	 *				the study ID, role was assigned to
	 * @param actionId
	 *				the action ID, to be performed
	 * @param currentUser
	 *				the user, that performs action (user logged into the system)
	 * @param message
	 *				buffer for messages, that should be displayed on the webpage in 'Alerts & Messages' tab
	 * @param respage
	 *				localized message source
	 */
	public boolean performActionOnStudyUserRole(int userId, int studyId, int actionId, UserAccountBean currentUser,
			StringBuilder message, ResourceBundle respage) throws Exception;
	
	/**
	 * Deletes a role for a user account.
	 * 
	 * @param userId
	 *				the user's account ID, that owns the role to be deleted
	 * @param studyId
	 *				the study ID, role was assigned to
	 * @param currentUser
	 *				the user, that performs delete operation (user logged into the system)
	 * @param message
	 *				buffer for messages, that should be displayed on the webpage in 'Alerts & Messages' tab
	 * @param respage
	 *				localized message source
	 */
	public boolean deleteStudyUserRole(int userId, int studyId, UserAccountBean currentUser, StringBuilder message,
			ResourceBundle respage) throws Exception;
	
	/**
	 * Removes a role for a user account. Sets role status as REMOVED.
	 * 
	 * @param userId
	 *				the user's account ID, that owns the role to be removed
	 * @param studyId
	 *				the study ID, role was assigned to
	 * @param currentUser
	 *				the user, that performs remove operation (user logged into the system)
	 * @param message
	 *				buffer for messages, that should be displayed on the webpage in 'Alerts & Messages' tab
	 * @param respage
	 *				localized message source
	 */
	public boolean removeStudyUserRole(int userId, int studyId, UserAccountBean currentUser, StringBuilder message,
			ResourceBundle respage) throws Exception;
	
	/**
	 * Removes a role for a user account. Sets role status as AUTO_REMOVED. 
	 * Use this method when removing the role should be performed in the context of some other operation, e.g. removing study/site. 
	 * 
	 * @param studyUserRole
	 *				the role to be removed
	 * @param currentUser
	 *				the user, that performs remove operation (user logged into the system)
	 */
	public void autoRemoveStudyUserRole(StudyUserRoleBean studyUserRole, UserAccountBean currentUser) throws Exception ;
	
	/**
	 * Restores a role for a user account.
	 * 
	 * @param userId
	 *				the user's account ID, that owns the role to be restored
	 * @param studyId
	 *				the study ID, role was assigned to
	 * @param currentUser
	 *				the user, that performs restore operation (user logged into the system)
	 * @param message
	 *				buffer for messages, that should be displayed on the webpage in 'Alerts & Messages' tab
	 * @param respage
	 *				localized message source
	 */
	public boolean restoreStudyUserRole(int userId, int studyId, UserAccountBean currentUser, StringBuilder message,
			ResourceBundle respage) throws Exception;
	
	/**
	 * Restores a role for a user account.
	 * Use this method when restoring the role should be performed in the context of some other operation, e.g. restoring study/site. 
	 * 
	 * @param studyUserRole
	 *				the role to be restored
	 * @param currentUser
	 *				the user, that performs restore operation (user logged into the system)
	 */
	public void autoRestoreStudyUserRole(StudyUserRoleBean studyUserRole, UserAccountBean currentUser) throws Exception;
	
	/**
	 * Discovers, if the user still has at least one role with status AVAILABLE.
	 * 
	 * @param userId
	 *				the user account ID, for which search should be performed
	 */
	public boolean doesUserHaveAvailableRole(int userId) throws Exception;
	
	/**
	 * Sets activeStudyId property of a user account.
	 * 
	 * @param user
	 *				the user account, to be updated
	 * @param studyId
	 *				study ID, to be set as activeStudyId
	 */
	public void setActiveStudyId(UserAccountBean user, int studyId) throws Exception;
}