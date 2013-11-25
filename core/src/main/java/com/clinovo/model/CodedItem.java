/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.model;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.*;

import com.clinovo.model.Status.CodeStatus;
import org.hibernate.annotations.Parameter;

import java.util.*;

/**
 * Encapsulates a definition of an item that can be coded.
 */
@Entity
@Table(name = "coded_item")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "coded_item_id_seq") })
public class CodedItem extends AbstractMutableDomainObject {

    private int itemId = -1;
    private int siteId = -1;
    private int studyId = -1;
    private int subjectId = -1;
    private int eventCrfId = -1;
    private int crfVersionId = -1;

    private String dictionary = "";
    private Boolean autoCoded = Boolean.FALSE;
    private String status = String.valueOf(CodeStatus.NOT_CODED);

    List<CodedItemElement> codedItemElement;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name="coded_Item_id", referencedColumnName = "id", nullable=false)
    public List<CodedItemElement> getCodedItemElements() {
        return codedItemElement;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }


    public int getEventCrfId() {
        return this.eventCrfId;
    }

    public void setEventCrfId(int eventCRFId) {
        this.eventCrfId = eventCRFId;
    }

    @Transient
    public CodeStatus getCodeStatus() {
        return CodeStatus.valueOf(status);
    }

    @Transient
    public boolean isCoded() {

        return !this.status.equals("NOT_CODED");
    }

    public int getCrfVersionId() {
        return this.crfVersionId;
    }

    public void setCrfVersionId(int crfVersionId) {
        this.crfVersionId = crfVersionId;
    }

    public int getSubjectId() {
        return this.subjectId;
    }

    public void setSubjectId(int studySubjectId) {
        this.subjectId = studySubjectId;
    }

    public int getStudyId() {
        return studyId;
    }

    public void setStudyId(int studyId) {
        this.studyId = studyId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public boolean isAutoCoded() {
        return autoCoded;
    }

    public void setAutoCoded(Boolean autoCoded) {
        this.autoCoded = autoCoded;
    }

    public void setCodedItemElements(List<CodedItemElement> codedItemElements) {
        this.codedItemElement = codedItemElements;
    }

    public void addCodedItemElements(CodedItemElement cItemElement) {
        if (codedItemElement == null) {
            codedItemElement = new ArrayList<CodedItemElement>();
        }
        codedItemElement.add(cItemElement);
    }

}
