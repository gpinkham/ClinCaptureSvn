package com.clinovo;

import org.akaza.openclinica.AbstractContextSentiveTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Ignore
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:servlet-context.xml")
public class BaseControllerTest extends AbstractContextSentiveTest{

	protected MockMvc mockMvc;

	// Managed controllers
	protected final String CODED_ITEM_CONTROLLER = "/codedItems";
	protected final String DICTIONARY_CONTROLLER = "/dictionaries";

	@Autowired
	protected WebApplicationContext wac;

	protected static final Logger logger = LoggerFactory.getLogger(BaseControllerTest.class);

	@Before
	public void setup() throws Exception {
		
		super.setUp();
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("BB", "is dreaming about halle berry"));
	}
}
