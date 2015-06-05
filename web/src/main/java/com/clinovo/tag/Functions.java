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
	 * @param buttonCaption the text message
	 * @param classType the button class type
	 * @return HTML button CSS class
	 */
	public static String getHtmlButtonCssClass(String buttonCaption, String classType) {
		final int maximumTextSizeForMediumButton = 16;
		String buttonClass = "button_medium" + (!classType.isEmpty() ? " medium_" + classType : "");
		if (buttonCaption.length() > maximumTextSizeForMediumButton) {
			buttonClass = "button_long" + (!classType.isEmpty() ? " long_" + classType : "");;
		}
		return buttonClass;
	}
}
