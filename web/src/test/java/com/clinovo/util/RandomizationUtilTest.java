package com.clinovo.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RandomizationUtilTest {

	@Test
	public void testThatIsCRFSpecifiedTrialIdValidReturnsTrueIfCRFIdIsValid() {
		
		assertTrue(RandomizationUtil.isCRFSpecifiedTrialIdValid("some-id"));
	}
	
	@Test
	public void testThatIsCRFSpecifiedTrialIdValidReturnsFalseIfCRFIdIsUndefined() {
		
		assertFalse(RandomizationUtil.isCRFSpecifiedTrialIdValid("undefined"));
	}
	
	@Test
	public void testThatIsCRFSpecifiedTrialIdValidReturnsFalseForEmptyString() {
		
		assertFalse(RandomizationUtil.isCRFSpecifiedTrialIdValid(""));
	}
	
	@Test
	public void testThatIsCRFSpecifiedTrialIdValidReturnsForNullString() {
		
		assertFalse(RandomizationUtil.isCRFSpecifiedTrialIdValid("null"));
	}
	
	@Test
	public void testThatIsConfiguredTrialIdValidReturnsTrueIfIdIsSpecified() {
		
		assertTrue(RandomizationUtil.isConfiguredTrialIdValid("some-configured-id"));
	}
	
	@Test
	public void testThatIsConfiguredTrialIdValidReturnsFalseIfCRFIdIsZero() {
		
		assertFalse(RandomizationUtil.isConfiguredTrialIdValid("0"));
	}
	
	@Test
	public void testThatIsConfiguredTrialIdValidReturnsFalseForEmptyString() {
		
		assertFalse(RandomizationUtil.isConfiguredTrialIdValid(""));
	}
	
	@Test
	public void testThatIsConfiguredTrialIdValidReturnsForNull() {
		
		assertFalse(RandomizationUtil.isConfiguredTrialIdValid(null));
	}
	
	@Test
	public void testThatIsTrialDoubleConfiguredReturnsTrueIfTrialIdIsSpecifiedInBothPlaces() {
		
		assertTrue(RandomizationUtil.isTrialIdDoubleConfigured("some-configured-trial-id", "some-crf-id"));
	}
	
	@Test
	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInPropertiesFileForUndefined() {
		
		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured("some-configured-id", "undefined"));
	}
	
	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInPropertiesFileForNull() {
		
		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured("some-configured-id", "null"));
	}
	
	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInPropertiesFileForEmptyString() {
		
		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured("some-configured-id", ""));
	}
	
	@Test
	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInCRFForUndefined() {
		
		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured("0", "some-crf-id"));
	}
	
	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInCRFForNull() {
		
		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured(null, "some-crf-id"));
	}
	
	public void testThatIsTrialDoubleConfiguredReturnsFalseIfTrialIdIsOnlyConfiguredInCRFForEmptyString() {
		
		assertFalse(RandomizationUtil.isTrialIdDoubleConfigured("", "some-crf-id"));
	}
}
