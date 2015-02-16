package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.MutableDomainObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * DnStudyEventMap.
 */
@Entity
@Table(name = "dn_study_event_map")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DnStudyEventMap implements MutableDomainObject, Serializable {

    private DnStudyEventMapId dnStudyEventMapId;
    private StudyEvent studyEvent;
    private DiscrepancyNote discrepancyNote;

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "studyEventId", column = @Column(name = "study_event_id")),
            @AttributeOverride(name = "discrepancyNoteId", column = @Column(name = "discrepancy_note_id")),
            @AttributeOverride(name = "columnName", column = @Column(name = "column_name"))})
    public DnStudyEventMapId getDnStudyEventMapId() {
        return this.dnStudyEventMapId;
    }

    public void setDnStudyEventMapId(DnStudyEventMapId id) {
        this.dnStudyEventMapId = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_event_id", insertable = false, updatable = false)
    public StudyEvent getStudyEvent() {
        return this.studyEvent;
    }

    public void setStudyEvent(StudyEvent studyEvent) {
        this.studyEvent = studyEvent;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discrepancy_note_id", insertable = false, updatable = false)
    public DiscrepancyNote getDiscrepancyNote() {
        return this.discrepancyNote;
    }

    public void setDiscrepancyNote(DiscrepancyNote discrepancyNote) {
        this.discrepancyNote = discrepancyNote;
    }

    @Transient
    public Integer getVersion() {
        return null;
    }

    public void setVersion(Integer version) {
    }

    @Transient
    public Integer getId() {
        return null;
    }

    public void setId(Integer id) {
    }

}
