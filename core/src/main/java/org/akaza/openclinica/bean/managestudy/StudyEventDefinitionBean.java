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

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.oid.OidGenerator;
import org.akaza.openclinica.bean.oid.StudyEventDefinitionOidGenerator;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings({"rawtypes", "serial"})
public class StudyEventDefinitionBean extends AuditableEntityBean implements Comparable {
	private String description = "";

	private boolean repeating = false;

	private String category = "";

	private String type = "";

	private int studyId;// fk for study table

	private String studyName = ""; // not in DB

	private ArrayList crfs = new ArrayList();

	private int crfNum = 0; // number of crfs, not in DB

	private int ordinal = 1;

	private boolean lockable = false;// not in the DB, check whether we can
	// show
	// lock link on the JSP

	private boolean populated = false;// not in DB

	// Will be used to show CRFs and their default version in the Event
	// Definition matrix
	private Map crfsWithDefaultVersion;

	private String oid;
	private OidGenerator oidGenerator;
	//Clinovo #62 start
	private int minDay = 0;
	private int maxDay = 0;
	private int emailDay = 0;
	private int scheduleDay = 0;
	private int userEmailId = 0;
	private boolean referenceVisit = false;
	//end
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
	
	//Clinovo ticket #65
	
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
