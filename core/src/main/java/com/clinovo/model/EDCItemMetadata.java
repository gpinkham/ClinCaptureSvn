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
* LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVOâ€™S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
*******************************************************************************/

package com.clinovo.model;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Event Definition CRF Item Metadata Bean.
 */
@Table(name = "edc_item_metadata")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence_name", value = "edc_item_metadata_id_seq") })
@Entity
public class EDCItemMetadata extends AbstractMutableDomainObject {

	@Column(name = "event_definition_crf_id")
	private int eventDefinitionCrfId;

	@Column(name = "crf_version_id")
	private int crfVersionId;

	@Column(name = "item_id")
	private int itemId;

	@Column(name = "sdv_required")
	private String sdvRequired;

	@Column(name = "study_event_definition_id")
	private int studyEventDefinitionId;

	public int getEventDefinitionCrfId() {
			return eventDefinitionCrfId;
	}

	public void setEventDefinitionCrfId(int eventDefinitionCrfId) {
		this.eventDefinitionCrfId = eventDefinitionCrfId;
	}

	public int getCrfVersionId() {
		return crfVersionId;
	}

	public void setCrfVersionId(int crfVersionId) {
		this.crfVersionId = crfVersionId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String isSdvRequired() {
		return sdvRequired;
	}

	/**
	 * Get value of sdvRequired column in boolean.
	 * @return boolean.
	 */
	public boolean sdvRequired() {
		return sdvRequired != null && sdvRequired.equals("1");
	}

	public void setSdvRequired(String sdvRequired) {
		this.sdvRequired = sdvRequired;
	}

	public int getStudyEventDefinitionId() {
		return studyEventDefinitionId;
	}

	public void setStudyEventDefinitionId(int studyEventDefinitionId) {
		this.studyEventDefinitionId = studyEventDefinitionId;
	}

	/**
	 * Clone object constructor.
	 * @param metadata EDCItemMetadata
	 */
	public EDCItemMetadata(EDCItemMetadata metadata) {
		this.setId(metadata.getId());
		this.setVersion(metadata.getVersion());
		this.setEventDefinitionCrfId(metadata.getEventDefinitionCrfId());
		this.setStudyEventDefinitionId(metadata.getStudyEventDefinitionId());
		this.setSdvRequired(metadata.isSdvRequired());
		this.setItemId(metadata.getItemId());
		this.setCrfVersionId(metadata.getCrfVersionId());
	}

	/**
	 * Default Constructor.
	 */
	public EDCItemMetadata() {
	}

	/**
	 * Set SDV Required param using boolean value.
	 * @param boolSdvRequired boolean
	 */
	public void setBoolSdvRequired(boolean boolSdvRequired) {
		this.setSdvRequired(boolSdvRequired ? "1" : "0");
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof EDCItemMetadata) {
			EDCItemMetadata otherMetadata = (EDCItemMetadata) other;
			return otherMetadata.getStudyEventDefinitionId() == this.getStudyEventDefinitionId()
					&& otherMetadata.getEventDefinitionCrfId() == this.getEventDefinitionCrfId()
					&& otherMetadata.getCrfVersionId() == this.getCrfVersionId()
					&& otherMetadata.getItemId() == this.getItemId()
					&& otherMetadata.sdvRequired() == this.sdvRequired();
		} else {
			return false;
		}
	}
}
