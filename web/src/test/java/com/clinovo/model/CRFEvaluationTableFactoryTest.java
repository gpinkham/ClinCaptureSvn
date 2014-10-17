package com.clinovo.model;

import com.clinovo.BaseControllerTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.control.core.BaseController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class CRFEvaluationTableFactoryTest extends BaseControllerTest {

	@Autowired
	protected MessageSource messageSource;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private CRFEvaluationTableFactory factory;
	private StudyBean currentStudy;
	private UserAccountBean currentUser;

	@Before
	public void setUp() throws Exception {

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();

		currentStudy = new StudyBean();
		currentStudy.setId(1);
		currentStudy.setStatus(org.akaza.openclinica.bean.core.Status.AVAILABLE);
		request.getSession().setAttribute(BaseController.STUDY, currentStudy);
		currentUser = new UserAccountBean();
		currentStudy.setId(1);
		request.getSession().setAttribute(BaseController.USER_BEAN_NAME, currentUser);

		StudyParameterValueBean studyParameter = new StudyParameterValueBean();
	    studyParameter.setParameter("evaluateWithContext");
		studyParameter.setValue("false");
		factory = new CRFEvaluationTableFactory(getDataSource(), messageSource, studyParameter, "false");
	}

	@Test
	public void testThatCRFEvaluationTableFactoryReturnsTableWithRowElement() {
		String tableHtml = factory.createTable(request, response).render();
		Assert.assertEquals(true, tableHtml.contains("crfEvaluationTable_row1"));
	}

	@Test
	public void testThatCRFEvaluationTableFactoryReturnsSummaryTable() {
		factory.createTable(request, response).render();
		String summaryTable = (String) request.getAttribute("summaryTable");
		Assert.assertEquals(true, summaryTable.contains("sumBoxParent"));
	}

	@Test
	public void testThatCRFEvaluationTableFactoryReturnsSummaryTableWithCounter() {
		factory.createTable(request, response).render();
		String summaryTable = (String) request.getAttribute("summaryTable");
		Assert.assertEquals(true, summaryTable.contains(">1<"));
	}
}

