package org.akaza.openclinica.multithreading;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.akaza.openclinica.control.core.SpringServlet;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class UnavailableCRFListTest {

	private static boolean errors;
	private static boolean cmErrors;
	private static final int TOTAL_USERS = 10;
	private static final int TOTAL_ECB_IDS = 10;
	private static final List<Thread> threadList = new ArrayList<Thread>();

	private class ServletThread extends Thread {

		private int userId;

		public ServletThread(int userId) {
			this.userId = userId;
		}

		@Override
		public void run() {
			try {
				for (int ecbId = 1; ecbId < TOTAL_ECB_IDS; ecbId++) {
					SpringServlet.lockThisEventCRF(ecbId, userId);
				}
				SpringServlet.removeLockedCRF(userId);
				for (int ecbId = 1; ecbId < TOTAL_ECB_IDS; ecbId++) {
					SpringServlet.lockThisEventCRF(ecbId, userId);
				}
				for (int ecbId = 1; ecbId < TOTAL_ECB_IDS; ecbId++) {
					if (SpringServlet.getUnavailableCRFList().containsKey(ecbId)) {
						SpringServlet.justRemoveLockedCRF(ecbId);
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

	private boolean shouldWait() {
		boolean result = false;
		if (!cmErrors && !errors) {
			for (Thread thread : threadList) {
				if (thread.isAlive()) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	@Test
	public void testThatOperationsWithUnavailableCRFLisDoNotThrowConcurrentModificationExceptions() throws Exception {
		for (int userId = 1; userId <= TOTAL_USERS; userId++) {
			ServletThread servletThread = new ServletThread(userId);
			threadList.add(servletThread);
			servletThread.start();
		}
		while (shouldWait())
			Thread.sleep(1000);
		assertFalse(errors);
		assertFalse(cmErrors);
	}
}
