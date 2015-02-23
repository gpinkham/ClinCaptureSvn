package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.user.UserAccount;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import java.util.Date;
import java.util.List;

/**
 * StudySubject.
 */
@Entity
@Table(name = "study_subject", uniqueConstraints = @UniqueConstraint(columnNames = "oc_oid"))
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "study_subject_study_subject_id_seq")})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StudySubject extends DataMapDomainObject {

    private int studySubjectId;
    private UserAccount userAccount;
    private Study study;
    private Status status;
    private Subject subject;
    private String label;
    private String secondaryLabel;
    private Date enrollmentDate;
    private Date dateCreated;
    private Date dateUpdated;
    private Integer updateId;
    private String ocOid;
    private List<SubjectGroupMap> subjectGroupMaps;
    private List<DnStudySubjectMap> dnStudySubjectMaps;
    private List<StudyEvent> studyEvents;
    private List<EventCrf> eventCrfs;

    @Id
    @Column(name = "study_subject_id", unique = true, nullable = false)
    @GeneratedValue(generator = "id-generator")
    public int getStudySubjectId() {
        return this.studySubjectId;
    }

    public void setStudySubjectId(int studySubjectId) {
        this.studySubjectId = studySubjectId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    public UserAccount getUserAccount() {
        return this.userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Column(name = "label", length = 30)
    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Column(name = "secondary_label", length = 30)
    public String getSecondaryLabel() {
        return this.secondaryLabel;
    }

    public void setSecondaryLabel(String secondaryLabel) {
        this.secondaryLabel = secondaryLabel;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "enrollment_date", length = 4)
    public Date getEnrollmentDate() {
        return this.enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "date_created", length = 4)
    public Date getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "date_updated", length = 4)
    public Date getDateUpdated() {
        return this.dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    @Column(name = "update_id")
    public Integer getUpdateId() {
        return this.updateId;
    }

    public void setUpdateId(Integer updateId) {
        this.updateId = updateId;
    }

    @Column(name = "oc_oid", unique = true, nullable = false, length = 40)
    public String getOcOid() {
        return this.ocOid;
    }

    public void setOcOid(String ocOid) {
        this.ocOid = ocOid;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "studySubject")
    public List<SubjectGroupMap> getSubjectGroupMaps() {
        return this.subjectGroupMaps;
    }

    public void setSubjectGroupMaps(List<SubjectGroupMap> subjectGroupMaps) {
        this.subjectGroupMaps = subjectGroupMaps;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "studySubject")
    public List<DnStudySubjectMap> getDnStudySubjectMaps() {
        return this.dnStudySubjectMaps;
    }

    public void setDnStudySubjectMaps(List<DnStudySubjectMap> dnStudySubjectMaps) {
        this.dnStudySubjectMaps = dnStudySubjectMaps;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_subject_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<StudyEvent> getStudyEvents() {
        return this.studyEvents;
    }

    public void setStudyEvents(List<StudyEvent> studyEvents) {
        this.studyEvents = studyEvents;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "studySubject")
    public List<EventCrf> getEventCrfs() {
        return this.eventCrfs;
    }

    public void setEventCrfs(List<EventCrf> eventCrfs) {
        this.eventCrfs = eventCrfs;
    }

}