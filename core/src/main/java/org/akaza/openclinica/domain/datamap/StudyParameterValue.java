package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * StudyParameterValue.
 */

@Table(name = "study_parameter_value")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StudyParameterValue extends AbstractMutableDomainObject {

    private StudyParameterValueId id;
    private Study study;
    private StudyParameter studyParameter;

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "studyParameterValueId", column = @Column(name = "study_parameter_value_id", nullable = false)),
            @AttributeOverride(name = "studyId", column = @Column(name = "study_id")),
            @AttributeOverride(name = "value", column = @Column(name = "value", length = 50)),
            @AttributeOverride(name = "parameter", column = @Column(name = "parameter"))})

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", insertable = false, updatable = false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parameter", insertable = false, updatable = false)
    public StudyParameter getStudyParameter() {
        return this.studyParameter;
    }

    public void setStudyParameter(StudyParameter studyParameter) {
        this.studyParameter = studyParameter;
    }

}
