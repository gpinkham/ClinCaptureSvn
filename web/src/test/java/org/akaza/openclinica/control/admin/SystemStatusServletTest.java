package org.akaza.openclinica.control.admin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.hibernate.DatabaseChangeLogDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.job.OpenClinicaSchedulerFactoryBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.quartz.impl.StdScheduler;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreResources.class})
public class SystemStatusServletTest {

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private SystemStatusServlet servlet;
	private StdScheduler stdScheduler;
	private OpenClinicaSchedulerFactoryBean scheduler;
	private DatabaseChangeLogDao databaseChangeLogDao;
	private StudyDAO studyDao;
	private DatasetDAO datasetDao;
	private ItemFormMetadataDAO itemFormMetadataDao;
	private UserAccountDAO userAccountDao;
	private StudyBean studyBean;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		servlet = Mockito.spy(new SystemStatusServlet());
		stdScheduler = Mockito.mock(StdScheduler.class);
		databaseChangeLogDao = Mockito.mock(DatabaseChangeLogDao.class);
		itemFormMetadataDao = Mockito.mock(ItemFormMetadataDAO.class);
		userAccountDao = Mockito.mock(UserAccountDAO.class);
		studyDao = Mockito.mock(StudyDAO.class);
		datasetDao = Mockito.mock(DatasetDAO.class);
		scheduler = Mockito.mock(OpenClinicaSchedulerFactoryBean.class);
		PowerMockito.mockStatic(CoreResources.class);
		PowerMockito.when(CoreResources.getField(SystemStatusServlet.FILE_PATH)).thenReturn("");
		Whitebox.setInternalState(servlet, "scheduler", scheduler);
		Mockito.when(scheduler.getScheduler()).thenReturn(stdScheduler);
		Mockito.when(servlet.getDatabaseChangeLogDao()).thenReturn(databaseChangeLogDao);
		Mockito.when(servlet.getDatasetDAO()).thenReturn(datasetDao);
		Mockito.when(servlet.getStudyDAO()).thenReturn(studyDao);
		Mockito.when(servlet.getUserAccountDAO()).thenReturn(userAccountDao);
		Mockito.when(servlet.getItemFormMetadataDAO()).thenReturn(itemFormMetadataDao);
		studyBean = new StudyBean();
		studyBean.setId(1);
		studyBean.setOid("S_DEFAULTS1");
		studyBean.setName("Default Study");
		Mockito.when(studyDao.findByPK(Mockito.anyInt())).thenReturn(studyBean);
	}

	@Test
	public void testThatProcessRequestReturns200IfIdIsNotPassed() throws Exception {
		servlet.processRequest(request, response);
		assertTrue(response.getStatus() == 200);
	}

	@Test
	public void testThatProcessRequestReturnsNotEmptyContent() throws Exception {
		request.setParameter("id", "1");
		servlet.processRequest(request, response);
		assertFalse(response.getContentAsString().isEmpty());
	}
}
