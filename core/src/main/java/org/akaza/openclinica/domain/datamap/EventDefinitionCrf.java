package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.user.UserAccount;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.Date;

/**
 * EventDefinitionCrf.
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "event_definition_crf")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence_name", value = "event_definition_crf_event_definition_crf_id_seq")})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class EventDefinitionCrf extends DataMapDomainObject {

    private int eventDefinitionCrfId;
    private UserAccount userAccount;
    private StudyEventDefinition studyEventDefinition;
    private CrfVersion crfVersion;
    private Study study;
    private Status status;
    private CrfBean crf;
    private Boolean requiredCrf;
    private Boolean doubleEntry;
    private Boolean requireAllTextFilled;
    private Boolean decisionConditions;
    private String nullValues;
    private Date dateCreated;
    private Date dateUpdated;
    private Integer updateId;
    private Integer ordinal;
    private Boolean electronicSignature;
    private Boolean hideCrf;
    private Integer sourceDataVerificationCode;
    private String selectedVersionIds;
    private Integer parentId;

    @Id
    @Column(name = "event_definition_crf_id", unique = true, nullable = false)
    @GeneratedValue(generator = "id-generator")
    public int getEventDefinitionCrfId() {
        return this.eventDefinitionCrfId;
    }

    public void setEventDefinitionCrfId(int eventDefinitionCrfId) {
        this.eventDefinitionCrfId = eventDefinitionCrfId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    public UserAccount getUserAccount() {
        return this.userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_event_definition_id")
    public StudyEventDefinition getStudyEventDefinition() {
        return this.studyEventDefinition;
    }

    public void setStudyEventDefinition(
            StudyEventDefinition studyEventDefinition) {
        this.studyEventDefinition = studyEventDefinition;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_version_id")
    public CrfVersion getCrfVersion() {
        return this.crfVersion;
    }

    public void setCrfVersion(CrfVersion crfVersion) {
        this.crfVersion = crfVersion;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @Type(type = "status")
    @Column(name = "status_id")
    public Status getStatus() {
        if (status != null) {
            return status;
        } else
            return Status.AVAILABLE;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crf_id")
    public CrfBean getCrf() {
        return this.crf;
    }

    public void setCrf(CrfBean crf) {
        this.crf = crf;
    }

    @Column(name = "required_crf")
    public Boolean getRequiredCrf() {
        return this.requiredCrf;
    }

    public void setRequiredCrf(Boolean requiredCrf) {
        this.requiredCrf = requiredCrf;
    }

    @Column(name = "double_entry")
    public Boolean getDoubleEntry() {
        return this.doubleEntry;
    }

    public void setDoubleEntry(Boolean doubleEntry) {
        this.doubleEntry = doubleEntry;
    }

    @Column(name = "require_all_text_filled")
    public Boolean getRequireAllTextFilled() {
        return this.requireAllTextFilled;
    }

    public void setRequireAllTextFilled(Boolean requireAllTextFilled) {
        this.requireAllTextFilled = requireAllTextFilled;
    }

    @Column(name = "decision_conditions")
    public Boolean getDecisionConditions() {
        return this.decisionConditions;
    }

    public void setDecisionConditions(Boolean decisionConditions) {
        this.decisionConditions = decisionConditions;
    }

    @Column(name = "null_values")
    public String getNullValues() {
        return this.nullValues;
    }

    public void setNullValues(String nullValues) {
        this.nullValues = nullValues;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created", length = 4)
    public Date getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_updated", length = 4)
    public Date getDateUpdated() {
        return this.dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    @Column(name = "update_id")
    public Integer getUpdateId() {
        return this.updateId;
    }

    public void setUpdateId(Integer updateId) {
        this.updateId = updateId;
    }

    @Column(name = "ordinal")
    public Integer getOrdinal() {
        return this.ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    @Column(name = "electronic_signature")
    public Boolean getElectronicSignature() {
        return this.electronicSignature;
    }

    public void setElectronicSignature(Boolean electronicSignature) {
        this.electronicSignature = electronicSignature;
    }

    @Column(name = "hide_crf")
    public Boolean getHideCrf() {
        return this.hideCrf;
    }

    public void setHideCrf(Boolean hideCrf) {
        this.hideCrf = hideCrf;
    }

    @Column(name = "source_data_verification_code")
    public Integer getSourceDataVerificationCode() {
        return this.sourceDataVerificationCode;
    }

    public void setSourceDataVerificationCode(Integer sourceDataVerificationCode) {
        this.sourceDataVerificationCode = sourceDataVerificationCode;
    }

    @Column(name = "selected_version_ids", length = 150)
    public String getSelectedVersionIds() {
        return this.selectedVersionIds;
    }

    public void setSelectedVersionIds(String selectedVersionIds) {
        this.selectedVersionIds = selectedVersionIds;
    }

    @Column(name = "parent_id")
    public Integer getParentId() {
        return this.parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
