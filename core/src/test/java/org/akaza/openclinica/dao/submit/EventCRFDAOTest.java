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

package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.EventCRFSDVFilter;
import org.akaza.openclinica.dao.EventCRFSDVSort;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class EventCRFDAOTest extends DefaultAppContextTest {

	@Test
	public void testFindAllNotReturnNull() throws OpenClinicaException {
		assertNotNull(eventCRFDAO.findAll());
	}

	@Test
	public void testFindAllHasCorrectSize() throws OpenClinicaException {
		assertEquals(7, eventCRFDAO.findAll().size());
	}

	@Test
	public void testFindByPKReturnsCorrectValue() throws OpenClinicaException {
		assertEquals("Krikor", ((EventCRFBean) eventCRFDAO.findByPK(1)).getInterviewerName());
	}

	@Test
	public void testGetAvailableWithFilterAndSortReturnsCorrectSizeOfSDVed() {
		int studyId = 1;
		int parentStudyId = 1;
		EventCRFSDVFilter filter = new EventCRFSDVFilter(1);
		EventCRFSDVSort sort = new EventCRFSDVSort();
		boolean allowSdvWithOpenQueries = true;
		int rowStart = 0;
		int rowEnd = 15;
		assertNotNull(eventCRFDAO.getAvailableWithFilterAndSort(studyId, parentStudyId, filter, sort,
				allowSdvWithOpenQueries, rowStart, rowEnd));
		assertEquals(
				0,
				eventCRFDAO.getAvailableWithFilterAndSort(studyId, parentStudyId, filter, sort,
						allowSdvWithOpenQueries, rowStart, rowEnd).size());
	}
}
