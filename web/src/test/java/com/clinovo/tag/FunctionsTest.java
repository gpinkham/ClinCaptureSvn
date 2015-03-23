package com.clinovo.tag;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FunctionsTest {
	
	@Test
	public void testThatButtonMediumCssClassIsReturnedForShorterButtonCaption() {
		assertEquals("button_medium", Functions.getHtmlButtonCssClass("Save & Next"));
	}
	
	@Test
	public void testThatButtonLongCssClassIsReturnedForLongerThan16CharacterButtonCaption() {
		assertEquals("button_long", Functions.getHtmlButtonCssClass("Customize Home Page"));
	}
}
