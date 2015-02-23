package org.akaza.openclinica.domain.datamap;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.akaza.openclinica.domain.DataMapDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * ResponseSet.
 */
@Entity
@Table(name = "response_set")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "response_set_response_set_id_seq") })
public class ResponseSet  extends DataMapDomainObject {

    private int responseSetId;
    private ResponseType responseType;
    private String label;
    private String optionsText;
    private String optionsValues;
    private Integer versionId;
    private List<ItemFormMetadata> itemFormMetadatas;

    @Id
    @Column(name = "response_set_id", unique = true, nullable = false)
    @GeneratedValue(generator = "id-generator")
    public int getResponseSetId() {
        return this.responseSetId;
    }

    public void setResponseSetId(int responseSetId) {
        this.responseSetId = responseSetId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_type_id")
    public ResponseType getResponseType() {
        return this.responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    @Column(name = "label", length = 80)
    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Column(name = "options_text", length = 4000)
    public String getOptionsText() {
        return this.optionsText;
    }

    public void setOptionsText(String optionsText) {
        this.optionsText = optionsText;
    }

    @Column(name = "options_values", length = 4000)
    public String getOptionsValues() {
        return this.optionsValues;
    }

    public void setOptionsValues(String optionsValues) {
        this.optionsValues = optionsValues;
    }

    @Column(name = "version_id")
    public Integer getVersionId() {
        return this.versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "responseSet")
    public List<ItemFormMetadata> getItemFormMetadatas() {
        return this.itemFormMetadatas;
    }

    public void setItemFormMetadatas(List<ItemFormMetadata> itemFormMetadatas) {
        this.itemFormMetadatas = itemFormMetadatas;
    }



}