package com.clinovo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * MedDRA low level term table.
 */
@Entity
@Table(name = "low_level_term")
public class LowLevelTerm implements Serializable {
    private int lltCode = 0;
    private String lltName;
    private String lltCurrency;
    private int ptCode = 0;
    private List<MedicalHierarchy> medicalHierarchy;

    @Id
    @Column(name = "llt_code")
    public int getLltCode() {
        return lltCode;
    }

    public void setLltCode(int lltCode) {
        this.lltCode = lltCode;
    }

    @Column(name = "llt_name")
    public String getLltName() {
        return lltName;
    }

    public void setLltName(String lltName) {
        this.lltName = lltName;
    }

    @Column(name = "llt_currency")
    public String getLltCurrency() {
        return lltCurrency;
    }

    public void setLltCurrency(String lltCurrency) {
        this.lltCurrency = lltCurrency;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "lowLevelTerm")
    public List<MedicalHierarchy> getMedicalHierarchy() {
        return medicalHierarchy;
    }

    public void setMedicalHierarchy(List<MedicalHierarchy> medicalHierarchy) {
        this.medicalHierarchy = medicalHierarchy;
    }

    @Column(name = "pt_code")
    public int getPtCode() {
        return ptCode;
    }

    public void setPtCode(int ptCode) {
        this.ptCode = ptCode;
    }
}
