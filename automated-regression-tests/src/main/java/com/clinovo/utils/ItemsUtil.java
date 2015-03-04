package com.clinovo.utils;

import net.thucydides.core.webelements.Checkbox;

public class ItemsUtil {
	public static void fillCheckbox(Checkbox chbox, String string) {
		if (!string.isEmpty() && !string.equals("0") && !string.equalsIgnoreCase("no") && 
				!string.equalsIgnoreCase("false") && !string.equalsIgnoreCase("unchecked")) {
			chbox.setChecked(true);
		} else {
			chbox.setChecked(false);
		}
	}
}
