package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

public class UserAccountDAOTest extends DefaultAppContextTest {

	@Test
	public void testThatGetUsersAssignedMetricReturnsCorrectValue() {
		assertEquals(userAccountDAO.getUsersAssignedMetric(1), 0);
	}
}
