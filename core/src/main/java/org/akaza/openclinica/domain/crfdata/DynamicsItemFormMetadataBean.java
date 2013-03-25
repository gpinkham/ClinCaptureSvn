/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.domain.crfdata;

import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * DynamicsItemFormMetadataBean
 * 
 * @author thickerson
 * 
 */
@Entity
@Table(name = "dyn_item_form_metadata")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "dyn_item_form_metadata_id_seq") })
public class DynamicsItemFormMetadataBean extends AbstractMutableDomainObject {

	private Boolean showItem;
	private Integer eventCrfId;
	private Integer itemId;
	private Integer itemFormMetadataId;
	private Integer crfVersionId;
	private Integer itemDataId;
	private Integer passedDde;

	public DynamicsItemFormMetadataBean() {
		showItem = false;
		eventCrfId = 0;
		itemId = 0;
		itemFormMetadataId = 0;
		crfVersionId = 0;
		itemDataId = 0;
		passedDde = 0;
	}

	public DynamicsItemFormMetadataBean(ItemFormMetadataBean metadataBean, EventCRFBean eventCRFBean) {
		setItemId(metadataBean.getItemId());
		setItemFormMetadataId(metadataBean.getId());
		setCrfVersionId(metadataBean.getCrfVersionId());
		setEventCrfId(eventCRFBean.getId());
		setPassedDde(0);
	}

	public DynamicsItemFormMetadataBean(ItemFormMetadataBean metadataBean, EventCRFBean eventCRFBean,
			ItemDataBean itemData) {
		setItemId(metadataBean.getItemId());
		setItemFormMetadataId(metadataBean.getId());
		setCrfVersionId(metadataBean.getCrfVersionId());
		setEventCrfId(eventCRFBean.getId());
		setItemDataId(itemData.getId());
		setPassedDde(0);
	}

	public boolean isShowItem() {
		return showItem;
	}

	public void setShowItem(boolean showItem) {
		this.showItem = showItem;
	}

	public Integer getPassedDde() {
		return passedDde;
	}

	public void setPassedDde(Integer passedDde) {
		this.passedDde = passedDde;
	}

	public Integer getItemDataId() {
		return itemDataId;
	}

	public void setItemDataId(Integer itemDataId) {
		this.itemDataId = itemDataId;
	}

	public int getEventCrfId() {
		return eventCrfId;
	}

	public void setEventCrfId(int eventCrfId) {
		this.eventCrfId = eventCrfId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getItemFormMetadataId() {
		return itemFormMetadataId;
	}

	public void setItemFormMetadataId(int itemFormMetadataId) {
		this.itemFormMetadataId = itemFormMetadataId;
	}

	public int getCrfVersionId() {
		return crfVersionId;
	}

	public void setCrfVersionId(int crfVersionId) {
		this.crfVersionId = crfVersionId;
	}
}
