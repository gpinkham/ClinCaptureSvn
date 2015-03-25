package com.clinovo.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Medical product table bean.
 */
@Entity
@Table(name = "medical_product")
public class MedicalProduct {

    private int medicinalprodId = -1;
    private String medid = "";
    private int drugRecordNumber = -1;
    private String sequenceNumber1 = "";
    private String sequenceNumber2 = "";
    private String sequenceNumber3 = "";
    private String sequenceNumber4 = "";
    private String generic = "";
    private String drugName = "";
    private String nameSpecified = "";
    private String marketingAuthorizationNumber = "";
    private String marketingAuthorizationDate = "";
    private String marketingAuthorizationWithdrawalDate = "";
    private String country = "";
    private int company = -1;
    private int marketingAuthorizationHolder = -1;
    private String referenceCode = "";
    private CountryCode sourceCountryBean;
    private String yearOfReference = "";
    private int productType = -1;
    private int productGroup = -1;
    private String createDate = "";
    private String dateChanged = "";

    private List<Ingredient> ingList = new ArrayList<Ingredient>();
    private List<Therapgroup> thgList = new ArrayList<Therapgroup>();


    public String getGeneric() {
        return generic;
    }

    public void setGeneric(String generic) {
        this.generic = generic;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getNameSpecified() {
        return nameSpecified;
    }

    public void setNameSpecified(String nameSpecified) {
        this.nameSpecified = nameSpecified;
    }

    public String getMarketingAuthorizationNumber() {
        return marketingAuthorizationNumber;
    }

    public void setMarketingAuthorizationNumber(String marketingAuthorizationNumber) {
        this.marketingAuthorizationNumber = marketingAuthorizationNumber;
    }

    public String getMarketingAuthorizationDate() {
        return marketingAuthorizationDate;
    }

    public void setMarketingAuthorizationDate(String marketingAuthorizationDate) {
        this.marketingAuthorizationDate = marketingAuthorizationDate;
    }

    public String getMarketingAuthorizationWithdrawalDate() {
        return marketingAuthorizationWithdrawalDate;
    }

    public void setMarketingAuthorizationWithdrawalDate(String marketingAuthorizationWithdrawalDate) {
        this.marketingAuthorizationWithdrawalDate = marketingAuthorizationWithdrawalDate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getCompany() {
        return company;
    }

    public void setCompany(int company) {
        this.company = company;
    }

    public int getMarketingAuthorizationHolder() {
        return marketingAuthorizationHolder;
    }

    public void setMarketingAuthorizationHolder(int marketingAuthorizationHolder) {
        this.marketingAuthorizationHolder = marketingAuthorizationHolder;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }


    public String getYearOfReference() {
        return yearOfReference;
    }

    public void setYearOfReference(String yearOfReference) {
        this.yearOfReference = yearOfReference;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public int getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(int productGroup) {
        this.productGroup = productGroup;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(String dateChanged) {
        this.dateChanged = dateChanged;
    }

    @Id
    @Column(name = "medicinalprod_id")
    public int getMedicinalprodId() {
        return medicinalprodId;
    }

    public void setMedicinalprodId(int medicinalprodId) {
        this.medicinalprodId = medicinalprodId;
    }

    public String getMedid() {
        return medid;
    }

    public void setMedid(String medid) {
        this.medid = medid;
    }

    public int getDrugRecordNumber() {
        return drugRecordNumber;
    }

    public void setDrugRecordNumber(int drugRecordNumber) {
        this.drugRecordNumber = drugRecordNumber;
    }

    @Column(name = "sequence_number_1")
    public String getSequenceNumber1() {
        return sequenceNumber1;
    }

    public void setSequenceNumber1(String sequenceNumber1) {
        this.sequenceNumber1 = sequenceNumber1;
    }

    @Column(name = "sequence_number_2")
    public String getSequenceNumber2() {
        return sequenceNumber2;
    }

    public void setSequenceNumber2(String sequenceNumber2) {
        this.sequenceNumber2 = sequenceNumber2;
    }

    @Column(name = "sequence_number_3")
    public String getSequenceNumber3() {
        return sequenceNumber3;
    }

    public void setSequenceNumber3(String sequenceNumber3) {
        this.sequenceNumber3 = sequenceNumber3;
    }

    @Column(name = "sequence_number_4")
    public String getSequenceNumber4() {
        return sequenceNumber4;
    }

    public void setSequenceNumber4(String sequenceNumber4) {
        this.sequenceNumber4 = sequenceNumber4;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "medicinalprod_id", referencedColumnName = "medicinalprod_id", nullable = false)
    public List<Ingredient> getIngList() {
        return ingList;
    }

    public void setIngList(List<Ingredient> ingList) {
        this.ingList = ingList;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_country")
    public CountryCode getSourceCountryBean() {
        return sourceCountryBean;
    }

    public void setSourceCountryBean(CountryCode sourceCountryBean) {
        this.sourceCountryBean = sourceCountryBean;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "medicinalprod_id", insertable = false, updatable = false, nullable = false)
    public List<Therapgroup> getThgList() {
        return thgList;
    }

    public void setThgList(List<Therapgroup> thgList) {
        this.thgList = thgList;
    }
}
