package com.clinovo.bean.display;

/**
 * This bean is used to display rows in the small horizontal bar widgets, with some extra field.
 */
@SuppressWarnings("serial")
public class WidgetsRowWithExtraField extends WidgetsRowWithName {

	private Object extraField;

	/**
	 * Get an extra field for this object.
	 * @return <code>Object</code> the extra field.
	 */
	public Object getExtraField() {
		return extraField;
	}

	/**
	 * Set an extra field for this object.
	 * @param extraField the value of the extra field.
	 */
	public void setExtraField(Object extraField) {
		this.extraField = extraField;
	}
}

