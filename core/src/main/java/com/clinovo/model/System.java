/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "system")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence_name", value = "system_id_seq") })
public class System extends AbstractMutableDomainObject {

	private String name;
	private String value;
	private PropertyValueType valueType;
	private boolean required;
	private int orderId;
	private PropertyType type;
	private String typeValues;
	private int size;
	private boolean showMeasurements;
	private boolean showDescription;
	private boolean showNote;
	private int groupId;
	private PropertyAccess crc;
	private PropertyAccess investigator;
	private PropertyAccess monitor;
	private PropertyAccess admin;
	private PropertyAccess root;
	private SystemGroup systemGroup;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Enumerated(EnumType.STRING)
	public PropertyValueType getValueType() {
		return valueType;
	}

	public void setValueType(PropertyValueType valueType) {
		this.valueType = valueType;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	@Enumerated(EnumType.STRING)
	public PropertyType getType() {
		return type;
	}

	public void setType(PropertyType type) {
		this.type = type;
	}

	public String getTypeValues() {
		return typeValues;
	}

	public void setTypeValues(String typeValues) {
		this.typeValues = typeValues;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isShowMeasurements() {
		return showMeasurements;
	}

	public void setShowMeasurements(boolean showMeasurements) {
		this.showMeasurements = showMeasurements;
	}

	public boolean isShowDescription() {
		return showDescription;
	}

	public void setShowDescription(boolean showDescription) {
		this.showDescription = showDescription;
	}

	public boolean isShowNote() {
		return showNote;
	}

	public void setShowNote(boolean showNote) {
		this.showNote = showNote;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	@Enumerated(EnumType.STRING)
	public PropertyAccess getCrc() {
		return crc;
	}

	public void setCrc(PropertyAccess crc) {
		this.crc = crc;
	}

	@Enumerated(EnumType.STRING)
	public PropertyAccess getInvestigator() {
		return investigator;
	}

	public void setInvestigator(PropertyAccess investigator) {
		this.investigator = investigator;
	}

	@Enumerated(EnumType.STRING)
	public PropertyAccess getMonitor() {
		return monitor;
	}

	public void setMonitor(PropertyAccess monitor) {
		this.monitor = monitor;
	}

	@Enumerated(EnumType.STRING)
	public PropertyAccess getAdmin() {
		return admin;
	}

	public void setAdmin(PropertyAccess admin) {
		this.admin = admin;
	}

	@Enumerated(EnumType.STRING)
	public PropertyAccess getRoot() {
		return root;
	}

	public void setRoot(PropertyAccess root) {
		this.root = root;
	}

	@ManyToOne
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "groupId", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
	public SystemGroup getSystemGroup() {
		return systemGroup;
	}

	public void setSystemGroup(SystemGroup systemGroup) {
		this.systemGroup = systemGroup;
	}
}