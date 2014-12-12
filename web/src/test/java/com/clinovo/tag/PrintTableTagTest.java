package com.clinovo.tag;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.tags.PrintTableTag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockJspWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;


@RunWith(PowerMockRunner.class)
@PrepareForTest(ResourceBundleProvider.class)
public class PrintTableTagTest {

	private PrintTableTag printTableTag;

	@Before
	public void setUp() throws Exception {

		HttpServletResponse response = new MockHttpServletResponse();
		printTableTag = Mockito.spy(new PrintTableTag());
		PageContext pageContext = Mockito.mock(PageContext.class);
		MockJspWriter jspWriter = new MockJspWriter(response);
		Mockito.when(pageContext.getOut()).thenReturn(jspWriter);

		ResourceBundleProvider.updateLocale(new Locale("ru"));
		ResourceBundle resText = ResourceBundleProvider.getTextsBundle();

		PowerMockito.mockStatic(ResourceBundleProvider.class);
		PowerMockito.when(ResourceBundleProvider.getTextsBundle(Mockito.any(Locale.class))).thenReturn(resText);

		printTableTag.setJspContext(pageContext);
	}

	@Test
	public void testThatDoTagDoesNotThrowAnExceptionIfSessionIsEmpty() throws IOException, JspException {
		printTableTag.doTag();
	}
}
