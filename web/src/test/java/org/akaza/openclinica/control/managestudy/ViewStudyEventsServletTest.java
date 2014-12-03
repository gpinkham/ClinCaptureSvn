package org.akaza.openclinica.control.managestudy;

import com.clinovo.util.SessionUtil;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ResourceBundleProvider.class, ViewStudyEventsServlet.class })
@SuppressWarnings("rawtypes")
public class ViewStudyEventsServletTest {

	@Spy
	private ViewStudyEventsServlet viewStudyEventsServlet = new ViewStudyEventsServlet();

	@Mock
	private MockHttpServletRequest request;

	@Mock
	private MockHttpServletResponse response;

	@Mock
	private ResourceBundle resformat;

	private MockHttpSession session = new MockHttpSession();

	@Before
	public void setUp() throws Exception {
		Mockito.when(request.getSession()).thenReturn(session);
		Mockito.when(request.getParameter("refreshPage")).thenReturn("1");
		Mockito.when(request.getRequestURL()).thenReturn(
				new StringBuffer("http://localhost:8080/clincapture/ViewStudyEvents"));
		PowerMockito.mockStatic(ResourceBundleProvider.class);
		PowerMockito.when(ResourceBundleProvider.getFormatBundle()).thenReturn(resformat);
		PowerMockito.doReturn("dd-MMM-yyyy").when(resformat).getString("date_format_string");
		Whitebox.setInternalState(viewStudyEventsServlet, "resformat", resformat);
		Whitebox.setInternalState(viewStudyEventsServlet, "logger", LoggerFactory.getLogger("ViewStudyEventsServlet"));
	}

	@Test
	public void testThatSavedUrlIsChangedIfRequestLocaleWasChanged() {
		SessionUtil.updateLocale(session, Locale.ENGLISH);
		String key = viewStudyEventsServlet.getUrlKey(request);
		viewStudyEventsServlet.getDefaultUrl(request);
		String savedUrl = (String) request.getSession().getAttribute(key);
		SessionUtil.updateLocale(session, Locale.JAPAN);
		viewStudyEventsServlet.getDefaultUrl(request);
		Assert.assertFalse(request.getSession().getAttribute(key).equals(savedUrl));
	}

	@Test
	public void testThatPrintUrlNotSavedInTheSession() throws Exception {

		viewStudyEventsServlet = PowerMockito.mock(ViewStudyEventsServlet.class);
		Mockito.doCallRealMethod().when(viewStudyEventsServlet).processRequest(request, response);
		Mockito.doCallRealMethod().when(viewStudyEventsServlet).getLocalDf(request);
		SessionUtil.updateLocale(session, Locale.ENGLISH);
		Validator validator = PowerMockito.mock(Validator.class);
		PowerMockito
				.when(viewStudyEventsServlet,
						PowerMockito.method(ViewStudyEventsServlet.class, "getValidator", HttpServletRequest.class))
				.withArguments(request).thenReturn(validator);
		Mockito.when(validator.validate()).thenReturn(new HashMap());
		Mockito.when(request.getMethod()).thenReturn("POST");
		Mockito.when(request.getAttribute("startDate")).thenReturn("11-Jan-2010");
		Mockito.when(request.getAttribute("endDate")).thenReturn("11-Jan-2011");
		Mockito.when(request.getParameter("print")).thenReturn("yes");
		StudyEventDefinitionDAO seddao = Mockito.mock(StudyEventDefinitionDAO.class);
		Mockito.when(viewStudyEventsServlet.getStudyEventDefinitionDAO()).thenReturn(seddao);
		Mockito.when(seddao.findAllAvailableByStudy(Mockito.any(StudyBean.class))).thenReturn(new ArrayList());
		viewStudyEventsServlet.processRequest(request, response);
		Assert.assertNull(request.getSession().getAttribute("savedViewStudyEventsUrl"));
	}

	@Test
	public void testThatUrlWithPrintParameterOpensCorrectPage() {
		SessionUtil.updateLocale(session, Locale.ENGLISH);
		String key = viewStudyEventsServlet.getUrlKey(request);
		viewStudyEventsServlet.getDefaultUrl(request);
		String savedUrl = (String) request.getSession().getAttribute(key);
		viewStudyEventsServlet.getDefaultUrl(request);
		Assert.assertTrue(request.getSession().getAttribute(key).equals(savedUrl));
	}
}
