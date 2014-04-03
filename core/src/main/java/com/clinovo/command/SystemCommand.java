package com.clinovo.command;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SystemCommand extends BaseCommand {

	private boolean backMode;

	private String newLogoUrl;

	private String newLogoPath;

	private MultipartFile logoFile;

	public boolean isBackMode() {
		return backMode;
	}

	public void setBackMode(boolean backMode) {
		this.backMode = backMode;
	}

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
