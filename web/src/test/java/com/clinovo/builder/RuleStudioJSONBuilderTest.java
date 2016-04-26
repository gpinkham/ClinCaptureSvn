package com.clinovo.builder;

import com.clinovo.BaseControllerTest;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * RuleStudioJSONBuilder Test
 */
public class RuleStudioJSONBuilderTest extends BaseControllerTest {

	private RuleStudioJSONBuilder builder;
	private UserAccountBean user;
	private StudyBean studyBean;
	private StudyEventDefinitionBean eventBean;
	private CRFBean crfBean;
	private CRFVersionBean versionBean;

	@Before
	public void prepare() {
		builder = new RuleStudioJSONBuilder(dataSource);

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
}
