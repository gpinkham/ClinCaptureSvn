package com.clinovo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * Atc table bean.
 */
@Entity
@Table(name = "atc_classification")
public class AtcClassification implements Serializable {

    private int id = -1;
    private String atcCode = "";
    private int level = 0;
    private String atcText = "";
    private List<Therapgroup> thgList;

    @Column(name = "atc_code")
    public String getAtcCode() {
        return atcCode;
    }

    public void setAtcCode(String atcCode) {
        this.atcCode = atcCode;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getAtcText() {
        return atcText;
    }

    public void setAtcText(String atcText) {
        this.atcText = atcText;
    }

    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "atc")
    public List<Therapgroup> getThgList() {
        return thgList;
    }

    public void setThgList(List<Therapgroup> thgList) {
        this.thgList = thgList;
    }
}
