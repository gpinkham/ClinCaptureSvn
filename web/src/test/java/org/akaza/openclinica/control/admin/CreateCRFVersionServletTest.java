/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package org.akaza.openclinica.control.admin;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

@RunWith(PowerMockRunner.class)
public class CreateCRFVersionServletTest {

	@Mock
	private CreateCRFVersionServlet createCRFVersionServlet;
	@Mock
	private CRFVersionBean crfVersionBean;
	@Mock
	private ItemDAO itemDAO;
	@Mock
	private Logger logger;
	private ItemBean existingItem1;
	private ItemBean existingItem2;
	private HashMap<String, ItemBean> items;

	@Before
	public void setUp() throws Exception {
		existingItem1 = new ItemBean();
		existingItem1.setId(11);
		existingItem1.setName("Item1");
		existingItem2 = new ItemBean();
		existingItem2.setId(12);
		existingItem2.setName("Item2");
		existingItem2.setUnits("");
		existingItem2.setDataType(ItemDataType.ST);
		populateItemsMap();
		Mockito.when(itemDAO.findByNameAndCRFId("Item1", crfVersionBean.getCrfId())).thenReturn(existingItem1);
		Mockito.when(itemDAO.findByNameAndCRFId("Item2", crfVersionBean.getCrfId())).thenReturn(existingItem2);
		Mockito.when(createCRFVersionServlet.getItemDAO()).thenReturn(itemDAO);
		PowerMockito.doCallRealMethod().when(createCRFVersionServlet).isItemSame(items, crfVersionBean);
		Whitebox.setInternalState(createCRFVersionServlet, "logger", logger);
	}

	private void populateItemsMap() {
		items = new HashMap<String, ItemBean>();
		ItemBean item = new ItemBean();
		item.setName("Item1");
		item.setUnits("mg");
		item.setDataType(ItemDataType.INTEGER);
		items.put("Item1", item);

		item = new ItemBean();
		item.setName("Item2");
		item.setUnits("");
		item.setDataType(ItemDataType.ST);
		items.put("Item2", item);
	}

	@Test
	public void testThatDiffItemIsFoundWhenUnitsChange() throws Exception {
		existingItem1.setUnits("cm");
		existingItem1.setDataType(ItemDataType.INTEGER);
		List<ItemBean> diffItems = createCRFVersionServlet.isItemSame(items, crfVersionBean);
		assertEquals(1, diffItems.size());
	}

	@Test
	public void testThatDiffItemIsFoundWhenDataTypeChanges() throws Exception {
		existingItem1.setUnits("mg");
		existingItem1.setDataType(ItemDataType.REAL);
		List<ItemBean> diffItems = createCRFVersionServlet.isItemSame(items, crfVersionBean);
		assertEquals(1, diffItems.size());
	}

	@Test
	public void testThatNoDiffItemIsFoundWhenUnitsAndDataTypeDontChange() throws Exception {
		existingItem1.setUnits("mg");
		existingItem1.setDataType(ItemDataType.INTEGER);
		List<ItemBean> diffItems = createCRFVersionServlet.isItemSame(items, crfVersionBean);
		assertEquals(0, diffItems.size());
	}
}
