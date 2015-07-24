package com.clinovo.validator;

import org.junit.Assert;
import org.junit.Test;

public class EmailValidatorTest {

	private String[] validEmails = new String[]{"mkyong@yahoo.com", "mkyong-100@yahoo.com", "mkyong.100@yahoo.com",
			"mkyong111@mkyong.com", "mkyong-100@mkyong.net", "mkyong.100@mkyong.com.au", "mkyong@1.com",
			"mkyong@gmail.com.com", "mkyong+100@gmail.com", "mkyong-100@yahoo-test.com"};

	private String[] invalidEmails = new String[]{"mkyong", "mkyong@.com.my", "mkyong123@gmail.a", "mkyong123@.com",
			"mkyong123@.com.com", ".mkyong@mkyong.com", "mkyong()*@gmail.com", "mkyong@%*.com",
			"mkyong..2002@gmail.com", "mkyong.@gmail.com", "mkyong@mkyong@gmail.com", "mkyong@gmail.com.1a"};

	@Test
	public void ValidEmailTest() {
		for (String email : validEmails) {
			Assert.assertEquals(EmailValidator.validate(email), true);
		}

	}

	@Test
	public void InValidEmailTest() {
		for (String email : invalidEmails) {
			Assert.assertEquals(EmailValidator.validate(email), false);
		}
	}
}
