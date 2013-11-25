package com.clinovo.model;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Table(name = "coded_item_element")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "coded_item_element_id_seq") })
public class CodedItemElement extends AbstractMutableDomainObject {

    public CodedItemElement() {

    }

    public CodedItemElement(int itemDataId, String itemName) {
        this.itemDataId = itemDataId;
        this.itemName = itemName;
    }

    public CodedItemElement(int itemDataId, String itemName, String itemCode) {
        this.itemDataId = itemDataId;
        this.itemName = itemName;
        this.itemCode = itemCode;
    }

    private CodedItem codedItem;

    private int itemDataId = -1;
    private String itemName = "";
    private String itemCode = "";

    @ManyToOne
    @JoinColumn(name="coded_Item_id", referencedColumnName = "id", insertable=false, updatable=false, nullable=false)
    public CodedItem getCodedItem() {
        return codedItem;
    }

    public void setCodedItem(CodedItem codedItem) {
        this.codedItem = codedItem;
    }

    public int getItemDataId() {
        return itemDataId;
    }


    public void setItemDataId(int itemDataId) {
        this.itemDataId = itemDataId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
}
