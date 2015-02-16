package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;

/**
 * StudyParameter.
 */

@Table(name = "study_parameter", uniqueConstraints = @UniqueConstraint(columnNames = "handle"))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StudyParameter extends AbstractMutableDomainObject {

    private int studyParameterId;
    private String handle;
    private String name;
    private String description;
    private String defaultValue;
    private Boolean inheritable;
    private Boolean overridable;
    private Set<StudyParameterValue> studyParameterValues = new HashSet<StudyParameterValue>(0);

    @Id
    @Column(name = "study_parameter_id", unique = true, nullable = false)
    public int getStudyParameterId() {
        return this.studyParameterId;
    }

    public void setStudyParameterId(int studyParameterId) {
        this.studyParameterId = studyParameterId;
    }

    @Column(name = "handle", unique = true, length = 50)
    public String getHandle() {
        return this.handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    @Column(name = "name", length = 50)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "default_value", length = 50)
    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Column(name = "inheritable")
    public Boolean getInheritable() {
        return this.inheritable;
    }

    public void setInheritable(Boolean inheritable) {
        this.inheritable = inheritable;
    }

    @Column(name = "overridable")
    public Boolean getOverridable() {
        return this.overridable;
    }

    public void setOverridable(Boolean overridable) {
        this.overridable = overridable;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "studyParameter")
    public Set<StudyParameterValue> getStudyParameterValues() {
        return this.studyParameterValues;
    }

    public void setStudyParameterValues(Set<StudyParameterValue> studyParameterValues) {
        this.studyParameterValues = studyParameterValues;
    }

}
