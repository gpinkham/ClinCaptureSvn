package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * ItemReferenceType.
 */
@Entity
@Table(name = "item_reference_type")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "item_reference_type_item_reference_type_id_seq")})
public class ItemReferenceType extends DataMapDomainObject {

    private int itemReferenceTypeId;
    private String name;
    private String description;
    private List<Item> items;

    @Id
    @Column(name = "item_reference_type_id", unique = true, nullable = false)
    @GeneratedValue(generator = "id-generator")
    public int getItemReferenceTypeId() {
        return this.itemReferenceTypeId;
    }

    public void setItemReferenceTypeId(int itemReferenceTypeId) {
        this.itemReferenceTypeId = itemReferenceTypeId;
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "itemReferenceType")
    public List<Item> getItems() {
        return this.items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
