package com.clinovo.rest.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.clinovo.rest.exception.RestException;

@SuppressWarnings("rawtypes")
public class ValidatorUtilTest {

	@Test
	public void testThatCheckForErrorsDoesNotThrowAnyExceptionIfPassedHashMapIsEmpty() throws Exception {
		ValidatorUtil.checkForErrors(new HashMap());
	}

	@Test(expected = RestException.class)
	public void testThatCheckForErrorsThrowsTheRestExceptionIfPassedHashMapIsNotEmpty() throws Exception {
		HashMap<String, List<String>> errors = new HashMap<String, List<String>>();
		List<String> listErrors = new ArrayList<String>();
		listErrors.add("Omg Error!!");
		errors.put("field", listErrors);
		ValidatorUtil.checkForErrors(errors);
	}
}
