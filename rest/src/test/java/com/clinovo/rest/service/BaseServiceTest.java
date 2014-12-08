package com.clinovo.rest.service;

import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.security.PermissionChecker;
import org.akaza.openclinica.AbstractContextSentiveTest;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * BaseServiceTest class.
 */
@Ignore
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:servlet-context.xml")
public class BaseServiceTest extends AbstractContextSentiveTest {

	protected StudyDAO studyDAO;
	protected UserAccountDAO userAccountDAO;

	protected MockMvc mockMvc;
	protected String userName;
	protected String password;
	protected String studyName;

	protected long timestamp;

	protected StudyBean studyBean;
	protected UserAccountBean rootUser;

	protected MockHttpServletRequest request = new MockHttpServletRequest();
	protected MockHttpSession session = new MockHttpSession();

	// Managed services
	public static final String API_WRONG_MAPPING = "/wrongmapping";
	public static final String API_AUTHENTICATION = "/authentication";

	public static final Locale LOCALE = new Locale("en");

	@Autowired
	protected WebApplicationContext wac;

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	protected org.akaza.openclinica.core.SecurityManager securityManager;

	private void setTestProperties() throws Exception {
		String resource = "rest-test.properties";
		Properties prop = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream(resource);
		prop.load(stream);
		userName = prop.getProperty("rest.userName");
		password = prop.getProperty("rest.password");
		studyName = prop.getProperty("rest.studyName");
	}

	protected StudyBean createSite(int studyId) throws Exception {
		StudyBean site = new StudyBean();
		site.setName("site_".concat(Long.toString(timestamp)));
		site.setParentStudyId(studyId);
		site.setOwner(rootUser);
		site.setCreatedDate(new Date());
		site.setStatus(Status.PENDING);
		return (StudyBean) studyDAO.create(site);
	}

	protected UserAccountBean createUser(String password, Role role, UserType userType, int studyId) throws Exception {
		UserAccountBean userAccountBean = new UserAccountBean();
		String userName = "john_dong_".concat(Long.toString(timestamp));
		userAccountBean.setName(userName);
		userAccountBean.setFirstName("john");
		userAccountBean.setLastName("dong");
		userAccountBean.setEmail("jd@gmail.com");
		userAccountBean.setPhone("234234234234");
		userAccountBean.setInstitutionalAffiliation("Clinovo");

		String passwordHash = securityManager.encrytPassword(password, null);

		userAccountBean.setPasswd(passwordHash);

		userAccountBean.setPasswdTimestamp(null);
		userAccountBean.setLastVisitDate(null);

		userAccountBean.setStatus(Status.AVAILABLE);
		userAccountBean.setPasswdChallengeQuestion("");
		userAccountBean.setPasswdChallengeAnswer("");
		userAccountBean.setOwner(rootUser);
		userAccountBean.setRunWebservices(false);

		userAccountBean.setActiveStudyId(studyId);

		if (role != null) {
			StudyUserRoleBean activeStudyRole = new StudyUserRoleBean();
			activeStudyRole.setStudyId(studyId);
			activeStudyRole.setRoleName(role.getCode());
			activeStudyRole.setStatus(Status.AVAILABLE);
			activeStudyRole.setOwner(rootUser);
			userAccountBean.addRole(activeStudyRole);
		}

		userAccountBean.addUserType(userType);
		return (UserAccountBean) userAccountDAO.create(userAccountBean);
	}

	protected void deleteUser(UserAccountBean userAccountBean) {
		userAccountDAO.execute("delete from study_user_role where user_name = '".concat(userAccountBean.getName())
				.concat("'"), new HashMap());
		userAccountDAO.execute(
				"delete from user_account where user_name = '".concat(userAccountBean.getName()).concat("'"),
				new HashMap());
	}

	protected void deleteStudy(StudyBean studyBean) {
		studyDAO.execute("delete from study where study_id = ".concat(Integer.toString(studyBean.getId())),
				new HashMap());
	}

	@Before
	public void setup() throws Exception {
		super.setUp();

		setTestProperties();

		studyDAO = new StudyDAO(dataSource);
		userAccountDAO = new UserAccountDAO(dataSource);

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		ResourceBundleProvider.updateLocale(LOCALE);

		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).secure(true).param("username", userName).param("password", password)
								.param("studyname", studyName).session(session))
				.andExpect(status().isOk())
				.andExpect(
						MockMvcResultMatchers.request().sessionAttribute(
								PermissionChecker.API_AUTHENTICATED_USER_DETAILS, IsInstanceOf.any(UserDetails.class)))
				.andExpect(
						content()
								.string("{\"username\":\"".concat(userName).concat("\",\"studyname\":\"")
										.concat(studyName).concat("\",\"role\":\"")
										.concat(Role.SYSTEM_ADMINISTRATOR.getCode()).concat("\",\"usertype\":\"")
										.concat(UserType.SYSADMIN.getCode()).concat("\"}")));

		timestamp = new Date().getTime();

		studyBean = (StudyBean) studyDAO.findByName(studyName);
		rootUser = (UserAccountBean) userAccountDAO.findByUserName(userName);
	}
}
