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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package org.akaza.openclinica.bean.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * StudyFeatureConfig.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "Features", namespace = "http://www.cdisc.org/ns/odm/v1.3")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonPropertyOrder({"crfAnnotation", "dynamicGroup", "calendaredVisits", "interactiveDashboards", "itemLevelSDV", "subjectCasebookInPDF", "crfMasking", "sasExtracts", "studyEvaluator", "randomization", "medicalCoding"})
public class StudyFeatureConfig {

	@JsonProperty("crfAnnotation")
	@XmlElement(name = "CRFAnnotation", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String crfAnnotation = "yes";
	@JsonProperty("dynamicGroup")
	@XmlElement(name = "DynamicGroup", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String dynamicGroup = "yes";
	@JsonProperty("calendaredVisits")
	@XmlElement(name = "CalendaredVisits", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String calendaredVisits = "yes";
	@JsonProperty("interactiveDashboards")
	@XmlElement(name = "InteractiveDashboards", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String interactiveDashboards = "yes";
	@JsonProperty("itemLevelSDV")
	@XmlElement(name = "ItemLevelSDV", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String itemLevelSDV = "yes";
	@JsonProperty("subjectCasebookInPDF")
	@XmlElement(name = "SubjectCasebookInPDF", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String subjectCasebookInPDF = "yes";
	@JsonProperty("crfMasking")
	@XmlElement(name = "CRFMasking", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String crfMasking = "yes";
	@JsonProperty("sasExtracts")
	@XmlElement(name = "SASExtracts", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String sasExtracts = "yes";
	@JsonProperty("studyEvaluator")
	@XmlElement(name = "StudyEvaluator", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String studyEvaluator = "yes";
	@JsonProperty("randomization")
	@XmlElement(name = "Randomization", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String randomization = "no";
	@JsonProperty("medicalCoding")
	@XmlElement(name = "MedicalCoding", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String medicalCoding = "yes";

	public String getCrfAnnotation() {
		return crfAnnotation;
	}

	public void setCrfAnnotation(String crfAnnotation) {
		this.crfAnnotation = crfAnnotation;
	}

	public String getDynamicGroup() {
		return dynamicGroup;
	}

	public void setDynamicGroup(String dynamicGroup) {
		this.dynamicGroup = dynamicGroup;
	}

	public String getCalendaredVisits() {
		return calendaredVisits;
	}

	public void setCalendaredVisits(String calendaredVisits) {
		this.calendaredVisits = calendaredVisits;
	}

	public String getInteractiveDashboards() {
		return interactiveDashboards;
	}

	public void setInteractiveDashboards(String interactiveDashboards) {
		this.interactiveDashboards = interactiveDashboards;
	}

	public String getItemLevelSDV() {
		return itemLevelSDV;
	}

	public void setItemLevelSDV(String itemLevelSDV) {
		this.itemLevelSDV = itemLevelSDV;
	}

	public String getSubjectCasebookInPDF() {
		return subjectCasebookInPDF;
	}

	public void setSubjectCasebookInPDF(String subjectCasebookInPDF) {
		this.subjectCasebookInPDF = subjectCasebookInPDF;
	}

	public String getCrfMasking() {
		return crfMasking;
	}

	public void setCrfMasking(String crfMasking) {
		this.crfMasking = crfMasking;
	}

	public String getSasExtracts() {
		return sasExtracts;
	}

	public void setSasExtracts(String sasExtracts) {
		this.sasExtracts = sasExtracts;
	}

	public String getStudyEvaluator() {
		return studyEvaluator;
	}

	public void setStudyEvaluator(String studyEvaluator) {
		this.studyEvaluator = studyEvaluator;
	}

	public String getRandomization() {
		return randomization;
	}

	public void setRandomization(String randomization) {
		this.randomization = randomization;
	}

	public String getMedicalCoding() {
		return medicalCoding;
	}

	public void setMedicalCoding(String medicalCoding) {
		this.medicalCoding = medicalCoding;
	}
}
