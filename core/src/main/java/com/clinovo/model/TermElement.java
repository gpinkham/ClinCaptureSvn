package com.clinovo.model;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Table(name = "term_element")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "term_element_id_seq") })
public class TermElement extends AbstractMutableDomainObject {

    private Term term;

    private String elementName = "";
    private String termName = "";
    private String termCode = "";

    public TermElement() {

    }

    public TermElement(String termName, String termCode, String elementName) {

        this.termName = termName;
        this.termCode = termCode;
        this.elementName = elementName;
    }

    @ManyToOne
    @JoinColumn(name="term_id", referencedColumnName = "id", insertable=false, updatable=false, nullable=false)
    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getTermCode() {
        return termCode;
    }

    public void setTermCode(String termCode) {
        this.termCode = termCode;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }
}
