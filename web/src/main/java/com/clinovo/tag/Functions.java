package com.clinovo.tag;

/**
 * 
 * Custom JSTL functions.
 * 
 */
public final class Functions {

	public final static int MAX_CAPTION_SIZE_FOR_MEDIUM_BTN = 16;

	private Functions() {

	}

	/**
	 * Gets CSS class for HTML button depending on buttonCaption length.
	 * 
	 * @param buttonCaption the text message
	 * @param classType the button class type
	 * @return HTML button CSS class
	 */
	public static String getHtmlButtonCssClass(String buttonCaption, String classType) {

		String buttonClass = "button_medium" + (!classType.isEmpty() ? " medium_" + classType : "");
		if (buttonCaption.length() > MAX_CAPTION_SIZE_FOR_MEDIUM_BTN) {
			buttonClass = "button_long" + (!classType.isEmpty() ? " long_" + classType : "");
		}
		return buttonClass;
	}
}
