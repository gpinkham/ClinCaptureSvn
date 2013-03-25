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

package org.akaza.openclinica.domain.rule.action;

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("1")
public class DiscrepancyNoteActionBean extends RuleActionBean {

    private String message;
    private String customMessage;

    public DiscrepancyNoteActionBean() {
        setActionType(ActionType.FILE_DISCREPANCY_NOTE);
		setRuleActionRun(new RuleActionRunBean(true, true, true, true, true));
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

    @Override
    @Transient
    public String getSummary() {
        return this.customMessage != null ? this.customMessage : this.message;
    }

    @Override
	@Transient
	public HashMap<String, Object> getPropertiesForDisplay() {
		LinkedHashMap<String, Object> p = new LinkedHashMap<String, Object>();
		p.put("rule_action_type", getActionType().getDescription());
		p.put("rule_action_message", "\"" + getMessage() + "\"");
		return p;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiscrepancyNoteActionBean other = (DiscrepancyNoteActionBean) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
        return true;
    }

    @Transient
    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }
}
