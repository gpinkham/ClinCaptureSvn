package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.user.UserAccount;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * StudyUserRole.
 */
@Table(name = "study_user_role")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StudyUserRole extends DataMapDomainObject {

    private StudyUserRoleId id;
    private UserAccount userAccount;

    private Study study;
    private Status status;

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "roleName", column = @Column(name = "role_name", length = 40)),
            @AttributeOverride(name = "studyId", column = @Column(name = "study_id")),
            @AttributeOverride(name = "statusId", column = @Column(name = "status_id")),
            @AttributeOverride(name = "ownerId", column = @Column(name = "owner_id")),
            @AttributeOverride(name = "dateCreated", column = @Column(name = "date_created", length = 4)),
            @AttributeOverride(name = "dateUpdated", column = @Column(name = "date_updated", length = 4)),
            @AttributeOverride(name = "updateId", column = @Column(name = "update_id")),
            @AttributeOverride(name = "userName", column = @Column(name = "user_name", length = 40))})

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", insertable = false, updatable = false)
    public UserAccount getUserAccount() {
        return this.userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", insertable = false, updatable = false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", insertable = false, updatable = false)
    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
