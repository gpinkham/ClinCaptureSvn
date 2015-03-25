package com.clinovo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Country code table bean.
 */
@Entity
@Table(name = "country_code")
public class CountryCode {

    private String countryCode = "";
    private String countryName = "";
    private List<MedicalProduct> mpList;

    @Id
    @Column(name = "country_code")
    public String getCountryCode() {
        return countryCode;
   }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @OneToMany(mappedBy = "sourceCountryBean")
    public List<MedicalProduct> getMpList() {
        return mpList;
    }

    public void setMpList(List<MedicalProduct> mpList) {
        this.mpList = mpList;
    }
}
