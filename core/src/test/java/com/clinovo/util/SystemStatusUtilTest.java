package com.clinovo.util;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.dao.core.CoreResources;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

import com.clinovo.bean.SystemStatusBean;

public class SystemStatusUtilTest extends DefaultAppContextTest {

	public static final String CLINCAPTURE_DATA = "clincapture-data";

	@Before
	public void setUp() throws Exception {
		CoreResources.setField("filePath", new DefaultResourceLoader().getResource(CLINCAPTURE_DATA).getFile()
				.getPath());
	}

	@Test
	public void testThatGetStatisticsForStudyReturnsCorrectValueIfParameterIdIsNull() {
		assertEquals(
				SystemStatusUtil.getStatisticsForStudy(new SystemStatusBean(null, studyDAO, userAccountDAO, imfdao)),
				"\n        Please, specify the study id to display study statistics.");
	}

	@Test
	public void testThatGetXmlStatisticsForStudyReturnsCorrectValueIfParameterIdIsNull() {
		assertEquals(
				SystemStatusUtil.getXmlStatisticsForStudy(new SystemStatusBean(null, studyDAO, userAccountDAO, imfdao)),
				"");
	}

	@Test
	public void testThatGetStatisticsForStudyReturnsCorrectValueIfStudyIsNotFound() {
		assertEquals(
				SystemStatusUtil.getStatisticsForStudy(new SystemStatusBean("3417", studyDAO, userAccountDAO, imfdao)),
				"\n        Parent study is not found.");
	}

	@Test
	public void testThatGetXmlStatisticsForStudyReturnsCorrectValueIfStudyIsNotFound() {
		assertEquals(SystemStatusUtil.getXmlStatisticsForStudy(new SystemStatusBean("3417", studyDAO, userAccountDAO,
				imfdao)), "");
	}
}