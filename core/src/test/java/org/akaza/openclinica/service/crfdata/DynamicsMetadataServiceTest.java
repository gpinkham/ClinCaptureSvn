package org.akaza.openclinica.service.crfdata;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.action.PropertyBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DynamicsMetadataServiceTest {

	@Mock
	private DynamicsMetadataService dynamicsMetadataService;

	@Mock
	private ExpressionService expressionService;

	@Mock
	private ItemDataDAO itemDataDAO;

	@Mock
	private EventCRFDAO eventCRFDAO;

	@Mock
	private StudyEventDAO studyEventDAO;

	@Mock
	private StudyDAO studyDAO;

	@Mock
	private StudySubjectDAO studySubjectDAO;

	@Mock
	private StudyEventDefinitionDAO studyEventDefinitionDAO;

	@Mock
	private ItemFormMetadataDAO itemFormMetadataDAO;

	@Mock
	private StudyParameterValueDAO studyParameterValueDAO;

	@Mock
	private Connection connection;

	private List<PropertyBean> properties;

	private int sourceItemDataId;

	private UserAccountBean user;

	private RuleSetBean ruleSetBean;

	private ItemDataBean sourceItemDataBean;

	private EventCRFBean sourceEventCrfBean;

	private StudyEventBean sourceStudyEventBean;

	private StudyBean subjectStudy;

	private StudySubjectBean studySubject;

	private String expression;

	private ItemBean destinationItemBean;

	@Before
	public void setupTest() {

		properties = new ArrayList<PropertyBean>();
		PropertyBean propertyBean = new PropertyBean();
		propertyBean.setId(10);
		propertyBean.setOid("OID10");
		properties.add(propertyBean);

		sourceItemDataId = 189;

		user = new UserAccountBean();
		user.setId(1);

		ruleSetBean = new RuleSetBean();
		ExpressionBean expressionBean = new ExpressionBean();
		expressionBean.setValue("value");
		ruleSetBean.setTarget(expressionBean);

		sourceItemDataBean = new ItemDataBean();
		sourceItemDataBean.setEventCRFId(420);

		sourceEventCrfBean = new EventCRFBean();
		sourceEventCrfBean.setStudyEventId(34);
		sourceEventCrfBean.setStudySubjectId(1290);
		sourceEventCrfBean.setCRFVersionId(23);

		sourceStudyEventBean = new StudyEventBean();
		sourceStudyEventBean.setId(333);
		sourceStudyEventBean.setStudySubjectId(1290);
		sourceStudyEventBean.setSampleOrdinal(1);

		subjectStudy = new StudyBean();
		subjectStudy.setId(13);

		studySubject = new StudySubjectBean();

		expression = "test_expression";

		destinationItemBean = new ItemBean();
		destinationItemBean.setId(900);

		// MOCKING
		MockitoAnnotations.initMocks(this);

		Mockito.when(dynamicsMetadataService.getExpressionService()).thenReturn(expressionService);
		Mockito.when(dynamicsMetadataService.getItemDataDAO()).thenReturn(itemDataDAO);
		Mockito.when(dynamicsMetadataService.getEventCRFDAO()).thenReturn(eventCRFDAO);
		Mockito.when(dynamicsMetadataService.getStudyEventDAO()).thenReturn(studyEventDAO);
		Mockito.when(dynamicsMetadataService.getStudyDAO()).thenReturn(studyDAO);
		Mockito.when(dynamicsMetadataService.getStudySubjectDAO()).thenReturn(studySubjectDAO);
		Mockito.when(dynamicsMetadataService.getStudyEventDefinitionDAO()).thenReturn(studyEventDefinitionDAO);
		Mockito.when(dynamicsMetadataService.getItemFormMetadataDAO()).thenReturn(itemFormMetadataDAO);
		Mockito.when(dynamicsMetadataService.getStudyParameterValueDAO()).thenReturn(studyParameterValueDAO);

		Mockito.when(itemDataDAO.findByPK(189)).thenReturn(sourceItemDataBean);
		Mockito.when(eventCRFDAO.findByPK(420)).thenReturn(sourceEventCrfBean);
		Mockito.when(studyEventDAO.findByPK(34)).thenReturn(sourceStudyEventBean);
		Mockito.when(studyDAO.findByStudySubjectId(1290)).thenReturn(subjectStudy);
		Mockito.when(studySubjectDAO.findBySubjectIdAndStudy(1290, subjectStudy)).thenReturn(studySubject);
		Mockito.when(expressionService.constructFullExpressionFromPartial(Mockito.any(String.class),
				Mockito.any(String.class))).thenReturn(expression);
		Mockito.when(expressionService.getItemBeanFromExpression(Mockito.any(String.class)))
				.thenReturn(destinationItemBean);
	}

	@Test
	public void testThatInsertBehavesCorrectlyWhenInsertDataIntoTheSameCRF() {

		// SETUP
		ItemFormMetadataBean destinationItemFormMetadata = new ItemFormMetadataBean();
		destinationItemFormMetadata.setId(123);

		Mockito.when(expressionService.getStudyEventDefenitionOid(expression)).thenReturn(null);
		Mockito.when(itemFormMetadataDAO.findByItemIdAndCRFVersionId(900, 23)).thenReturn(destinationItemFormMetadata);

		Mockito.doCallRealMethod().when(dynamicsMetadataService).insert(sourceItemDataId, properties, user, ruleSetBean,
				connection);

		// TEST
		dynamicsMetadataService.insert(sourceItemDataId, properties, user, ruleSetBean, connection);

		// VERIFY
		Mockito.verify(dynamicsMetadataService, Mockito.times(1)).updateSourceEventCRF(studySubject, properties.get(0),
				subjectStudy, sourceEventCrfBean, sourceStudyEventBean, sourceItemDataBean, destinationItemBean,
				expression, ruleSetBean, user, connection);
	}

	@Test
	public void testThatInsertBehavesCorrectlyWhenInsertDataIntoAnotherNotStartedCRFInsideTheSameStudyEvent() {

		// SETUP
		ItemFormMetadataBean destinationItemFormMetadata = new ItemFormMetadataBean();
		destinationItemFormMetadata.setId(0);

		Mockito.when(expressionService.getStudyEventDefenitionOid(expression)).thenReturn(null);
		Mockito.when(itemFormMetadataDAO.findByItemIdAndCRFVersionId(900, 23)).thenReturn(destinationItemFormMetadata);
		Mockito.when(expressionService.getCrfOid(expression)).thenReturn("CRF_VER_OID10");
		Mockito.when(eventCRFDAO.findAllByStudyEventAndCrfOrCrfVersionOid(sourceStudyEventBean, "CRF_VER_OID10"))
				.thenReturn(new ArrayList<EventCRFBean>());

		Mockito.doCallRealMethod().when(dynamicsMetadataService).insert(sourceItemDataId, properties, user, ruleSetBean,
				connection);

		// TEST
		dynamicsMetadataService.insert(sourceItemDataId, properties, user, ruleSetBean, connection);

		// VERIFY
		Mockito.verify(dynamicsMetadataService, Mockito.times(1)).createNewEventCRF(properties.get(0), subjectStudy,
				sourceEventCrfBean, sourceStudyEventBean, sourceItemDataBean, destinationItemBean, expression,
				ruleSetBean, user, connection);
	}

	@Test
	public void testThatInsertBehavesCorrectlyWhenInsertDataIntoAnotherStartedCRFInsideTheSameStudyEvent() {

		// SETUP
		ItemFormMetadataBean destinationItemFormMetadata = new ItemFormMetadataBean();
		destinationItemFormMetadata.setId(0);
		List<EventCRFBean> eventCRFBeans = new ArrayList<EventCRFBean>();
		eventCRFBeans.add(new EventCRFBean());

		Mockito.when(expressionService.getStudyEventDefenitionOid(expression)).thenReturn(null);
		Mockito.when(itemFormMetadataDAO.findByItemIdAndCRFVersionId(900, 23)).thenReturn(destinationItemFormMetadata);
		Mockito.when(expressionService.getCrfOid(expression)).thenReturn("CRF_VER_OID10");
		Mockito.when(eventCRFDAO.findAllByStudyEventAndCrfOrCrfVersionOid(sourceStudyEventBean, "CRF_VER_OID10"))
				.thenReturn(eventCRFBeans);

		Mockito.doCallRealMethod().when(dynamicsMetadataService).insert(sourceItemDataId, properties, user, ruleSetBean,
				connection);

		// TEST
		dynamicsMetadataService.insert(sourceItemDataId, properties, user, ruleSetBean, connection);

		// VERIFY
		Mockito.verify(dynamicsMetadataService, Mockito.times(1)).updateDestinationEventCRF(studySubject,
				properties.get(0), subjectStudy, sourceEventCrfBean, eventCRFBeans.get(0), sourceStudyEventBean,
				sourceItemDataBean, destinationItemBean, expression, ruleSetBean, user, connection);
	}

	@Test
	public void testThatInsertBehavesCorrectlyWhenInsertDataIntoCRFInsideDifferentNotScheduledStudyEvent() {

		// SETUP
		StudyEventDefinitionBean destinationStudyEventDefinitionBean = new StudyEventDefinitionBean();
		destinationStudyEventDefinitionBean.setId(67);
		StudyEventBean destinationStudyEventBean = new StudyEventBean();
		destinationStudyEventBean.setId(0);
		StudyEventBean newScheduledStudyEventBean = new StudyEventBean();
		newScheduledStudyEventBean.setId(111);
		StudyParameterValueBean studyParameterValueBean = new StudyParameterValueBean();
		studyParameterValueBean.setValue("yes");
		ItemFormMetadataBean destinationItemFormMetadata = new ItemFormMetadataBean();
		destinationItemFormMetadata.setId(0);

		Mockito.when(expressionService.getStudyEventDefenitionOid(expression)).thenReturn("SE_OID15");
		Mockito.when(studyEventDefinitionDAO.findByOid("SE_OID15")).thenReturn(destinationStudyEventDefinitionBean);
		Mockito.when(dynamicsMetadataService.destinationEventDefIsInSameStudyAsSubject(
				destinationStudyEventDefinitionBean, subjectStudy)).thenReturn(true);
		Mockito.when(studyEventDAO.findByStudySubjectIdAndDefinitionIdAndOrdinal(1290, 67, 1))
				.thenReturn(destinationStudyEventBean);
		Mockito.when(studyParameterValueDAO.findByHandleAndStudy(13, "allowRulesAutoScheduling"))
				.thenReturn(studyParameterValueBean);
		Mockito.when(dynamicsMetadataService.scheduleEvent(sourceStudyEventBean, destinationStudyEventBean,
				destinationStudyEventDefinitionBean, subjectStudy)).thenReturn(newScheduledStudyEventBean);
		Mockito.when(itemFormMetadataDAO.findByItemIdAndCRFVersionId(900, 23)).thenReturn(destinationItemFormMetadata);
		Mockito.when(expressionService.getCrfOid(expression)).thenReturn("CRF_VER_OID10");
		Mockito.when(eventCRFDAO.findAllByStudyEventAndCrfOrCrfVersionOid(sourceStudyEventBean, "CRF_VER_OID10"))
				.thenReturn(new ArrayList<EventCRFBean>());
		InOrder inOrder  = Mockito.inOrder(dynamicsMetadataService);

		Mockito.doCallRealMethod().when(dynamicsMetadataService).insert(sourceItemDataId, properties, user, ruleSetBean,
				connection);

		// TEST
		dynamicsMetadataService.insert(sourceItemDataId, properties, user, ruleSetBean, connection);

		// VERIFY
		inOrder.verify(dynamicsMetadataService, Mockito.times(1)).scheduleEvent(sourceStudyEventBean,
				destinationStudyEventBean, destinationStudyEventDefinitionBean, subjectStudy);
		inOrder.verify(dynamicsMetadataService, Mockito.times(1)).createNewEventCRF(properties.get(0), subjectStudy,
				sourceEventCrfBean, newScheduledStudyEventBean, sourceItemDataBean, destinationItemBean, expression,
				ruleSetBean, user, connection);
	}

	@Test
	public void testThatInsertBehavesCorrectlyWhenInsertDataIntoNotStartedCRFInsideDifferentScheduledStudyEvent() {

		// SETUP
		StudyEventDefinitionBean destinationStudyEventDefinitionBean = new StudyEventDefinitionBean();
		destinationStudyEventDefinitionBean.setId(67);
		StudyEventBean destinationStudyEventBean = new StudyEventBean();
		destinationStudyEventBean.setId(111);
		ItemFormMetadataBean destinationItemFormMetadata = new ItemFormMetadataBean();
		destinationItemFormMetadata.setId(0);

		Mockito.when(expressionService.getStudyEventDefenitionOid(expression)).thenReturn("SE_OID15");
		Mockito.when(studyEventDefinitionDAO.findByOid("SE_OID15")).thenReturn(destinationStudyEventDefinitionBean);
		Mockito.when(dynamicsMetadataService.destinationEventDefIsInSameStudyAsSubject(
				destinationStudyEventDefinitionBean, subjectStudy)).thenReturn(true);
		Mockito.when(studyEventDAO.findByStudySubjectIdAndDefinitionIdAndOrdinal(1290, 67, 1))
				.thenReturn(destinationStudyEventBean);
		Mockito.when(itemFormMetadataDAO.findByItemIdAndCRFVersionId(900, 23)).thenReturn(destinationItemFormMetadata);
		Mockito.when(expressionService.getCrfOid(expression)).thenReturn("CRF_VER_OID10");
		Mockito.when(eventCRFDAO.findAllByStudyEventAndCrfOrCrfVersionOid(sourceStudyEventBean, "CRF_VER_OID10"))
				.thenReturn(new ArrayList<EventCRFBean>());
		InOrder inOrder  = Mockito.inOrder(dynamicsMetadataService, studyEventDAO);

		Mockito.doCallRealMethod().when(dynamicsMetadataService).insert(sourceItemDataId, properties, user, ruleSetBean,
				connection);

		// TEST
		dynamicsMetadataService.insert(sourceItemDataId, properties, user, ruleSetBean, connection);

		// VERIFY
		inOrder.verify(studyEventDAO, Mockito.times(1)).findByStudySubjectIdAndDefinitionIdAndOrdinal(1290, 67, 1);
		inOrder.verify(dynamicsMetadataService, Mockito.times(1)).createNewEventCRF(properties.get(0), subjectStudy,
				sourceEventCrfBean, destinationStudyEventBean, sourceItemDataBean, destinationItemBean, expression,
				ruleSetBean, user, connection);
	}

	@Test
	public void testThatInsertBehavesCorrectlyWhenInsertDataIntoStartedCRFInsideDifferentScheduledStudyEvent() {

		// SETUP
		StudyEventDefinitionBean destinationStudyEventDefinitionBean = new StudyEventDefinitionBean();
		destinationStudyEventDefinitionBean.setId(67);
		StudyEventBean destinationStudyEventBean = new StudyEventBean();
		destinationStudyEventBean.setId(117);
		ItemFormMetadataBean destinationItemFormMetadata = new ItemFormMetadataBean();
		destinationItemFormMetadata.setId(0);
		List<EventCRFBean> eventCRFBeans = new ArrayList<EventCRFBean>();
		eventCRFBeans.add(new EventCRFBean());

		Mockito.when(expressionService.getStudyEventDefenitionOid(expression)).thenReturn("SE_OID15");
		Mockito.when(studyEventDefinitionDAO.findByOid("SE_OID15")).thenReturn(destinationStudyEventDefinitionBean);
		Mockito.when(dynamicsMetadataService.destinationEventDefIsInSameStudyAsSubject(
				destinationStudyEventDefinitionBean, subjectStudy)).thenReturn(true);
		Mockito.when(studyEventDAO.findByStudySubjectIdAndDefinitionIdAndOrdinal(1290, 67, 1))
				.thenReturn(destinationStudyEventBean);
		Mockito.when(itemFormMetadataDAO.findByItemIdAndCRFVersionId(900, 23)).thenReturn(destinationItemFormMetadata);
		Mockito.when(expressionService.getCrfOid(expression)).thenReturn("CRF_VER_OID10");
		Mockito.when(eventCRFDAO.findAllByStudyEventAndCrfOrCrfVersionOid(destinationStudyEventBean, "CRF_VER_OID10"))
				.thenReturn(eventCRFBeans);
		InOrder inOrder  = Mockito.inOrder(studyEventDAO, dynamicsMetadataService);

		Mockito.doCallRealMethod().when(dynamicsMetadataService).insert(sourceItemDataId, properties, user, ruleSetBean,
				connection);

		// TEST
		dynamicsMetadataService.insert(sourceItemDataId, properties, user, ruleSetBean, connection);

		// VERIFY
		inOrder.verify(studyEventDAO, Mockito.times(1)).findByStudySubjectIdAndDefinitionIdAndOrdinal(1290, 67, 1);
		inOrder.verify(dynamicsMetadataService, Mockito.times(1)).updateDestinationEventCRF(studySubject,
				properties.get(0), subjectStudy, sourceEventCrfBean, eventCRFBeans.get(0), destinationStudyEventBean,
				sourceItemDataBean, destinationItemBean, expression, ruleSetBean, user, connection);
	}

	@Test
	public void testThatInsertBehavesCorrectlyWhenTryToInsertDataIntoEventDefinitionFromDifferentStudy() {

		// SETUP
		StudyEventDefinitionBean destinationStudyEventDefinitionBean = new StudyEventDefinitionBean();
		destinationStudyEventDefinitionBean.setId(67);

		Mockito.when(expressionService.getStudyEventDefenitionOid(expression)).thenReturn("SE_OID15");
		Mockito.when(studyEventDefinitionDAO.findByOid("SE_OID15")).thenReturn(destinationStudyEventDefinitionBean);
		Mockito.when(dynamicsMetadataService.destinationEventDefIsInSameStudyAsSubject(
				destinationStudyEventDefinitionBean, subjectStudy)).thenReturn(false);

		Mockito.doCallRealMethod().when(dynamicsMetadataService).insert(sourceItemDataId, properties, user, ruleSetBean,
				connection);

		// TEST
		dynamicsMetadataService.insert(sourceItemDataId, properties, user, ruleSetBean, connection);

		// VERIFY
		Mockito.verify(studyEventDAO, Mockito.never())
				.findByStudySubjectIdAndDefinitionIdAndOrdinal(Mockito.any(Integer.class), Mockito.any(Integer.class),
						Mockito.any(Integer.class));

		Mockito.verify(dynamicsMetadataService, Mockito.never()).scheduleEvent(Mockito.any(StudyEventBean.class),
				Mockito.any(StudyEventBean.class), Mockito.any(StudyEventDefinitionBean.class),
				Mockito.any(StudyBean.class));

		Mockito.verify(dynamicsMetadataService, Mockito.never()).createNewEventCRF(Mockito.any(PropertyBean.class),
				Mockito.any(StudyBean.class), Mockito.any(EventCRFBean.class), Mockito.any(StudyEventBean.class),
				Mockito.any(ItemDataBean.class), Mockito.any(ItemBean.class), Mockito.any(String.class),
				Mockito.any(RuleSetBean.class), Mockito.any(UserAccountBean.class), Mockito.any(Connection.class));

		Mockito.verify(dynamicsMetadataService, Mockito.never())
				.updateDestinationEventCRF(Mockito.any(StudySubjectBean.class), Mockito.any(PropertyBean.class),
						Mockito.any(StudyBean.class), Mockito.any(EventCRFBean.class), Mockito.any(EventCRFBean.class),
						Mockito.any(StudyEventBean.class), Mockito.any(ItemDataBean.class), Mockito.any(ItemBean.class),
						Mockito.any(String.class), Mockito.any(RuleSetBean.class), Mockito.any(UserAccountBean.class),
						Mockito.any(Connection.class));
	}
}
