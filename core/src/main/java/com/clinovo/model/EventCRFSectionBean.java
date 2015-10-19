package com.clinovo.model;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Event CRF Section Bean.
 */
@Entity
@Table(name = "event_crf_section")
@GenericGenerator(name = "id-generator", strategy = "native",
		parameters = { @Parameter(name = "sequence", value = "event_crf_section_id_seq") })
public class EventCRFSectionBean extends AbstractMutableDomainObject {

	private int sectionId;
	private int eventCRFId;
	private boolean partialSaved;
	
	private int studyId;
	
	@Transient
	public int getStudyId() {
		return studyId;
	}

	@Transient
	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public int getSectionId() {
		return sectionId;
	}

	public void setSectionId(int sectionId) {
		this.sectionId = sectionId;
	}
	
	@Column(name="event_crf_id")
	public int getEventCRFId() {
		return eventCRFId;
	}

	public void setEventCRFId(int eventCRFId) {
		this.eventCRFId = eventCRFId;
	}

	public boolean isPartialSaved() {
		return partialSaved;
	}

	public void setPartialSaved(boolean partialSaved) {
		this.partialSaved = partialSaved;
	}
}
