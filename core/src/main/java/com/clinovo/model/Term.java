/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "term")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence_name", value = "term_id_seq") })
public class Term extends AbstractMutableDomainObject {

	private String httpPath = "";
	private Dictionary dictionary;
	private String localAlias = "";
	private String preferredName = "";
	private Date dateCreated = new Date();
	private List<TermElement> termElementList;
	private String externalDictionaryName = "";

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name="term_id", referencedColumnName = "id", nullable=false)
    public List<TermElement> getTermElementList() {
        return termElementList;
    }

    public void setTermElementList(List<TermElement> termElementList) {
        this.termElementList = termElementList;
    }

    public void addTermElement(TermElement termElement) {
        if (termElementList == null) {
            termElementList = new ArrayList<TermElement>();
        }
        termElementList.add(termElement);
    }

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getPreferredName() {
		return preferredName;
	}

	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}

	@ManyToOne
	@JoinColumn(name="dictionary_id")
	public Dictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	public String getExternalDictionaryName() {
		return externalDictionaryName;
	}

	public void setExternalDictionaryName(String externDictionaryName) {
		this.externalDictionaryName = externDictionaryName;
	}

    public String getHttpPath() {
        return httpPath;
    }

    public void setHttpPath(String httpPath) {
        this.httpPath = httpPath;
    }

	public String getLocalAlias() {
		return localAlias;
	}

	public void setLocalAlias(String localAlias) {
		this.localAlias = localAlias;
	}
}
