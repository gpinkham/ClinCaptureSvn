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
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.domain.rule.action;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("4")
public class InsertActionBean extends RuleActionBean {

	private List<PropertyBean> properties;

	public InsertActionBean() {
		setActionType(ActionType.INSERT);
		setRuleActionRun(new RuleActionRunBean(true, true, true, false, false));
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	@JoinColumn(name = "rule_action_id", nullable = true)
	public List<PropertyBean> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyBean> properties) {
		this.properties = properties;
	}

	@Transient
	public void addProperty(PropertyBean property) {
		if (properties == null) {
			properties = new ArrayList<PropertyBean>();
		}
		properties.add(property);
	}

	@Override
	@Transient
	public String getSummary() {
		return "";
	}

	@Override
	@Transient
	public HashMap<String, Object> getPropertiesForDisplay() {
		LinkedHashMap<String, Object> p = new LinkedHashMap<String, Object>();
		p.put("rule_action_type", getActionType().getDescription());
		return p;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (properties == null ? 0 : properties.hashCode());
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
		InsertActionBean other = (InsertActionBean) obj;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else {
			if (properties.size() != other.properties.size())
				return false;
			for (PropertyBean propertyBean : other.properties) {
				if (!properties.contains(propertyBean))
					return false;
			}
		}
		return true;
	}

}
