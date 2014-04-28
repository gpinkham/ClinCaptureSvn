package com.clinovo.command;

import com.clinovo.model.SystemGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SystemGroupHolder implements Serializable {

	private SystemGroup group;
	private boolean opened;
	private boolean isStudySpecific;

	private List<SystemGroupHolder> subGroups = new ArrayList<SystemGroupHolder>();

	private List<com.clinovo.model.System> systemProperties = new ArrayList<com.clinovo.model.System>();

	public SystemGroupHolder() {
	}

	public SystemGroupHolder(SystemGroup group) {
		this.group = group;
	}

	public SystemGroup getGroup() {
		return group;
	}

	public void setGroup(SystemGroup group) {
		this.group = group;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public List<SystemGroupHolder> getSubGroups() {
		return subGroups;
	}

	public void setSubGroups(List<SystemGroupHolder> subGroups) {
		this.subGroups = subGroups;
	}

	public List<com.clinovo.model.System> getSystemProperties() {
		return systemProperties;
	}

	public void setSystemProperties(List<com.clinovo.model.System> systemProperties) {
		this.systemProperties = systemProperties;
	}

	/**
	 * @return the isStudySpecific
	 */
	public boolean getIsStudySpecific() {
		return isStudySpecific;
	}

	/**
	 * @param isStudySpecific
	 *            the isStudySpecific to set
	 */
	public void setIsStudySpecific(boolean isStudySpecific) {
		this.isStudySpecific = isStudySpecific;
	}
}
