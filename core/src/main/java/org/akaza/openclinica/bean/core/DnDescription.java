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
 * If not, see <http://www.gnu.org/licenses/>.  Updated by Clinovo Inc on 05/19/2013.
 ******************************************************************************/
package org.akaza.openclinica.bean.core;

public class DnDescription extends EntityBean {

	private static final long serialVersionUID = 1L;
	private String description;
	private boolean isSiteVisible;
	private int studyId;
	
	public DnDescription() {
		description = "";
		isSiteVisible = true;
		studyId = 0;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isSiteVisible() {
		return isSiteVisible;
	}
	
	public void setSiteVisible(boolean isSiteVisible) {
		this.isSiteVisible = isSiteVisible;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}
	
	
	
}
