package com.clinovo.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.clinovo.BaseControllerTest;
import com.clinovo.coding.Search;
import com.clinovo.dao.SystemDAO;
import com.clinovo.i18n.LocaleResolver;
import com.clinovo.model.System;

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
				MockMvcRequestBuilders.get("/codeItem").sessionAttr(LocaleResolver.CURRENT_SESSION_LOCALE, LOCALE)
						.param("item", "2").param("dictionary", "WHODrug Nov14")).andExpect(status().isOk());
	}

	@Test
	public void testThatGetFullItemInfoReturnsCode200() throws Exception {
		CodedItemsController controller = Mockito.mock(CodedItemsController.class);
		Mockito.when(controller.getSearch()).thenReturn(Mockito.mock(Search.class));
		SystemDAO systemDAO = Mockito.mock(SystemDAO.class);

		Whitebox.setInternalState(controller, "systemDAO", systemDAO);
		Mockito.when(systemDAO.findByName("defaultBioontologyURL")).thenReturn(new System());
		Mockito.when(systemDAO.findByName("medicalCodingApiKey")).thenReturn(new System());

		MockHttpServletRequest request = new MockHttpServletRequest();
		Mockito.when(controller.termAdditionalFieldsHandler(request)).thenCallRealMethod();
		request.setParameter("codedItemUrl", "http://purl.bioontology.org/ontology/ICD9/10019198");
		request.setParameter("term", "Head pain");
		request.getSession().setAttribute(LocaleResolver.CURRENT_SESSION_LOCALE, new Locale("en"));
		controller.termAdditionalFieldsHandler(request);
	}
}
