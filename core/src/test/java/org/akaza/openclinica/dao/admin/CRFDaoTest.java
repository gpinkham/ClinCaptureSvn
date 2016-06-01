/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

package org.akaza.openclinica.dao.admin;

import java.util.HashMap;
import java.util.Map;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

public class CRFDaoTest extends DefaultAppContextTest {

	private StudyBean studyBean;
	private String dbTypeOracle;
	private String dbTypePostgres;
	private CRFBean testCRFBean;
	private Map<Integer, Integer> expectedSetTypes;
	private HashMap<String, Object> crfBeanProperties;

	@Before
	public void setUp() {
		studyBean = (StudyBean) studyDAO.findByPK(1);

		dbTypeOracle = "oracle";
		dbTypePostgres = "postgres";

		testCRFBean = new CRFBean();
		testCRFBean.setId(12);
		testCRFBean.setStatusId(Status.AVAILABLE.getId());
		testCRFBean.setStatus(Status.AVAILABLE);
		testCRFBean.setStudyId(1);
		testCRFBean.setName("TestForm");
		testCRFBean.setDescription("TestForm description");
		testCRFBean.setOid("F_10_TESTFORM");
		testCRFBean.setAutoLayout(true);
		UserAccountBean owner = new UserAccountBean();
		owner.setId(1);
		testCRFBean.setOwner(owner);
		UserAccountBean updater = new UserAccountBean();
		updater.setId(1);
		testCRFBean.setUpdater(updater);

		expectedSetTypes = new HashMap<Integer, Integer>();
		expectedSetTypes.put(1, TypeNames.INT);
		expectedSetTypes.put(2, TypeNames.INT);
		expectedSetTypes.put(3, TypeNames.STRING);
		expectedSetTypes.put(4, TypeNames.STRING);
		expectedSetTypes.put(5, TypeNames.INT);
		expectedSetTypes.put(6, TypeNames.TIMESTAMP);
		expectedSetTypes.put(7, TypeNames.TIMESTAMP);
		expectedSetTypes.put(8, TypeNames.INT);
		expectedSetTypes.put(9, TypeNames.STRING);
		expectedSetTypes.put(10, TypeNames.INT);
		expectedSetTypes.put(11, TypeNames.BOOL);

		crfBeanProperties = new HashMap<String, Object>();
		crfBeanProperties.put("crf_id", testCRFBean.getId());
		crfBeanProperties.put("name", testCRFBean.getName());
		crfBeanProperties.put("description", testCRFBean.getDescription());
		crfBeanProperties.put("oc_oid", testCRFBean.getOid());
		crfBeanProperties.put("source_study_id", testCRFBean.getStudyId());
		crfBeanProperties.put("status_id", testCRFBean.getStatus().getId());
		crfBeanProperties.put("owner_id", testCRFBean.getOwner().getId());
		crfBeanProperties.put("update_id", testCRFBean.getUpdater().getId());
		crfBeanProperties.put("auto_layout", testCRFBean.isAutoLayout());

	}

	@Test
	public void testThatFindByOidReturnsCRFWithCorrectOID() throws OpenClinicaException {
		String cfrOid = "F_CONC";
		assertEquals(cfrOid, crfdao.findByOid(cfrOid).getOid());
	}

	@Test
	public void testThatFindByNameAndStudyMethodWorksFine() throws OpenClinicaException {
		String cfrName = "Agent Administration";
		CRFBean crfBean = (CRFBean) crfdao.findByNameAndStudy(cfrName, studyBean);
		assertTrue(crfBean.getId() > 0);
		assertEquals(cfrName, crfBean.getName());
	}

	@Test
	public void testThatUpdateSetsTheChangedField() throws OpenClinicaException {
		CRFBean crf;
		String cfrOid = "F_AGEN";
		String newDescription = "Test update";

		crf = crfdao.findByOid(cfrOid);
		crf.setDescription(newDescription);
		crf.setUpdater((UserAccountBean) userAccountDAO.findByPK(1));

		crfdao.update(crf);

		assertTrue(newDescription.equals(crfdao.findByOid(cfrOid).getDescription()));
	}

	@Test
	public void testThatFindAllDoesNotReturnAnEmptyList() throws OpenClinicaException {
		assertFalse(crfdao.findAll().isEmpty());
	}

	@Test
	public void testThatFindAllByStudyReturnsAllExistingCRFs() throws OpenClinicaException {
		int studyId = 1;
		assertEquals(6, crfdao.findAllByStudy(studyId).size());
	}

	@Test
	public void testThatGetCountofActiveCRFsReturnsValidSize() throws OpenClinicaException {
		assertTrue(crfdao.getCountofActiveCRFs() > 0);
	}

	@Test
	public void testThatFindByPKDoesNotReturnNull() throws OpenClinicaException {
		int CrfPK = 2;
		assertNotNull(crfdao.findByPK(CrfPK));
	}

