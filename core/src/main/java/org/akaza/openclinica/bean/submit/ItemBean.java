/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.bean.submit;

import java.util.ArrayList;
import java.util.Comparator;

import javax.sql.DataSource;

import com.clinovo.model.ItemRenderMetadata;
import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.extract.SasNameValidator;
import org.akaza.openclinica.bean.oid.ItemOidGenerator;
import org.akaza.openclinica.bean.oid.OidGenerator;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemDataBean;

/**
 * ItemBean for item table.
 *
 * @author thickerson
 */
@SuppressWarnings({"rawtypes", "serial"})
public class ItemBean extends AuditableEntityBean implements Comparable {

	private String description = "";
	private String units = "";
	private boolean phiStatus = false;
	private int itemDataTypeId = 0;
	private String crfVersion;
	private ImportItemDataBean importItemDataBean;
	private ItemDataType dataType;
	private int itemReferenceTypeId = 0;
	private int statusId = 1;
	private ItemFormMetadataBean itemMeta; // not in DB, for display
	private ItemRenderMetadata itemRenderMetadata;
	private ArrayList itemMetas; // not in DB, one item can have multiple meta
	private ArrayList<ItemDataBean> itemDataElements;
	private boolean selected = false; // not in DB, used for creating dataset
	private String defName = ""; // not in DB
	private int defId; // not in DB
	private String crfName = ""; // not in DB
	private String oid;
	private OidGenerator oidGenerator;
	private String datasetItemMapKey = "";
	private String sasName = "";

	/**
	 * Default constructor.
	 */
	public ItemBean() {
		dataType = ItemDataType.ST;
		itemMetas = new ArrayList();
		this.oidGenerator = new ItemOidGenerator();
	}

	/**
	 * Clones to ItemBean.
	 * @param ib ItemBean
	 * @return ItemBean
	 */
	public ItemBean cloneTo(ItemBean ib) {
		ib.id = id;
		ib.name = name;
		ib.active = active;
		ib.createdDate = createdDate;
		ib.updatedDate = updatedDate;
		ib.ownerId = ownerId;
		ib.owner = owner;
		ib.updaterId = updaterId;
		ib.updater = updater;
		ib.statusCode = statusCode;
		ib.status = status;
		ib.oldStatus = oldStatus;
		ib.udao = udao;
		ib.description = description;
		ib.units = units;
		ib.phiStatus = phiStatus;
		ib.itemDataTypeId = itemDataTypeId;
		ib.crfVersion = crfVersion;
		ib.importItemDataBean = importItemDataBean;
		ib.dataType = dataType;
		ib.itemReferenceTypeId = itemReferenceTypeId;
		ib.statusId = statusId;
		ib.itemMeta = itemMeta;
		ib.itemMetas = itemMetas;
		ib.itemDataElements = itemDataElements;
		ib.selected = selected;
		ib.defName = defName;
		ib.defId = defId;
		ib.crfName = crfName;
		ib.oid = oid;
		ib.oidGenerator = oidGenerator;
		ib.datasetItemMapKey = datasetItemMapKey;
		ib.sasName = sasName;
		ib.itemRenderMetadata = itemRenderMetadata;
		return ib;
	}

	/**
	 * Clones ItemBean.
	 *
	 * @return ItemBean
	 */
	public ItemBean clone() {
		return cloneTo(new ItemBean());
	}

	public String getCrfVersion() {
		return crfVersion;
	}

