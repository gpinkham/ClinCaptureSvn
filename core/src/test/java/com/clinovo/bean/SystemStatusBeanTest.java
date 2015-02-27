package com.clinovo.bean;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.CoreResources;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

public class SystemStatusBeanTest extends DefaultAppContextTest {

	public static final String CLINCAPTURE_DATA = "clincapture-data";

	private StudyBean studyBean;
	private SystemStatusBean systemStatusBean;

	@Before
	public void setUp() throws Exception {
		CoreResources.setField("filePath", new DefaultResourceLoader().getResource(CLINCAPTURE_DATA).getFile()
				.getPath());
		studyBean = (StudyBean) studyDAO.findByPK(1);
		systemStatusBean = new SystemStatusBean("1", studyDAO, userAccountDAO, imfdao);
	}

	@Test
	public void testThatSystemStatusBeanIsNotNull() {
		assertNotNull(systemStatusBean);
	}

	@Test
	public void testThatValueForStudyIdIsCorrect() {
		assertEquals(systemStatusBean.getStudyId(), 1);
	}

	@Test
	public void testThatValueForParameterIdIsCorrect() {
		assertEquals(systemStatusBean.getParameterId(), "1");
	}

	@Test
	public void testThatValueForStudyOidIsCorrect() {
		assertEquals(systemStatusBean.getStudyOid(), studyBean.getOid());
	}

	@Test
	public void testThatValueForStudyNameIsCorrect() {
		assertEquals(systemStatusBean.getStudyName(), studyBean.getName());
	}

	@Test
	public void testThatValueForAssignedUsersIsCorrect() {
		assertEquals(systemStatusBean.getAssignedUsers(), 0);
	}

	@Test
	public void testThatValueForCrfSectionsIsCorrect() {
		assertEquals(systemStatusBean.getCrfSections(), 10);
	}

	@Test
	public void testThatValueForDataImportSizeIsCorrect() {
		assertTrue(systemStatusBean.getDataImportSize() > 0);
	}

	@Test
	public void testThatValueForDataImportSizeValueIsCorrect() {
		assertTrue(systemStatusBean.getDataImportSizeValue().contains("Kb"));
	}

	@Test
	public void testThatValueForDataExportSizeIsCorrect() {
		assertTrue(systemStatusBean.getDataExportSize() > 0);
	}

	@Test
	public void testThatValueForDataExportSizeValueIsCorrect() {
		assertTrue(systemStatusBean.getDataExportSizeValue().contains("Mb"));
	}

	@Test
	public void testThatValueForFileAttachmentsSizeIsCorrect() {
		assertTrue(systemStatusBean.getFileAttachmentsSize() > 0);
	}

	@Test
	public void testThatValueForFileAttachmentsSizeValueIsCorrect() {
		assertTrue(systemStatusBean.getFileAttachmentsSizeValue().contains("Bytes"));
	}
}
