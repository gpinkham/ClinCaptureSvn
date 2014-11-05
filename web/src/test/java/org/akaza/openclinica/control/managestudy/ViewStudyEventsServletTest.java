package org.akaza.openclinica.control.managestudy;

import com.clinovo.util.SessionUtil;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.Locale;
import java.util.ResourceBundle;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ResourceBundleProvider.class, ViewStudyEventsServlet.class })
public class ViewStudyEventsServletTest {

	@Spy
	private ViewStudyEventsServlet viewStudyEventsServlet = new ViewStudyEventsServlet();

	@Mock
	private MockHttpServletRequest request;

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
	public void testThatSavedUrlIsNotChangedIfRequestLocaleWasNotChanged() {
		SessionUtil.updateLocale(session, Locale.ENGLISH);
		String key = viewStudyEventsServlet.getUrlKey(request);
		viewStudyEventsServlet.getDefaultUrl(request);
		String savedUrl = (String) request.getSession().getAttribute(key);
		viewStudyEventsServlet.getDefaultUrl(request);
		Assert.assertTrue(request.getSession().getAttribute(key).equals(savedUrl));
	}
}
