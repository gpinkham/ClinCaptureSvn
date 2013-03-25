/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.web.table.scheduledjobs;

/**
 * This is a row element for each scheduled bean.
 * 
 * @author jnyayapathi
 * 
 */
public class ScheduledJobs {
	private String datasetId; // misnomer its actually dataset name
	private String scheduledFireTime;
	private String checkbox;
	private String fireTime;
	private String action;
	private String exportFileName;
	private String jobStatus;

	public ScheduledJobs() {
		fireTime = "";
		datasetId = "";
		scheduledFireTime = "";
		checkbox = "";
	}

	public String getFireTime() {
		return fireTime;
	}

	public void setFireTime(String fireTime) {
		this.fireTime = fireTime;
	}

	public String getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public String getScheduledFireTime() {
		return scheduledFireTime;
	}

	public void setScheduledFireTime(String scheduledFireTime) {
		this.scheduledFireTime = scheduledFireTime;
	}

	public String getCheckbox() {
		return checkbox;
	}

	public void setCheckbox(String checkbox) {
		this.checkbox = checkbox;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setExportFileName(String exportFileName) {
		this.exportFileName = exportFileName;
	}

	public String getExportFileName() {
		return exportFileName;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	public String getJobStatus() {
		return jobStatus;
	}

}
