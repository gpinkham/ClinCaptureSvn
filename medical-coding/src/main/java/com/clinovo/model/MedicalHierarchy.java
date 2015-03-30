package com.clinovo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;

/**
 * MEDDRA term hierarchy table bean.
 */
@Entity
@Table(name = "term_hierarchy")
@SuppressWarnings("serial")
public class MedicalHierarchy implements Serializable {

    private long id;
    private int hltCode = 0;
    private int hlgtCode = 0;
    private int socCode = 0;
    private String ptName = "";
    private String hltName = "";
    private String hlgtName = "";
    private String socName = "";
    private String socAbbrev = "";
    private int ptSocCode = 0;
    private String primarySocFg = "";
    private int ptCode = 0;

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getHltCode() {
        return hltCode;
    }

    public void setHltCode(int hltCode) {
        this.hltCode = hltCode;
    }

    public int getHlgtCode() {
        return hlgtCode;
    }

    public void setHlgtCode(int hlgtCode) {
        this.hlgtCode = hlgtCode;
    }

    public int getSocCode() {
        return socCode;
    }

    public void setSocCode(int socCode) {
        this.socCode = socCode;
    }

    public String getPtName() {
        return ptName;
    }

    public void setPtName(String ptName) {
        this.ptName = ptName;
    }

    public String getHltName() {
        return hltName;
    }

    public void setHltName(String hltName) {
        this.hltName = hltName;
    }

    public String getHlgtName() {
        return hlgtName;
    }

    public void setHlgtName(String hlgtName) {
        this.hlgtName = hlgtName;
    }

    public String getSocName() {
        return socName;
    }

    public void setSocName(String socName) {
        this.socName = socName;
    }

    public String getSocAbbrev() {
        return socAbbrev;
    }

    public void setSocAbbrev(String socAbbrev) {
        this.socAbbrev = socAbbrev;
    }

    public int getPtSocCode() {
        return ptSocCode;
    }

    public void setPtSocCode(int ptSocCode) {
        this.ptSocCode = ptSocCode;
    }

    public String getPrimarySocFg() {
        return primarySocFg;
    }

    public void setPrimarySocFg(String primarySocFg) {
        this.primarySocFg = primarySocFg;
    }

    public int getPtCode() {
        return ptCode;
    }

    public void setPtCode(int ptCode) {
        this.ptCode = ptCode;
    }
}
