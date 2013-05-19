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
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.bean.submit;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.oid.ItemGroupOidGenerator;
import org.akaza.openclinica.bean.oid.OidGenerator;

import javax.sql.DataSource;
import java.util.ArrayList;

@SuppressWarnings({"rawtypes", "serial"})
public class ItemGroupBean extends AuditableEntityBean {

	private Integer crfId = 0;
	private ItemGroupMetadataBean meta = new ItemGroupMetadataBean();
	private ArrayList itemGroupMetaBeans = new ArrayList();

	private String oid;
	private OidGenerator oidGenerator;

	public ItemGroupBean() {
		super();
		crfId = 0;
		name = "";
		meta = new ItemGroupMetadataBean();
		oidGenerator = new ItemGroupOidGenerator();
	}

	/**
	 * @return the crfId
	 */
	public Integer getCrfId() {
		return crfId;
	}

	/**
	 * @param crfId
	 *            the crfId to set
	 */
	public void setCrfId(Integer crfId) {
		this.crfId = crfId;
	}

	/**
	 * @return the meta
	 */
	public ItemGroupMetadataBean getMeta() {
		return meta;
	}

	/**
	 * @param meta
	 *            the meta to set
	 */
	public void setMeta(ItemGroupMetadataBean meta) {
		this.meta = meta;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public OidGenerator getOidGenerator(DataSource ds) {
		if (oidGenerator != null) {
			oidGenerator.setDataSource(ds);
		}
		return oidGenerator;
	}

	public void setOidGenerator(OidGenerator oidGenerator) {
		this.oidGenerator = oidGenerator;
	}

	public ArrayList getItemGroupMetaBeans() {
		return itemGroupMetaBeans;
	}

	public void setItemGroupMetaBeans(ArrayList itemGroupMetaBeans) {
		this.itemGroupMetaBeans = itemGroupMetaBeans;
	}
}
