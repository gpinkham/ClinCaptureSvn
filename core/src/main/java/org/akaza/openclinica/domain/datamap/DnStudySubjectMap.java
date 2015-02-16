package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;
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

/**
 * DnStudySubjectMap.
 */
@Entity
@Table(name = "dn_study_subject_map")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DnStudySubjectMap extends DataMapDomainObject {

    private DnStudySubjectMapId dnStudySubjectMapId;
    private StudySubject studySubject;
    private DiscrepancyNote discrepancyNote;

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "studySubjectId", column = @Column(name = "study_subject_id")),
            @AttributeOverride(name = "discrepancyNoteId", column = @Column(name = "discrepancy_note_id")),
            @AttributeOverride(name = "columnName", column = @Column(name = "column_name"))})
    public DnStudySubjectMapId getDnStudySubjectMapId() {
        return this.dnStudySubjectMapId;
    }

    public void setDnStudySubjectMapId(DnStudySubjectMapId id) {
        this.dnStudySubjectMapId = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_subject_id", insertable = false, updatable = false)
    public StudySubject getStudySubject() {
        return this.studySubject;
    }

    public void setStudySubject(StudySubject studySubject) {
        this.studySubject = studySubject;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discrepancy_note_id", insertable = false, updatable = false)
    public DiscrepancyNote getDiscrepancyNote() {
        return this.discrepancyNote;
    }

    public void setDiscrepancyNote(DiscrepancyNote discrepancyNote) {
        this.discrepancyNote = discrepancyNote;
    }


}
