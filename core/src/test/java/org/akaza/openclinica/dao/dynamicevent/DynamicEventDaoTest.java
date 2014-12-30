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
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.dynamicevent.DynamicEventBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

@SuppressWarnings({ "deprecation" })
public class DynamicEventDaoTest extends DefaultAppContextTest {

	@Test
	public void testThatUpdateSetsTheChangedFields() throws OpenClinicaException {
		String description = "LALALA!";
		DynamicEventBean dynamicEventBean = (DynamicEventBean) dynamicEventDao.findByPK(1);
		assertNotNull(dynamicEventBean);
		dynamicEventBean.setDescription(description);
		dynamicEventDao.update(dynamicEventBean);
		assertEquals(description, ((DynamicEventBean) dynamicEventDao.findByPK(1)).getDescription());
	}

	@Test
	public void testThatFindByPKDoesNotReturnNull() throws OpenClinicaException {
		DynamicEventBean dyn1 = (DynamicEventBean) dynamicEventDao.findByPK(1);
		assertNotNull(dyn1);
	}

	@Test
	public void testThatFindByPKReturnsDynamicEventWithCorrectId() throws OpenClinicaException {
		DynamicEventBean dyn2 = (DynamicEventBean) dynamicEventDao.findByPK(2);
		assertEquals(2, dyn2.getId());
	}

	@Test
	public void testFindByPKReturnsDynamicEventWithCorrectDescription() throws OpenClinicaException {
		DynamicEventBean dyn2 = (DynamicEventBean) dynamicEventDao.findByPK(2);
		assertEquals("test dynamic event 2", dyn2.getDescription());
	}

	@Test
	public void testThatFindByPKReturnsDynamicEventWithCorrectDate() throws OpenClinicaException {
		DynamicEventBean dyn2 = (DynamicEventBean) dynamicEventDao.findByPK(2);
		assertNotNull(dyn2.getCreatedDate());
	}

	@Test
	public void testFindByPKReturnsDynamicEventWithCorrectOrdinal() throws OpenClinicaException {
		DynamicEventBean dyn2 = (DynamicEventBean) dynamicEventDao.findByPK(2);
		assertEquals(2, dyn2.getOrdinal());
	}

	@Test
	public void testFindByPKReturnsDynamicEventWithUpdatedDate() throws OpenClinicaException {
		DynamicEventBean dyn2 = (DynamicEventBean) dynamicEventDao.findByPK(2);
		assertNotNull(dyn2.getUpdatedDate());
	}

	@Test
	public void testFindByPKReturnsDynamicEventWithUpdaterId() throws OpenClinicaException {
		DynamicEventBean dyn2 = (DynamicEventBean) dynamicEventDao.findByPK(2);
		assertEquals(1, dyn2.getUpdaterId());
	}

	@Test
	public void testFindByPKReturnsDynamicEventWithName() throws OpenClinicaException {
		DynamicEventBean dyn2 = (DynamicEventBean) dynamicEventDao.findByPK(2);
		assertEquals("dynamic event 2", dyn2.getName());
	}

	@Test
	public void testFindByPKReturnsDynamicEventWithStudyGroupClassId() throws OpenClinicaException {
		DynamicEventBean dyn2 = (DynamicEventBean) dynamicEventDao.findByPK(2);
		assertEquals(3, dyn2.getStudyGroupClassId());
	}

	@Test
	public void testFindByPKReturnsDynamicEventWithStudyEventDefinitionId() throws OpenClinicaException {
		DynamicEventBean dyn2 = (DynamicEventBean) dynamicEventDao.findByPK(2);
		assertEquals(2, dyn2.getStudyEventDefinitionId());
	}

	@Test
	public void testFindByPKReturnsDynamicEventWithStudyId() throws OpenClinicaException {
		DynamicEventBean dyn2 = (DynamicEventBean) dynamicEventDao.findByPK(2);
		assertEquals(1, dyn2.getStudyId());
	}

	@Test
	public void testThatCreateWorksFine() throws OpenClinicaException {

		DynamicEventBean dynamicEventBean = new DynamicEventBean();
		dynamicEventBean.setStudyGroupClassId(2);
		dynamicEventBean.setStudyEventDefinitionId(2);
		dynamicEventBean.setStudyId(1);
		dynamicEventBean.setOrdinal(2);
		dynamicEventBean.setOwnerId(2);
		dynamicEventBean.setName("dynamic event 2");
		dynamicEventBean.setDescription("test dynamic event 2");
		EntityBean dynamicEvent = dynamicEventDao.create(dynamicEventBean);

		assertTrue(dynamicEvent.getId() > 0);
	}

	@Test
	public void testThatDeleteByPKRemovesTheDynamicEvent() throws OpenClinicaException {
		dynamicEventDao.deleteByPK(1);
		assertEquals(0, ((DynamicEventBean) dynamicEventDao.findByPK(1)).getStudyEventDefinitionId());
	}

	@Test
	public void testThatDeleteAllFromStudyGroupClassRemoveAllDynamicEventsAttachedToStudy() throws OpenClinicaException {
		dynamicEventDao.deleteAllFromStudyGroupClass(1);
		assertEquals(2, ((DynamicEventBean) dynamicEventDao.findByPK(2)).getStudyEventDefinitionId());
	}

	@Test
	public void testThatFindByStudyEventDefinitionIdReturnsNull() throws OpenClinicaException {
		final int studyEventDefinitionId = 9;
		assertNull(dynamicEventDao.findByStudyEventDefinitionId(studyEventDefinitionId));
	}

	@Test
	public void testThatFindByStudyEventDefinitionIdReturnsNotNull() throws OpenClinicaException {
		final int studyEventDefinitionId = 5;
		assertNotNull(dynamicEventDao.findByStudyEventDefinitionId(studyEventDefinitionId));
	}
}
