package org.akaza.openclinica.domain.rule.action;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.dao.hibernate.RuleActionRunLogDao;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.logic.rulerunner.RuleRunner;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EmailEngine.class)
public class EmailActionProcessorTest {

	private DataSource ds;
	private RuleBean ruleBean;
	private UserAccountBean ub;
	private StudyBean studyBean;
	private MimeMessage mimeMessage;
	private ItemDataBean itemDataBean;
	private HashMap<String, String> map;
	private RuleSetRuleBean ruleSetRule;
	private JavaMailSenderImpl mailSender;
	private EmailActionBean emailActionBean;
	private boolean emailActionProcessorResult;
	private EmailActionProcessor emailActionProcessor1;
	private EmailActionProcessor emailActionProcessor2;
	private RuleActionRunLogDao ruleActionRunLogDao;
	private CustomDiscrepancyNoteService customDiscrepancyNoteService;

	class CustomDiscrepancyNoteService extends DiscrepancyNoteService {

		public CustomDiscrepancyNoteService(DataSource ds) {
			super(ds);
		}

		public void saveFieldNotes(String description, int entityId, String entityType, Connection connection,
				StudyBean sb, UserAccountBean ub, boolean assignToUser) {
			emailActionProcessorResult = true;
		}
	}

	@Before
	public void setUp() throws Exception {
		map = new HashMap<String, String>();
		map.put("body", "test body");
		map.put("subject", "test subject");

		studyBean = Mockito.mock(StudyBean.class);
		studyBean.setId(1);
		studyBean.setName("Default Study");
		studyBean.setOid("S_DEFAULTS1");

		ub = new UserAccountBean();
		ub.setId(1);
		ub.setName("root");

		ds = Mockito.mock(DataSource.class);
		ruleBean = Mockito.mock(RuleBean.class);
		Mockito.when(ruleBean.getOid()).thenReturn("test_oid");
		itemDataBean = Mockito.mock(ItemDataBean.class);
		Mockito.when(itemDataBean.getId()).thenReturn(1);
		Mockito.when(itemDataBean.getValue()).thenReturn("test value");
		ruleSetRule = Mockito.mock(RuleSetRuleBean.class);
		Mockito.when(ruleSetRule.getRuleBean()).thenReturn(ruleBean);
		mailSender = Mockito.mock(JavaMailSenderImpl.class);
		emailActionBean = Mockito.mock(EmailActionBean.class);
		Mockito.when(emailActionBean.getActionType()).thenReturn(ActionType.EMAIL);
		Mockito.when(emailActionBean.getTo()).thenReturn("test@test.com");
		customDiscrepancyNoteService = new CustomDiscrepancyNoteService(ds);
		ruleActionRunLogDao = Mockito.mock(RuleActionRunLogDao.class);
		emailActionProcessor1 = Mockito.mock(EmailActionProcessor.class);
		Mockito.when(
				emailActionProcessor1.execute(RuleRunner.RuleRunnerMode.DATA_ENTRY, ExecutionMode.SAVE,
						emailActionBean, itemDataBean, DiscrepancyNoteBean.ITEM_DATA, studyBean, ub, map))
				.thenCallRealMethod();
		mimeMessage = Mockito.mock(MimeMessage.class);
		mailSender = Mockito.mock(JavaMailSenderImpl.class);
		Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
		Whitebox.setInternalState(emailActionProcessor1, "discrepancyNoteService", customDiscrepancyNoteService);
		Whitebox.setInternalState(emailActionProcessor1, "ruleActionRunLogDao", ruleActionRunLogDao);
		Whitebox.setInternalState(emailActionProcessor1, "logger", Mockito.mock(Logger.class));
		Whitebox.setInternalState(emailActionProcessor1, "ruleSetRule", ruleSetRule);
		Whitebox.setInternalState(emailActionProcessor1, "mailSender", mailSender);
		PowerMockito.mockStatic(EmailEngine.class);
		emailActionProcessor2 = Mockito.mock(EmailActionProcessor.class);
		Mockito.when(
				emailActionProcessor2.execute(RuleRunner.RuleRunnerMode.DATA_ENTRY, ExecutionMode.SAVE,
						emailActionBean, itemDataBean, DiscrepancyNoteBean.ITEM_DATA, studyBean, ub, map))
				.thenCallRealMethod();
		Whitebox.setInternalState(emailActionProcessor2, "discrepancyNoteService", customDiscrepancyNoteService);
		Whitebox.setInternalState(emailActionProcessor2, "ruleActionRunLogDao", ruleActionRunLogDao);
		Whitebox.setInternalState(emailActionProcessor2, "logger", Mockito.mock(Logger.class));
		Whitebox.setInternalState(emailActionProcessor2, "ruleSetRule", ruleSetRule);
		Whitebox.setInternalState(emailActionProcessor2, "mailSender", mailSender);
	}

	@Test
	public void testThatEmailActionProcessorResultIsTrue() {
		emailActionProcessorResult = false;
		PowerMockito.when(EmailEngine.getAdminEmail()).thenReturn("");
		emailActionProcessor1.execute(RuleRunner.RuleRunnerMode.DATA_ENTRY, ExecutionMode.SAVE, emailActionBean,
				itemDataBean, DiscrepancyNoteBean.ITEM_DATA, studyBean, ub, map);
		assertTrue(emailActionProcessorResult);
	}

	@Test
	public void testThatEmailActionProcessorResultIsFalse() {
		emailActionProcessorResult = false;
		PowerMockito.when(EmailEngine.getAdminEmail()).thenReturn("admin@test.com");
		emailActionProcessor2.execute(RuleRunner.RuleRunnerMode.DATA_ENTRY, ExecutionMode.SAVE, emailActionBean,
				itemDataBean, DiscrepancyNoteBean.ITEM_DATA, studyBean, ub, map);
		assertFalse(emailActionProcessorResult);
	}
}
