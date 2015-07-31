package com.clinovo.util;

import com.clinovo.BaseControllerTest;
import com.clinovo.command.SystemCommand;
import com.clinovo.command.SystemGroupHolder;
import com.clinovo.model.System;
import com.clinovo.model.SystemGroup;
import com.clinovo.validation.SystemValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SystemValidatorTest extends BaseControllerTest {

	private SystemValidator systemValidator;
	private SystemCommand systemCommand;
	private Locale locale;
	private Errors errors;

	@Before
	public void setUp() throws Exception {
		com.clinovo.model.System bioontologyUrl = new System();
		bioontologyUrl.setName("defaultBioontologyURL");
		bioontologyUrl.setValue("http://data.bioontology.org");
		com.clinovo.model.System bioontologyKey = new System();
		bioontologyKey.setName("medicalCodingApiKey");
		bioontologyKey.setValue("key");
		List<System> systemList = new ArrayList<System>();
		systemList.add(bioontologyUrl);
		systemList.add(bioontologyKey);
		SystemGroup medicalCodingGroup = new SystemGroup();
		medicalCodingGroup.setName("medical_coding");
		SystemGroupHolder systemGroupHolder = new SystemGroupHolder();
		systemGroupHolder.setGroup(medicalCodingGroup);
		systemGroupHolder.setSystemProperties(systemList);
		List<SystemGroupHolder> systemGroupHolderList = new ArrayList<SystemGroupHolder>();
		systemGroupHolderList.add(systemGroupHolder);

		systemCommand = new SystemCommand();
		systemCommand.setSystemPropertyGroups(systemGroupHolderList);
		systemValidator = new SystemValidator();
		locale = new Locale("en");
		errors = Mockito.spy(new BeanPropertyBindingResult(systemCommand, "systemCommand"));

		Whitebox.setInternalState(systemValidator, "validator", Mockito.mock(Validator.class));
		Whitebox.setInternalState(systemValidator, "messageSource", messageSource);
	}

	@Test
	public void testThatValidatorTrimsBioportalUrlValue() throws Exception {
		systemCommand.getSystemPropertyGroups().get(0).getSystemProperties()
				.get(0).setValue(" http://data.bioontology.org ");
		systemValidator.validate(systemCommand, errors, locale);
		assertEquals("http://data.bioontology.org", systemCommand
				.getSystemPropertyGroups().get(0).getSystemProperties().get(0).getValue());
	}

	@Test
	public void testThatValidatorTrimsBioportalApiValue() throws Exception {
		systemCommand.getSystemPropertyGroups().get(0).getSystemProperties()
				.get(1).setValue(" apikey ");
		systemValidator.validate(systemCommand, errors, locale);
		assertEquals("apikey", systemCommand
				.getSystemPropertyGroups().get(0).getSystemProperties().get(1).getValue());
	}

	@Test
	public void testThatValidatorRequiresApiKeyIfBiooportalUrlToBeSet() throws Exception {
		systemCommand.getSystemPropertyGroups().get(0).getSystemProperties().get(1).setValue("");
		systemValidator.validate(systemCommand, errors, locale);
		assertEquals("please_supply_the_biootology_api_key", errors.getAllErrors().get(0).getCode());
	}

	@Test
	public void testThatValidatorRequiresRemoveShashInTheEndOfBioportalUrl() throws Exception {
		systemCommand.getSystemPropertyGroups().get(0).getSystemProperties()
				.get(0).setValue("http://data.bioontology.org/");
		systemValidator.validate(systemCommand, errors, locale);
		assertEquals("please_remove_the_final_slash", errors.getAllErrors().get(0).getCode());
	}

	@Test
	public void testThatValidatorValidatesBioportalUrl() throws Exception {
		systemCommand.getSystemPropertyGroups().get(0).getSystemProperties()
				.get(0).setValue("http:data.bioontology.org");
		systemValidator.validate(systemCommand, errors, locale);
		assertEquals("please_provide_a_valid_url", errors.getAllErrors().get(0).getCode());
	}
}

