package com.clinovo.model;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created to support new dynamic render.
 */
@Entity
@Table(name = "item_render_metadata")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "item_render_metadata_id_seq") })
public class ItemRenderMetadata extends AbstractMutableDomainObject {
	private int crfVersionId;
	private int itemId;
	private int width;
	private int leftItemTextWidth;

	public int getCrfVersionId() {
		return crfVersionId;
	}

	public void setCrfVersionId(int crfVersionId) {
		this.crfVersionId = crfVersionId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getLeftItemTextWidth() {
		return leftItemTextWidth;
	}

	public void setLeftItemTextWidth(int leftItemTextWidth) {
		this.leftItemTextWidth = leftItemTextWidth;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}
