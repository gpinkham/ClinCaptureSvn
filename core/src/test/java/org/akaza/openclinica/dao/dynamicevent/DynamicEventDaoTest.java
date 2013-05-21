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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.akaza.openclinica.dao.dynamicevent;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.dynamicevent.DynamicEventBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

@SuppressWarnings({"deprecation"})
public class DynamicEventDaoTest extends DefaultAppContextTest {

	@Test
	public void testUpdate() throws OpenClinicaException {
		String description = "LALALA!";
		DynamicEventBean dynamicEventBean = (DynamicEventBean) dynamicEventDao.findByPK(1);
		assertNotNull(dynamicEventBean);
		dynamicEventBean.setDescription(description);
		dynamicEventDao.update(dynamicEventBean);
		assertEquals(description, ((DynamicEventBean) dynamicEventDao.findByPK(1)).getDescription());
	} 
	
	@Test
	public void testCreate() throws OpenClinicaException {
		DynamicEventBean dynamicEventBean = new DynamicEventBean();
		dynamicEventBean.setStudyGroupClassId(2);
		dynamicEventBean.setStudyEventDefinitionId(2);
		dynamicEventBean.setStudyId(2);
		dynamicEventBean.setOrdinal(2);
		dynamicEventBean.setOwnerId(2);
		dynamicEventBean.setName("dynamic event 2");
		dynamicEventBean.setDescription("test dynamic event 2");
		dynamicEventDao.create(dynamicEventBean);
	}
	
	@Test
	public void testDeleteByPK() throws OpenClinicaException {
		dynamicEventDao.deleteByPK(1);
		assertEquals(0,((DynamicEventBean)dynamicEventDao.findByPK(1)).getStudyEventDefinitionId());
	}
	@Test
	public void deleteAllFromStudyGroupClass() throws OpenClinicaException {
		dynamicEventDao.deleteAllFromStudyGroupClass(1);
		assertEquals(0,((DynamicEventBean)dynamicEventDao.findByPK(2)).getStudyEventDefinitionId());
	}
}
