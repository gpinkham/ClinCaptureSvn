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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.clinovo.enums.eventdefenition.ReminderEmailRecipient;
import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.oid.OidGenerator;
import org.akaza.openclinica.bean.oid.StudyEventDefinitionOidGenerator;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * StudyEventDefinitionBean.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "StudyEventDefinition", namespace = "http://www.cdisc.org/ns/odm/v1.3")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE,
		getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE,
		setterVisibility = JsonAutoDetect.Visibility.NONE)
@SuppressWarnings({"rawtypes", "serial"})
public class StudyEventDefinitionBean extends AuditableEntityBean implements Comparable {

	@JsonProperty("eventName")
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

	private int studyId;

	private String studyName = ""; // not in DB

	private ArrayList crfs = new ArrayList();

	private int crfNum = 0; // number of crfs, not in DB

	private int ordinal = 1;

	private boolean populated = false; // not in DB

	// Will be used to show CRFs and their default version in the Event
	// Definition matrix
	private Map crfsWithDefaultVersion;

	@JsonProperty("oid")
	@XmlElement(name = "Oid", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String oid;

	private OidGenerator oidGenerator;

	@JsonProperty("minDay")
	@XmlElement(name = "MinDay", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private int minDay = 0;

	@JsonProperty("maxDay")
	@XmlElement(name = "MaxDay", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private int maxDay = 0;

	@JsonProperty("emailDay")
	@XmlElement(name = "EmailDay", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private int emailDay = 0;

	@JsonProperty("schDay")
	@XmlElement(name = "SchDay", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private int scheduleDay = 0;

	@JsonProperty("reminderEmailRecipients")
	@XmlElement(name = "reminderEmailRecipient", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private Set<ReminderEmailRecipient> reminderEmailRecipients = new HashSet<ReminderEmailRecipient>();

	@JsonProperty("otherStudyUsers")
	@XmlElement(name = "otherStudyUser", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private Set<String> otherStudyUsers = new HashSet<String>();

	@JsonProperty("userEmailId")
	@XmlElement(name = "UserEmailId", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private int userEmailId = 0;

	@JsonProperty("isReference")
	@XmlElement(name = "IsReference", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private boolean referenceVisit = false;

	@JsonProperty("eventDefinitionCrfs")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@XmlElement(name = "EventDefinitionCrf", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private List<EventDefinitionCRFBean> eventDefinitionCrfs = new ArrayList<EventDefinitionCRFBean>();

	@Override
	public void setName(String name) {
		super.name = name;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Default constructor.
	 */
	public StudyEventDefinitionBean() {
		this.oidGenerator = new StudyEventDefinitionOidGenerator();
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	/**
	 * Returns oid generator.
	 *
	 * @param ds DataSource
	 * @return OidGenerator
	 */
	public OidGenerator getOidGenerator(DataSource ds) {
		if (oidGenerator != null) {
			oidGenerator.setDataSource(ds);
		}
		return oidGenerator;
	}

	public void setOidGenerator(OidGenerator oidGenerator) {
		this.oidGenerator = oidGenerator;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public ArrayList getCrfs() {
		return crfs;
	}

	public void setCrfs(ArrayList crfs) {
		this.crfs = crfs;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRepeating() {
		return repeating;
	}

	public void setRepeating(boolean repeating) {
		this.repeating = repeating;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isPopulated() {
		return populated;
	}

	public void setPopulated(boolean populated) {
		this.populated = populated;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getCrfNum() {
		return crfNum;
	}

	public void setCrfNum(int crfNum) {
		this.crfNum = crfNum;
	}

	public Map getCrfsWithDefaultVersion() {
		return crfsWithDefaultVersion;
	}

	public void setCrfsWithDefaultVersion(Map crfsWithDefaultVersion) {
		this.crfsWithDefaultVersion = crfsWithDefaultVersion;
	}

	/**
	 * {@inheritDoc}
	 */
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

	public int getMinDay() {
		return minDay;
	}

	public void setMinDay(int minDay) {
		this.minDay = minDay;
	}

	public int getMaxDay() {
		return maxDay;
	}

	public void setMaxDay(int maxDay) {
		this.maxDay = maxDay;
	}

	public int getEmailDay() {
		return emailDay;
	}

	public void setEmailDay(int emailDay) {
		this.emailDay = emailDay;
	}

	public int getScheduleDay() {
		return scheduleDay;
	}

	public void setScheduleDay(int scheduleDay) {
		this.scheduleDay = scheduleDay;
	}

	public Set<ReminderEmailRecipient> getReminderEmailRecipients() {
		return new HashSet<ReminderEmailRecipient>(this.reminderEmailRecipients);
	}

	public void setReminderEmailRecipients(Set<ReminderEmailRecipient> reminderEmailRecipients) {
		this.reminderEmailRecipients = new HashSet<ReminderEmailRecipient>(reminderEmailRecipients);
	}

	public Set<String> getOtherStudyUsers() {
		return new HashSet<String>(this.otherStudyUsers);
	}

	public void setOtherStudyUsers(Set<String> otherStudyUsers) {
		this.otherStudyUsers = new HashSet<String>(otherStudyUsers);
	}

	public int getUserEmailId() {
		return userEmailId;
	}

	public void setUserEmailId(int userEmailId) {
		this.userEmailId = userEmailId;
	}

	public boolean getReferenceVisit() {
		return referenceVisit;
	}

	public void setReferenceVisit(boolean isReferenceVisit) {
		referenceVisit = isReferenceVisit;
	}

	public String getStudyName() {
		return studyName;
	}

	public void setStudyName(String name) {
		this.studyName = name;
	}

	public List<EventDefinitionCRFBean> getEventDefinitionCrfs() {
		return eventDefinitionCrfs;
	}

	public void setEventDefinitionCrfs(List<EventDefinitionCRFBean> eventDefinitionCrfs) {
		this.eventDefinitionCrfs = eventDefinitionCrfs;
	}

	/**
	 * Comparator by ordinal.
	 */
	public static class StudyEventDefinitionBeanOrdinalComparator implements Comparator<StudyEventDefinitionBean> {
		/**
		 * {@inheritDoc}
		 */
		public int compare(StudyEventDefinitionBean sed1, StudyEventDefinitionBean sed2) {
			return ((Integer) sed1.getOrdinal()).compareTo(sed2.getOrdinal());
		}
	}
}
