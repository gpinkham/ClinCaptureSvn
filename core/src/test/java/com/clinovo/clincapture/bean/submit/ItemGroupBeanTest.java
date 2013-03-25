/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/**
 * 
 */
package com.clinovo.clincapture.bean.submit;

import static org.junit.Assert.*;
import org.akaza.openclinica.bean.submit.ItemGroupBean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Tom
 * 
 */
public class ItemGroupBeanTest {

	public ItemGroupBean groupBean;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		groupBean = new ItemGroupBean();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		groupBean = null;
	}

	@Test
	public void testNullValues() {
		// fail("Not yet implemented");
		// ItemGroupBean groupBean = new ItemGroupBean();
		assertNull(groupBean.getOid());

	}

	@Test
	public void testZeroValueInCRFId() {
		assertEquals(groupBean.getCrfId().intValue(), 0);
	}

	@Test
	public void testPrimaryKeyZero() {
		assertEquals(groupBean.getId(), 0);
		assertSame(groupBean.getId(), groupBean.getCrfId().intValue());
	}

}