	@Test
	public void testGetAllCRFNamesFromStudyNotReturnNull() throws OpenClinicaException {
		assertNotNull(crfdao.getAllCRFNamesFromStudy(1));
	}

	@Test
	public void testGetAllCRFNamesFromStudyReturnCorrectSize() throws OpenClinicaException {
		assertEquals(6, crfdao.getAllCRFNamesFromStudy(1).size());
	}

	@Test
	public void testThatFindAllActiveByDefinitionsForCurrentStudyReturnsCorrectSize() throws Exception {
		assertEquals(6, crfdao.findAllActiveByDefinitionsForCurrentStudy(1).size());
	}

	@Test
	public void testThatFindAllActiveCrfsReturnsCorrectSize() throws OpenClinicaException {
		assertEquals(6, crfdao.findAllActiveCrfs().size());
	}

	@Test
	public void testThatRemoveCrfByIdRemovesCrfSuccessfully() throws Exception {

		// deleting records from tables item_data and event_crf,
		// which are bound to crf with id = 3
		itemDataDAO.delete(63);
		eventCRFDAO.delete(14);

		// now we are able to delete crf
		crfdao.deleteCrf(3);
		CRFBean crfBean = (CRFBean) crfdao.findByPK(3);
		assertFalse(crfBean.isActive());
	}

	@Test
	public void testThatSetTypesExpectedSetsCorrectDataTypeForCRFId() {

		int typeId = 1;
		crfdao.setTypesExpected();
		assertTrue(expectedSetTypes.get(typeId) == crfdao.getTypeExpected(typeId));
	}

	@Test
	public void testThatSetTypesExpectedSetsCorrectDataTypeForCRFStatusId() {

		int typeId = 2;
		crfdao.setTypesExpected();
		assertTrue(expectedSetTypes.get(typeId) == crfdao.getTypeExpected(typeId));
	}

	@Test
	public void testThatSetTypesExpectedSetsCorrectDataTypeForCRFName() {

		int typeId = 3;
		crfdao.setTypesExpected();
		assertTrue(expectedSetTypes.get(typeId) == crfdao.getTypeExpected(typeId));
	}

	@Test
	public void testThatSetTypesExpectedSetsCorrectDataTypeForCRFDescription() {

		int typeId = 4;
		crfdao.setTypesExpected();
		assertTrue(expectedSetTypes.get(typeId) == crfdao.getTypeExpected(typeId));
	}

	@Test
	public void testThatSetTypesExpectedSetsCorrectDataTypeForCRFOwnerId() {

		int typeId = 5;
		crfdao.setTypesExpected();
		assertTrue(expectedSetTypes.get(typeId) == crfdao.getTypeExpected(typeId));
	}

	@Test
	public void testThatSetTypesExpectedSetsCorrectDataTypeForCRFDateCreated() {

		int typeId = 6;
		crfdao.setTypesExpected();
		assertTrue(expectedSetTypes.get(typeId) == crfdao.getTypeExpected(typeId));
	}

	@Test
	public void testThatSetTypesExpectedSetsCorrectDataTypeForCRFDateUpdated() {

		int typeId = 7;
		crfdao.setTypesExpected();
		assertTrue(expectedSetTypes.get(typeId) == crfdao.getTypeExpected(typeId));
	}

	@Test
	public void testThatSetTypesExpectedSetsCorrectDataTypeForCRFUpdaterId() {

		int typeId = 8;
		crfdao.setTypesExpected();
		assertTrue(expectedSetTypes.get(typeId) == crfdao.getTypeExpected(typeId));
	}

	@Test
	public void testThatSetTypesExpectedSetsCorrectDataTypeForCRFOID() {

		int typeId = 9;
		crfdao.setTypesExpected();
		assertTrue(expectedSetTypes.get(typeId) == crfdao.getTypeExpected(typeId));
	}

	@Test
	public void testThatSetTypesExpectedSetsCorrectDataTypeForCRFStudyId() {

		int typeId = 10;
		crfdao.setTypesExpected();
		assertTrue(expectedSetTypes.get(typeId) == crfdao.getTypeExpected(typeId));
	}

	@Test
	public void testThatSetTypesExpectedSetsCorrectDataTypeForCRFAutolayoutForDBTypeOracle() {

		Whitebox.setInternalState(crfdao, "dbType", dbTypeOracle);

		int typeId = 11;
		expectedSetTypes.put(typeId, TypeNames.INT);
		crfdao.setTypesExpected();
		assertTrue(expectedSetTypes.get(typeId) == crfdao.getTypeExpected(typeId));

		Whitebox.setInternalState(crfdao, "dbType", dbTypePostgres);
	}

	@Test
	public void testThatSetTypesExpectedSetsCorrectDataTypeForCRFAutolayoutForDBTypePostgres() {

		int typeId = 11;
		crfdao.setTypesExpected();
		assertTrue(expectedSetTypes.get(typeId) == crfdao.getTypeExpected(typeId));
	}

