package org.akaza.openclinica.dao.submit;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.oid.MeasurementUnitOidGenerator;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class ItemDAOTest extends DefaultAppContextTest {

	@Test
	public void testThatCreateWorksFine() throws OpenClinicaException {
		ItemBean itemBean = new ItemBean();
		itemBean.setOid("xx_test_item");
		itemBean.setName("");
		itemBean.setDescription("");
		itemBean.setUnits("");
		itemBean.setItemDataTypeId(1);
		itemBean.setItemReferenceTypeId(1);
		itemBean.setStatus(Status.AVAILABLE);
		itemBean.setOwner((UserAccountBean) userAccountDAO.findByPK(1));
		itemBean = (ItemBean) idao.create(itemBean);
		assertTrue(itemBean.getId() > 0);
	}

	@Test
	public void testThatGetValidUnitOidWorksFine() throws Exception {
		MeasurementUnitOidGenerator measurementUnitOidGenerator = new MeasurementUnitOidGenerator();
		measurementUnitOidGenerator.setDataSource(dataSource);
		String unitName = "Units";
		String unitOid = measurementUnitOidGenerator.generateOid(unitName);
		List<String> unitOidList = new ArrayList<String>();
		unitOidList.add(unitOid);
		String newUnitOid = idao.getValidUnitOid(measurementUnitOidGenerator, unitName, unitOidList);
		assertFalse(newUnitOid.equals(unitOid));
	}
}