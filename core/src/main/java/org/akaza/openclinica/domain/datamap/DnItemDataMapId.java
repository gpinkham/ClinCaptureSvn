package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * DnItemDataMapId.
 */
@Embeddable
public class DnItemDataMapId extends DataMapDomainObject {

    private Integer itemDataId;
    private Integer discrepancyNoteId;
    private String columnName;
    private Integer studySubjectId;

    @Column(name = "item_data_id")
    public Integer getItemDataId() {
        return this.itemDataId;
    }

    public void setItemDataId(Integer itemDataId) {
        this.itemDataId = itemDataId;
    }

    @Column(name = "discrepancy_note_id")
    public Integer getDiscrepancyNoteId() {
        return this.discrepancyNoteId;
    }

    public void setDiscrepancyNoteId(Integer discrepancyNoteId) {
        this.discrepancyNoteId = discrepancyNoteId;
    }

    @Column(name = "column_name")
    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Column(name = "study_subject_id")
    public Integer getStudySubjectId() {
        return this.studySubjectId;
    }

    public void setStudySubjectId(Integer studySubjectId) {
        this.studySubjectId = studySubjectId;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof DnItemDataMapId))
            return false;
        DnItemDataMapId castOther = (DnItemDataMapId) other;

        return ((this.getItemDataId() == castOther.getItemDataId())
                || (this.getItemDataId() != null && castOther.getItemDataId() != null
                && this.getItemDataId().equals(castOther.getItemDataId())))
                && ((this.getDiscrepancyNoteId() == castOther.getDiscrepancyNoteId())
                || (this.getDiscrepancyNoteId() != null
                && castOther.getDiscrepancyNoteId() != null
                && this.getDiscrepancyNoteId().equals(castOther.getDiscrepancyNoteId())))
                && ((this.getColumnName() == castOther.getColumnName())
                || (this.getColumnName() != null
                && castOther.getColumnName() != null && this.getColumnName().equals(castOther.getColumnName())))
                && ((this.getStudySubjectId() == castOther.getStudySubjectId())
                || (this.getStudySubjectId() != null
                && castOther.getStudySubjectId() != null
                && this.getStudySubjectId().equals(castOther.getStudySubjectId())));
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + (getItemDataId() == null ? 0 : this.getItemDataId().hashCode());
        result = 37 * result + (getDiscrepancyNoteId() == null ? 0 : this.getDiscrepancyNoteId().hashCode());
        result = 37 * result + (getColumnName() == null ? 0 : this.getColumnName().hashCode());
        result = 37 * result + (getStudySubjectId() == null ? 0 : this.getStudySubjectId().hashCode());
        return result;
    }
}
