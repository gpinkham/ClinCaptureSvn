package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;
import org.akaza.openclinica.domain.user.UserAccount;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.Date;

/**
 * StudyGroupClass.
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "study_group_class")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "study_group_class_study_group_class_id_seq")})
public class StudyGroupClass extends DataMapDomainObject {

    private int studyGroupClassId;
    private UserAccount userAccount;
    private GroupClassTypes groupClassTypes;
    private String name;
    private Integer studyId;
    private Date dateCreated;
    private Date dateUpdated;
    private Integer updateId;
    private String subjectAssignment;

    @Id
    @Column(name = "study_group_class_id", unique = true, nullable = false)
    public int getStudyGroupClassId() {
        return this.studyGroupClassId;
    }

    public void setStudyGroupClassId(int studyGroupClassId) {
        this.studyGroupClassId = studyGroupClassId;
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
    @JoinColumn(name = "group_class_type_id")
    public GroupClassTypes getGroupClassTypes() {
        return this.groupClassTypes;
    }

    public void setGroupClassTypes(GroupClassTypes groupClassTypes) {
        this.groupClassTypes = groupClassTypes;
    }

    @Column(name = "name", length = 30)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "study_id")
    public Integer getStudyId() {
        return this.studyId;
    }

    public void setStudyId(Integer studyId) {
        this.studyId = studyId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created", length = 4)
    public Date getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Temporal(TemporalType.TIMESTAMP)
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

    @Column(name = "subject_assignment", length = 30)
    public String getSubjectAssignment() {
        return this.subjectAssignment;
    }

    public void setSubjectAssignment(String subjectAssignment) {
        this.subjectAssignment = subjectAssignment;
    }
}
