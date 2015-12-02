package com.clinovo.bean.display;

import com.clinovo.model.EDCItemMetadata;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;

/**
 * Row for tables on Configure Item Level SDV page.
 */
public class DisplayItemLevelSDVRow {

	private ItemBean itemBean;
	private ItemFormMetadataBean itemFormMetadataBean;
	private EDCItemMetadata edcItemMetadata;

	public EDCItemMetadata getEdcItemMetadata() {
		return edcItemMetadata;
	}

	public void setEdcItemMetadata(EDCItemMetadata edcItemMetadata) {
		this.edcItemMetadata = edcItemMetadata;
	}

	public ItemBean getItemBean() {
		return itemBean;
	}

	public void setItemBean(ItemBean itemBean) {
		this.itemBean = itemBean;
	}

	public ItemFormMetadataBean getItemFormMetadataBean() {
		return itemFormMetadataBean;
	}

	public void setItemFormMetadataBean(ItemFormMetadataBean itemFormMetadataBean) {
		this.itemFormMetadataBean = itemFormMetadataBean;
	}
}
