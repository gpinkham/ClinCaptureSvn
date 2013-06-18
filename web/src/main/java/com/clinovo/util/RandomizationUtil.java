package com.clinovo.util;


public class RandomizationUtil {

	public static boolean isTrialIdDoubleConfigured(String configuredTrialId, String crfSpecifiedId) {

		if (isConfiguredTrialIdValid(configuredTrialId) && isCRFSpecifiedTrialIdValid(crfSpecifiedId)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isConfiguredTrialIdValid(String trialId) {

		return trialId != null && trialId.length() > 0 && !trialId.equals("0");
	}

	public static boolean isCRFSpecifiedTrialIdValid(String trialId) {

		return !trialId.equals("null") && trialId.length() > 0 && !trialId.equals("undefined");
	}
}
