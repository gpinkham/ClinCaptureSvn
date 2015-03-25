package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;

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
 * DnEventCrfMap.
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "dn_event_crf_map")
public class DnEventCrfMap extends DataMapDomainObject {

    private DnEventCrfMapId dnEventCrfMapId;
    private EventCrf eventCrf;
    private DiscrepancyNote discrepancyNote;

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "eventCrfId", column = @Column(name = "event_crf_id")),
            @AttributeOverride(name = "discrepancyNoteId", column = @Column(name = "discrepancy_note_id")),
            @AttributeOverride(name = "columnName", column = @Column(name = "column_name"))})
    public DnEventCrfMapId getDnEventCrfMapId() {
        return this.dnEventCrfMapId;
    }

    public void setDnEventCrfMapId(DnEventCrfMapId id) {
        this.dnEventCrfMapId = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_crf_id", insertable = false, updatable = false)
    public EventCrf getEventCrf() {
        return this.eventCrf;
    }

    public void setEventCrf(EventCrf eventCrf) {
        this.eventCrf = eventCrf;
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
