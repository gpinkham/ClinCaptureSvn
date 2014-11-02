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
package org.akaza.openclinica.dao.login;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Privilege;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.PasswordHistoryBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/**
 * <P>
 * UserAccountDAO, the data access object for the User_Account table in the OpenClinica 2.0 database.
 *
 * @author thickerson
 *
 *         TODO
 *         <P>
 *         add functions for admin use cases such as assign user to study, remove user from study, etc.
 *         <P>
 *         add ability to get role and priv objects from database when U select
 *         <P>
 *         expand on query to get all that from a select star?
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class UserAccountDAO extends AuditableEntityDAO {

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_USERACCOUNT;
	}

	protected void setQueryNames() {
		getCurrentPKName = "getCurrentPK";
		getNextPKName = "getNextPK";
	}

	public UserAccountDAO(DataSource ds) {
		super(ds);
		setQueryNames();
	}

	public UserAccountDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
		setQueryNames();
	}

	@Override
	public void setTypesExpected() {
		this.unsetTypeExpected();
		// assuming select star query on user_account
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.STRING);
		this.setTypeExpected(3, TypeNames.STRING);
		this.setTypeExpected(4, TypeNames.STRING);
		this.setTypeExpected(5, TypeNames.STRING);
		this.setTypeExpected(6, TypeNames.STRING);
		this.setTypeExpected(7, TypeNames.INT);
		this.setTypeExpected(8, TypeNames.STRING);
		this.setTypeExpected(9, TypeNames.INT);
		this.setTypeExpected(10, TypeNames.INT);
		this.setTypeExpected(11, TypeNames.DATE);// created
		this.setTypeExpected(12, TypeNames.DATE);// updated
		this.setTypeExpected(13, TypeNames.TIMESTAMP);// lastvisit, changed
		// from date
		this.setTypeExpected(14, TypeNames.DATE);// passwd timestamp
		this.setTypeExpected(15, TypeNames.STRING);
		this.setTypeExpected(16, TypeNames.STRING);
		this.setTypeExpected(17, TypeNames.STRING);
		this.setTypeExpected(18, TypeNames.INT);
		this.setTypeExpected(19, TypeNames.INT);
		this.setTypeExpected(20, TypeNames.BOOL);
		this.setTypeExpected(21, TypeNames.BOOL);
		this.setTypeExpected(22, TypeNames.INT);
		this.setTypeExpected(23, TypeNames.BOOL);
	}

	public void setPrivilegeTypesExpected() {
		this.unsetTypeExpected();
		// assuming we are selecting privs from a join on privilege and
		// role_priv_map tables, tbh
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.STRING);
		this.setTypeExpected(3, TypeNames.STRING);
		this.setTypeExpected(4, TypeNames.STRING);

	}

	public void setRoleTypesExpected() {
		this.unsetTypeExpected();
		// assuming select star from study_user_role
		this.setTypeExpected(1, TypeNames.STRING);
		this.setTypeExpected(2, TypeNames.INT);
		this.setTypeExpected(3, TypeNames.INT);
		this.setTypeExpected(4, TypeNames.INT);
		this.setTypeExpected(5, TypeNames.DATE);
		this.setTypeExpected(6, TypeNames.DATE);
		this.setTypeExpected(7, TypeNames.INT);
		this.setTypeExpected(8, TypeNames.STRING);
	}

	public boolean updatePentahoAutoLoginParams(UserAccountBean ub) {
		HashMap variables = new HashMap();
		variables.put(1, ub.getPentahoUserSession());
		variables.put(2, new Timestamp(ub.getPentahoTokenDate().getTime()));
		variables.put(3, ub.getId());

		String sql = digester.getQuery("updatePentahoAutoLoginParams");
		this.execute(sql, variables);

		return this.isQuerySuccessful();
	}

	public EntityBean update(EntityBean eb) {
		UserAccountBean uab = (UserAccountBean) eb;
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();

		variables.put(1, uab.getName());
		variables.put(2, uab.getPasswd());
		variables.put(3, uab.getFirstName());
		variables.put(4, uab.getLastName());
		variables.put(5, uab.getEmail());
		if (uab.getActiveStudyId() == 0) {
			nullVars.put(6, TypeNames.INT);
			variables.put(6, null);
		} else {
			variables.put(6, uab.getActiveStudyId());
		}
		variables.put(7, uab.getInstitutionalAffiliation());
		variables.put(8, uab.getStatus().getId());
		variables.put(9, uab.getUpdaterId());
		if (uab.getLastVisitDate() == null) {
			nullVars.put(10, TypeNames.TIMESTAMP);
			variables.put(10, null);
		} else {
			variables.put(10, new Timestamp(uab.getLastVisitDate().getTime()));
		}
		if (uab.getPasswdTimestamp() == null) {
			nullVars.put(11, TypeNames.DATE);
			variables.put(11, null);
		} else {
			variables.put(11, uab.getPasswdTimestamp());
		}
		variables.put(12, uab.getPasswdChallengeQuestion());
		variables.put(13, uab.getPasswdChallengeAnswer());
		variables.put(14, uab.getPhone());

		if (uab.isTechAdmin()) {
			variables.put(15, UserType.TECHADMIN.getId());
		} else if (uab.isSysAdmin()) {
			variables.put(15, UserType.SYSADMIN.getId());
		} else {
			variables.put(15, UserType.USER.getId());
		}

		variables.put(16, uab.getAccountNonLocked());
		variables.put(17, uab.getLockCounter());
		variables.put(18, uab.getRunWebservices());

		variables.put(19, uab.getEnabled());

		variables.put(20, uab.getId());

		String sql = digester.getQuery("update");
		this.execute(sql, variables, nullVars);

		if (!this.isQuerySuccessful()) {
			eb.setId(0);
			logger.warn("query failed: " + sql);
		}

		return eb;
	}

	/**
	 * deleteTestOnly, used only to clean up after unit testing, tbh
	 *
	 * @param name
	 *            String
	 */
	public void deleteTestOnly(String name) {
		HashMap variables = new HashMap();
		variables.put(1, name);
		this.execute(digester.getQuery("deleteTestOnly"), variables);
	}

	public void delete(UserAccountBean u) {
		HashMap variables;

		variables = new HashMap();
		variables.put(1, u.getName());
		this.execute(digester.getQuery("deleteStudyUserRolesIncludeAutoRemove"), variables);

		variables.put(1, u.getUpdaterId());
		variables.put(2, u.getId());
		this.execute(digester.getQuery("delete"), variables);
	}

	public void restore(UserAccountBean u) {
		HashMap variables = new HashMap();
		variables.put(1, u.getPasswd());
		variables.put(2, u.getUpdaterId());
		variables.put(3, u.getId());
		this.execute(digester.getQuery("restore"), variables);

		variables = new HashMap();
		variables.put(1, u.getName());
		this.execute(digester.getQuery("restoreStudyUserRolesByUserID"), variables);
	}

	public void updateLockCounter(Integer id, Integer newCounterNumber) {
		HashMap variables = new HashMap();
		variables.put(1, newCounterNumber);
		variables.put(2, id);
		this.execute(digester.getQuery("updateLockCounter"), variables);
	}

	public void lockUser(Integer id) {
		HashMap variables = new HashMap();
		variables.put(1, false);
		variables.put(2, Status.LOCKED.getId());
		variables.put(3, id);
		this.execute(digester.getQuery("lockUser"), variables);
	}

	public EntityBean create(EntityBean eb) {
		UserAccountBean uab = (UserAccountBean) eb;
		HashMap variables = new HashMap();
		int id = getNextPK();
		variables.put(1, id);
		variables.put(2, uab.getName());
		variables.put(3, uab.getPasswd());
		variables.put(4, uab.getFirstName());
		variables.put(5, uab.getLastName());
		variables.put(6, uab.getEmail());
		variables.put(7, uab.getActiveStudyId());
		variables.put(8, uab.getInstitutionalAffiliation());
		variables.put(9, uab.getStatus().getId());
		variables.put(10, uab.getOwnerId());
		variables.put(11, uab.getPasswdChallengeQuestion());
		variables.put(12, uab.getPasswdChallengeAnswer());
		variables.put(13, uab.getPhone());

		if (uab.isTechAdmin()) {
			variables.put(14, UserType.TECHADMIN.getId());
		} else if (uab.isSysAdmin()) {
			variables.put(14, UserType.SYSADMIN.getId());
		} else {
			variables.put(14, UserType.USER.getId());
		}

		variables.put(15, uab.getRunWebservices());

		boolean success;
		this.execute(digester.getQuery("insert"), variables);
		success = isQuerySuccessful();

		ArrayList<StudyUserRoleBean> userRoles = uab.getRoles();
		for (StudyUserRoleBean studyRole : userRoles) {
			createStudyUserRole(uab, studyRole);
			success = success && isQuerySuccessful();
		}
		if (success) {
			uab.setId(id);
		}
		return uab;
	}

	public StudyUserRoleBean createStudyUserRole(UserAccountBean user, StudyUserRoleBean studyRole) {
		Locale currentLocale = ResourceBundleProvider.getLocale();
		ResourceBundleProvider.updateLocale(Locale.US);
		HashMap variables = new HashMap();
		variables.put(1, studyRole.getRoleName());
		variables.put(2, studyRole.getStudyId());
		variables.put(3, studyRole.getStatus().getId());
		variables.put(4, user.getName());
		variables.put(5, studyRole.getOwnerId());
		this.execute(digester.getQuery("insertStudyUserRole"), variables);
		ResourceBundleProvider.updateLocale(currentLocale);
		return studyRole;
	}

	public UserAccountBean findStudyUserRole(UserAccountBean user, StudyUserRoleBean studyRole) {
		this.setTypesExpected();
		this.setTypeExpected(1, TypeNames.STRING);
		this.setTypeExpected(2, TypeNames.INT);
		this.setTypeExpected(3, TypeNames.INT);
		this.setTypeExpected(4, TypeNames.INT);
		this.setTypeExpected(5, TypeNames.DATE);
		this.setTypeExpected(6, TypeNames.DATE);
		this.setTypeExpected(7, TypeNames.INT);
		this.setTypeExpected(8, TypeNames.STRING);
		HashMap variables = new HashMap();

		variables.put(1, studyRole.getRoleName());
		variables.put(2, studyRole.getStudyId());
		variables.put(3, studyRole.getStatus().getId());
		variables.put(4, user.getName());

		ArrayList alist = this.select(digester.getQuery("findStudyUserRole"), variables);
		UserAccountBean eb = new UserAccountBean();
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			eb.setName((String) ((HashMap) it.next()).get("user_name"));
		}
		return eb;
	}

	public Object getEntityFromHashMap(HashMap hm) {
		return this.getEntityFromHashMap(hm, true);
	}

	public StudyUserRoleBean getRoleFromHashMap(HashMap hm) {
		StudyUserRoleBean surb = new StudyUserRoleBean();

		Date dateCreated = (Date) hm.get("date_created");
		Date dateUpdated = (Date) hm.get("date_updated");
		Integer statusId = (Integer) hm.get("status_id");
		Integer studyId = (Integer) hm.get("study_id");
		surb.setUserName((String) hm.get("user_name"));
		surb.setName((String) hm.get("role_name"));
		surb.setRoleName((String) hm.get("role_name"));
		surb.setCreatedDate(dateCreated);
		surb.setUpdatedDate(dateUpdated);
		surb.setStatus(Status.get(statusId));
		surb.setStudyId(studyId);
		return surb;
	}

	public Privilege getPrivilegeFromHashMap(HashMap hm) {
		Integer privId = (Integer) hm.get("priv_id");

		return Privilege.get(privId);
	}

	@SuppressWarnings("deprecation")
	public Object getEntityFromHashMap(HashMap hm, boolean findOwner) {
		UserAccountBean eb = new UserAccountBean();

		// pull out objects from hashmap
		String firstName = (String) hm.get("first_name");
		String lastName = (String) hm.get("last_name");
		String userName = (String) hm.get("user_name");
		eb.setEmail((String) hm.get("email"));
		eb.setPasswd((String) hm.get("passwd"));
		Integer userId = (Integer) hm.get("user_id");
		Integer activeStudy = (Integer) hm.get("active_study");
		Integer statusId = (Integer) hm.get("status_id");
		Date dateCreated = (Date) hm.get("date_created");
		Date dateUpdated = (Date) hm.get("date_updated");
		Date dateLastVisit = (Date) hm.get("date_lastvisit");
		Date pwdTimestamp = (Date) hm.get("passwd_timestamp");
		String passwdChallengeQuestion = (String) hm.get("passwd_challenge_question");
		String passwdChallengeAnswer = (String) hm.get("passwd_challenge_answer");
		Integer userTypeId = (Integer) hm.get("user_type_id");
		Integer ownerId = (Integer) hm.get("owner_id");
		Integer updateId = (Integer) hm.get("update_id");

		// begin to set objects in the bean
		eb.setId(userId);
		eb.setActiveStudyId(activeStudy);
		eb.setInstitutionalAffiliation((String) hm.get("institutional_affiliation"));
		eb.setStatus(Status.get(statusId));
		eb.setCreatedDate(dateCreated);
		eb.setUpdatedDate(dateUpdated);
		eb.setLastVisitDate(dateLastVisit);
		eb.setPasswdTimestamp(pwdTimestamp);
		eb.setPhone((String) hm.get("phone"));
		eb.addUserType(UserType.get(userTypeId));
		eb.setEnabled((Boolean) hm.get("enabled"));
		eb.setAccountNonLocked((Boolean) hm.get("account_non_locked"));
		eb.setLockCounter(((Integer) hm.get("lock_counter")));
		eb.setRunWebservices((Boolean) hm.get("run_webservices"));
		eb.setOwnerId(ownerId);
		eb.setUpdaterId(updateId);

		// below block is set up to avoid recursion, etc.
		if (findOwner) {
			UserAccountBean owner = (UserAccountBean) this.findByPK(ownerId, false);
			eb.setOwner(owner);
			UserAccountBean updater = (UserAccountBean) this.findByPK(updateId, false);
			eb.setUpdater(updater);
		}
		// end of if block to avoid recursion

		eb.setFirstName(firstName);
		eb.setLastName(lastName);
		eb.setName(userName);
		eb.setPasswdChallengeQuestion(passwdChallengeQuestion);
		eb.setPasswdChallengeAnswer(passwdChallengeAnswer);

		// pull out the roles and privs here, tbh
		ArrayList userRoleBeans = (ArrayList) this.findAllRolesByUserName(eb.getName());
		eb.setRoles(userRoleBeans);

		eb.setActive(true);
		return eb;
	}

	public Collection findAll() {
		return findAllByLimit(false);
	}

	public Collection findAllByLimit(boolean hasLimit) {
		this.setTypesExpected();
		ArrayList alist;
		if (hasLimit) {
			alist = this.select(digester.getQuery("findAllByLimit"));
		} else {
			alist = this.select(digester.getQuery("findAll"));
		}
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			UserAccountBean eb = (UserAccountBean) this.getEntityFromHashMap((HashMap) anAlist, true);
			al.add(eb);
		}
		return al;
	}

	/**
	 * next on our list, how can we affect the query??? SELECT FROM USER_ACCOUNT ORDER BY ? DESC?
	 *
	 * @see org.akaza.openclinica.dao.core.DAOInterface#findAll(java.lang.String, boolean, java.lang.String)
	 */
	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	public EntityBean findByPK(int ID) {
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, ID);

		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		UserAccountBean eb = new UserAccountBean();
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (UserAccountBean) this.getEntityFromHashMap((HashMap) it.next(), true);
		}

		return eb;
	}

	public EntityBean findByPK(int ID, boolean findOwner) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, ID);
		ArrayList alist = this.select(digester.getQuery("findByPK"), variables);
		UserAccountBean eb = new UserAccountBean();
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			eb = (UserAccountBean) this.getEntityFromHashMap((HashMap) it.next(), findOwner);
		}
		return eb;
	}

	public EntityBean findByUserName(String name) {
		this.setTypesExpected();
		HashMap variables = new HashMap();

		variables.put(1, name);

		ArrayList alist = this.select(digester.getQuery("findByUserName"), variables);
		UserAccountBean eb = new UserAccountBean();
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			eb = (UserAccountBean) this.getEntityFromHashMap((HashMap) it.next(), true);
		}
		return eb;
	}

	/**
	 * Finds all the studies with roles for a user
	 *
	 * @param user
	 *            UserAccountBean
	 * @param allStudies
	 *            The result of calling StudyDAO.findAll();
	 */
	public ArrayList findStudyByUser(UserAccountBean user, ArrayList allStudies) {
		this.unsetTypeExpected();

		this.setTypeExpected(1, TypeNames.STRING);
		this.setTypeExpected(2, TypeNames.INT);
		this.setTypeExpected(3, TypeNames.STRING);
		HashMap allStudyUserRoleBeans = new HashMap();

		HashMap variables = new HashMap();
		variables.put(1, user.getName());
		ArrayList alist = this.select(digester.getQuery("findStudyByUser"), variables);

		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			String roleName = user.getName().equals(UserAccountBean.ROOT) ? user.getSysAdminRole().getRoleName()
					: (String) hm.get("role_name");
			String studyName = (String) hm.get("name");
			Integer studyId = (Integer) hm.get("study_id");
			StudyUserRoleBean sur = new StudyUserRoleBean();
			sur.setRoleName(roleName);
			sur.setStudyId(studyId);
			sur.setStudyName(studyName);
			sur.setRole(user.getName().equals(UserAccountBean.ROOT) ? user.getSysAdminRole().getRole() : Role
					.getByName(roleName));
			allStudyUserRoleBeans.put(studyId, sur);
		}

		ArrayList answer = new ArrayList();

		StudyDAO sdao = new StudyDAO(ds);

		HashMap childrenByParentId = sdao.getChildrenByParentIds(allStudies);

		for (Object allStudy : allStudies) {
			StudyBean parent = (StudyBean) allStudy;

			if (parent == null || parent.getParentStudyId() > 0) {
				continue;
			}

			boolean parentAdded = false;
			Integer studyId = parent.getId();
			StudyUserRoleBean roleInStudy;

			ArrayList subTreeRoles = new ArrayList();

			if (allStudyUserRoleBeans.containsKey(studyId)) {
				roleInStudy = (StudyUserRoleBean) allStudyUserRoleBeans.get(studyId);

				subTreeRoles.add(roleInStudy);
				parentAdded = true;
			} else { // we do this so that we can compute Role.max below
				// without
				// throwing a NullPointerException
				roleInStudy = new StudyUserRoleBean();
			}

			ArrayList children = (ArrayList) childrenByParentId.get(studyId);
			if (children == null) {
				children = new ArrayList();
			}

			for (Object aChildren : children) {
				StudyBean child = (StudyBean) aChildren;
				Integer childId = child.getId();

				if (allStudyUserRoleBeans.containsKey(childId)) {
					if (!parentAdded) {
						roleInStudy.setStudyId(studyId);
						roleInStudy.setRole(Role.INVALID);
						roleInStudy.setStudyName(parent.getName());
						subTreeRoles.add(roleInStudy);
						parentAdded = true;
					}

					StudyUserRoleBean roleInChild = (StudyUserRoleBean) allStudyUserRoleBeans.get(childId);
					Role max = Role.max(roleInChild.getRole(), roleInStudy.getRole());
					roleInChild.setRole(max);
					roleInChild.setParentStudyId(studyId);
					subTreeRoles.add(roleInChild);
				} else {
					StudyUserRoleBean roleInChild = new StudyUserRoleBean();
					roleInChild.setStudyId(child.getId());
					roleInChild.setStudyName(child.getName());
					roleInChild.setRole(roleInStudy.getRole());
					roleInChild.setParentStudyId(studyId);
					subTreeRoles.add(roleInChild);
				}
			}
			if (parentAdded) {
				answer.addAll(subTreeRoles);
			}
		}

		return answer;
	}

	public Collection findAllRolesByUserName(String userName) {
		this.setRoleTypesExpected();
		ArrayList answer = new ArrayList();

		HashMap variables = new HashMap();
		variables.put(1, userName);
		ArrayList alist = this.select(digester.getQuery("findAllRolesByUserName"), variables);
		for (Object anAlist : alist) {
			StudyUserRoleBean surb = this.getRoleFromHashMap((HashMap) anAlist);
			answer.add(surb);
		}

		return answer;
	}

	/**
	 * Finds all user and roles in a study
	 *
	 * @param studyId
	 *            int
	 */
	public ArrayList findAllByStudyId(int studyId) {

		return findAllUsersByStudyIdAndLimit(studyId, false);

	}

	/**
	 * Finds all user and roles in a study
	 *
	 * @param studyId
	 *            int
	 * @param isLimited
	 *            boolean
	 */
	public ArrayList findAllUsersByStudyIdAndLimit(int studyId, boolean isLimited) {
		this.setRoleTypesExpected();
		ArrayList answer = new ArrayList();

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, studyId);
		ArrayList alist;
		if (isLimited) {
			alist = this.select(digester.getQuery("findAllByStudyIdAndLimit"), variables);
		} else {
			alist = this.select(digester.getQuery("findAllByStudyId"), variables);
		}
		for (Object anAlist : alist) {
			StudyUserRoleBean surb = this.getRoleFromHashMap((HashMap) anAlist);
			answer.add(surb);
		}

		return answer;

	}

	public void deleteUserRole(StudyUserRoleBean role) {
		HashMap variables = new HashMap();
		variables.put(1, role.getUserName());
		variables.put(2, role.getRole().getName());
		variables.put(3, role.getStudyId());
		String sql = digester.getQuery("deleteUserRole");
		this.execute(sql, variables);
	}

	/**
	 * Finds all user and roles in a study
	 *
	 * @param studyId
	 *            int
	 */
	public ArrayList findAllUsersByStudy(int studyId) {
		this.unsetTypeExpected();

		this.setTypeExpected(1, TypeNames.STRING);
		this.setTypeExpected(2, TypeNames.STRING);
		this.setTypeExpected(3, TypeNames.STRING);
		this.setTypeExpected(4, TypeNames.STRING);
		this.setTypeExpected(5, TypeNames.INT);
		this.setTypeExpected(6, TypeNames.INT);
		this.setTypeExpected(7, TypeNames.DATE);
		this.setTypeExpected(8, TypeNames.INT);
		this.setTypeExpected(9, TypeNames.STRING);
		this.setTypeExpected(10, TypeNames.INT);
		this.setTypeExpected(11, TypeNames.INT);

		ArrayList answer = new ArrayList();

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, studyId);
		ArrayList alist = this.select(digester.getQuery("findAllUsersByStudy"), variables);
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			StudyUserRoleBean surb = new StudyUserRoleBean();
			surb.setUserName((String) hm.get("user_name"));
			surb.setLastName((String) hm.get("last_name"));
			surb.setFirstName((String) hm.get("first_name"));
			surb.setRoleName((String) hm.get("role_name"));
			surb.setStudyName((String) hm.get("name"));
			surb.setStudyId((Integer) hm.get("study_id"));
			surb.setParentStudyId((Integer) hm.get("parent_study_id"));
			surb.setUserAccountId((Integer) hm.get("user_id"));
			Integer statusId = (Integer) hm.get("status_id");
			Date dateUpdated = (Date) hm.get("date_updated");

			surb.setUpdatedDate(dateUpdated);
			surb.setStatus(Status.get(statusId));
			answer.add(surb);
		}

		return answer;

	}

	/**
	 * Finds all roles (including roles with status Removed) in a study
	 *
	 * @param studyId
	 *            int
	 */
	public ArrayList findAllRolesByStudy(int studyId) {
		this.unsetTypeExpected();

		this.setTypeExpected(1, TypeNames.STRING);
		this.setTypeExpected(2, TypeNames.STRING);
		this.setTypeExpected(3, TypeNames.STRING);
		this.setTypeExpected(4, TypeNames.STRING);
		this.setTypeExpected(5, TypeNames.INT);
		this.setTypeExpected(6, TypeNames.INT);
		this.setTypeExpected(7, TypeNames.DATE);
		this.setTypeExpected(8, TypeNames.INT);
		this.setTypeExpected(9, TypeNames.STRING);
		this.setTypeExpected(10, TypeNames.INT);
		this.setTypeExpected(11, TypeNames.INT);

		ArrayList answer = new ArrayList();

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, studyId);
		ArrayList alist = this.select(digester.getQuery("findAllRolesByStudy"), variables);
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			StudyUserRoleBean surb = new StudyUserRoleBean();
			surb.setUserName((String) hm.get("user_name"));
			surb.setLastName((String) hm.get("last_name"));
			surb.setFirstName((String) hm.get("first_name"));
			surb.setRoleName((String) hm.get("role_name"));
			surb.setStudyName((String) hm.get("name"));
			surb.setStudyId((Integer) hm.get("study_id"));
			surb.setParentStudyId((Integer) hm.get("parent_study_id"));
			surb.setUserAccountId((Integer) hm.get("user_id"));
			Integer statusId = (Integer) hm.get("status_id");
			Date dateUpdated = (Date) hm.get("date_updated");

			surb.setUpdatedDate(dateUpdated);
			surb.setStatus(Status.get(statusId));
			answer.add(surb);
		}

		return answer;

	}

	public ArrayList findAllUsersByStudyOrSite(int studyId, int parentStudyId, int studySubjectId) {
		this.unsetTypeExpected();

		this.setTypeExpected(1, TypeNames.STRING);
		this.setTypeExpected(2, TypeNames.STRING);
		this.setTypeExpected(3, TypeNames.STRING);
		this.setTypeExpected(4, TypeNames.STRING);
		this.setTypeExpected(5, TypeNames.INT);
		this.setTypeExpected(6, TypeNames.INT);
		this.setTypeExpected(7, TypeNames.DATE);
		this.setTypeExpected(8, TypeNames.INT);
		this.setTypeExpected(9, TypeNames.STRING);
		this.setTypeExpected(10, TypeNames.INT);
		this.setTypeExpected(11, TypeNames.INT);

		ArrayList answer = new ArrayList();

		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, parentStudyId);
		variables.put(3, studySubjectId);
		ArrayList alist = this.select(digester.getQuery("findAllUsersByStudyOrSite"), variables);
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			StudyUserRoleBean surb = new StudyUserRoleBean();
			surb.setUserName((String) hm.get("user_name"));
			surb.setLastName((String) hm.get("last_name"));
			surb.setFirstName((String) hm.get("first_name"));
			surb.setRoleName((String) hm.get("role_name"));
			surb.setStudyName((String) hm.get("name"));
			surb.setStudyId((Integer) hm.get("study_id"));
			surb.setParentStudyId((Integer) hm.get("parent_study_id"));
			surb.setUserAccountId((Integer) hm.get("user_id"));
			Integer statusId = (Integer) hm.get("status_id");
			Date dateUpdated = (Date) hm.get("date_updated");

			surb.setUpdatedDate(dateUpdated);
			surb.setStatus(Status.get(statusId));
			answer.add(surb);
		}

		return answer;

	}

	public Collection findPrivilegesByRole(int roleId) {
		this.setPrivilegeTypesExpected();
		ArrayList al = new ArrayList();
		HashMap variables = new HashMap();
		variables.put(1, roleId);
		ArrayList alist = this.select(digester.getQuery("findPrivilegesByRole"), variables);
		for (Object anAlist : alist) {
			Privilege pb = this.getPrivilegeFromHashMap((HashMap) anAlist);
			al.add(pb);
		}
		return al;
	}

	public Collection findPrivilegesByRoleName(String roleName) {
		this.setPrivilegeTypesExpected();
		ArrayList al = new ArrayList();
		HashMap variables = new HashMap();
		variables.put(1, roleName);
		ArrayList alist = this.select(digester.getQuery("findPrivilegesByRoleName"), variables);
		for (Object anAlist : alist) {
			Privilege p = this.getPrivilegeFromHashMap((HashMap) anAlist);
			al.add(p);
		}
		return al;
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	public StudyUserRoleBean updateStudyUserRole(StudyUserRoleBean s, String userName) {
		HashMap variables = new HashMap();

		variables.put(1, s.getRoleName());
		variables.put(2, s.getStatus().getId());
		variables.put(3, s.getUpdaterId());
		variables.put(4, s.getStudyId());
		variables.put(5, userName);

		String sql = digester.getQuery("updateStudyUserRole");
		this.execute(sql, variables);

		return s;
	}

	public StudyUserRoleBean findRoleByUserNameAndStudyId(String userName, int studyId) {
		Collection roles = findAllRolesByUserName(userName);

		for (Object role : roles) {
			StudyUserRoleBean s = (StudyUserRoleBean) role;
			if (s.getStudyId() == studyId) {
				s.setActive(true);
				return s;
			}
		}

		StudyUserRoleBean doesntExist = new StudyUserRoleBean();
		doesntExist.setActive(false);
		return doesntExist;
	}

	public int findRoleCountByUserNameAndStudyId(String userName, int studyId, int childStudyId) {

		this.setRoleTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, userName);
		variables.put(2, studyId);

		ArrayList alist;
		if (childStudyId == 0) {
			alist = this.select(digester.getQuery("findRoleCountByUserNameAndStudyId"), variables);
		} else {
			variables.put(3, childStudyId);
			alist = this.select(digester.getQuery("findRoleByUserNameAndStudyIdOrSiteId"), variables);
		}
		return alist.size();
	}

	public void setSysAdminRole(UserAccountBean uab, boolean creating) {
		HashMap variables = new HashMap();
		variables.put(1, uab.getName());

		if (uab.isSysAdmin() && !uab.isTechAdmin()) {
			// we remove first so that there are no duplicate roles
			this.execute(digester.getQuery("removeSysAdminRole"), variables);

			int ownerId = creating ? uab.getOwnerId() : uab.getUpdaterId();
			variables.put(2, ownerId);
			variables.put(3, ownerId);
			this.execute(digester.getQuery("addSysAdminRole"), variables);
		} else {
			this.execute(digester.getQuery("removeSysAdminRole"), variables);
		}
	}

	public Collection findAllByRole(String role) {
		return this.findAllByRole(role, "");
	}

	public Collection findAllByRole(String role1, String role2) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, role1);
		variables.put(2, role2);
		ArrayList alist;
		alist = this.select(digester.getQuery("findAllByRole"), variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			UserAccountBean eb = (UserAccountBean) this.getEntityFromHashMap((HashMap) anAlist, true);
			al.add(eb);
		}
		return al;
	}

	public void setPasswordTypesExpected() {
		this.unsetTypeExpected();
		// assuming select star query on "password"
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.STRING);
		this.setTypeExpected(3, TypeNames.INT);
		this.setTypeExpected(4, TypeNames.STRING);
		this.setTypeExpected(5, TypeNames.TIMESTAMP);
		this.setTypeExpected(6, TypeNames.TIMESTAMP);
	}

	public PasswordHistoryBean getPasswdHistoryFromHashMap(HashMap hm) {
		PasswordHistoryBean pwb = new PasswordHistoryBean();
		pwb.setUserName((String) hm.get("user_name"));
		pwb.setUserId((Integer) hm.get("user_id"));
		pwb.setPassword((String) hm.get("passwd"));
		pwb.setDateFirstUsed((Date) hm.get("date_first_used"));
		pwb.setDateLastUsed((Date) hm.get("date_last_used"));
		return pwb;
	}

	public void updatePasswdHistory(UserAccountBean uab) {
		if (getPasswdHistory(uab.getName(), uab.getPasswd()) == null) { // there is no record for current password
			insertPasswdHistory(uab);
		} else {
			HashMap variables = new HashMap();
			variables.put(1, new Date());
			variables.put(2, uab.getName());
			variables.put(3, uab.getPasswd());
			this.execute(digester.getQuery("updatePasswdHistory"), variables);
		}
	}

	public void insertPasswdHistory(UserAccountBean uab) {
		HashMap variables = new HashMap();
		Date passwordTimestamp = uab.getPasswdTimestamp();

		if (passwordTimestamp == null) {
			uab.setPasswdTimestamp(new Date());
			update(uab);
		}

		variables.put(1, uab.getName());
		variables.put(2, uab.getId());
		variables.put(3, uab.getPasswd());
		variables.put(4, uab.getPasswdTimestamp());
		variables.put(5, new Date());
		this.execute(digester.getQuery("insertPasswdHistory"), variables);
	}

	public PasswordHistoryBean getPasswdHistory(String userName, String password) {
		this.setPasswordTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, userName);
		variables.put(2, password);
		String sql = digester.getQuery("getPasswdHistory");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();
		ArrayList al = new ArrayList();
		while (it.hasNext()) {
			PasswordHistoryBean pwb = this.getPasswdHistoryFromHashMap((HashMap) it.next());
			al.add(pwb);
		}
		if (al.isEmpty()) {
			return null;
		}
		return (PasswordHistoryBean) al.get(0);
	}

	public Date getDateLastUsedFromPasswdHistory(String userName, String password) {
		PasswordHistoryBean pwb = getPasswdHistory(userName, password);
		if (pwb == null) {
			return null;
		}
		return pwb.getDateLastUsed();
	}

	public EntityBean findByUserEmail(String email) {
		this.setTypesExpected();
		HashMap variables = new HashMap();

		variables.put(1, email);

		ArrayList alist = this.select(digester.getQuery("findByUserEmail"), variables);
		UserAccountBean eb = new UserAccountBean();
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			eb = (UserAccountBean) this.getEntityFromHashMap((HashMap) it.next(), true);
		}
		return eb;
	}

	public int getUsersAssignedMetric() {
		int usersAssigned = 0;
		unsetTypeExpected();
		setTypeExpected(1, TypeNames.INT);
		ArrayList rows = select(digester.getQuery("usersAssignedMetric"));
		Iterator it = rows.iterator();
		if (it.hasNext()) {
			usersAssigned = (Integer) ((HashMap) it.next()).get("count");
		}
		return usersAssigned;
	}

}
