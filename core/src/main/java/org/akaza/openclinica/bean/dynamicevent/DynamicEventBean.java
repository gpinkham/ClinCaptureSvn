package org.akaza.openclinica.bean.dynamicevent;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

@SuppressWarnings({ "rawtypes", "serial" })
public class DynamicEventBean extends AuditableEntityBean implements Comparable {

	private int studyGroupClassId;

	private int studyEventDefinitionId;

	private int studyId;

	private int ordinal;

	private String name;

	private String description;
	
	public int getStudyGroupClassId() {
		return studyGroupClassId;
	}

	public void setStudyGroupClassId(int studyGroupClassId) {
		this.studyGroupClassId = studyGroupClassId;
	}

	public int getStudyEventDefinitionId() {
		return studyEventDefinitionId;
	}

	public void setStudyEventDefinitionId(int studyEventDefinitionId) {
		this.studyEventDefinitionId = studyEventDefinitionId;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		return 0; // TODO!
	}

	@Override
	public boolean equals(Object obj) {
		return false; // TODO!
	}

	public int compareTo(Object o) {
		return 0; // TODO!
	}
}
