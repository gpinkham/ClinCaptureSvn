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

package org.akaza.openclinica.bean.rule;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

import java.util.ArrayList;

public class AuditableBeanWrapper<T extends AuditableEntityBean> {
	private T auditableBean;
	private boolean isSavable;
	private ArrayList<String> importErrors;

	public AuditableBeanWrapper(T auditableBean) {
		importErrors = new ArrayList<String>();
		this.auditableBean = auditableBean;
		isSavable = true;
	}

	public void error(String message) {
		importErrors.add(message);
		setSavable(false);
	}

	public void warning(String message) {
		importErrors.add(message);
	}

	public T getAuditableBean() {
		return auditableBean;
	}

	public void setAuditableBean(T auditableBean) {
		this.auditableBean = auditableBean;
	}

	public ArrayList<String> getImportErrors() {
		return importErrors;
	}

	public void setImportErrors(ArrayList<String> importErrors) {
		this.importErrors = importErrors;
	}

	public boolean isSavable() {
		return isSavable;
	}

	public void setSavable(boolean isSavable) {
		this.isSavable = isSavable;
	}

}
