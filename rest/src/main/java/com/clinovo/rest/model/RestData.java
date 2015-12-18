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

package com.clinovo.rest.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;

/**
 * RestData.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "RestData", namespace = "http://www.cdisc.org/ns/odm/v1.3")
public class RestData {

	@XmlElement(name = "Error", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private Error error;

	@XmlElement(name = "Response", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private Response response;

	@XmlElement(name = "UserDetails", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private UserDetails userDetails;

	@XmlElement(name = "UserAccount", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private UserAccountBean userAccountBean;

	@XmlElement(name = "StudyEventDefinition", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private StudyEventDefinitionBean studyEventDefinitionBean;

	@XmlElement(name = "EventDefinitionCrf", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private EventDefinitionCRFBean eventDefinitionCRFBean;

	@XmlElement(name = "CrfVersion", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private CRFVersionBean crfVersionBean;

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public UserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(UserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public UserAccountBean getUserAccountBean() {
		return userAccountBean;
	}

	public void setUserAccountBean(UserAccountBean userAccountBean) {
		this.userAccountBean = userAccountBean;
	}

	public StudyEventDefinitionBean getStudyEventDefinitionBean() {
		return studyEventDefinitionBean;
	}

	public void setStudyEventDefinitionBean(StudyEventDefinitionBean studyEventDefinitionBean) {
		this.studyEventDefinitionBean = studyEventDefinitionBean;
	}

	public EventDefinitionCRFBean getEventDefinitionCRFBean() {
		return eventDefinitionCRFBean;
	}

	public void setEventDefinitionCRFBean(EventDefinitionCRFBean eventDefinitionCRFBean) {
		this.eventDefinitionCRFBean = eventDefinitionCRFBean;
	}

	public CRFVersionBean getCrfVersionBean() {
		return crfVersionBean;
	}

	public void setCrfVersionBean(CRFVersionBean crfVersionBean) {
		this.crfVersionBean = crfVersionBean;
	}
}
