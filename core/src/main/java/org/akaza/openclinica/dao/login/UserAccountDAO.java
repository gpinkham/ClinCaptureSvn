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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.sql.DataSource;

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

/**
 * <p/>
 * UserAccountDAO, the data access object for the User_Account table in the OpenClinica 2.0 database.
 *
 * @author thickerson
 *         <p/>
 *         TODO
 *         <p/>
 *         add functions for admin use cases such as assign user to study, remove user from study, etc.
 *         <p/>
 *         add ability to get role and priv objects from database when U select
 *         <p/>
 *         expand on query to get all that from a select star?
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class UserAccountDAO extends AuditableEntityDAO {

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_USERACCOUNT;
	}

	protected void setQueryNames() {
		getCurrentPKName = "getCurrentPK";
		getNextPKName = "getNextPK";
	}

	/**
	 * Create User account DAO and set DataSource to it.
	 *
	 * @param ds
	 *            the DataSource to set.
	 */
	public UserAccountDAO(DataSource ds) {
		super(ds);
		setQueryNames();
	}

	/**
	 * Create User account DAO and set DataSource and DAODigester to it.
	 *
	 * @param ds
	 *            the DataSource to set.
	 * @param digester
	 *            the Digister to set.
	 */
	public UserAccountDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
		setQueryNames();
	}

	@Override
	public void setTypesExpected() {
		this.unsetTypeExpected();
		// assuming select star query on user_account
		int index = 1;
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // created
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // updated
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // lastvisit, changed
		// from date
		this.setTypeExpected(index++, TypeNames.TIMESTAMP); // passwd timestamp
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.BOOL);
		this.setTypeExpected(index++, TypeNames.BOOL);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.BOOL);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index, TypeNames.STRING);
	}

	/**
	 * Set Types expected for a join.
	 */
	public void setPrivilegeTypesExpected() {
		this.unsetTypeExpected();
		// assuming we are selecting privs from a join on privilege and
		// role_priv_map tables, tbh
		int index = 1;
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index, TypeNames.STRING);

	}

	/**
	 * Set Types expected for study_user_role table.
	 */
	public void setRoleTypesExpected() {
		this.unsetTypeExpected();
		// assuming select star from study_user_role
		int index = 1;
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index, TypeNames.INT);
	}

	/**
	 * This method is used to update pentaho auto login params for user.
	 *
	 * @param ub
	 *            the UserAccount
	 * @return result of check if query successful
	 */
	public boolean updatePentahoAutoLoginParams(UserAccountBean ub) {
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, ub.getPentahoUserSession());
		variables.put(index++, new Timestamp(ub.getPentahoTokenDate().getTime()));
		variables.put(index, ub.getId());

		String sql = digester.getQuery("updatePentahoAutoLoginParams");
		this.execute(sql, variables);

		return this.isQuerySuccessful();
	}

	/**
	 * Update UserAccountBean.
	 *
	 * @param eb
	 *            - the UserAccountBean to update.
	 * @return updated UserAccountBean.
	 */
	public EntityBean update(EntityBean eb) {
		UserAccountBean uab = (UserAccountBean) eb;
		HashMap variables = new HashMap();
		HashMap nullVars = new HashMap();

		int index = 1;
		variables.put(index++, uab.getName());
		variables.put(index++, uab.getPasswd());
		variables.put(index++, uab.getFirstName());
		variables.put(index++, uab.getLastName());
		variables.put(index++, uab.getEmail());
		if (uab.getActiveStudyId() == 0) {
			nullVars.put(index, TypeNames.INT);
			variables.put(index++, null);
		} else {
			variables.put(index++, uab.getActiveStudyId());
		}
		variables.put(index++, uab.getInstitutionalAffiliation());
		variables.put(index++, uab.getStatus().getId());
		variables.put(index++, uab.getUpdaterId());
		if (uab.getLastVisitDate() == null) {
			nullVars.put(index, TypeNames.TIMESTAMP);
			variables.put(index++, null);
		} else {
			variables.put(index++, new Timestamp(uab.getLastVisitDate().getTime()));
		}
		if (uab.getPasswdTimestamp() == null) {
			nullVars.put(index, TypeNames.TIMESTAMP);
			variables.put(index++, null);
		} else {
			variables.put(index++, new Timestamp(uab.getPasswdTimestamp().getTime()));
		}
		variables.put(index++, uab.getPasswdChallengeQuestion());
		variables.put(index++, uab.getPasswdChallengeAnswer());
		variables.put(index++, uab.getPhone());

		if (uab.isTechAdmin()) {
			variables.put(index++, UserType.TECHADMIN.getId());
		} else if (uab.isSysAdmin()) {
			variables.put(index++, UserType.SYSADMIN.getId());
		} else {
			variables.put(index++, UserType.USER.getId());
		}

		variables.put(index++, uab.getAccountNonLocked());
		variables.put(index++, uab.getLockCounter());
		variables.put(index++, uab.getRunWebservices());

		variables.put(index++, uab.getEnabled());
		variables.put(index++, uab.getUserTimeZoneId());

		variables.put(index, uab.getId());

		String sql = digester.getQuery("update");
		this.execute(sql, variables, nullVars);

		if (!this.isQuerySuccessful()) {
			eb.setId(0);
			logger.warn("query failed: " + sql);
		}

		return eb;
	}

	/**
	 * Updates UserAccountBean's status.
	 *
	 * @param userAccountBean
	 *            UserAccountBean
	 */
	public void updateStatus(UserAccountBean userAccountBean) {
		int ind = 1;
		HashMap variables = new HashMap();
		variables.put(ind++, userAccountBean.getStatus().getId());
		variables.put(ind++, userAccountBean.getUpdaterId());
		variables.put(ind, userAccountBean.getId());
		this.execute(digester.getQuery("updateStatus"), variables);
	}

	/**
	 * DeleteTestOnly, used only to clean up after unit testing.
	 *
	 * @param name
	 *            String
	 */
	public void deleteTestOnly(String name) {
		HashMap variables = new HashMap();
		variables.put(1, name);
		this.execute(digester.getQuery("deleteTestOnly"), variables);
	}

	/**
	 * This method is used to delete UserAccountBean.
	 *
	 * @param u
	 *            the UserAccountBean to delete.
	 */
	public void delete(UserAccountBean u) {
		HashMap variables;

		int index = 1;
		variables = new HashMap();
		variables.put(index, u.getName());
		this.execute(digester.getQuery("deleteStudyUserRolesIncludeAutoRemove"), variables);

		variables.put(index++, u.getUpdaterId());
		variables.put(index, u.getId());
		this.execute(digester.getQuery("delete"), variables);
	}

	/**
	 * Restore UserAccountBean.
	 *
	 * @param u
	 *            the UserAccountBean to restore.
	 */
	public void restore(UserAccountBean u) {
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, u.getPasswd());
		variables.put(index++, u.getUpdaterId());
		variables.put(index, u.getId());
		this.execute(digester.getQuery("restore"), variables);

		variables = new HashMap();
		int index2 = 1;
		variables.put(index2, u.getName());
		this.execute(digester.getQuery("restoreStudyUserRolesByUserID"), variables);
	}

	/**
	 * Set new count number for UserAccountBean by id.
	 *
	 * @param id
	 *            the user id.
	 * @param newCounterNumber
	 *            the new counter number to set.
	 */
	public void updateLockCounter(Integer id, Integer newCounterNumber) {
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, newCounterNumber);
		variables.put(index, id);
		this.execute(digester.getQuery("updateLockCounter"), variables);
	}

	/**
	 * Lock UserAccountBean.
	 *
	 * @param id
	 *            the ID of the user, which will be locked.
	 */
	public void lockUser(Integer id) {
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, false);
		variables.put(index++, Status.LOCKED.getId());
		variables.put(index, id);
		this.execute(digester.getQuery("lockUser"), variables);
	}

	/**
	 * Create UserAccountBean.
	 *
	 * @param eb
	 *            - the UserAccountBean to create.
	 * @return created UserAccountBean.
	 */
	public EntityBean create(EntityBean eb) {
		UserAccountBean uab = (UserAccountBean) eb;
		HashMap variables = new HashMap();
		int id = getNextPK();
		int index = 1;
		variables.put(index++, id);
		variables.put(index++, uab.getName());
		variables.put(index++, uab.getPasswd());
		variables.put(index++, uab.getFirstName());
		variables.put(index++, uab.getLastName());
		variables.put(index++, uab.getEmail());
		variables.put(index++, uab.getActiveStudyId());
		variables.put(index++, uab.getInstitutionalAffiliation());
		variables.put(index++, uab.getStatus().getId());
		variables.put(index++, uab.getOwnerId());
		variables.put(index++, uab.getPasswdChallengeQuestion());
		variables.put(index++, uab.getPasswdChallengeAnswer());
		variables.put(index++, uab.getPhone());

		if (uab.isTechAdmin()) {
			variables.put(index++, UserType.TECHADMIN.getId());
		} else if (uab.isSysAdmin()) {
			variables.put(index++, UserType.SYSADMIN.getId());
		} else {
			variables.put(index++, UserType.USER.getId());
		}

		variables.put(index++, uab.getRunWebservices());
		variables.put(index, uab.getUserTimeZoneId());

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

	/**
	 * Create StudyUserRoleBean for user.
	 *
	 * @param user
	 *            for which role will be created.
	 * @param studyRole
	 *            which will be created.
	 * @return created StudyUserRoleBean.
	 */
	public StudyUserRoleBean createStudyUserRole(UserAccountBean user, StudyUserRoleBean studyRole) {
		Locale currentLocale = ResourceBundleProvider.getLocale();
		ResourceBundleProvider.updateLocale(Locale.US);
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, studyRole.getRoleCode());
		variables.put(index++, studyRole.getStudyId());
		variables.put(index++, studyRole.getStatus().getId());
		variables.put(index++, user.getName());
		variables.put(index, studyRole.getOwnerId());
		this.execute(digester.getQuery("insertStudyUserRole"), variables);
		ResourceBundleProvider.updateLocale(currentLocale);
		return studyRole;
	}

	/**
	 * This method is used to check if UserAccountBeanHave any role in the study.
	 *
	 * @param user
	 *            for which role will be searched.
	 * @param studyRole
	 *            which will be searched..
	 * @return the UserAccountBean.
	 */
	public UserAccountBean findStudyUserRole(UserAccountBean user, StudyUserRoleBean studyRole) {
		this.setTypesExpected();
		int index = 1;
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index, TypeNames.STRING);
		HashMap variables = new HashMap();
		int index2 = 1;
		variables.put(index2++, studyRole.getRoleCode());
		variables.put(index2++, studyRole.getStudyId());
		variables.put(index2++, studyRole.getStatus().getId());
		variables.put(index2, user.getName());

		ArrayList alist = this.select(digester.getQuery("findStudyUserRole"), variables);
		UserAccountBean eb = new UserAccountBean();
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			eb.setName((String) ((HashMap) it.next()).get("user_name"));
		}
		return eb;
	}

	/**
	 * Get UserAccountBean from HashMap.
	 *
	 * @param hm
	 *            the HashMap from which UserAccountBean will be taken.
	 * @return the UserAccountBean.
	 */
	public Object getEntityFromHashMap(HashMap hm) {
		return this.getEntityFromHashMap(hm, true);
	}

	/**
	 * Get StudyUserRoleBean from HashMap.
	 *
	 * @param hm
	 *            the HashMap from which StudyUserRoleBean will be taken.
	 * @return the StudyUserRoleBean.
	 */
	public StudyUserRoleBean getRoleFromHashMap(HashMap hm) {
		StudyUserRoleBean surb = new StudyUserRoleBean();

		Date dateCreated = (Date) hm.get("date_created");
		Date dateUpdated = (Date) hm.get("date_updated");
		Integer statusId = (Integer) hm.get("status_id");
		Integer studyId = (Integer) hm.get("study_id");
		surb.setUserName((String) hm.get("user_name"));
		surb.setName((String) hm.get("role_name"));
		surb.setRoleName((String) hm.get("role_name"));
		surb.setPrimaryKey((Integer) hm.get("study_user_role_id"));
		surb.setCreatedDate(dateCreated);
		surb.setUpdatedDate(dateUpdated);
		surb.setStatus(Status.get(statusId));
		surb.setStudyId(studyId);
		return surb;
	}

	/**
	 * Get Privilege from HashMap.
	 *
	 * @param hm
	 *            the HashMap from which Privilege will be taken.
	 * @return the Privilege.
	 */
	public Privilege getPrivilegeFromHashMap(HashMap hm) {
		Integer privId = (Integer) hm.get("priv_id");

		return Privilege.get(privId);
	}

	/**
	 * Get UserAccountBean from HashMap.
	 *
	 * @param hm
	 *            the HashMap from which UserAccountBean will be taken.
	 * @param findOwner
	 *            the boolean marker to check if owner should be found to.
	 * @return the UserAccountBean.
	 */
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
		eb.setUserTimeZoneId((String) hm.get("time_zone_id"));
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

	/**
	 * Find all UserAccountBeans.
	 *
	 * @return the Collection of the UserAccountBeans.
	 */
	public Collection findAll() {
		return findAllByLimit(false);
	}

	/**
	 * Find all UserAccountBeans with limit.
	 *
	 * @param hasLimit
	 *            the boolean parameter to check if there are some limit.
	 * @return the Collection of the UserAccountBeans.
	 */
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
	 * Find all UserAccountBean.
	 *
	 * @param strOrderByColumn
	 *            the Column using which result will be ordered.
	 * @param blnAscendingSort
	 *            the boolean parameter.
	 * @param strSearchPhrase
	 *            the search phrase.
	 * @return ArrayList.
	 */
	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	/**
	 * Find UserAccountBean by ID (PK).
	 *
	 * @param id
	 *            the ID to search.
	 * @return the UserAccountBean.
	 */
	public EntityBean findByPK(int id) {
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, id);

		String sql = digester.getQuery("findByPK");
		ArrayList alist = this.select(sql, variables);
		UserAccountBean eb = new UserAccountBean();
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (UserAccountBean) this.getEntityFromHashMap((HashMap) it.next(), true);
		}

		return eb;
	}

	/**
	 * Find UserAccountBean by ID (PK).
	 *
	 * @param id
	 *            the ID to search.
	 * @param findOwner
	 *            the boolean marker to check if owner should be found to.
	 * @return the UserAccountBean.
	 */
	public EntityBean findByPK(int id, boolean findOwner) {
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, id);
		ArrayList alist = this.select(digester.getQuery("findByPK"), variables);
		UserAccountBean eb = new UserAccountBean();
		Iterator it = alist.iterator();
		if (it.hasNext()) {
			eb = (UserAccountBean) this.getEntityFromHashMap((HashMap) it.next(), findOwner);
		}
		return eb;
	}

	/**
	 * Find UserAccountBean by user name.
	 *
	 * @param name
	 *            the user name by which user will be found.
	 * @return the UserAccountBean.
	 */
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
	 * Finds all the studies with roles for a user.
	 *
	 * @param user
	 *            UserAccountBean
	 * @param allStudies
	 *            The result of calling StudyDAO.findAll();
	 * @return the UserAccountBean.
	 */
	public ArrayList findStudyByUser(UserAccountBean user, ArrayList allStudies) {
		this.unsetTypeExpected();
		int index = 1;
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index, TypeNames.STRING);
		HashMap allStudyUserRoleBeans = new HashMap();

		HashMap variables = new HashMap();
		variables.put(1, user.getName());
		ArrayList alist = this.select(digester.getQuery("findStudyByUser"), variables);

		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			String roleName = user.getName().equals(UserAccountBean.ROOT)
					? user.getSysAdminRole().getRoleCode()
					: (String) hm.get("role_name");
			String studyName = (String) hm.get("name");
			Integer studyId = (Integer) hm.get("study_id");
			StudyUserRoleBean sur = new StudyUserRoleBean();
			sur.setRoleName(roleName);
			sur.setStudyId(studyId);
			sur.setStudyName(studyName);
			sur.setRole(user.getName().equals(UserAccountBean.ROOT)
					? user.getSysAdminRole().getRole()
					: Role.getByName(roleName));
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

	/**
	 * This method is used to find all StudyUserRoleBeans for some user.
	 *
	 * @param userName
	 *            the name of the user for which all StudyUserRoleBeans will be found.
	 * @return the Collection of the StudyUserRoleBeans.
	 */
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
	 * Finds all user and roles in a study.
	 *
	 * @param studyId
	 *            int.
	 * @return ArrayList of UserAccountBeans.
	 */
	public ArrayList findAllByStudyId(int studyId) {

		return findAllUsersByStudyIdAndLimit(studyId, false);
	}

	/**
	 * Finds all user and roles in a study.
	 *
	 * @param studyId
	 *            the ID of the study.
	 * @param isLimited
	 *            boolean
	 * @return ArrayList of UserAccountBeans.
	 */
	public ArrayList findAllUsersByStudyIdAndLimit(int studyId, boolean isLimited) {
		this.setRoleTypesExpected();
		ArrayList answer = new ArrayList();

		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, studyId);
		variables.put(index, studyId);
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

	/**
	 * Delete StudyUserRoleBean.
	 *
	 * @param role
	 *            the StudyUserRoleBean to delete.
	 */
	public void deleteUserRole(StudyUserRoleBean role) {
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, role.getUserName());
		variables.put(index++, role.getRole().getCode());
		variables.put(index, role.getStudyId());
		String sql = digester.getQuery("deleteUserRole");
		this.execute(sql, variables);
	}

	/**
	 * Finds all user and roles in a study.
	 *
	 * @param studyId
	 *            int
	 * @return ArrayList of UserAccountBeans.
	 */
	public ArrayList findAllUsersByStudy(int studyId) {
		this.unsetTypeExpected();
		int index = 1;
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index, TypeNames.INT);

		ArrayList answer = new ArrayList();

		HashMap variables = new HashMap();
		int index2 = 1;
		variables.put(index2++, studyId);
		variables.put(index2, studyId);
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
	 * Finds all roles (including roles with status Removed) in a study.
	 *
	 * @param studyId
	 *            int.
	 * @return ArrayList of UserAccountBeans.
	 */
	public ArrayList findAllRolesByStudy(int studyId) {
		this.unsetTypeExpected();
		int index = 1;
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index, TypeNames.INT);

		ArrayList answer = new ArrayList();

		HashMap variables = new HashMap();
		int index2 = 1;
		variables.put(index2++, studyId);
		variables.put(index2, studyId);
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

	/**
	 * Find all StudyUserRoleBeans by Study or Site.
	 *
	 * @param studyId
	 *            the ID of the study.
	 * @param parentStudyId
	 *            the ID of the parent study if exists.
	 * @param studySubjectId
	 *            the study subject ID to search by.
	 * @return the ArrayList of the UserAccountBeans.
	 */
	public ArrayList findAllUsersByStudyOrSite(int studyId, int parentStudyId, int studySubjectId) {
		this.unsetTypeExpected();
		int index = 1;
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index, TypeNames.INT);

		ArrayList answer = new ArrayList();
		int index2 = 1;
		HashMap variables = new HashMap();
		variables.put(index2++, studyId);
		variables.put(index2++, parentStudyId);
		variables.put(index2, studySubjectId);
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

	/**
	 * Find all privileges by role ID.
	 *
	 * @param roleId
	 *            the ID of the role to search by.
	 * @return Collection of the Privileges.
	 */
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

	/**
	 * Find all privileges by role name.
	 *
	 * @param roleName
	 *            the name of the role to search by.
	 * @return Collection of the Privileges.
	 */
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

	/**
	 * Find all by permission.
	 *
	 * @param objCurrentUser
	 *            the user to search.
	 * @param intActionType
	 *            the action type.
	 * @param strOrderByColumn
	 *            the Column using which result will be ordered.
	 * @param blnAscendingSort
	 *            the boolean parameter.
	 * @param strSearchPhrase
	 *            the search phrase.
	 * @return Collection
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	/**
	 * Find all by permission.
	 *
	 * @param objCurrentUser
	 *            the user to search.
	 * @param intActionType
	 *            the action type.
	 * @return Collection.
	 */
	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	/**
	 * Update StudyUserRoleBean.
	 *
	 * @param s
	 *            the StudyUserRoleBean to update.
	 * @param userName
	 *            the the name of the user for which all StudyUserRoleBeans will be updated.
	 * @return updated StudyUserRoleBean.
	 */
	public StudyUserRoleBean updateStudyUserRole(StudyUserRoleBean s, String userName) {
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, s.getRoleCode());
		variables.put(index++, s.getStatus().getId());
		variables.put(index++, s.getUpdaterId());
		variables.put(index++, s.getStudyId());
		variables.put(index, userName);

		String sql = digester.getQuery("updateStudyUserRole");
		this.execute(sql, variables);

		return s;
	}

	/**
	 * Find StudyUserRoleBean by user name and study ID.
	 *
	 * @param userName
	 *            the name of the user for which StudyUserRoleBean will be found.
	 * @param studyId
	 *            the ID of the study.
	 * @return StudyUserRoleBean.
	 */
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

	/**
	 * Find count of the roles for user by user name, study ID and site ID.
	 *
	 * @param userName
	 *            the name of the user for which all StudyUserRoleBeans will be found.
	 * @param studyId
	 *            the ID of the study.
	 * @param childStudyId
	 *            the ID of the site.
	 * @return count of the roles.
	 */
	public int findRoleCountByUserNameAndStudyId(String userName, int studyId, int childStudyId) {

		this.setRoleTypesExpected();
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, userName);
		variables.put(index++, studyId);

		ArrayList alist;
		if (childStudyId == 0) {
			alist = this.select(digester.getQuery("findRoleCountByUserNameAndStudyId"), variables);
		} else {
			variables.put(index, childStudyId);
			alist = this.select(digester.getQuery("findRoleByUserNameAndStudyIdOrSiteId"), variables);
		}
		return alist.size();
	}

	/**
	 * Set sysadmin role for UserAccountBean.
	 *
	 * @param uab
	 *            the UserAccountBean for which role will be set.
	 * @param creating
	 *            the boolean param to check if user should be updated or created.
	 */
	public void setSysAdminRole(UserAccountBean uab, boolean creating) {
		HashMap variables = new HashMap();
		int index = 1;
		variables.put(index++, uab.getName());

		if (uab.isSysAdmin() && !uab.isTechAdmin()) {
			// we remove first so that there are no duplicate roles
			this.execute(digester.getQuery("removeSysAdminRole"), variables);

			int ownerId = creating ? uab.getOwnerId() : uab.getUpdaterId();
			variables.put(index++, ownerId);
			variables.put(index, ownerId);
			this.execute(digester.getQuery("addSysAdminRole"), variables);
		} else {
			this.execute(digester.getQuery("removeSysAdminRole"), variables);
		}
	}

	/**
	 * Find all UserAccountBeans by role name.
	 *
	 * @param role
	 *            the name of the role.
	 * @return collection of the UserAccountBeans.
	 */
	public Collection findAllByRole(String role) {
		return this.findAllByRole(role, "");
	}

	/**
	 * Find all UserAccountBeans by role names.
	 *
	 * @param role1
	 *            the name of the role.
	 * @param role2
	 *            the name of the role.
	 * @return collection of the UserAccountBeans.
	 */
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

	/**
	 * Set expected types for "password" query.
	 */
	public void setPasswordTypesExpected() {
		this.unsetTypeExpected();
		// assuming select star query on "password"
		int index = 1;
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.INT);
		this.setTypeExpected(index++, TypeNames.STRING);
		this.setTypeExpected(index++, TypeNames.TIMESTAMP);
		this.setTypeExpected(index, TypeNames.TIMESTAMP);
	}

	/**
	 * Get Password History from the HashMap.
	 *
	 * @param hm
	 *            the HashMap from which Password History will be taken.
	 * @return the Password History.
	 */
	public PasswordHistoryBean getPasswdHistoryFromHashMap(HashMap hm) {
		PasswordHistoryBean pwb = new PasswordHistoryBean();
		pwb.setUserName((String) hm.get("user_name"));
		pwb.setUserId((Integer) hm.get("user_id"));
		pwb.setPassword((String) hm.get("passwd"));
		pwb.setDateFirstUsed((Date) hm.get("date_first_used"));
		pwb.setDateLastUsed((Date) hm.get("date_last_used"));
		return pwb;
	}

	/**
	 * Update Password History for the UserAccountBean.
	 *
	 * @param uab
	 *            the the UserAccountBean for which history will be updated.
	 */
	public void updatePasswdHistory(UserAccountBean uab) {
		if (getPasswdHistory(uab.getName(), uab.getPasswd()) == null) { // there is no record for current password
			insertPasswdHistory(uab);
		} else {
			HashMap variables = new HashMap();
			int index = 1;
			variables.put(index++, new Date());
			variables.put(index++, uab.getName());
			variables.put(index, uab.getPasswd());
			this.execute(digester.getQuery("updatePasswdHistory"), variables);
		}
	}

	/**
	 * Insert password history.
	 *
	 * @param uab
	 *            the the UserAccountBean for which history will be inserted.
	 */
	public void insertPasswdHistory(UserAccountBean uab) {
		HashMap variables = new HashMap();
		Date passwordTimestamp = uab.getPasswdTimestamp();

		if (passwordTimestamp == null) {
			uab.setPasswdTimestamp(new Date());
			update(uab);
		}
		int index = 1;
		variables.put(index++, uab.getName());
		variables.put(index++, uab.getId());
		variables.put(index++, uab.getPasswd());
		variables.put(index++, uab.getPasswdTimestamp());
		variables.put(index, new Date());
		this.execute(digester.getQuery("insertPasswdHistory"), variables);
	}

	/**
	 * Get password history for a user.
	 *
	 * @param userName
	 *            the name of the user for which PasswordHistory will be found.
	 * @param password
	 *            the password.
	 * @return the PasswordHistoryBean.
	 */
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

	/**
	 * Get date last used from password history.
	 *
	 * @param userName
	 *            the name of the user for which PasswordHistory will be found.
	 * @param password
	 *            the password.
	 * @return the Date of the last use.
	 */
	public Date getDateLastUsedFromPasswdHistory(String userName, String password) {
		PasswordHistoryBean pwb = getPasswdHistory(userName, password);
		if (pwb == null) {
			return null;
		}
		return pwb.getDateLastUsed();
	}

	/**
	 * Find user by email.
	 *
	 * @param email
	 *            the email by which user will be found.
	 * @return the UserAccountBean.
	 */
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

	/**
	 * Returns users assigned metric.
	 * 
	 * @param studyId
	 *            int
	 * @return int
	 */
	public int getUsersAssignedMetric(int studyId) {
		int usersAssigned = 0;
		unsetTypeExpected();
		setTypeExpected(1, TypeNames.INT);
		HashMap variables = new HashMap();
		variables.put(1, studyId);
		variables.put(2, studyId);
		ArrayList rows = select(digester.getQuery("usersAssignedMetric"), variables);
		Iterator it = rows.iterator();
		if (it.hasNext()) {
			usersAssigned = (Integer) ((HashMap) it.next()).get("count");
		}
		return usersAssigned;
	}

	/**
	 * Method checks that user is present in study.
	 * 
	 * @param userName
	 *            String
	 * @param studyId
	 *            int
	 * @return boolean
	 */
	public boolean isUserPresentInStudy(String userName, int studyId) {
		this.setRoleTypesExpected();
		HashMap variables = new HashMap();
		int ind = 1;
		variables.put(ind++, userName);
		variables.put(ind++, studyId);
		variables.put(ind, studyId);
		ArrayList resultList = this.select(digester.getQuery("isUserPresentInStudy"), variables);
		return resultList.size() > 0;
	}
}
