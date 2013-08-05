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

package org.akaza.openclinica.bean.core;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EntityBeanTest {

	private EntityBean bean = null;

	@Before
	public void setUp() throws Exception {
		bean = new EntityBean();
	}

	@Test
	public void testNameDefaultValue() {
		assertEquals("", bean.getName());
	}
	
	@Test
	public void testIdDefaultValue() {
		assertEquals(0, bean.getId());
	}
	
	@Test
	public void testActiveDefaultValue() {
		assertEquals(false, bean.isActive());
	}
	
	public void testSetName() {
		String beanName = "NmaeOfBean";
		
		bean.setName(beanName);
		assertEquals(beanName, bean.getName());
	}
	
	public void testSetActive() {
		boolean beanActive = true;
		
		bean.setActive(beanActive);
		assertEquals(beanActive, bean.isActive());
	}
	
	public void testSetId() {
		int beanId = 2567398;
		
		bean.setId(beanId);
		assertEquals(beanId, bean.getId());
	}

	@Test
	public void testActiveValueViaSetIdCaseNegative() {
		bean.setId(-5);
		assertEquals(false, bean.isActive());
	}
	
	@Test
	public void testActiveValueViaSetIdCaseZero() {
		bean.setId(0);
		assertEquals(false, bean.isActive());
	}
	
	@Test
	public void testActiveValueViaSetIdCasePositive() {
		bean.setId(10);
		assertEquals(true, bean.isActive());
	}
	
	
	//Should this test be included?
	
	/*@Test
	public void testActiveValueViaSetIdCasePositiveToNegative() {
		bean.setId(10);
		bean.setId(-1);
		assertEquals(false, bean.isActive());
	}*/

	@Test
	public void testEqualityOfObjectsCaseTheSameObject() {
		EntityBean bean2 = bean;

		assertTrue(bean.equals(bean2));
	}
	
	@Test
	public void testEqualityOfObjectsCaseNewObject() {
		EntityBean bean2 = new EntityBean();

		assertTrue(bean.equals(bean2));
	}

	@Test
	public void testInequalityOfObjectsCaseNullPointer() {
		assertFalse(bean.equals(null));
	}
	
	@Test
	public void testInequalityOfObjectsCaseInequalityOfTypes() {
		AuditableEntityBean action = new AuditableEntityBean();

		assertFalse(bean.equals(action));
	}
	
	@Test
	public void testInequalityOfObjectsCaseNewObjectWithValuesChanged() {
		EntityBean bean2 = new EntityBean();

		bean2.setId(15);
		bean2.setName("name");
		assertFalse(bean.equals(bean2));
	}

	@Test
	public void testHashCodeCase01() {
		int hashExpected;

		bean.setId(10);
		bean.setName(null);
		hashExpected = 1213092;
		assertEquals(hashExpected, bean.hashCode());
	}
	
	@Test
	public void testHashCodeCase02() {
		int hashExpected;

		bean.setId(10);
		bean.setName("name");
		hashExpected = 4586799;
		assertEquals(hashExpected, bean.hashCode());
	}
}
