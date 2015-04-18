/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.akaza.openclinica.service.crfdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class SimpleConditionalDisplayServiceTest extends DefaultAppContextTest {

	private int itemId;
	private int sectionId;
	private int crfVersionId;

	@Before
	public void before() {
		itemId = 1;
		sectionId = 1;
		crfVersionId = 1;
	}

	@Test
	public void testThatConvertSectionBeansToDisplaySectionBeansWithSCDInfoSetsCorrectSCDInfo() {
		ArrayList its = new ArrayList();
		HashMap versionMap = new HashMap();
		versionMap.put(sectionId, its);
		ItemBean item = new ItemBean();
		ItemFormMetadataBean ifm = imfdao.findByItemIdAndCRFVersionId(itemId, crfVersionId);
		item.setItemMeta(ifm);
		its.add(item);
		List<SectionBean> sections = new ArrayList<SectionBean>();
		sections.add((SectionBean) sectionDAO.findByPK(sectionId));
		List<DisplaySectionBean> displaySectionBeanList = simpleConditionalDisplayService
				.convertSectionBeansToDisplaySectionBeansWithSCDInfo(sections, versionMap);
		assertNull(displaySectionBeanList.get(0).getItems().get(0).getScdData().getScdItemMetadataBean().getId());
	}
}
