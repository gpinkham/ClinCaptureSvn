package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.user.UserAccount;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import java.util.Date;
import java.util.List;

/**
 * Section.
 */
@Entity
@Table(name = "section")
@SuppressWarnings("serial")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence_name", value = "section_section_id_seq")})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Section extends DataMapDomainObject {

    private int sectionId;
    private UserAccount userAccount;
    private CrfVersion crfVersion;
    private Status status;
    private String label;
    private String title;
    private String subtitle;
    private String instructions;
    private String pageNumberLabel;
    private Integer ordinal;
    private Integer parentId;
    private Date dateCreated;
    private Date dateUpdated;
    private Integer updateId;
    private Integer borders;
    private List<ItemFormMetadata> itemFormMetadatas;

    @Id
    @Column(name = "section_id", unique = true, nullable = false)
    @GeneratedValue(generator = "id-generator")

    public int getSectionId() {
        return this.sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    public UserAccount getUserAccount() {
        return this.userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crf_version_id", nullable = false)
    public CrfVersion getCrfVersion() {
        return this.crfVersion;
    }

    public void setCrfVersion(CrfVersion crfVersion) {
        this.crfVersion = crfVersion;
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

    @Column(name = "label", length = 2000)
    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Column(name = "title", length = 2000)
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "subtitle", length = 2000)
    public String getSubtitle() {
        return this.subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Column(name = "instructions", length = 2000)
    public String getInstructions() {
        return this.instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @Column(name = "page_number_label", length = 5)
    public String getPageNumberLabel() {
        return this.pageNumberLabel;
    }

    public void setPageNumberLabel(String pageNumberLabel) {
        this.pageNumberLabel = pageNumberLabel;
    }

    @Column(name = "ordinal")
    public Integer getOrdinal() {
        return this.ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    @Column(name = "parent_id")
    public Integer getParentId() {
        return this.parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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

    @Column(name = "borders")
    public Integer getBorders() {
        return this.borders;
    }

    public void setBorders(Integer borders) {
        this.borders = borders;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "section")
    public List<ItemFormMetadata> getItemFormMetadatas() {
        return this.itemFormMetadatas;
    }

    public void setItemFormMetadatas(List<ItemFormMetadata> itemFormMetadatas) {
        this.itemFormMetadatas = itemFormMetadatas;
    }


}
