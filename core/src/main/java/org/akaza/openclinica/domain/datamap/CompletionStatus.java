package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;
import org.akaza.openclinica.domain.Status;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * CompletionStatus.
 */
@Entity
@Table(name = "completion_status")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "completion_status_completion_status_id_seq")})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CompletionStatus extends DataMapDomainObject {

    private int completionStatusId;
    private Status status;
    private String name;
    private String description;
    private List<EventCrf> eventCrfs;

    @Id
    @Column(name = "completion_status_id", unique = true, nullable = false)
    @GeneratedValue(generator = "id-generator")
    public int getCompletionStatusId() {
        return this.completionStatusId;
    }

    public void setCompletionStatusId(int completionStatusId) {
        this.completionStatusId = completionStatusId;
    }

    @Type(type = "status")
    @Column(name = "status_id")
    public Status getStatus() {
        if (status != null) {
            return status;
        } else
            return Status.AVAILABLE;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Column(name = "name")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description", length = 1000)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "completionStatus")
    public List<EventCrf> getEventCrfs() {
        return this.eventCrfs;
    }

    public void setEventCrfs(List<EventCrf> eventCrfs) {
        this.eventCrfs = eventCrfs;
    }
}