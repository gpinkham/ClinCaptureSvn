package org.akaza.openclinica.multithreading;

import java.util.ConcurrentModificationException;

import org.akaza.openclinica.control.core.Controller;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class UnavailableCRFListTest {

	private static boolean errors;
	private static boolean cmErrors;
	private static final int totalUsers = 1000;
	private static final int totalEcbIds = 1000;

	private class ServletThread extends Thread {

		private int userId;

		public ServletThread(int userId) {
			this.userId = userId;
		}

		@Override
		public void run() {
			try {
				for (int ecbId = 1; ecbId < totalEcbIds; ecbId++) {
					Controller.lockThisEventCRF(ecbId, userId);
				}
                Controller.removeLockedCRF(userId);
				for (int ecbId = 1; ecbId < totalEcbIds; ecbId++) {
                    Controller.lockThisEventCRF(ecbId, userId);
				}
				for (int ecbId = 1; ecbId < totalEcbIds; ecbId++) {
					if (Controller.getUnavailableCRFList().containsKey(ecbId)) {
                        Controller.justRemoveLockedCRF(ecbId);
					}
				}
			} catch (ConcurrentModificationException e) {
				cmErrors = true;
			} catch (Exception e) {
				errors = true;
			}
		}

	}

	@Before
	public void setUp() throws Exception {
		//
	}

	@Test
	public void testThatOperationsWithUnavailableCRFLisDoNotThrowConcurrentModificationExceptions() throws Exception {
		for (int userId = 1; userId < totalUsers; userId++) {
			new ServletThread(userId).start();
		}
		assertFalse(errors);
		assertFalse(cmErrors);
	}
}
