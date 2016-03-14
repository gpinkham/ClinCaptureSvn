package com.clinovo.model;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created to support new dynamic render.
 */
@Entity
@Table(name = "item_render_metadata")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence_name", value = "item_render_metadata_id_seq") })
public class ItemRenderMetadata extends AbstractMutableDomainObject {
	private int crfVersionId;
	private int itemId;
	private int width;
	private int leftItemTextWidth;
	private int rightBlockWidth; // not in the database;
	private boolean inheritedFromPreviousRow; // not in the database;

	/**
	 * Default constructor.
	 */
	public ItemRenderMetadata() {
	}

	/**
	 * Clone constructor.
	 * @param source ItemRenderMetadata
	 */
	public ItemRenderMetadata(ItemRenderMetadata source) {
		this.setCrfVersionId(source.getCrfVersionId());
		this.setItemId(source.getItemId());
		this.setWidth(source.getWidth());
		this.setLeftItemTextWidth(source.getLeftItemTextWidth());
		this.setRightBlockWidth(source.getRightBlockWidth());
		this.setInheritedFromPreviousRow(source.isInheritedFromPreviousRow());
	}

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

	@Transient
	public int getRightBlockWidth() {
		return rightBlockWidth;
	}

	public void setRightBlockWidth(int rightBlockWidth) {
		this.rightBlockWidth = rightBlockWidth;
	}

	@Transient
	public boolean isInheritedFromPreviousRow() {
		return inheritedFromPreviousRow;
	}

	public void setInheritedFromPreviousRow(boolean inheritedFromPreviousRow) {
		this.inheritedFromPreviousRow = inheritedFromPreviousRow;
	}
}
