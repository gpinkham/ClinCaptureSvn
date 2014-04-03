package com.clinovo.command;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class BaseCommand implements Serializable {

	private String formWithStateFlag;

	public String getFormWithStateFlag() {
		return formWithStateFlag;
	}

	public void setFormWithStateFlag(String formWithStateFlag) {
		this.formWithStateFlag = formWithStateFlag;
	}
}
