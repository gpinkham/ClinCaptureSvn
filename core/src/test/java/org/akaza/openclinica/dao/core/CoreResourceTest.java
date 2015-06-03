package org.akaza.openclinica.dao.core;

import org.akaza.openclinica.AbstractContextSentiveTest;
import org.junit.Before;
import org.junit.Test;

public class CoreResourceTest extends AbstractContextSentiveTest {

	public static final String SYS_URL = "sysURL";
	public static final String WEBAPP_NAME = "clincapture";
	public static final String $_WEBAPP_LOWER = "${webapp}";
	public static final String $_WEBAPP_UPPER = "${WEBAPP}";
	public static final String HTTP_WWW_CLINOVO_COM = "http://www.clinovo.com/";
	public static final String CURRENT_WEB_APP_NAME = "currentWebAppName";

	@Before
	public void before() {
		CoreResources.setField(CURRENT_WEB_APP_NAME, WEBAPP_NAME);
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

	@Test
	public void testThatSysUrlReturnsUrlWithSlash() {
		CoreResources.setField(SYS_URL, "http://localhost:8080/clincapture");
		CoreResources.prepareDataInfoProperties();
		assertEquals(CoreResources.getSystemURL(), "http://localhost:8080/clincapture/");
	}
}
