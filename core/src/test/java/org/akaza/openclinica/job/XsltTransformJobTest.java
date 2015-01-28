package org.akaza.openclinica.job;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.extract.ExtractPropertyBean;
import org.akaza.openclinica.core.OpenClinicaMailSender;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class XsltTransformJobTest extends DefaultAppContextTest {

	private JobDataMap dataMap;
	private SimpleTriggerImpl trigger;
	private MessageSource messageSource;
	private JobExecutionContext context;
	private XsltTransformJob xsltTransformJob;
	private OpenClinicaMailSender openClinicaMailSender;

	@Before
	public void setUp() throws Exception {
		Logger logger = Mockito.mock(Logger.class);
		messageSource = Mockito.mock(MessageSource.class);
		MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
		ExtractPropertyBean epBean = Mockito.mock(ExtractPropertyBean.class);
		PowerMockito.when(epBean.getId()).thenReturn(10);
		JavaMailSenderImpl mailSender = Mockito.mock(JavaMailSenderImpl.class);
		openClinicaMailSender = Mockito.mock(OpenClinicaMailSender.class);
		ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
		SchedulerContext schedulerContext = Mockito.mock(SchedulerContext.class);
		Scheduler scheduler = Mockito.mock(Scheduler.class);
		context = Mockito.mock(JobExecutionContext.class);
		JobDetail jobDetail = Mockito.mock(JobDetail.class);
		dataMap = Mockito.mock(JobDataMap.class);
		xsltTransformJob = Mockito.mock(XsltTransformJob.class);
		trigger = Mockito.mock(SimpleTriggerImpl.class);		
		Map lookup = new HashMap();
		lookup.put("date_time_format_string", "dd-MM-yyyy");
		lookup.put("date_format_string", "dd-MM-yyyy");
		lookup.put("date_format_year", "yyyy");
		lookup.put("date_format_year_month", "MM");
		PowerMockito.mockStatic(CoreResources.class);
		PowerMockito.when(context.getMergedJobDataMap()).thenReturn(dataMap);
		PowerMockito.when(context.getScheduler()).thenReturn(scheduler);
		PowerMockito.when(context.getTrigger()).thenReturn(trigger);
		PowerMockito.when(context.getJobDetail()).thenReturn(jobDetail);
		PowerMockito.when(jobDetail.getJobDataMap()).thenReturn(dataMap);
		PowerMockito.when(scheduler.getContext()).thenReturn(schedulerContext);
		PowerMockito.when(schedulerContext.get("applicationContext")).thenReturn(applicationContext);
		PowerMockito.when(applicationContext.getBean("openClinicaMailSender")).thenReturn(openClinicaMailSender);
		PowerMockito.when(applicationContext.getBean("dataSource")).thenReturn(dataSource);
		PowerMockito.when(applicationContext.getBean("messageSource")).thenReturn(messageSource);
		PowerMockito.when(applicationContext.getBean("openClinicaMailSender")).thenReturn(openClinicaMailSender);
		PowerMockito.when(dataMap.getString(Mockito.anyString())).thenReturn("");
		PowerMockito.when(dataMap.getString("locale")).thenReturn("en-US");
		PowerMockito.when(dataMap.get("epBean")).thenReturn(epBean);
		PowerMockito.when(dataMap.getInt("user_id")).thenReturn(1);
		PowerMockito.when(xsltTransformJob, "executeInternal", context).thenCallRealMethod();
		PowerMockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
		Whitebox.setInternalState(xsltTransformJob, "logger", logger);
		Whitebox.setInternalState(openClinicaMailSender, "logger", logger);
		Whitebox.setInternalState(openClinicaMailSender, "mailSender", mailSender);
		Whitebox.setInternalState(ResourceBundleProvider.getFormatBundle(), "lookup", lookup);
	}

	@Test
	public void testThatApplicationContextIsNotNull() throws Exception {
		assertNotNull(context.getScheduler().getContext().get("applicationContext"));
	}

	@Test
	public void testThatJobThrowsCorrectExceptionWhenTheSasJobDirWasNotSpecified() throws Exception {
		CoreResources.setField("sas.dir", "");
		String errorMessage = "sasDataset.exception.sasExtractFolder";
		PowerMockito.when(dataMap.get("sasJobDir")).thenReturn(null);
		PowerMockito.when(openClinicaMailSender, "sendEmail", Mockito.anyString(), Mockito.anyString(),
				Mockito.contains(errorMessage), Mockito.anyString(), Mockito.anyBoolean()).thenCallRealMethod();
		PowerMockito.when(messageSource.getMessage(errorMessage, null, new Locale("en-US"))).thenReturn(errorMessage);
		xsltTransformJob.executeInternal(context);
		Mockito.verify(openClinicaMailSender).sendEmail(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.contains(errorMessage), Mockito.anyBoolean());
	}
}
