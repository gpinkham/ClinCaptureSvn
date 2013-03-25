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

/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 
 *
 */

package org.akaza.openclinica.bean.odmbeans;

/**
 * 
 * @author ywang (Aug., 2010)
 * 
 */

public class ConditionsAndEligibilityBean {
	// elements
	private String conditions;
	private String keywords;
	private String eligibilityCriteria;
	private String sex;
	private String healthyVolunteersAccepted;
	private Integer expectedTotalEnrollment;
	private AgeBean age = new AgeBean();

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getEligibilityCriteria() {
		return eligibilityCriteria;
	}

	public void setEligibilityCriteria(String eligibilityCriteria) {
		this.eligibilityCriteria = eligibilityCriteria;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getHealthyVolunteersAccepted() {
		return healthyVolunteersAccepted;
	}

	public void setHealthyVolunteersAccepted(String healthyVolunteersAccepted) {
		this.healthyVolunteersAccepted = healthyVolunteersAccepted;
	}

	public Integer getExpectedTotalEnrollment() {
		return expectedTotalEnrollment;
	}

	public void setExpectedTotalEnrollment(Integer expectedTotalEnrollment) {
		this.expectedTotalEnrollment = expectedTotalEnrollment;
	}

	public AgeBean getAge() {
		return age;
	}

	public void setAge(AgeBean age) {
		this.age = age;
	}
}
