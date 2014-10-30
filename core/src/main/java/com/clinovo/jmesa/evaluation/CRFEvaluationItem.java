package com.clinovo.jmesa.evaluation;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * CRFEvaluationItem class.
 */
public class CRFEvaluationItem {

	public static final Logger LOGGER = LoggerFactory.getLogger(CRFEvaluationItem.class);

	private int eventCrfId;
	private int eventDefinitionCrfId;
	private int studyEventId;
	private int studyEventDefinitionId;
	private int studySubjectId;
	private int studyId;
	private int crfVersionId;
	private int crfId;
	private String crfName;
	private String studyEventName;
	private String studySubjectLabel;
	private Status status;
	private SubjectEventStatus subjectEventStatus;
	private int validatorId;
	private int updaterId;
	private Date dateValidate;
	private Date dateCompleted;
	private Date dateValidateCompleted;
	private Date dateCreated;
	private Date dateUpdated;
	private CRFBean crfBean;
	private CRFVersionBean crfVersionBean;
	private DisplayEventCRFBean displayEventCRFBean;

	public Date getDateValidate() {
		return dateValidate;
	}

	public void setDateValidate(Date dateValidate) {
		this.dateValidate = dateValidate;
	}

	public Date getDateCompleted() {
		return dateCompleted;
	}

	public void setDateCompleted(Date dateCompleted) {
		this.dateCompleted = dateCompleted;
	}

	public Date getDateValidateCompleted() {
		return dateValidateCompleted;
	}

	public void setDateValidateCompleted(Date dateValidateCompleted) {
		this.dateValidateCompleted = dateValidateCompleted;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public int getUpdaterId() {
		return updaterId;
	}

	public void setUpdaterId(int updaterId) {
		this.updaterId = updaterId;
	}

	private int ownerId;
	private boolean sdv;

	public int getEventCrfId() {
		return eventCrfId;
	}

	public void setEventCrfId(int eventCrfId) {
		this.eventCrfId = eventCrfId;
	}

	public int getEventDefinitionCrfId() {
		return eventDefinitionCrfId;
	}

	public void setEventDefinitionCrfId(int eventDefinitionCrfId) {
		this.eventDefinitionCrfId = eventDefinitionCrfId;
	}

	public int getStudyEventId() {
		return studyEventId;
	}

	public void setStudyEventId(int studyEventId) {
		this.studyEventId = studyEventId;
	}

	public int getStudyEventDefinitionId() {
		return studyEventDefinitionId;
	}

	public void setStudyEventDefinitionId(int studyEventDefinitionId) {
		this.studyEventDefinitionId = studyEventDefinitionId;
	}

	public int getStudySubjectId() {
		return studySubjectId;
	}

	public void setStudySubjectId(int studySubjectId) {
		this.studySubjectId = studySubjectId;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public int getCrfVersionId() {
		return crfVersionId;
	}

	public void setCrfVersionId(int crfVersionId) {
		this.crfVersionId = crfVersionId;
	}

	public int getCrfId() {
		return crfId;
	}

	public void setCrfId(int crfId) {
		this.crfId = crfId;
	}

	public String getCrfName() {
		return crfName;
	}

	public void setCrfName(String crfName) {
		this.crfName = crfName;
	}

	public String getStudyEventName() {
		return studyEventName;
	}

	public void setStudyEventName(String studyEventName) {
		this.studyEventName = studyEventName;
	}

	public String getStudySubjectLabel() {
		return studySubjectLabel;
	}

	public void setStudySubjectLabel(String studySubjectLabel) {
		this.studySubjectLabel = studySubjectLabel;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public SubjectEventStatus getSubjectEventStatus() {
		return subjectEventStatus;
	}

	public void setSubjectEventStatus(SubjectEventStatus subjectEventStatus) {
		this.subjectEventStatus = subjectEventStatus;
	}

	public int getValidatorId() {
		return validatorId;
	}

	public void setValidatorId(int validatorId) {
		this.validatorId = validatorId;
	}

	/**
	 * Method that returns DataEntryStage for current object.
	 * 
	 * @return DataEntryStage
	 */
	public DataEntryStage getStage() {
		DataEntryStage stage = DataEntryStage.INVALID;
		try {
			EventCRFBean eventCRFBean = new EventCRFBean();
			eventCRFBean.setActive(true);
			eventCRFBean.setStatus(status);
			eventCRFBean.getStatus().setActive(true);
			eventCRFBean.setValidatorId(validatorId);
			stage = eventCRFBean.getStage();
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return stage;
	}

	public boolean isSdv() {
		return sdv;
	}

	public void setSdv(boolean sdv) {
		this.sdv = sdv;
	}

	public DisplayEventCRFBean getDisplayEventCRFBean() {
		return displayEventCRFBean;
	}

	public void setDisplayEventCRFBean(DisplayEventCRFBean displayEventCRFBean) {
		this.displayEventCRFBean = displayEventCRFBean;
	}

	public CRFVersionBean getCrfVersionBean() {
		return crfVersionBean;
	}

	public void setCrfVersionBean(CRFVersionBean crfVersionBean) {
		this.crfVersionBean = crfVersionBean;
	}

	public CRFBean getCrfBean() {
		return crfBean;
	}

	public void setCrfBean(CRFBean crfBean) {
		this.crfBean = crfBean;
	}
}
