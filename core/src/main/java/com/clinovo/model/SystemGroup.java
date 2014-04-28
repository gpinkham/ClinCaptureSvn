package com.clinovo.model;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "system_group")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "system_group_id_seq") })
public class SystemGroup extends AbstractMutableDomainObject {

	private String name;
	private int parentId;
	private int orderId;
	private boolean isStudySpecific;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the isStudySpecific
	 */
	public boolean getIsStudySpecific() {
		return isStudySpecific;
	}

	/**
	 * @param isStudySpecific
	 *            the isStudySpecific to set
	 */
	public void setIsStudySpecific(boolean isStudySpecific) {
		this.isStudySpecific = isStudySpecific;
	}
}
