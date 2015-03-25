package com.clinovo.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Therapgroup table bean.
 */
@Entity
@Table(name = "therapgroup")
public class Therapgroup {

    private int therapgroupId = -1;
    private int createDate = -1;
    private String officialAtcCode = "";
    private MedicalProduct mp;
    private AtcClassification atc;

    @Id
    public int getTherapgroupId() {
        return therapgroupId;
    }

    public void setTherapgroupId(int therapgroupId) {
        this.therapgroupId = therapgroupId;
    }

    public int getCreateDate() {
        return createDate;
    }

    public void setCreateDate(int createDate) {
        this.createDate = createDate;
    }

    public String getOfficialAtcCode() {
        return officialAtcCode;
    }

    public void setOfficialAtcCode(String officialAtcCode) {
        this.officialAtcCode = officialAtcCode;
    }

    @ManyToOne
    @JoinColumn(name = "medicinalprod_id", insertable = false, updatable = false, nullable = false)
    public MedicalProduct getMp() {
        return mp;
    }

    public void setMp(MedicalProduct mp) {
        this.mp = mp;
    }

    @ManyToOne
    @JoinColumn(name = "atc_code", referencedColumnName = "atc_code", insertable = false, updatable = false, nullable = false)
    public AtcClassification getAtc() {
        return atc;
    }


    public void setAtc(AtcClassification atc) {
        this.atc = atc;
    }
}
