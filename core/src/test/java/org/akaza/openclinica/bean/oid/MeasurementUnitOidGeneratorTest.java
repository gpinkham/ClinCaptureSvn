package org.akaza.openclinica.bean.oid;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

public class MeasurementUnitOidGeneratorTest extends DefaultAppContextTest {

	@Test
	public void testThatMeasurementUnitOidGeneratorDoesNotGenerateOIDWithMoreThan40Symbols() {
		MeasurementUnitOidGenerator measurementUnitOidGenerator = new MeasurementUnitOidGenerator();
		measurementUnitOidGenerator.setDataSource(dataSource);
		String oid = measurementUnitOidGenerator.createOid("THE_ITEM_WITH_NAME_INITIALFOLEY_INSERTIONS_DATE");
		oid = measurementUnitOidGenerator.randomizeOid(oid);
		assertTrue(oid != null);
		assertTrue(oid.length() <= 40);
	}
}
