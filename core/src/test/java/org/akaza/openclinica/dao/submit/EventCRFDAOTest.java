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

import java.util.HashMap;
import java.util.Locale;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.EventCRFSDVFilter;
import org.akaza.openclinica.dao.EventCRFSDVSort;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Before;
import org.junit.Test;

import com.clinovo.jmesa.evaluation.CRFEvaluationFilter;
import com.clinovo.jmesa.evaluation.CRFEvaluationSort;

@SuppressWarnings("rawtypes")
public class EventCRFDAOTest extends DefaultAppContextTest {

	private UserAccountBean ub;

	@Before
	public void setUp() {
		ub = new UserAccountBean();
		ub.setId(1);
	}

	@Test
	public void testFindAllNotReturnNull() throws OpenClinicaException {
		assertNotNull(eventCRFDAO.findAll());
	}

	@Test
	public void testFindAllHasCorrectSize() throws OpenClinicaException {
		assertEquals(9, eventCRFDAO.findAll().size());
	}

	@Test
	public void testFindByPKReturnsCorrectValue() throws OpenClinicaException {
		assertEquals("Krikor", ((EventCRFBean) eventCRFDAO.findByPK(1)).getInterviewerName());
	}

	@Test
	public void testGetAvailableWithFilterAndSortReturnsCorrectSizeOfSDVed() {
		int studyId = 1;
		int userId = 0;
		EventCRFSDVFilter filter = new EventCRFSDVFilter(1);
		EventCRFSDVSort sort = new EventCRFSDVSort();
		int rowStart = 0;
		int rowEnd = 15;
		assertNotNull(eventCRFDAO.getAvailableWithFilterAndSort(studyId, filter, sort, true, rowStart, rowEnd, userId));
		assertEquals(1, eventCRFDAO
				.getAvailableWithFilterAndSort(studyId, filter, sort, true, rowStart, rowEnd, userId).size());
	}

	@Test
	public void testFindAllStartedByCrfReturnsCorrectCollectionSize() {
		assertEquals(eventCRFDAO.findAllStartedByCrf(1).size(), 2);
	}

	@Test
	public void testFindAllStartedByCrfVersionReturnsCorrectCollectionSize() {
		assertEquals(eventCRFDAO.findAllStartedByCrfVersion(1).size(), 2);
	}

	@Test
	public void testThatCountOfAllEventCrfsForEvaluationReturnsCorrectValue() {
		CRFEvaluationFilter filter = new CRFEvaluationFilter(new HashMap<Object, Status>(), messageSource,
				Locale.ENGLISH);
		StudyBean currentStudy = new StudyBean();
		currentStudy.setId(1);
		assertEquals(1, eventCRFDAO.countOfAllEventCrfsForEvaluation(filter, currentStudy));
	}

	@Test
	public void testThatFindAllEventCrfsForEvaluationReturnsCorrectCollectionSize() {
		CRFEvaluationFilter filter = new CRFEvaluationFilter(new HashMap<Object, Status>(), messageSource,
				Locale.ENGLISH);
		CRFEvaluationSort sort = new CRFEvaluationSort();
		StudyBean currentStudy = new StudyBean();
		currentStudy.setId(1);
		assertEquals(1, eventCRFDAO.findAllEventCrfsForEvaluation(currentStudy, filter, sort, 0, 15).size());
	}

	@Test
	public void testThatUpdateSavesTheOwnerId() {
		UserAccountBean root = (UserAccountBean) userAccountDAO.findByPK(1);
		UserAccountBean newOwner = new UserAccountBean();
		newOwner.setEmail("test@gmail.com");
		newOwner.setName("test_user");
		newOwner.setActiveStudyId(1);
		newOwner.setEnabled(true);
		newOwner.setAccountNonLocked(true);
		newOwner.setFirstName("user");
		newOwner.setLastName("user");
		newOwner.setPhone("123123123123");
		newOwner.setOwner(root);
		newOwner.setUpdater(root);
		newOwner = (UserAccountBean) userAccountDAO.create(newOwner);
		EventCRFBean eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(1);
		eventCRFBean.setOwner(newOwner);
		eventCRFBean = (EventCRFBean) eventCRFDAO.update(eventCRFBean);
		assertEquals(eventCRFBean.getOwnerId(), newOwner.getId());
		eventCRFBean.setOwner(root);
		eventCRFDAO.update(eventCRFBean);
		userAccountDAO.execute("delete from user_account where user_id = ".concat(Integer.toString(newOwner.getId())),
				new HashMap());
	}

	@Test
	public void testThatUpdateStatusMethodWorksFine() throws OpenClinicaException {
		EventCRFBean eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(2);
		assertEquals(eventCRFBean.getStatus(), Status.AVAILABLE);
		eventCRFBean.setUpdater((UserAccountBean) userAccountDAO.findByPK(1));
		eventCRFBean.setStatus(Status.DELETED);
		eventCRFDAO.updateStatus(eventCRFBean);
		eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(2);
		assertEquals(eventCRFBean.getStatus(), Status.DELETED);
	}
}
