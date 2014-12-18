package com.clinovo.rest.service;

import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.security.PermissionChecker;
import org.akaza.openclinica.AbstractContextSentiveTest;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.hamcrest.core.IsInstanceOf;
import org.json.JSONObject;
import org.junit.After;
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
import org.springframework.test.web.servlet.MvcResult;
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

	protected StudyBean newSite;
	protected StudyBean newStudy;
	protected UserAccountBean newUser;

	protected MockHttpServletRequest request = new MockHttpServletRequest();
	protected MockHttpSession session = new MockHttpSession();

	// Managed services
	public static final String API_WRONG_MAPPING = "/wrongmapping";
	public static final String API_USER_CREATE_USER = "/user/create";
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

	private StudyBean createStudy() throws Exception {
		StudyBean study = new StudyBean();
		study.setName("study_".concat(Long.toString(timestamp)));
		study.setOwner(rootUser);
		study.setCreatedDate(new Date());
		study.setStatus(Status.PENDING);
		return (StudyBean) studyDAO.create(study);
	}

	private StudyBean createSite(int studyId) throws Exception {
		StudyBean site = new StudyBean();
		site.setName("site_".concat(Long.toString(timestamp)));
		site.setParentStudyId(studyId);
		site.setOwner(rootUser);
		site.setCreatedDate(new Date());
		site.setStatus(Status.PENDING);
		return (StudyBean) studyDAO.create(site);
	}

	private void deleteUser(UserAccountBean userAccountBean) {
		userAccountDAO.execute(
				"delete from authorities where username = '".concat(userAccountBean.getName()).concat("'"),
				new HashMap());
		userAccountDAO.execute("delete from study_user_role where user_name = '".concat(userAccountBean.getName())
				.concat("'"), new HashMap());
		userAccountDAO.execute(
				"delete from user_account where user_name = '".concat(userAccountBean.getName()).concat("'"),
				new HashMap());
	}

	private void deleteStudy(StudyBean studyBean) {
		studyDAO.execute("delete from study where study_id = ".concat(Integer.toString(studyBean.getId())),
				new HashMap());
	}

	protected void createNewStudy() throws Exception {
		newStudy = createStudy();
		assertTrue(newStudy.getId() > 0);
	}

	protected void createNewSite(int studyId) throws Exception {
		newSite = createSite(studyId);
		assertTrue(newSite.getId() > 0);
	}

	protected void createNewUser(int studyId, UserType userType, Role role) throws Exception {
		String userName = "userName_".concat(Long.toString(timestamp));
		String firstName = "firstName_".concat(Long.toString(timestamp));
		String lastName = "lastName_".concat(Long.toString(timestamp));
		String email = "email@gmail.com";
		String phone = "375295676363";
		String company = "home";
		MvcResult result = this.mockMvc
				.perform(
						post(API_USER_CREATE_USER).param("username", userName).param("firstname", firstName)
								.param("lastname", lastName).param("email", email).param("phone", phone)
								.param("company", company).param("usertype", Integer.toString(userType.getId()))
								.param("allowsoap", "true").param("displaypassword", "true")
								.param("scope", Integer.toString(studyId))
								.param("role", Integer.toString(role.getId())).secure(true).session(session))
				.andExpect(status().isOk()).andReturn();
		String password = (String) new JSONObject(result.getResponse().getContentAsString()).get("password");
		newUser = (UserAccountBean) userAccountDAO.findByUserName(userName);
		assertTrue(newUser.getId() > 0);
		newUser.setPasswd(password);
	}

	protected void createUserWithoutRole(UserType userType, int studyId) throws Exception {
		String password = securityManager.genPassword();
		UserAccountBean userAccountBean = new UserAccountBean();
		String userName = "john_dong_".concat(Long.toString(timestamp));
		userAccountBean.setName(userName);
		userAccountBean.setFirstName("john");
		userAccountBean.setLastName("dong");
		userAccountBean.setEmail("jd@gmail.com");
		userAccountBean.setPhone("234234234234");
		userAccountBean.setInstitutionalAffiliation("Clinovo");
		userAccountBean.setActiveStudyId(studyId);
		userAccountBean.setPasswd(securityManager.encryptPassword(password, null));
		userAccountBean.setRunWebservices(false);
		userAccountBean.addUserType(userType);
		userAccountBean.setPasswdTimestamp(null);
		userAccountBean.setLastVisitDate(null);
		userAccountBean.setStatus(Status.AVAILABLE);
		userAccountBean.setPasswdChallengeQuestion("");
		userAccountBean.setPasswdChallengeAnswer("");
		userAccountBean.setOwner(rootUser);
		newUser = (UserAccountBean) userAccountDAO.create(userAccountBean);
		assertTrue(newUser.getId() > 0);
		newUser.setPasswd(password);
	}

	protected void login(String userName, UserType userType, Role role, String password, String studyName)
			throws Exception {
		session.clearAttributes();
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).secure(true).param("username", userName).param("password", password)
								.param("studyname", studyName).session(session))
				.andExpect(status().isOk())
				.andExpect(
						MockMvcResultMatchers.request().sessionAttribute(
								PermissionChecker.API_AUTHENTICATED_USER_DETAILS, IsInstanceOf.any(UserDetails.class)))
				.andExpect(
						content().string(
								"{\"username\":\"".concat(userName).concat("\",\"studyname\":\"").concat(studyName)
										.concat("\",\"role\":\"").concat(role.getCode()).concat("\",\"usertype\":\"")
										.concat(userType.getCode()).concat("\"}")));
	}

	@Before
	public void setup() throws Exception {
		super.setUp();

		setTestProperties();

		studyDAO = new StudyDAO(dataSource);
		userAccountDAO = new UserAccountDAO(dataSource);

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		ResourceBundleProvider.updateLocale(LOCALE);

		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, studyName);

		timestamp = new Date().getTime();

		studyBean = (StudyBean) studyDAO.findByName(studyName);
		rootUser = (UserAccountBean) userAccountDAO.findByUserName(userName);
	}

	@After
	public void tearDown() {
		if (newUser != null && newUser.getId() > 0) {
			deleteUser(newUser);
		}
		if (newStudy != null && newStudy.getId() > 0) {
			deleteStudy(newStudy);
		}
		if (newSite != null && newSite.getId() > 0) {
			deleteStudy(newSite);
		}
	}
}
