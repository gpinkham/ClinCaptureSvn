package org.akaza.openclinica.dao.core;

import static junit.framework.Assert.assertEquals;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

public class CoreResourceTest {

	public static final String WEBAPP = "webapp";
	public static final String SYS_URL = "sysURL";
	public static final String WEBAPP_NAME = "clincapture";
	public static final String $_WEBAPP_LOWER = "${webapp}";
	public static final String $_WEBAPP_UPPER = "${WEBAPP}";
	public static final String HTTP_WWW_CLINOVO_COM = "http://www.clinovo.com/";

	private Properties properties = new Properties();

	@Before
	public void before() {
		CoreResources coreResources = new CoreResources();
		coreResources.setDataInfo(properties);
		properties.setProperty("dbType", "postgres");
		properties.setProperty("mailHost", "");
		properties.setProperty("mailPort", "");
		properties.setProperty("mailProtocol", "");
		properties.setProperty("mailUsername", "");
		properties.setProperty("mailPassword", "");
		properties.setProperty("mailAuth", "");
		properties.setProperty("mailTls", "");
		properties.setProperty("mailSmtpConnectionTimeout", "");
		properties.setProperty("mailErrorMsg", "");
		Whitebox.setInternalState(coreResources, WEBAPP, WEBAPP_NAME);
	}

	@Test
	public void testThatWebappInLowerCaseIsReplacedInSysUrl() {
		CoreResources.setField(SYS_URL, HTTP_WWW_CLINOVO_COM.concat($_WEBAPP_LOWER));
		CoreResources.prepareDataInfoProperties();
		assertEquals(CoreResources.getField(SYS_URL),
				HTTP_WWW_CLINOVO_COM.concat($_WEBAPP_LOWER).replace($_WEBAPP_LOWER, WEBAPP_NAME));
	}

	@Test
	public void testThatWebappInUpperCaseIsReplacedInSysUrl() {
		CoreResources.setField(SYS_URL, HTTP_WWW_CLINOVO_COM.concat($_WEBAPP_UPPER));
		CoreResources.prepareDataInfoProperties();
		assertEquals(CoreResources.getField(SYS_URL),
				HTTP_WWW_CLINOVO_COM.concat($_WEBAPP_UPPER).replace($_WEBAPP_UPPER, WEBAPP_NAME));
	}
}
