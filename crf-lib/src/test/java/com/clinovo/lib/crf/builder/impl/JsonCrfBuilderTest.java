package com.clinovo.lib.crf.builder.impl;

import static org.junit.Assert.assertNotNull;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.clinovo.lib.crf.service.ImportCrfService;

public class JsonCrfBuilderTest {

	@Mock
	private StudyBean studyBean;
	@Mock
	private DataSource dataSource;
	@Mock
	private UserAccountBean owner;

	private JsonCrfBuilder jsonCrfBuilder;

	@Mock
	private ImportCrfService importCrfService;
	@Mock
	private ResourceBundle pageMessagesResourceBundle;

	@Before
	public void before() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		jsonCrfBuilder = new JsonCrfBuilder(new JSONObject("{}"), owner, studyBean, dataSource, Locale.ENGLISH,
				ResourceBundleProvider.getPageMessagesBundle(), importCrfService);
	}

	@Test
	public void testDefaultValues() {
		assertNotNull(jsonCrfBuilder.getJsonObject());
		assertNotNull(jsonCrfBuilder.getErrorMessageProducer());
	}
}
