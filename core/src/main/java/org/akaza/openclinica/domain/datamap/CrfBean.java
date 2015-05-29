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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import java.util.Date;
import java.util.List;

/**
 * Crf bean.
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "crf", uniqueConstraints = @UniqueConstraint(columnNames = "oc_oid"))
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "crf_crf_id_seq")})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CrfBean extends DataMapDomainObject {

    private int crfId;
    private UserAccount userAccount;
    private Study study;
    private Status status;
    private String name;
    private String description;
    private Date dateCreated;
    private Date dateUpdated;
    private Integer updateId;
    private String ocOid;
    private List<ItemGroup> itemGroups;
    private List<EventDefinitionCrf> eventDefinitionCrfs;
    private List<CrfVersion> crfVersions;
    @Id
    @Column(name = "crf_id", unique = true, nullable = false)
    @GeneratedValue(generator = "id-generator")
    public int getCrfId() {
        return this.crfId;
    }

    public void setCrfId(int crfId) {
        this.crfId = crfId;
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
    @JoinColumn(name = "source_study_id")
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @Type(type = "status")
    @Column(name = "status_id")
    public Status getStatus() {
        return this.status;
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

    @Column(name = "description", length = 2048)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Column(name = "oc_oid", unique = true, nullable = false, length = 40)
    public String getOcOid() {
        return this.ocOid;
    }

    public void setOcOid(String ocOid) {
        this.ocOid = ocOid;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "crf")
    public List<ItemGroup> getItemGroups() {
        return this.itemGroups;
    }

    public void setItemGroups(List<ItemGroup> itemGroups) {
        this.itemGroups = itemGroups;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "crf")
    public List<EventDefinitionCrf> getEventDefinitionCrfs() {
        return this.eventDefinitionCrfs;
    }

    public void setEventDefinitionCrfs(List<EventDefinitionCrf> eventDefinitionCrfs) {
        this.eventDefinitionCrfs = eventDefinitionCrfs;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "crf")
    public List<CrfVersion> getCrfVersions() {
        return this.crfVersions;
    }

    public void setCrfVersions(List<CrfVersion> crfVersions) {
        this.crfVersions = crfVersions;
    }
}
