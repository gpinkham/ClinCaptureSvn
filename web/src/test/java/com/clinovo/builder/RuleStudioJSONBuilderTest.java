package com.clinovo.builder;

import com.clinovo.BaseControllerTest;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.action.ActionType;
import org.akaza.openclinica.domain.rule.action.HideActionBean;
import org.akaza.openclinica.domain.rule.action.InsertActionBean;
import org.akaza.openclinica.domain.rule.action.PropertyBean;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

/**
 * RuleStudioJSONBuilder Test
 */
public class RuleStudioJSONBuilderTest extends BaseControllerTest {

	private RuleStudioJSONBuilder builder;
	private RuleStudioJSONBuilder mockedBuilder;
	private RuleBean ruleBean;
	private RuleSetRuleBean ruleSetRule;
	private UserAccountBean user;
	private StudyBean studyBean;
	private StudyEventDefinitionBean eventBean;
	private CRFBean crfBean;
	private CRFVersionBean versionBean;

	public static final String TARGET = "SE_TE1.F_FORM1.IG_GROUP1.I_Q1";
	public static final String EX_ITEM1 = "I_Q1";
	public static final String EX_ACTION = " eq ";
	public static final String EX_ITEM2 = "I_Q2";
	public static final String EXPRESSION = EX_ITEM1 + EX_ACTION + EX_ITEM2;
	public static final String DESTINATION = "SE_TE2.F_FORM2.IG_GROUP2.I_Q12";
	public static final String INSERT_VALUE = "F_FORM1.IG_GROUP1.I_Q3";

	@Before
	public void prepare() throws Exception {
		builder = new RuleStudioJSONBuilder(dataSource);
		mockedBuilder = Mockito.mock(RuleStudioJSONBuilder.class);

		user = new UserAccountBean();
		user.setName("root");

		studyBean = new StudyBean();
		studyBean.setOid("S_DEFAULTS1");
		studyBean.setId(1);

		eventBean = new StudyEventDefinitionBean();
		eventBean.setOid("SE_ED1NONRE");
		eventBean.setId(1);

		crfBean = new CRFBean();
		crfBean.setOid("F_AGEN");
		crfBean.setId(1);

		versionBean = new CRFVersionBean();
		versionBean.setOid("F_AGEN_V20");
		versionBean.setId(1);

		ruleSetRule = new RuleSetRuleBean();
		ruleBean = new RuleBean();
		ruleBean.setStudy(studyBean);
		List<RuleSetRuleBean> ruleSetRuleBeanList = new ArrayList<RuleSetRuleBean>();
		RuleSetRuleBean ruleSetRuleBean = new RuleSetRuleBean();
		RuleSetBean ruleSetBean = new RuleSetBean();
		ExpressionBean target = new ExpressionBean();
		target.setValue(TARGET);
		ruleSetBean.setOriginalTarget(target);
		ruleSetRuleBean.setRuleSetBean(ruleSetBean);
		ruleSetRuleBeanList.add(ruleSetRuleBean);
		ruleBean.setRuleSetRules(ruleSetRuleBeanList);
		ExpressionBean expression = new ExpressionBean();
		expression.setValue(EXPRESSION);
		ruleBean.setExpression(expression);
		List<RuleActionBean> actions = new ArrayList<RuleActionBean>();
		ruleSetRule.setActions(actions);

		StudyDAO mockedStudyDao = Mockito.mock(StudyDAO.class);
		Mockito.doReturn(mockedStudyDao).when(mockedBuilder).getStudyDao();
		Mockito.doReturn(studyBean).when(mockedStudyDao).findByPK(Mockito.anyInt());
		Mockito.doCallRealMethod().when(mockedBuilder).getCascadeRuleData(user, ruleBean, ruleSetRule);
	}

	@Test
	public void testThatBuildStudiesArrayReturnsCorrectResultSize() throws JSONException {
		JSONArray studies = builder.buildStudiesArray(user);
		Assert.assertEquals(1, studies.length());
	}

	@Test
	public void testThatBuildStudiesArrayReturnsCorrectStudy() throws JSONException {
		JSONArray studies = builder.buildStudiesArray(user);
		JSONObject study = studies.getJSONObject(0);
		Assert.assertEquals(studyBean.getOid(), study.getString("oid"));
	}

	@Test
	public void testThatBuildEventsArrayReturnsCorrectResultSize() throws Exception {
		JSONArray events = builder.buildEventsArray(studyBean);
		Assert.assertEquals(6, events.length());
	}

	@Test
	public void testThatBuildEventsArrayReturnsCorrectEvents() throws Exception {
		JSONArray events = builder.buildEventsArray(studyBean);
		boolean containsExpectedEvent = false;
		for (int i = 0; i < events.length(); i++) {
			JSONObject event = events.getJSONObject(i);
			if (event.getString("oid").equals(eventBean.getOid())) {
				containsExpectedEvent = true;
				break;
			}
		}
		Assert.assertTrue(containsExpectedEvent);
	}

	@Test
	public void testThatBuildCRFsArrayReturnsCorrectResultSize() throws Exception {
		JSONArray forms = builder.buildCRFsArray(eventBean, studyBean);
		Assert.assertEquals(3, forms.length());
	}

	@Test
	public void testThatBuildCRFsArrayReturnsCorrectCRFs() throws Exception {
		JSONArray forms = builder.buildCRFsArray(eventBean, studyBean);
		boolean containsExpectedCRFs = false;
		for (int i = 0; i < forms.length(); i++) {
			JSONObject crf = forms.getJSONObject(i);
			if (crf.getString("oid").equals(crfBean.getOid())) {
				containsExpectedCRFs = true;
				break;
			}
		}
		Assert.assertTrue(containsExpectedCRFs);
	}

