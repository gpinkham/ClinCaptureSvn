package com.clinovo.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.clinovo.coding.Search;
import com.clinovo.dao.SystemDAO;
import com.clinovo.model.System;
import com.clinovo.util.SessionUtil;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.junit.Before;
import org.junit.Test;

import com.clinovo.BaseControllerTest;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Locale;

public class CodedItemsControllerTest extends BaseControllerTest {


		private StudyBean currentStudy;
		private UserAccountBean userBean;

		@Before
		public void setUp() {
			currentStudy = new StudyBean();
			currentStudy.setId(1);
			currentStudy.setStatus(Status.AVAILABLE);

			userBean = new UserAccountBean();
			userBean.setId(1);
		}

	@Test
	public void testThatSearchCodeItemReturnsCode200() throws Exception {
		this.mockMvc.perform(
				MockMvcRequestBuilders.get("/codeItem").sessionAttr(SessionUtil.CURRENT_SESSION_LOCALE, LOCALE)
						.param("item", "2")).andExpect(status().isOk());
	}

	@Test
	public void testThatGetFullItemInfoReturnsCode200() throws Exception {
		CodedItemsController controller = Mockito.mock(CodedItemsController.class);
		Search search = Mockito.mock(Search.class);
		SystemDAO systemDAO = Mockito.mock(SystemDAO.class);

		Whitebox.setInternalState(controller, "search", search);
		Whitebox.setInternalState(controller, "systemDAO", systemDAO);
		Mockito.when(systemDAO.findByName("defaultBioontologyURL")).thenReturn(new System());
		Mockito.when(systemDAO.findByName("medicalCodingApiKey")).thenReturn(new System());

		MockHttpServletRequest request = new MockHttpServletRequest();
		Mockito.when(controller.termAdditionalFieldsHandler(request)).thenCallRealMethod();
		request.setParameter("codedItemUrl", "http://purl.bioontology.org/ontology/ICD9/10019198");
		request.setParameter("term", "Head pain");
		request.getSession().setAttribute(SessionUtil.CURRENT_SESSION_LOCALE, new Locale("en"));
		controller.termAdditionalFieldsHandler(request);
	}
}
