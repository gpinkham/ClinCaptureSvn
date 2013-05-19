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

package org.akaza.openclinica.service.crfdata;

import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;

/**
 * MetadataServiceInterface, our abstract interface for Dynamics
 * 
 * @author thickerson, Mar 3rd, 2010 initial methods: isShown, show and hide (can add others later to
 *         enable/disable/color/uncolor, etc initial implementations: ItemMetadataService and GroupMetadataService
 * 
 */
public interface MetadataServiceInterface {

	public abstract boolean isShown(Object metadataBean, EventCRFBean eventCrfBean);

	public abstract boolean hide(Object metadataBean, EventCRFBean eventCrfBean);

	public abstract boolean showItem(ItemFormMetadataBean metadataBean, EventCRFBean eventCrfBean,
			ItemDataBean itemDataBean);

	public abstract boolean showGroup(ItemGroupMetadataBean metadataBean, EventCRFBean eventCrfBean);
}
