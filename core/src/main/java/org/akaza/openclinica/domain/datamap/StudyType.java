package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * StudyType.
 */

@Table(name = "study_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StudyType extends DataMapDomainObject {

    private int studyTypeId;
    private String name;
    private String description;
    private Set<Study> studies = new HashSet<Study>();

    @Id
    @Column(name = "study_type_id", unique = true, nullable = false)
    public int getStudyTypeId() {
        return this.studyTypeId;
    }

    public void setStudyTypeId(int studyTypeId) {
        this.studyTypeId = studyTypeId;
    }

    @Column(name = "name")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description", length = 1000)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "studyType")
    public Set<Study> getStudies() {
        return this.studies;
    }

    public void setStudies(Set studies) {
        this.studies = studies;
    }

}
