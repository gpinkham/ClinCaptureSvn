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
 * DnSubjectMap.
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "dn_subject_map")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DnSubjectMap extends DataMapDomainObject {

    private DnSubjectMapId dnSubjectMapId;
    private DiscrepancyNote discrepancyNote;
    private Subject subject;

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "subjectId", column = @Column(name = "subject_id")),
            @AttributeOverride(name = "discrepancyNoteId", column = @Column(name = "discrepancy_note_id")),
            @AttributeOverride(name = "columnName", column = @Column(name = "column_name"))})
    public DnSubjectMapId getDnSubjectMapId() {
        return this.dnSubjectMapId;
    }

    public void setDnSubjectMapId(DnSubjectMapId id) {
        this.dnSubjectMapId = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discrepancy_note_id", insertable = false, updatable = false)
    public DiscrepancyNote getDiscrepancyNote() {
        return this.discrepancyNote;
    }

    public void setDiscrepancyNote(DiscrepancyNote discrepancyNote) {
        this.discrepancyNote = discrepancyNote;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", insertable = false, updatable = false)
    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }


}
