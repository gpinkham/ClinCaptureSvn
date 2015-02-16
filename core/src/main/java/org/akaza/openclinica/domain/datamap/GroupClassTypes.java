package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * GroupClassTypes.
 */
@Entity
@Table(name = "group_class_types")
public class GroupClassTypes extends DataMapDomainObject {

    private int groupClassTypeId;
    private String name;
    private String description;
    private List<StudyGroupClass> studyGroupClasses;

    @Id
    @Column(name = "group_class_type_id", unique = true, nullable = false)
    public int getGroupClassTypeId() {
        return this.groupClassTypeId;
    }

    public void setGroupClassTypeId(int groupClassTypeId) {
        this.groupClassTypeId = groupClassTypeId;
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "groupClassTypes")
    public List<StudyGroupClass> getStudyGroupClasses() {
        return this.studyGroupClasses;
    }

    public void setStudyGroupClasses(List<StudyGroupClass> studyGroupClasses) {
        this.studyGroupClasses = studyGroupClasses;
    }
}
