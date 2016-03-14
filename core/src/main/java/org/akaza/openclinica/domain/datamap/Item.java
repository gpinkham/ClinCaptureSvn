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
 * Item.
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "item", uniqueConstraints = @UniqueConstraint(columnNames = "oc_oid"))
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence_name", value = "item_item_id_seq")})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Item extends DataMapDomainObject {

    private int itemId;
    private UserAccount userAccount;
    private ItemReferenceType itemReferenceType;
    private Status status;
    private ItemDataType itemDataType;
    private String name;
    private String description;
    private String units;
    private Boolean phiStatus;
    private Date dateCreated;
    private Date dateUpdated;
    private Integer updateId;
    private String ocOid;
    private List<ItemFormMetadata> itemFormMetadatas;
    private List<ItemData> itemDatas;

    private List<ItemGroupMetadata> itemGroupMetadatas;


    @Id
    @Column(name = "item_id", unique = true, nullable = false)
    @GeneratedValue(generator = "id-generator")
    public int getItemId() {
        return this.itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
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
    @JoinColumn(name = "item_reference_type_id")
    public ItemReferenceType getItemReferenceType() {
        return this.itemReferenceType;
    }

    public void setItemReferenceType(ItemReferenceType itemReferenceType) {
        this.itemReferenceType = itemReferenceType;
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
    @JoinColumn(name = "item_data_type_id")
    public ItemDataType getItemDataType() {
        return this.itemDataType;
    }

    public void setItemDataType(ItemDataType itemDataType) {
        this.itemDataType = itemDataType;
    }

    @Column(name = "name")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description", length = 4000)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "units", length = 64)
    public String getUnits() {
        return this.units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @Column(name = "phi_status")
    public Boolean getPhiStatus() {
        return this.phiStatus;
    }

    public void setPhiStatus(Boolean phiStatus) {
        this.phiStatus = phiStatus;
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public List<ItemFormMetadata> getItemFormMetadatas() {
        return this.itemFormMetadatas;
    }

    public void setItemFormMetadatas(List<ItemFormMetadata> itemFormMetadatas) {
        this.itemFormMetadatas = itemFormMetadatas;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public List<ItemData> getItemDatas() {
        return this.itemDatas;
    }

    public void setItemDatas(List<ItemData> itemDatas) {
        this.itemDatas = itemDatas;
    }


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public List<ItemGroupMetadata> getItemGroupMetadatas() {
        return this.itemGroupMetadatas;
    }

    public void setItemGroupMetadatas(List<ItemGroupMetadata> itemGroupMetadatas) {
        this.itemGroupMetadatas = itemGroupMetadatas;
    }
}
