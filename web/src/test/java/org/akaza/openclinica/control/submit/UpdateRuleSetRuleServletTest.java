package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockRequestDispatcher;
import org.springframework.mock.web.MockServletContext;

@PrepareForTest(UpdateRuleSetRuleServlet.class)
public class UpdateRuleSetRuleServletTest extends DefaultAppContextTest {

	private MockHttpServletResponse response;
	private MockHttpServletRequest request;
	private UpdateRuleSetRuleServlet updateRuleSetRuleServlet;
	private String ruleSetRuleId = "5";

	@Autowired
	protected RuleDao ruleDao;

	@Autowired
	protected RuleSetRuleDao ruleSetRuleDao;

	@Before
	public void setUp() throws Exception {

		updateRuleSetRuleServlet = Mockito.mock(UpdateRuleSetRuleServlet.class);
		UserAccountBean userAccountBean = new UserAccountBean();
		userAccountBean.setId(1);
		MockServletContext servletContext;
		MockRequestDispatcher requestDispatcher;
		servletContext = Mockito.mock(MockServletContext.class);
		requestDispatcher = Mockito.mock(MockRequestDispatcher.class);

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		request.setParameter("action", "delete");
		request.setParameter("ruleSetRuleId", ruleSetRuleId);
		request.getSession().setAttribute("userBean", userAccountBean);

		RuleSetRuleBean ruleSetRuleBean = new RuleSetRuleBean();
		ruleSetRuleBean.setId(Integer.valueOf(ruleSetRuleId));

		PowerMockito.doReturn(ruleSetRuleDao).when(updateRuleSetRuleServlet).getRuleSetRuleDao();
		PowerMockito.doReturn(ruleDao).when(updateRuleSetRuleServlet).getRuleDao();
		PowerMockito.doCallRealMethod().when(updateRuleSetRuleServlet).processRequest(request, response);
		PowerMockito.when(servletContext.getRequestDispatcher(Mockito.any(String.class))).thenReturn(requestDispatcher);
	}

	@Test
	public void testThatDeleteRuleSetRuleReturnsCorrectResultAfterDeletion() throws Exception {
		updateRuleSetRuleServlet.processRequest(request, response);
		RuleSetRuleBean ruleSetRuleBeanAfterUpdate = ruleSetRuleDao.findById(Integer.valueOf(ruleSetRuleId));
		Assert.assertNull(ruleSetRuleBeanAfterUpdate);
	}
}
