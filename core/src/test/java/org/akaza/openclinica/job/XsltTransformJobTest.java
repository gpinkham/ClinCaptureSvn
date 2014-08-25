package org.akaza.openclinica.job;

import org.akaza.openclinica.core.OpenClinicaMailSender;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

import java.util.ResourceBundle;

import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ResourceBundleProvider.class, JobExecutionContext.class })
public class XsltTransformJobTest {

	private JobExecutionContext context;
	private XsltTransformJob xsltTransformJob;

	@Before
	public void setUp() throws Exception {
		OpenClinicaMailSender openClinicaMailSender = Mockito.mock(OpenClinicaMailSender.class);
		ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
		SchedulerContext schedulerContext = Mockito.mock(SchedulerContext.class);
		Scheduler scheduler = Mockito.mock(Scheduler.class);
		context = Mockito.mock(JobExecutionContext.class);
		JobDataMap dataMap = Mockito.mock(JobDataMap.class);
		xsltTransformJob = Mockito.mock(XsltTransformJob.class);
		ResourceBundle pageMessages = Mockito.mock(ResourceBundle.class);
		PowerMockito.mockStatic(ResourceBundleProvider.class);
		PowerMockito.when(ResourceBundleProvider.getPageMessagesBundle()).thenReturn(pageMessages);
		PowerMockito.when(context.getMergedJobDataMap()).thenReturn(dataMap);
		PowerMockito.when(context.getScheduler()).thenReturn(scheduler);
		PowerMockito.when(scheduler.getContext()).thenReturn(schedulerContext);
		PowerMockito.when(schedulerContext.get("applicationContext")).thenReturn(applicationContext);
		PowerMockito.when(applicationContext.getBean("openClinicaMailSender")).thenReturn(openClinicaMailSender);
		PowerMockito.when(dataMap.getString(Mockito.anyString())).thenReturn("");
		PowerMockito.when(xsltTransformJob, "executeInternal", context).thenCallRealMethod();
		Whitebox.setInternalState(xsltTransformJob, "logger", Mockito.mock(Logger.class));
	}

	@Test
	public void testThatApplicationContextIsNotNull() throws Exception {
		assertNotNull(context.getScheduler().getContext().get("applicationContext"));
	}
}
