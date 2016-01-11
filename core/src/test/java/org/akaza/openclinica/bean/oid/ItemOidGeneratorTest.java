package org.akaza.openclinica.bean.oid;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

public class ItemOidGeneratorTest extends DefaultAppContextTest {

	@Test
	public void testThatItemOidGeneratorDoesNotGenerateOIDWithMoreThan40Symbols() {
		ItemOidGenerator itemOidGenerator = new ItemOidGenerator();
		itemOidGenerator.setDataSource(dataSource);
		String oid = itemOidGenerator.createOid("DEVICRF", "INITIAL_FOLEY_INSERTIONS_DATE");
		oid = itemOidGenerator.randomizeOid(oid);
		assertTrue(oid != null);
		assertTrue(oid.length() <= 40);
	}
}