	@Test
	public void testThatBuildVersionsArrayReturnsCorrectResultSize() throws Exception {
		JSONArray versions = builder.buildVersionsArray(crfBean);
		Assert.assertEquals(1, versions.length());
	}

	@Test
	public void testThatBuildVersionsArrayReturnsCorrectVersions() throws Exception {
		JSONArray versions = builder.buildVersionsArray(crfBean);
		boolean containsExpectedVersions = false;
		for (int i = 0; i < versions.length(); i++) {
			JSONObject version = versions.getJSONObject(i);
			if (version.getString("oid").equals(versionBean.getOid())) {
				containsExpectedVersions = true;
				break;
			}
		}
		Assert.assertTrue(containsExpectedVersions);
	}

	@Test
	public void testThatBuildItemsArrayReturnsCorrectResultSize() throws Exception {
		JSONArray items = builder.buildItemsArray(versionBean, studyBean);
		Assert.assertEquals(27, items.length());
	}

	@Test
	public void testThatBuildItemsArrayReturnsCorrectItems() throws Exception {
		JSONArray items = builder.buildItemsArray(versionBean, studyBean);
		boolean containsExpectedItems = false;
		for (int i = 0; i < items.length(); i++) {
			JSONObject item = items.getJSONObject(i);
			if (item.getString("oid").equals("I_AGEN_PERIODSTART")) {
				containsExpectedItems = true;
				break;
			}
		}
		Assert.assertTrue(containsExpectedItems);
	}

	@Test
	public void testThatGetCascadeRuleDataParsesAllItemsInTheExpression() throws Exception {
		mockedBuilder.getCascadeRuleData(user, ruleBean, ruleSetRule);
		Mockito.verify(mockedBuilder).cascadeParse(Mockito.eq(studyBean), Mockito.any(JSONArray.class),
				Mockito.eq(EX_ITEM2), Mockito.isNull(ExpressionBean.class));
		Mockito.verify(mockedBuilder).cascadeParse(Mockito.eq(studyBean), Mockito.any(JSONArray.class),
				Mockito.eq(EX_ITEM2), Mockito.isNull(ExpressionBean.class));
	}

	@Test
	public void testThatGetCascadeRuleDataParsesRuleTarget() throws Exception {
		mockedBuilder.getCascadeRuleData(user, ruleBean, ruleSetRule);
		Mockito.verify(mockedBuilder).cascadeParse(Mockito.eq(studyBean), Mockito.any(JSONArray.class),
				Mockito.eq(TARGET), Mockito.any(ExpressionBean.class));
	}

	@Test
	public void testThatGetCascadeRuleDataParsesInsertRuleDestination() throws Exception {
		setInsertAction();
		mockedBuilder.getCascadeRuleData(user, ruleBean, ruleSetRule);
		Mockito.verify(mockedBuilder).cascadeParse(Mockito.eq(studyBean), Mockito.any(JSONArray.class),
				Mockito.eq(DESTINATION), Mockito.any(ExpressionBean.class));
	}

	@Test
	public void testThatGetCascadeRuleDataParsesInsertRuleValueIfItsItem() throws Exception {
		setInsertAction();
		mockedBuilder.getCascadeRuleData(user, ruleBean, ruleSetRule);
		Mockito.verify(mockedBuilder).cascadeParse(Mockito.eq(studyBean), Mockito.any(JSONArray.class),
				Mockito.eq(INSERT_VALUE), Mockito.any(ExpressionBean.class));
	}

	@Test
	public void testThatGetCascadeRuleDataParsesShowHideRuleDestination() throws Exception {
		setHideAction();
		mockedBuilder.getCascadeRuleData(user, ruleBean, ruleSetRule);
		Mockito.verify(mockedBuilder).cascadeParse(Mockito.eq(studyBean), Mockito.any(JSONArray.class),
				Mockito.eq(DESTINATION), Mockito.any(ExpressionBean.class));
	}

	private void setInsertAction() {
		InsertActionBean insertAction = new InsertActionBean();
		insertAction.setActionType(ActionType.INSERT);
		List<PropertyBean> propertyBeans = new ArrayList<PropertyBean>();
		PropertyBean propertyBean = new PropertyBean();
		ExpressionBean expressionBean = new ExpressionBean();
		expressionBean.setValue(INSERT_VALUE);
		propertyBean.setValueExpression(expressionBean);
		propertyBean.setOid(DESTINATION);
		propertyBeans.add(propertyBean);
		insertAction.setProperties(propertyBeans);

		List<RuleActionBean> actions = new ArrayList<RuleActionBean>();
		actions.add(insertAction);
		ruleSetRule.setActions(actions);
	}

	private void setHideAction() {
		HideActionBean hideAction = new HideActionBean();
		hideAction.setActionType(ActionType.HIDE);
		List<PropertyBean> propertyBeans = new ArrayList<PropertyBean>();
		PropertyBean propertyBean = new PropertyBean();
		propertyBean.setOid(DESTINATION);
		propertyBeans.add(propertyBean);
		hideAction.setProperties(propertyBeans);

		List<RuleActionBean> actions = new ArrayList<RuleActionBean>();
		actions.add(hideAction);
		ruleSetRule.setActions(actions);
	}
}
