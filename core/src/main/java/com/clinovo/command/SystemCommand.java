package com.clinovo.command;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("serial")
public class SystemCommand implements Serializable {

	private String newLogoUrl;

	private String newLogoPath;

	private MultipartFile logoFile;

	public MultipartFile getLogoFile() {
		return logoFile;
	}

	public void setLogoFile(MultipartFile logoFile) {
		this.logoFile = logoFile;
	}

	private List<SystemGroupHolder> systemPropertyGroups = new ArrayList<SystemGroupHolder>();

	public List<SystemGroupHolder> getSystemPropertyGroups() {
		return systemPropertyGroups;
	}

	public void setSystemPropertyGroups(List<SystemGroupHolder> systemPropertyGroups) {
		this.systemPropertyGroups = systemPropertyGroups;
	}

	public String getNewLogoUrl() {
		return newLogoUrl;
	}

	public void setNewLogoUrl(String newLogoUrl) {
		this.newLogoUrl = newLogoUrl;
	}

	public String getNewLogoPath() {
		return newLogoPath;
	}

	public void setNewLogoPath(String newLogoPath) {
		this.newLogoPath = newLogoPath;
	}
}
