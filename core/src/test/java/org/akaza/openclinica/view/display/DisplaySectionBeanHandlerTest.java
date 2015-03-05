/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.akaza.openclinica.view.display;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.junit.Test;

import java.util.List;

public class DisplaySectionBeanHandlerTest extends DefaultAppContextTest {

	@Test
	public void testThatGetDisplaySectionBeansMethodReturnsCorrectCollectionSizeAndDoesNotThrowAnException()
			throws Exception {
		DisplaySectionBeanHandler displaySectionBeanHandler = new DisplaySectionBeanHandler(true, dataSource,
				new DynamicsMetadataService(dynamicsItemFormMetadataDao, dynamicsItemGroupMetadataDao, dataSource));
		displaySectionBeanHandler.setEventCRFId(0);
		displaySectionBeanHandler.setCrfVersionId(1);
		List<DisplaySectionBean> displaySectionBeanList = displaySectionBeanHandler.getDisplaySectionBeans();
		assertEquals(displaySectionBeanList.size(), 3);
	}
}
