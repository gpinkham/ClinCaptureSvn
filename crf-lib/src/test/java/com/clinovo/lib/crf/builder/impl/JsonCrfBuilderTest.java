package com.clinovo.lib.crf.builder.impl;

import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import javax.sql.DataSource;

import com.clinovo.service.ItemRenderMetadataService;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.MessageSource;

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
	private ItemRenderMetadataService metadataService;
	@Mock
	private MessageSource messageSource;

	@Before
	public void before() throws Exception {
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		jsonCrfBuilder = new JsonCrfBuilder(new JSONObject("{}"), owner, studyBean, dataSource, Locale.ENGLISH,
				messageSource, importCrfService, metadataService);
	}

	@Test
	public void testDefaultValues() {
		assertNotNull(jsonCrfBuilder.getJsonObject());
		assertNotNull(jsonCrfBuilder.getErrorMessageProducer());
	}
}
