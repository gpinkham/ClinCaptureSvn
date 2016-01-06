package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.RequestUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ViewStudyEventsServlet.class, RequestUtil.class, CoreResources.class})
@SuppressWarnings("rawtypes")
public class ViewStudyEventsServletTest {

	@Spy
	private ViewStudyEventsServlet viewStudyEventsServlet = new ViewStudyEventsServlet();

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;

	private UserAccountBean currentUser;

	private MockHttpSession session;

	@Before
	public void setUp() throws Exception {

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		session = new MockHttpSession();
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		LocaleResolver.updateLocale(session, Locale.ENGLISH);
		PowerMockito.mockStatic(RequestUtil.class);
		PowerMockito.when(RequestUtil.getRequest()).thenReturn(request);

		request.setContextPath("clincapture");
		request.setSession(session);
		request.setParameter("refreshPage", "1");
		request.setScheme("http");
		request.setServerName("localhost");
		request.setServerPort(8080);
		request.setRequestURI("/clincapture/ViewStudyEvents");

		currentUser = new UserAccountBean();
		currentUser.setId(1);
		currentUser.setName("root");
		currentUser.setUserTimeZoneId("Europe/Madrid");
		Mockito.when(viewStudyEventsServlet.getUserAccountBean()).thenReturn(currentUser);

		Whitebox.setInternalState(viewStudyEventsServlet, "logger", LoggerFactory.getLogger("ViewStudyEventsServlet"));

		String url = "http://localhost:8080/clincapture/";
		PowerMockito.mockStatic(CoreResources.class);
		PowerMockito.when(CoreResources.getSystemURL()).thenReturn(url);
	}

	@Test
	public void testThatSavedUrlIsChangedIfRequestLocaleWasChanged() {

		String key = viewStudyEventsServlet.getUrlKey();
		viewStudyEventsServlet.getDefaultUrl(request);
		String savedUrl = (String) request.getSession().getAttribute(key);
		LocaleResolver.updateLocale(session, Locale.JAPAN);
		viewStudyEventsServlet.getDefaultUrl(request);
		Assert.assertFalse(request.getSession().getAttribute(key).equals(savedUrl));
	}

	@Test
	public void testThatPrintUrlNotSavedInTheSession() throws Exception {

		viewStudyEventsServlet = PowerMockito.mock(ViewStudyEventsServlet.class);
		Mockito.doCallRealMethod().when(viewStudyEventsServlet).processRequest(request, response);
		Mockito.when(viewStudyEventsServlet.getUserAccountBean()).thenReturn(currentUser);
		Validator validator = PowerMockito.mock(Validator.class);
		PowerMockito
				.when(viewStudyEventsServlet,
						PowerMockito.method(ViewStudyEventsServlet.class, "getValidator", HttpServletRequest.class))
				.withArguments(request).thenReturn(validator);
		Mockito.when(validator.validate()).thenReturn(new HashMap());
		request.setMethod("POST");
		request.setAttribute("startDate", "11-Jan-2010");
		request.setAttribute("endDate", "11-Jan-2011");
		request.setAttribute("print", "yes");
		StudyEventDefinitionDAO seddao = Mockito.mock(StudyEventDefinitionDAO.class);
		Mockito.when(viewStudyEventsServlet.getStudyEventDefinitionDAO()).thenReturn(seddao);
		Mockito.when(seddao.findAllAvailableByStudy(Mockito.any(StudyBean.class))).thenReturn(new ArrayList());
		viewStudyEventsServlet.processRequest(request, response);
		Assert.assertNull(request.getSession().getAttribute("savedViewStudyEventsUrl"));
	}

	@Test
	public void testThatUrlWithPrintParameterOpensCorrectPage() {

		String key = viewStudyEventsServlet.getUrlKey();
		viewStudyEventsServlet.getDefaultUrl(request);
		String savedUrl = (String) request.getSession().getAttribute(key);
		viewStudyEventsServlet.getDefaultUrl(request);
		Assert.assertTrue(request.getSession().getAttribute(key).equals(savedUrl));
	}
}
