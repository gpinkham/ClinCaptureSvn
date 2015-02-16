package org.akaza.openclinica.domain;

import java.io.Serializable;

import javax.persistence.Transient;

public class DataMapDomainObject implements MutableDomainObject,Serializable {


	public void setId(Integer id) {
	}

	@Transient
	public Integer getVersion() {
		return null;
	}

	public void setVersion(Integer version) {
	}

	@Transient
	public Integer getId() {
		return null;
	}
	

}