	public void setCrfVersion(String crfVersion) {
		this.crfVersion = crfVersion;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the itemDataTypeId.
	 */
	public int getItemDataTypeId() {
		return dataType.getId();
	}

	/**
	 * @param itemDataTypeId
	 *            The itemDataTypeId to set.
	 */
	public void setItemDataTypeId(int itemDataTypeId) {
		dataType = ItemDataType.get(itemDataTypeId);
		// this.itemDataTypeId = itemDataTypeId;
	}

	/**
	 * @return Returns the itemReferenceTypeId.
	 */
	public int getItemReferenceTypeId() {
		return itemReferenceTypeId;
	}

	/**
	 * @param itemReferenceTypeId
	 *            The itemReferenceTypeId to set.
	 */
	public void setItemReferenceTypeId(int itemReferenceTypeId) {
		this.itemReferenceTypeId = itemReferenceTypeId;
	}

	/**
	 * @return Returns the phiStatus.
	 */
	public boolean isPhiStatus() {
		return phiStatus;
	}

	/**
	 * @param phiStatus
	 *            The phiStatus to set.
	 */
	public void setPhiStatus(boolean phiStatus) {
		this.phiStatus = phiStatus;
	}

	/**
	 * @return Returns the statusId.
	 */
	public int getStatusId() {
		return statusId;
	}

	/**
	 * @param statusId
	 *            The statusId to set.
	 */
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	/**
	 * @return Returns the units.
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * @param units
	 *            The units to set.
	 */
	public void setUnits(String units) {
		this.units = units;
	}

	/**
	 * @return Returns the dataType.
	 */
	public ItemDataType getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            The dataType to set.
	 */
	public void setDataType(ItemDataType dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return Returns the itemMeta.
	 */
	public ItemFormMetadataBean getItemMeta() {
		return itemMeta;
	}

	/**
	 * @param itemMeta
	 *            The itemMeta to set.
	 */
	public void setItemMeta(ItemFormMetadataBean itemMeta) {
		this.itemMeta = itemMeta;
	}

	/**
	 * @return Returns the itemMetas.
	 */
	public ArrayList getItemMetas() {
		return itemMetas;
	}

	/**
	 * @param itemMetas
	 *            The itemMetas to set.
	 */
	public void setItemMetas(ArrayList itemMetas) {
		this.itemMetas = itemMetas;
	}

	/**
	 * @return Returns the itemMetas.
	 */
	public ArrayList<ItemDataBean> getItemDataElements() {
		return itemDataElements;
	}

	/**
	 * Add new item data element.
	 * @param el ItemDataBean.
	 */
	public void addItemDataElement(ItemDataBean el) {
		if (itemDataElements == null) {
			itemDataElements = new ArrayList<ItemDataBean>();
		}
		itemDataElements.add(el);
	}

	/**
	 * @param itemDataElements
	 *            The itemDataElements to set.
	 */
	public void setItemDataElements(ArrayList<ItemDataBean> itemDataElements) {
		this.itemDataElements = itemDataElements;
	}

	/**
	 * @return Returns the selected.
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            The selected to set.
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return Returns the defName.
	 */
	public String getDefName() {
		return defName;
	}

	/**
	 * @param defName
	 *            The defName to set.
	 */
	public void setDefName(String defName) {
		this.defName = defName;
	}

	/**
	 * @return Returns the crfName.
	 */
	public String getCrfName() {
		return crfName;
	}

	/**
	 * @param crfName
	 *            The crfName to set.
	 */
	public void setCrfName(String crfName) {
		this.crfName = crfName;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	/**
	 * Get OidGenerator.
	 * @param ds DataSource
	 * @return OidGenerator
	 */
	public OidGenerator getOidGenerator(DataSource ds) {
		if (oidGenerator != null) {
			oidGenerator.setDataSource(ds);
		}
		return oidGenerator;
	}

	public void setOidGenerator(OidGenerator oidGenerator) {
		this.oidGenerator = oidGenerator;
	}

	public String getDatasetItemMapKey() {
		return datasetItemMapKey;
	}

	public void setDatasetItemMapKey(String key) {
		this.datasetItemMapKey = key;
	}

	public int getDefId() {
		return defId;
	}

	public void setDefId(int defId) {
		this.defId = defId;
	}

	public ImportItemDataBean getImportItemDataBean() {
		return importItemDataBean;
	}

	public void setImportItemDataBean(ImportItemDataBean importItemDataBean) {
		this.importItemDataBean = importItemDataBean;
	}

	public ItemRenderMetadata getItemRenderMetadata() {
		return itemRenderMetadata;
	}

	public void setItemRenderMetadata(ItemRenderMetadata itemRenderMetadata) {
		this.itemRenderMetadata = itemRenderMetadata;
	}

	/**
	 * ItemBean comparator.
	 * @param o Object to compare with.
	 * @return int
	 */
	public int compareTo(Object o) {
		if (!o.getClass().equals(this.getClass())) {
			return 0;
		}

		ItemBean arg = (ItemBean) o;
		if (!getItemMetas().isEmpty() && !arg.getItemMetas().isEmpty()) {
			ItemFormMetadataBean m1 = (ItemFormMetadataBean) getItemMetas().get(0);
			ItemFormMetadataBean m2 = (ItemFormMetadataBean) arg.getItemMetas().get(0);
			return m1.getOrdinal() - m2.getOrdinal();
		} else {
			return getName().compareTo(arg.getName());
		}
	}

	/**
	 * Get valid sas name.
	 * @return String
	 */
	public String getSasName() {
		if (sasName.isEmpty()) {
			SasNameValidator sasNameValidator = new SasNameValidator();
			this.sasName = sasNameValidator.getValidName(this.getName());
		}
		return sasName;
	}

	public void setSasName(String sasName) {
		this.sasName = sasName;
	}

	/**
	 * Custom ItemBean Comparator.
	 */
	public static class ItemBeanComparator implements Comparator<ItemBean> {

		/**
		 * Custom comparator.
		 * @param itemBean1 ItemBean
		 * @param itemBean2 ItemBean
		 * @return comparison result.
		 */
		public int compare(ItemBean itemBean1, ItemBean itemBean2) {
			int result;

			if (itemBean1.getDefId() == itemBean2.getDefId()) {
				if (itemBean1.getItemMeta().getCrfVersionName().equals(itemBean2.getItemMeta().getCrfVersionName())) {
					result = ((Integer) itemBean1.getId()).compareTo(itemBean2.getId());
				} else {
					result = itemBean1.getItemMeta().getCrfVersionName()
							.compareTo(itemBean2.getItemMeta().getCrfVersionName());
				}
			} else {
				result = ((Integer) itemBean1.getDefId()).compareTo(itemBean2.getDefId());
			}
			return result;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((crfName == null) ? 0 : crfName.hashCode());
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((datasetItemMapKey == null) ? 0 : datasetItemMapKey.hashCode());
		result = prime * result + defId;
		result = prime * result + ((defName == null) ? 0 : defName.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + itemDataTypeId;
		result = prime * result + ((itemMeta == null) ? 0 : itemMeta.hashCode());
		result = prime * result + ((itemMetas == null) ? 0 : itemMetas.hashCode());
		result = prime * result + itemReferenceTypeId;
		result = prime * result + ((oid == null) ? 0 : oid.hashCode());
		result = prime * result + ((oidGenerator == null) ? 0 : oidGenerator.hashCode());
		result = prime * result + (phiStatus ? 1231 : 1237);
		result = prime * result + (selected ? 1231 : 1237);
		result = prime * result + statusId;
		result = prime * result + ((units == null) ? 0 : units.hashCode());
		result = prime * result + ((sasName == null) ? 0 : sasName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ItemBean other = (ItemBean) obj;
		if (crfName == null) {
			if (other.crfName != null) {
				return false;
			}
		} else if (!crfName.equals(other.crfName)) {
			return false;
		}
		if (dataType == null) {
			if (other.dataType != null) {
				return false;
			}
		} else if (!dataType.equals(other.dataType)) {
			return false;
		}
		if (datasetItemMapKey == null) {
			if (other.datasetItemMapKey != null) {
				return false;
			}
		} else if (!datasetItemMapKey.equals(other.datasetItemMapKey)) {
			return false;
		}
		if (defId != other.defId) {
			return false;
		}
		if (defName == null) {
			if (other.defName != null) {
				return false;
			}
		} else if (!defName.equals(other.defName)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (itemDataTypeId != other.itemDataTypeId) {
			return false;
		}
		if (itemMeta == null) {
			if (other.itemMeta != null) {
				return false;
			}
		} else if (!itemMeta.equals(other.itemMeta)) {
			return false;
		}
		if (itemMetas == null) {
			if (other.itemMetas != null) {
				return false;
			}
		} else if (!itemMetas.equals(other.itemMetas)) {
			return false;
		}
		if (itemReferenceTypeId != other.itemReferenceTypeId) {
			return false;
		}
		if (oid == null) {
			if (other.oid != null) {
				return false;
			}
		} else if (!oid.equals(other.oid)) {
			return false;
		}
		if (oidGenerator == null) {
			if (other.oidGenerator != null) {
				return false;
			}
		} else if (!oidGenerator.equals(other.oidGenerator)) {
			return false;
		}
		if (phiStatus != other.phiStatus) {
			return false;
		}
		if (selected != other.selected) {
			return false;
		}
		if (statusId != other.statusId) {
			return false;
		}
		if (units == null) {
			if (other.units != null) {
				return false;
			}
		} else if (!units.equals(other.units)) {
			return false;
		} else if (!sasName.equals(other.sasName)) {
			return false;
		}
		return true;
	}
}