	@Test
	public void testThatGetEntityFromHashMapReturnsCRFBeanWithCorrectId() {

		CRFBean tempCRFBean = (CRFBean) crfdao.getEntityFromHashMap((HashMap<String, Object>) crfBeanProperties);
		assertTrue(tempCRFBean.getId() == (Integer) crfBeanProperties.get("crf_id"));
	}

	@Test
	public void testThatGetEntityFromHashMapReturnsCRFBeanWithCorrectName() {

		CRFBean tempCRFBean = (CRFBean) crfdao.getEntityFromHashMap((HashMap<String, Object>) crfBeanProperties);
		assertTrue(tempCRFBean.getName().equals(crfBeanProperties.get("name")));
	}

	@Test
	public void testThatGetEntityFromHashMapReturnsCRFBeanWithCorrectDescription() {

		CRFBean tempCRFBean = (CRFBean) crfdao.getEntityFromHashMap((HashMap<String, Object>) crfBeanProperties);
		assertTrue(tempCRFBean.getDescription().equals(crfBeanProperties.get("description")));
	}

	@Test
	public void testThatGetEntityFromHashMapReturnsCRFBeanWithCorrectOID() {

		CRFBean tempCRFBean = (CRFBean) crfdao.getEntityFromHashMap((HashMap<String, Object>) crfBeanProperties);
		assertTrue(tempCRFBean.getOid().equals(crfBeanProperties.get("oc_oid")));
	}

	@Test
	public void testThatGetEntityFromHashMapReturnsCRFBeanWithCorrectStudyId() {

		CRFBean tempCRFBean = (CRFBean) crfdao.getEntityFromHashMap((HashMap<String, Object>) crfBeanProperties);
		assertTrue(tempCRFBean.getStudyId() == (Integer) crfBeanProperties.get("source_study_id"));
	}

	@Test
	public void testThatGetEntityFromHashMapReturnsCRFBeanWithCorrectStatusId() {

		CRFBean tempCRFBean = (CRFBean) crfdao.getEntityFromHashMap((HashMap<String, Object>) crfBeanProperties);
		assertTrue(tempCRFBean.getStatus().getId() == (Integer) crfBeanProperties.get("status_id"));
	}

	@Test
	public void testThatGetEntityFromHashMapReturnsCRFBeanWithCorrectOwnerId() {

		CRFBean tempCRFBean = (CRFBean) crfdao.getEntityFromHashMap((HashMap<String, Object>) crfBeanProperties);
		assertTrue(tempCRFBean.getOwnerId() == (Integer) crfBeanProperties.get("owner_id"));
	}

	@Test
	public void testThatGetEntityFromHashMapReturnsCRFBeanWithCorrectUpdaterId() {

		CRFBean tempCRFBean = (CRFBean) crfdao.getEntityFromHashMap((HashMap<String, Object>) crfBeanProperties);
		assertTrue(tempCRFBean.getUpdaterId() == (Integer) crfBeanProperties.get("update_id"));
	}

	@Test
	public void testThatGetEntityFromHashMapReturnsCRFBeanWithCorrectAutolayoutForDBTypeOracle() {

		Whitebox.setInternalState(crfdao, "dbType", dbTypeOracle);

		crfBeanProperties.put("auto_layout", 0);
		CRFBean tempCRFBean = (CRFBean) crfdao.getEntityFromHashMap((HashMap<String, Object>) crfBeanProperties);
		assertFalse(tempCRFBean.isAutoLayout());

		Whitebox.setInternalState(crfdao, "dbType", dbTypePostgres);
	}

	@Test
	public void testThatGetEntityFromHashMapReturnsCRFBeanWithCorrectAutolayoutForDBTypePostgres() {

		CRFBean tempCRFBean = (CRFBean) crfdao.getEntityFromHashMap((HashMap<String, Object>) crfBeanProperties);
		assertTrue(tempCRFBean.isAutoLayout());
	}

	@Test
	public void testThatCreateWorksFine() throws OpenClinicaException {
		CRFBean crfBean = new CRFBean();
		crfBean.setStudyId(1);
		crfBean.setStatus(Status.AVAILABLE);
		crfBean.setName(testCRFBean.getName());
		crfBean.setDescription(testCRFBean.getDescription());
		crfBean.setOwner(testCRFBean.getOwner());
		crfBean = (CRFBean) crfdao.create(crfBean);
		assertTrue(crfBean.getId() > 0);
		assertTrue(crfBean.isActive());
	}

	@Test
	public void testThatFindAllEvaluableCrfsReturnsCorrectCollectionSize() {
		assertTrue(crfdao.findAllEvaluableCrfs(1).size() == 2);
	}

	@Test
	public void testThatFindAllActiveUnmaskedByDefinitionReturnsCorrectResult() {
		StudyEventDefinitionBean sed = new StudyEventDefinitionBean();
		sed.setId(1);
		UserAccountBean ub = new UserAccountBean();
		ub.setActiveStudyId(1);
		ub.setId(1);
		assertEquals(2, crfdao.findAllActiveUnmaskedByDefinition(sed, ub).size());
	}
}
