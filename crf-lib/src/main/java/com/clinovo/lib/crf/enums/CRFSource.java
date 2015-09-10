package com.clinovo.lib.crf.enums;

/**
 * Source of the CRF.
 */
public enum CRFSource {

	SOURCE_DEFAULT("excel"),
	SOURCE_FORM_STUDIO("formstudio");

	private String name;

	CRFSource(String name) {
		this.name = name;
	}

	public String getSourceName() {
		return name;
	}
}