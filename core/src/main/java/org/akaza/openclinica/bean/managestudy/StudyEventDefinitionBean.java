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

package org.akaza.openclinica.bean.managestudy;

import java.util.ArrayList;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.oid.OidGenerator;
import org.akaza.openclinica.bean.oid.StudyEventDefinitionOidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * StudyEventDefinitionBean.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "StudyEvent", namespace = "http://www.cdisc.org/ns/odm/v1.3")
@JsonPropertyOrder({"id", "oid", "name", "type", "repeating", "description", "category", "isreference", "minday",
		"maxday", "schday", "emailday", "useremailid"})
@SuppressWarnings({"rawtypes", "serial"})
public class StudyEventDefinitionBean extends AuditableEntityBean implements Comparable {

	@JsonProperty("name")
	@XmlElement(name = "EventName", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String name;

	@JsonProperty("description")
	@XmlElement(name = "EventDescription", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String description = "";

	@JsonProperty("repeating")
	@XmlElement(name = "Repeating", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private boolean repeating = false;

	@JsonProperty("category")
	@XmlElement(name = "Category", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String category = "";

	@JsonProperty("type")
	@XmlElement(name = "EventType", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String type = "";

	@JsonIgnore
	@XmlTransient
	private int studyId; // fk for study table

	@JsonIgnore
	@XmlTransient
	private String studyName = ""; // not in DB

	@JsonIgnore
	@XmlTransient
	private ArrayList crfs = new ArrayList();

	@JsonIgnore
	@XmlTransient
	private int crfNum = 0; // number of crfs, not in DB

	@JsonIgnore
	@XmlTransient
	private int ordinal = 1;

	@JsonIgnore
	@XmlTransient
	private boolean lockable = false; // not in the DB, check whether we can
	// show
	// lock link on the JSP

	@JsonIgnore
	@XmlTransient
	private boolean populated = false; // not in DB

	// Will be used to show CRFs and their default version in the Event
	// Definition matrix
	@JsonIgnore
	@XmlTransient
	private Map crfsWithDefaultVersion;
	@JsonProperty("oid")
	@XmlElement(name = "Oid", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String oid;
	@JsonIgnore
	@XmlTransient
	private OidGenerator oidGenerator;
	// Clinovo #62 start
	@JsonProperty("minday")
	@XmlElement(name = "MinDay", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private int minDay = 0;
	@JsonProperty("maxday")
	@XmlElement(name = "MaxDay", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private int maxDay = 0;
	@JsonProperty("emailday")
	@XmlElement(name = "EmailDay", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private int emailDay = 0;
	@JsonProperty("schday")
	@XmlElement(name = "SchDay", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private int scheduleDay = 0;
	@JsonProperty("useremailid")
	@XmlElement(name = "UserEmailId", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private int userEmailId = 0;
	@JsonProperty("isreference")
	@XmlElement(name = "IsReference", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private boolean referenceVisit = false;

	// end

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public StudyEventDefinitionBean() {
		this.oidGenerator = new StudyEventDefinitionOidGenerator();
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public OidGenerator getOidGenerator(DataSource ds) {
		if (oidGenerator != null) {
			oidGenerator.setDataSource(ds);
		}
		return oidGenerator;
	}

	public void setOidGenerator(OidGenerator oidGenerator) {
		this.oidGenerator = oidGenerator;
	}

	/**
	 * @return Returns the category.
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            The category to set.
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return Returns the crfs.
	 */
	public ArrayList getCrfs() {
		return crfs;
	}

	/**
	 * @param crfs
	 *            The crfs to set.
	 */
	public void setCrfs(ArrayList crfs) {
		this.crfs = crfs;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the repeating.
	 */
	public boolean isRepeating() {
		return repeating;
	}

	/**
	 * @param repeating
	 *            The repeating to set.
	 */
	public void setRepeating(boolean repeating) {
		this.repeating = repeating;
	}

	/**
	 * @return Returns the studyId.
	 */
	public int getStudyId() {
		return studyId;
	}

	/**
	 * @param studyId
	 *            The studyId to set.
	 */
	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return Returns the lockable.
	 */
	public boolean isLockable() {
		return lockable;
	}

	/**
	 * @param lockable
	 *            The lockable to set.
	 */
	public void setLockable(boolean lockable) {
		this.lockable = lockable;
	}

	/**
	 * @return Returns the populated.
	 */
	public boolean isPopulated() {
		return populated;
	}

	/**
	 * @param populated
	 *            The isPopulated to set.
	 */
	public void setPopulated(boolean populated) {
		this.populated = populated;
	}

	/**
	 * @return Returns the ordinal.
	 */
	public int getOrdinal() {
		return ordinal;
	}

	/**
	 * @param ordinal
	 *            The ordinal to set.
	 */
	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	/**
	 * @return Returns the crfNum.
	 */
	public int getCrfNum() {
		return crfNum;
	}

	/**
	 * @param crfNum
	 *            The crfNum to set.
	 */
	public void setCrfNum(int crfNum) {
		this.crfNum = crfNum;
	}

	public Map getCrfsWithDefaultVersion() {
		return crfsWithDefaultVersion;
	}

	public void setCrfsWithDefaultVersion(Map crfsWithDefaultVersion) {
		this.crfsWithDefaultVersion = crfsWithDefaultVersion;
	}

	public int compareTo(Object o) {
		if (o == null || !o.getClass().equals(this.getClass())) {
			return 0;
		}

		StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) o;
		return this.ordinal - sedb.ordinal;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().equals(this.getClass())) {
			return false;
		}
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) obj;
		return sed.id == id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	// Clinovo ticket #65

	public int getMinDay() {
		return minDay;
	}

	public void setMinDay(int min_day) {
		this.minDay = min_day;
	}

	public int getMaxDay() {
		return maxDay;
	}

	public void setMaxDay(int max_day) {
		this.maxDay = max_day;
	}

	public int getEmailDay() {
		return emailDay;
	}

	public void setEmailDay(int email_day) {
		this.emailDay = email_day;
	}

	public int getScheduleDay() {
		return scheduleDay;
	}

	public void setScheduleDay(int schedule_day) {
		this.scheduleDay = schedule_day;
	}

	public int getUserEmailId() {
		return userEmailId;
	}

	public void setUserEmailId(int user_email_id) {
		this.userEmailId = user_email_id;
	}

	public boolean getReferenceVisit() {
		return referenceVisit;
	}

	public void setReferenceVisit(boolean isRevernseVisit) {
		referenceVisit = isRevernseVisit;
	}

	public String getStudyName() {
		return studyName;
	}

	public void setStudyName(String name) {
		this.studyName = name;
	}
}
