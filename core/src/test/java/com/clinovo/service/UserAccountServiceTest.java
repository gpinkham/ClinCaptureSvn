package com.clinovo.service;

import com.clinovo.service.impl.UserAccountServiceImpl;
import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.EntityAction;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class UserAccountServiceTest extends DefaultAppContextTest {

	private UserAccountService spyUserAccountService;

	private StudyDAO spyStudyDAO;

	private UserAccountDAO spyUserAccountDAO;

	private ResourceBundle respage = ResourceBundleProvider.getPageMessagesBundle();

	private UserAccountBean currentUser;

	@Before
	public void setUp() throws Exception {

		UserAccountService userAccountService = new UserAccountServiceImpl();
		spyUserAccountService = spy(userAccountService);

		spyStudyDAO = spy(studyDAO);
		PowerMockito.when(spyUserAccountService, spyUserAccountService.getClass().getMethod("getStudyDAO"))
				.withNoArguments().thenReturn(spyStudyDAO);

		spyUserAccountDAO = spy(userAccountDAO);
		PowerMockito.when(spyUserAccountService, spyUserAccountService.getClass().getMethod("getUserAccountDAO"))
				.withNoArguments().thenReturn(spyUserAccountDAO);

		currentUser = new UserAccountBean();
		currentUser.setId(1);
		currentUser.setActiveStudyId(1);

	}

	@Test
	public void testThatDoesUserHaveRoleInStudiesRuturnsTrue() throws Exception {

		UserAccountBean user = new UserAccountBean();

		List<StudyUserRoleBean> userRolesList = new ArrayList<StudyUserRoleBean>();

		StudyUserRoleBean roleBean = new StudyUserRoleBean();
		roleBean.setRole(Role.INVESTIGATOR);
		roleBean.setStudyId(5);
		userRolesList.add(roleBean);
		roleBean = new StudyUserRoleBean();
		roleBean.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		roleBean.setStudyId(7);
		userRolesList.add(roleBean);
		user.setRoles((ArrayList<StudyUserRoleBean>) userRolesList);

		List<StudyBean> studyList = new ArrayList<StudyBean>();
		StudyBean parentStudy = new StudyBean();
		parentStudy.setId(1);
		studyList.add(parentStudy);
		parentStudy = new StudyBean();
		parentStudy.setId(2);
		studyList.add(parentStudy);

		List<StudyBean> siteListFirst = new ArrayList<StudyBean>();
		StudyBean site = new StudyBean();
		site.setId(3);
		siteListFirst.add(site);
		site = new StudyBean();
		site.setId(4);
		siteListFirst.add(site);

		List<StudyBean> siteListSecond = new ArrayList<StudyBean>();
		site = new StudyBean();
		site.setId(5);
		siteListSecond.add(site);
		site = new StudyBean();
		site.setId(6);
		siteListSecond.add(site);

		doReturn(siteListFirst).when(spyStudyDAO).findAllByParentAndActive(studyList.get(0).getId());
		doReturn(siteListSecond).when(spyStudyDAO).findAllByParentAndActive(studyList.get(1).getId());

		assertTrue(spyUserAccountService.doesUserHaveRoleInStudies(user, studyList));
	}

	@Test
	public void testThatDoesUserHaveRoleInStudiesRuturnsFalse() throws Exception {

		UserAccountBean user = new UserAccountBean();

		List<StudyUserRoleBean> userRolesList = new ArrayList<StudyUserRoleBean>();

		StudyUserRoleBean roleBean = new StudyUserRoleBean();
		roleBean.setRole(Role.INVESTIGATOR);
		roleBean.setStudyId(11);
		userRolesList.add(roleBean);
		roleBean = new StudyUserRoleBean();
		roleBean.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		roleBean.setStudyId(25);
		userRolesList.add(roleBean);
		user.setRoles((ArrayList<StudyUserRoleBean>) userRolesList);

		List<StudyBean> studyList = new ArrayList<StudyBean>();
		StudyBean parentStudy = new StudyBean();
		parentStudy.setId(1);
		studyList.add(parentStudy);
		parentStudy = new StudyBean();
		parentStudy.setId(2);
		studyList.add(parentStudy);

		List<StudyBean> siteListFirst = new ArrayList<StudyBean>();
		StudyBean site = new StudyBean();
		site.setId(12);
		siteListFirst.add(site);
		site = new StudyBean();
		site.setId(8);
		siteListFirst.add(site);

		List<StudyBean> siteListSecond = new ArrayList<StudyBean>();
		site = new StudyBean();
		site.setId(20);
		siteListSecond.add(site);
		site = new StudyBean();
		site.setId(17);
		siteListSecond.add(site);

		doReturn(siteListFirst).when(spyStudyDAO).findAllByParentAndActive(studyList.get(0).getId());
		doReturn(siteListSecond).when(spyStudyDAO).findAllByParentAndActive(studyList.get(1).getId());

		assertFalse(spyUserAccountService.doesUserHaveRoleInStudies(user, studyList));
	}

	@Test
	public void testThatPerformActionOnStudyUserRoleReturnsTrueOnActionRemove() throws Exception {

		int userId = 1;
		int studyId = 1;
		StringBuilder messages = new StringBuilder("");

		doReturn(Boolean.TRUE).when(spyUserAccountService).removeStudyUserRole(userId, studyId, currentUser, messages,
				respage);
		assertTrue(spyUserAccountService.performActionOnStudyUserRole(userId, studyId, EntityAction.REMOVE.getId(),
				currentUser, messages, respage));
	}

	@Test
	public void testThatPerformActionOnStudyUserRoleReturnsFalseOnActionEdit() throws Exception {

		assertFalse(spyUserAccountService.performActionOnStudyUserRole(1, 1, EntityAction.EDIT.getId(), currentUser,
				new StringBuilder(""), respage));
	}

	@Test
	public void testThatPerformActionOnStudyUserRoleReturnsFalseOnInvalidAction() throws Exception {

		int invalidActionId = 7;
		assertFalse(spyUserAccountService.performActionOnStudyUserRole(1, 1, invalidActionId, currentUser,
				new StringBuilder(""), respage));
	}

	@Test
	public void testThatDeleteStudyUserRoleReturnsFalseOnInvalidUserAccount() throws Exception {

		int userId = 34;
		int studyId = 5;

		doReturn(new UserAccountBean()).when(spyUserAccountDAO).findByPK(userId);

		assertFalse(spyUserAccountService.deleteStudyUserRole(userId, studyId, currentUser, new StringBuilder(""),
				respage));
	}

	@Test
	public void testThatDeleteStudyUserRoleReturnsFalseOnInvalidStudyUserRole() throws Exception {

		int userId = 34;
		int studyId = 5;
		String userName = "Mockito";

		UserAccountBean user = new UserAccountBean();
		user.setId(userId);
		user.setName(userName);

		doReturn(user).when(spyUserAccountDAO).findByPK(userId);
		doReturn(new StudyUserRoleBean()).when(spyUserAccountDAO).findRoleByUserNameAndStudyId(user.getName(), studyId);

		assertFalse(spyUserAccountService.deleteStudyUserRole(userId, studyId, currentUser, new StringBuilder(""),
				respage));
	}

	@Test
	public void testThatDeleteStudyUserRoleLocksUserAccountSinceUserHaveNoAvailableRole() throws Exception {

		int userId = 18;
		int studyId = 1;
		String userName = "Mockito";
		String studyName = "MockitoStudy";

		StudyBean study = new StudyBean();
		study.setId(studyId);
		study.setName(studyName);

		UserAccountBean user = new UserAccountBean();
		user.setId(userId);
		user.setName(userName);
		user.setActiveStudyId(studyId);

		List<StudyUserRoleBean> userRolesList = new ArrayList<StudyUserRoleBean>();

		StudyUserRoleBean roleBean = new StudyUserRoleBean();
		roleBean.setRole(Role.INVESTIGATOR);
		roleBean.setStudyId(11);
		roleBean.setStatus(Status.AUTO_DELETED);
		userRolesList.add(roleBean);
		roleBean = new StudyUserRoleBean();
		roleBean.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		roleBean.setStudyId(studyId);
		roleBean.setStatus(Status.DELETED);
		userRolesList.add(roleBean);
		user.setRoles((ArrayList<StudyUserRoleBean>) userRolesList);

		doReturn(user).when(spyUserAccountDAO).findByPK(userId);
		doReturn(user.getRoles().get(1)).when(spyUserAccountDAO).findRoleByUserNameAndStudyId(user.getName(), studyId);
		doReturn(study).when(spyStudyDAO).findByPK(studyId);

		assertTrue(spyUserAccountService.deleteStudyUserRole(userId, studyId, currentUser, new StringBuilder(""),
				respage));

		verify(spyUserAccountDAO).deleteUserRole(user.getRoles().get(1));
		verify(spyUserAccountDAO).lockUser(user.getId());
	}

	@Test
	public void testThatDeleteStudyUserRoleWillSetNewActiveStudyIdForUser() throws Exception {

		int userId = 18;
		int studyId = 1;
		int newActiveStudyId = 11;
		String userName = "Mockito";
		String studyName = "MockitoStudy";

		StudyBean study = new StudyBean();
		study.setId(studyId);
		study.setName(studyName);

		UserAccountBean user = new UserAccountBean();
		user.setId(userId);
		user.setName(userName);
		user.setActiveStudyId(studyId);

		List<StudyUserRoleBean> userRolesList = new ArrayList<StudyUserRoleBean>();
		StudyUserRoleBean roleBean = new StudyUserRoleBean();
		roleBean.setRole(Role.INVESTIGATOR);
		roleBean.setStudyId(newActiveStudyId);
		roleBean.setStatus(Status.AVAILABLE);
		userRolesList.add(roleBean);
		roleBean = new StudyUserRoleBean();
		roleBean.setRole(Role.CLINICAL_RESEARCH_COORDINATOR);
		roleBean.setStudyId(studyId);
		roleBean.setStatus(Status.DELETED);
		userRolesList.add(roleBean);
		user.setRoles((ArrayList<StudyUserRoleBean>) userRolesList);

		doReturn(user).when(spyUserAccountDAO).findByPK(userId);
		doReturn(user.getRoles().get(1)).when(spyUserAccountDAO).findRoleByUserNameAndStudyId(user.getName(), studyId);
		doReturn(study).when(spyStudyDAO).findByPK(studyId);
		doReturn(user).when(spyUserAccountDAO).update(user);

		assertTrue(spyUserAccountService.deleteStudyUserRole(userId, studyId, currentUser, new StringBuilder(""),
				respage));

		verify(spyUserAccountDAO).deleteUserRole(user.getRoles().get(1));
		verify(spyUserAccountDAO).update(user);
		verify(spyUserAccountDAO, never()).lockUser(user.getId());
		assertEquals(newActiveStudyId, user.getActiveStudyId());
	}

	@Test
	public void testThatRemoveStudyUserRoleReturnsFalseOnInvalidUserAccount() throws Exception {

		int userId = 129;
		int studyId = 43;

		doReturn(new UserAccountBean()).when(spyUserAccountDAO).findByPK(userId);

		assertFalse(spyUserAccountService.removeStudyUserRole(userId, studyId, currentUser, new StringBuilder(""),
				respage));
	}

	@Test
	public void testThatRemoveStudyUserRoleReturnsFalseOnInvalidStudyUserRole() throws Exception {

		int userId = 129;
		int studyId = 43;
		String userName = "Mockito";

		UserAccountBean user = new UserAccountBean();
		user.setId(userId);
		user.setName(userName);

		doReturn(user).when(spyUserAccountDAO).findByPK(userId);
		doReturn(new StudyUserRoleBean()).when(spyUserAccountDAO).findRoleByUserNameAndStudyId(user.getName(), studyId);

		assertFalse(spyUserAccountService.removeStudyUserRole(userId, studyId, currentUser, new StringBuilder(""),
				respage));
	}

	@Test
	public void testThatRemoveStudyUserRoleLocksUserAccountSinceUserHaveNoAvailableRole() throws Exception {

		int userId = 3;
		int studyId = 12;
		String userName = "Mockito";
		String studyName = "MockitoStudy";

		StudyBean study = new StudyBean();
		study.setId(studyId);
		study.setName(studyName);

		UserAccountBean user = new UserAccountBean();
		user.setId(userId);
		user.setName(userName);
		user.setActiveStudyId(studyId);

		List<StudyUserRoleBean> userRolesList = new ArrayList<StudyUserRoleBean>();

		StudyUserRoleBean roleBean = new StudyUserRoleBean();
		roleBean.setRole(Role.STUDY_ADMINISTRATOR);
		roleBean.setStudyId(11);
		roleBean.setStatus(Status.DELETED);
		roleBean.setUserName(userName);
		userRolesList.add(roleBean);
		roleBean = new StudyUserRoleBean();
		roleBean.setRole(Role.STUDY_CODER);
		roleBean.setStudyId(studyId);
		roleBean.setStatus(Status.AVAILABLE);
		roleBean.setUserName(userName);
		userRolesList.add(roleBean);
		user.setRoles((ArrayList<StudyUserRoleBean>) userRolesList);

		doReturn(user).when(spyUserAccountDAO).findByPK(userId);
		doReturn(user).when(spyUserAccountDAO).findByUserName(userName);
		doReturn(user.getRoles().get(1)).when(spyUserAccountDAO).findRoleByUserNameAndStudyId(user.getName(), studyId);
		doReturn(study).when(spyStudyDAO).findByPK(studyId);

		assertTrue(spyUserAccountService.removeStudyUserRole(userId, studyId, currentUser, new StringBuilder(""),
				respage));

		verify(spyUserAccountDAO).updateStudyUserRole(user.getRoles().get(1), user.getName());
		verify(spyUserAccountDAO).lockUser(user.getId());
	}

	@Test
	public void testThatRemoveStudyUserRoleWillSetNewActiveStudyIdForUser() throws Exception {

		int userId = 8;
		int studyId = 88;
		int newActiveStudyId = 122;
		String userName = "Mockito";
		String studyName = "MockitoStudy";

		StudyBean study = new StudyBean();
		study.setId(studyId);
		study.setName(studyName);

		UserAccountBean user = new UserAccountBean();
		user.setId(userId);
		user.setName(userName);
		user.setActiveStudyId(studyId);

		List<StudyUserRoleBean> userRolesList = new ArrayList<StudyUserRoleBean>();
		StudyUserRoleBean roleBean = new StudyUserRoleBean();
		roleBean.setRole(Role.STUDY_ADMINISTRATOR);
		roleBean.setStudyId(newActiveStudyId);
		roleBean.setStatus(Status.AVAILABLE);
		roleBean.setUserName(userName);
		userRolesList.add(roleBean);
		roleBean = new StudyUserRoleBean();
		roleBean.setRole(Role.STUDY_CODER);
		roleBean.setStudyId(studyId);
		roleBean.setStatus(Status.AVAILABLE);
		roleBean.setUserName(userName);
		userRolesList.add(roleBean);
		user.setRoles((ArrayList<StudyUserRoleBean>) userRolesList);

		doReturn(user).when(spyUserAccountDAO).findByPK(userId);
		doReturn(user).when(spyUserAccountDAO).findByUserName(userName);
		doReturn(user.getRoles().get(1)).when(spyUserAccountDAO).findRoleByUserNameAndStudyId(user.getName(), studyId);
		doReturn(study).when(spyStudyDAO).findByPK(studyId);
		doReturn(user).when(spyUserAccountDAO).update(user);

		assertTrue(spyUserAccountService.removeStudyUserRole(userId, studyId, currentUser, new StringBuilder(""),
				respage));

		verify(spyUserAccountDAO).updateStudyUserRole(user.getRoles().get(1), user.getName());
		verify(spyUserAccountDAO).update(user);
		verify(spyUserAccountDAO, never()).lockUser(user.getId());
		assertEquals(newActiveStudyId, user.getActiveStudyId());
	}

	@Test
	public void testThatRestoreStudyUserRoleReturnsFalseOnInvalidUserAccount() throws Exception {

		int userId = 129;
		int studyId = 43;

		doReturn(new UserAccountBean()).when(spyUserAccountDAO).findByPK(userId);

		assertFalse(spyUserAccountService.restoreStudyUserRole(userId, studyId, currentUser, new StringBuilder(""),
				respage));
	}

	@Test
	public void testThatRestoreStudyUserRoleReturnsFalseOnInvalidStudyUserRole() throws Exception {

		int userId = 129;
		int studyId = 43;
		String userName = "Mockito";

		UserAccountBean user = new UserAccountBean();
		user.setId(userId);
		user.setName(userName);

		doReturn(user).when(spyUserAccountDAO).findByPK(userId);
		doReturn(new StudyUserRoleBean()).when(spyUserAccountDAO).findRoleByUserNameAndStudyId(user.getName(), studyId);

		assertFalse(spyUserAccountService.restoreStudyUserRole(userId, studyId, currentUser, new StringBuilder(""),
				respage));
	}

	@Test
	public void testThatRestoreStudyUserRoleWillNotRestoreUserRoleSinceTheStudyIsRemoved() throws Exception {

		int userId = 8;
		int studyId = 88;
		int removedStudyId = 123;
		String userName = "Mockito";
		String removedStudyName = "RemovedStudy";

		StudyBean removedStudy = new StudyBean();
		removedStudy.setId(removedStudyId);
		removedStudy.setName(removedStudyName);
		removedStudy.setStatus(Status.DELETED);

		UserAccountBean user = new UserAccountBean();
		user.setId(userId);
		user.setName(userName);
		user.setActiveStudyId(studyId);
		user.setStatus(Status.AVAILABLE);

		List<StudyUserRoleBean> userRolesList = new ArrayList<StudyUserRoleBean>();
		StudyUserRoleBean roleBean = new StudyUserRoleBean();
		roleBean.setRole(Role.STUDY_ADMINISTRATOR);
		roleBean.setStudyId(removedStudyId);
		roleBean.setStatus(Status.AUTO_DELETED);
		roleBean.setUserName(userName);
		userRolesList.add(roleBean);
		user.setRoles((ArrayList<StudyUserRoleBean>) userRolesList);

		doReturn(user).when(spyUserAccountDAO).findByPK(userId);
		doReturn(user).when(spyUserAccountDAO).findByUserName(userName);
		doReturn(user.getRoles().get(0)).when(spyUserAccountDAO).findRoleByUserNameAndStudyId(user.getName(),
				removedStudyId);
		doReturn(removedStudy).when(spyStudyDAO).findByPK(removedStudyId);

		assertFalse(spyUserAccountService.restoreStudyUserRole(userId, removedStudyId, currentUser, new StringBuilder(
				""), respage));

		verify(spyUserAccountDAO, never()).updateStudyUserRole(user.getRoles().get(0), user.getName());
		verify(spyUserAccountDAO, never()).update(user);
	}

	@Test
	public void testThatRestoreStudyUserRoleWillNotRestoreUserRoleSinceTheUserAccountIsRemoved() throws Exception {

		int userId = 10;
		int studyId = 213;
		String userName = "Mockito";
		String studyName = "MockitoStudy";

		StudyBean study = new StudyBean();
		study.setId(studyId);
		study.setName(studyName);
		study.setStatus(Status.AVAILABLE);

		UserAccountBean user = new UserAccountBean();
		user.setId(userId);
		user.setName(userName);
		user.setActiveStudyId(studyId);
		user.setStatus(Status.DELETED);

		List<StudyUserRoleBean> userRolesList = new ArrayList<StudyUserRoleBean>();
		StudyUserRoleBean roleBean = new StudyUserRoleBean();
		roleBean.setRole(Role.STUDY_ADMINISTRATOR);
		roleBean.setStudyId(studyId);
		roleBean.setStatus(Status.AUTO_DELETED);
		roleBean.setUserName(userName);
		userRolesList.add(roleBean);
		user.setRoles((ArrayList<StudyUserRoleBean>) userRolesList);

		doReturn(user).when(spyUserAccountDAO).findByPK(userId);
		doReturn(user).when(spyUserAccountDAO).findByUserName(userName);
		doReturn(user.getRoles().get(0)).when(spyUserAccountDAO).findRoleByUserNameAndStudyId(user.getName(), studyId);
		doReturn(study).when(spyStudyDAO).findByPK(studyId);

		assertFalse(spyUserAccountService.restoreStudyUserRole(userId, studyId, currentUser, new StringBuilder(""),
				respage));

		verify(spyUserAccountDAO, never()).updateStudyUserRole(user.getRoles().get(0), user.getName());
		verify(spyUserAccountDAO, never()).update(user);
	}

	@Test
	public void testThatRestoreStudyUserRoleWillRestoreUserRole() throws Exception {

		int userId = 10;
		int studyId = 213;
		int newActiveSstudyId = 444;
		String userName = "Mockito";
		String studyName = "MockitoStudy";

		StudyBean study = new StudyBean();
		study.setId(newActiveSstudyId);
		study.setName(studyName);
		study.setStatus(Status.AVAILABLE);

		UserAccountBean user = new UserAccountBean();
		user.setId(userId);
		user.setName(userName);
		user.setActiveStudyId(studyId);
		user.setStatus(Status.AVAILABLE);

		List<StudyUserRoleBean> userRolesList = new ArrayList<StudyUserRoleBean>();
		StudyUserRoleBean roleBean = new StudyUserRoleBean();
		roleBean.setRole(Role.STUDY_ADMINISTRATOR);
		roleBean.setStudyId(newActiveSstudyId);
		roleBean.setStatus(Status.DELETED);
		roleBean.setUserName(userName);
		userRolesList.add(roleBean);
		user.setRoles((ArrayList<StudyUserRoleBean>) userRolesList);

		doReturn(user).when(spyUserAccountDAO).findByPK(userId);
		doReturn(user).when(spyUserAccountDAO).findByUserName(userName);
		doReturn(user.getRoles().get(0)).when(spyUserAccountDAO).findRoleByUserNameAndStudyId(user.getName(),
				newActiveSstudyId);
		doReturn(study).when(spyStudyDAO).findByPK(newActiveSstudyId);

		assertTrue(spyUserAccountService.restoreStudyUserRole(userId, newActiveSstudyId, currentUser,
				new StringBuilder(""), respage));

		verify(spyUserAccountDAO).updateStudyUserRole(user.getRoles().get(0), user.getName());
		verify(spyUserAccountDAO).update(user);
		assertEquals(newActiveSstudyId, user.getActiveStudyId());
	}
}
