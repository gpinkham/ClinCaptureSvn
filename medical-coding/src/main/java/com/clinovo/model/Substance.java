package com.clinovo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Substance table bean.
 */
@Entity
@Table(name = "substance")
public class Substance {

    private int substanceId = -1;
    private String casNumber = "";
    private String languageCode = "";
    private String substanceName = "";
    private String yearOfReference = "";
    private String referenceCode = "";

    private List<Ingredient> ingList;

    @Id
    @Column(name = "substance_id")
    public int getSubstanceId() {
        return substanceId;
    }

    public void setSubstanceId(int substanceId) {
        this.substanceId = substanceId;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getSubstanceName() {
        return substanceName;
    }

    public void setSubstanceName(String substanceName) {
        this.substanceName = substanceName;
    }

    public String getYearOfReference() {
        return yearOfReference;
    }

    public void setYearOfReference(String yearOfReference) {
        this.yearOfReference = yearOfReference;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    @OneToMany(mappedBy = "sun")
    public List<Ingredient> getIngList() {
        return ingList;
    }

    public void setIngList(List<Ingredient> ingList) {
        this.ingList = ingList;
    }

    public String getCasNumber() {
        return casNumber;
    }

    public void setCasNumber(String casNumber) {
        this.casNumber = casNumber;
    }

}
