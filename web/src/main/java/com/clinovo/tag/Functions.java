package com.clinovo.tag;

/**
 * 
 * Custom JSTL functions.
 * 
 */
public class Functions {

	/**
	 * Gets CSS class for HTML button depending on buttonCaption length.
	 * 
	 * @param buttonCaption
	 *            String
	 * @return HTML button CSS class
	 */
	public static String getHtmlButtonCssClass(String buttonCaption) {
		final int maximumTextSizeForMediumButton = 16;
		String buttonClass = "button_medium";
		if (buttonCaption.length() > maximumTextSizeForMediumButton) {
			buttonClass = "button_long";
		}
		return buttonClass;
	}
}
