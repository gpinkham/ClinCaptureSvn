package com.clinovo.controller;

import com.clinovo.BaseControllerTest;
import com.clinovo.i18n.LocaleResolver;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for Includes SpringServlet.
 */
public class IncludesControllerTest extends BaseControllerTest{

	@Test
	public void testThatGetPageContentReturnsCode200() throws Exception {
		this.mockMvc.perform(
				post(INCLUDES_CONTROLLER).sessionAttr(LocaleResolver.CURRENT_SESSION_LOCALE, LOCALE)
						.param("page","")).andExpect(status().isOk());
	}
}
