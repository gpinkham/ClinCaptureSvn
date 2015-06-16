package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.akaza.openclinica.AbstractContextSentiveTest;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.cdisc.ns.odm.v130.ODM;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.StringContains;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.http.MediaType;
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

import com.clinovo.rest.filter.RestFilter;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.odm.RestOdmContainer;
import com.clinovo.rest.security.PermissionChecker;

@Ignore
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:servlet-context.xml")
@SuppressWarnings("rawtypes")
public class BaseServiceTest extends AbstractContextSentiveTest {

	protected MediaType mediaType = MediaType.APPLICATION_JSON;

	protected MvcResult result;

	protected RestOdmContainer restOdmContainer;

	protected Schema schema;

	protected StudyDAO studyDAO;
	protected UserAccountDAO userAccountDAO;

	protected MockMvc mockMvc;

	protected String userName;
	protected String password;
	protected String studyName;
	protected long timestamp;

	protected StudyBean studyBean;
	protected UserAccountBean userBean;

	protected StudyBean newSite;
	protected StudyBean newStudy;
	protected UserAccountBean newUser;

	protected MockHttpServletRequest request = new MockHttpServletRequest();
	protected MockHttpSession session = new MockHttpSession();

	// Managed services
	public static final String API_EVENT = "/event";
	public static final String API_EVENT_ADD_CRF = "/event/addCrf";
	public static final String API_EVENT_CREATE = "/event/create";
	public static final String API_WRONG_MAPPING = "/wrongmapping";
	public static final String API_USER_CREATE = "/user/create";
	public static final String API_AUTHENTICATION = "/authentication";
	public static final String API_WADL = "/wadl";
	public static final String API_ODM = "/odm";

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
		study.setOwner(userBean);
		study.setCreatedDate(new Date());
		study.setStatus(Status.PENDING);
		return (StudyBean) studyDAO.create(study);
	}

	private StudyBean createSite(int studyId) throws Exception {
		StudyBean site = new StudyBean();
		site.setName("site_".concat(Long.toString(timestamp)));
		site.setParentStudyId(studyId);
		site.setOwner(userBean);
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

	protected void createNewUser(UserType userType, Role role) throws Exception {
		String userName = "userName_".concat(Long.toString(timestamp));
		String firstName = "firstName_".concat(Long.toString(timestamp));
		String lastName = "lastName_".concat(Long.toString(timestamp));
		String email = "email@gmail.com";
		String phone = "375295676363";
		String company = "home";
		MvcResult result = this.mockMvc
				.perform(
						post(API_USER_CREATE).accept(mediaType).param("username", userName)
								.param("firstname", firstName).param("lastname", lastName).param("email", email)
								.param("phone", phone).param("company", company)
								.param("usertype", Integer.toString(userType.getId())).param("allowsoap", "true")
								.param("displaypassword", "true").param("role", Integer.toString(role.getId()))
								.secure(true).session(session)).andExpect(status().isOk()).andReturn();
		String password = mediaType.equals(MediaType.APPLICATION_JSON)
				? (String) new JSONObject(result.getResponse().getContentAsString()).get("password")
				: result.getResponse().getContentAsString().split("<Password>")[1].split("</Password>")[0];
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
		userAccountBean.setOwner(userBean);
		newUser = (UserAccountBean) userAccountDAO.create(userAccountBean);
		assertTrue(newUser.getId() > 0);
		newUser.setPasswd(password);
	}

	protected void login(String userName, UserType userType, Role role, String password, String studyName)
			throws Exception {
		studyBean = (StudyBean) studyDAO.findByName(studyName);
		userBean = (UserAccountBean) userAccountDAO.findByUserName(userName);
		session.clearAttributes();
		this.mockMvc
				.perform(
						post(API_AUTHENTICATION).accept(mediaType).secure(true).param("username", userName)
								.param("password", password).param("studyname", studyName).session(session))
				.andExpect(status().isOk())
				.andExpect(
						MockMvcResultMatchers.request().sessionAttribute(
								PermissionChecker.API_AUTHENTICATED_USER_DETAILS, IsInstanceOf.any(UserDetails.class)))
				.andExpect(
						content().string(
								mediaType.equals(MediaType.APPLICATION_JSON) ? StringContains
										.containsString("{\"username\":\"".concat(userName)
												.concat("\",\"userstatus\":\"").concat(userBean.getStatus().getName())
												.concat("\",\"studyname\":\"").concat(studyName)
												.concat("\",\"studystatus\":\"")
												.concat(studyBean.getStatus().getName()).concat("\",\"role\":\"")
												.concat(role.getCode()).concat("\",\"usertype\":\"")
												.concat(userType.getCode()).concat("\"}")) : StringContains
										.containsString("<ODM Description=\"REST Data\"")));
	}

	@Before
	public void before() throws Exception {
		super.setUp();

		result = null;
		restOdmContainer = null;

		schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
				new FileSystemResourceLoader().getResource("classpath:properties/ClinCapture_Rest_ODM1-3-0.xsd")
						.getURL());

		setTestProperties();

		studyDAO = new StudyDAO(dataSource);
		userAccountDAO = new UserAccountDAO(dataSource);

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilters(new RestFilter()).build();

		ResourceBundleProvider.updateLocale(LOCALE);

		timestamp = new Date().getTime();

		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, studyName);
	}

	protected void unmarshalResult() {
		if (result != null && mediaType == MediaType.APPLICATION_XML) {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(ODM.class, RestOdmContainer.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				jaxbUnmarshaller.setSchema(schema);
				restOdmContainer = (RestOdmContainer) jaxbUnmarshaller.unmarshal(new StringReader(result.getResponse()
						.getContentAsString()));
			} catch (Exception ex) {
				//
			}
			assertNotNull(restOdmContainer);
		}
	}

	@After
	public void after() {
		if (newUser != null && newUser.getId() > 0) {
			deleteUser(newUser);
		}
		if (newStudy != null && newStudy.getId() > 0) {
			deleteStudy(newStudy);
		}
		if (newSite != null && newSite.getId() > 0) {
			deleteStudy(newSite);
		}
		unmarshalResult();
	}
}
