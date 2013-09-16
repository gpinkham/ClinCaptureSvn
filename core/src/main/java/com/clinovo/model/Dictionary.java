package com.clinovo.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.clinovo.model.Status.DictionaryType;

@Entity
@Table(name = "dictionary")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "dictionary_id_seq") })
public class Dictionary extends AbstractMutableDomainObject {

	private String name = "";
	private Date dateCreated;
	private Date dateUpdated;
	private String description = "";
	private int type = DictionaryType.UNKNOWN.ordinal();

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String dictionary) {
		this.description = dictionary;
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
}
