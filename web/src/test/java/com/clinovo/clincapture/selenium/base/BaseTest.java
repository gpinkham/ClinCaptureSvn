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
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package com.clinovo.clincapture.selenium.base;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.server.SeleniumServer;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestBase;
import com.thoughtworks.selenium.Selenium;

public class BaseTest extends SeleneseTestBase {

	public static final int DELAY = 100;

	public static final int EXCEPTIONS_LIMIT = 50;

	public static final String PAGE_TIME_OUT = "60000";

	protected int exceptionCounter = 0;

	protected SeleniumServer seleniumServer;

	protected Selenium selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:8080");

	public static final String ROOT = "root";

	public static final String ROOT_PASSWORD = "password";

	public static final String CRC = "a_crc1";

	public static final String CRC_PASSWORD = "password";

	public static final String INVESTIGATOR = "a_pi1";

	public static final String INVESTIGATOR_PASSWORD = "password";

	public static final String TEST_SUBJECT_1 = "1-001";

	public static final int MAX_EVENT_DEFINITIONS = 20;

	public static final String CC_CONTEXT = "/ClinCapture-1.0.2";

	public static final String LOGIN_URL = CC_CONTEXT;

    public static final String LIST_USER_ACCOUNTS_URL = CC_CONTEXT + "/ListUserAccounts";

	public static final String SUBJECT_MATRIX_URL = CC_CONTEXT + "/ListStudySubjects";

	public static final String DISC_NOTES_URL = CC_CONTEXT + "/ViewNotes?module=submit";

	public static final String LOGOUT_URL = CC_CONTEXT + "/j_spring_security_logout";

	public static final String VERSION = "1.0.2";

	protected String groupId;

	protected String mainWinId;

	protected String groupRowSelector;

	protected String ignoreMarkCRFCompleteMSG = null;

	protected class RowsInfo {
		public int totalRows = 1;
		public int hiddenRows = 2;
		public int visibleRows = 0;
	}

	protected RowsInfo rowsInfo = new RowsInfo();

	protected enum DiscrepancyNoteType {
		FAILED_VALIDATION_CHECK(1), ANNOTATION(2), QUERY(3), REASON_FOR_CHANGE(4);

		private int id;

		public int getId() {
			return id;
		}

		DiscrepancyNoteType(int id) {
			this.id = id;
		}
	}

	@Before
	public void setUp() throws Exception {
		seleniumServer = new SeleniumServer();
		seleniumServer.start();
		selenium.start();
		selenium.setTimeout(PAGE_TIME_OUT);
	}

	@After
	public void tearDown() throws Exception {
		if (ignoreMarkCRFCompleteMSG != null) {
			selenium.createCookie("ignoreMarkCRFCompleteMSG=" + ignoreMarkCRFCompleteMSG, "");
		}
		logout();
		selenium.stop();
		seleniumServer.stop();
	}

	// **********************
	// *** common methods ***
	// **********************

	// the method waits for the condition
	protected void wait(String condition) throws Exception {
		try {
			Thread.sleep(DELAY);
			selenium.waitForCondition(condition, PAGE_TIME_OUT);
			exceptionCounter = 0;
		} catch (Exception e) {
			if (!e.getMessage().contains("Is this HTML page fully loaded")
					&& !e.getMessage().contains("Timed out after " + PAGE_TIME_OUT + "ms")) {
				throw e;
			} else if (exceptionCounter > EXCEPTIONS_LIMIT) {
				e.printStackTrace();
				throw new Exception("Wait method has generated more than " + EXCEPTIONS_LIMIT + " exceptions");
			}
			exceptionCounter++;
			wait(condition);
		}
	}

	// the method waits for the condition and does the action if the condition is not appeared
	protected void waitAndDo(String condition, String action) throws Exception {
		try {
			Thread.sleep(DELAY);
			selenium.waitForCondition(condition, "" + DELAY);
			exceptionCounter = 0;
		} catch (Exception e) {
			if (!e.getMessage().contains("Is this HTML page fully loaded")
					&& !e.getMessage().contains("Timed out after " + DELAY + "ms")) {
				throw e;
			} else if (exceptionCounter > EXCEPTIONS_LIMIT) {
				e.printStackTrace();
				throw new Exception("waitAndDo method has generated more than " + EXCEPTIONS_LIMIT + " exceptions");
			}
			exceptionCounter++;
			selenium.click(action);
			waitAndDo(condition, action);
		}
	}

	// the method waits until the window will be opened
	protected String waitWindow(String winId) throws Exception {
		return waitWindow(winId, false);
	}

	// the method waits until the window will be opened
	protected String waitWindow(String winId, boolean shouldContains) throws Exception {
		String result = "";
		try {
			Thread.sleep(DELAY);
			String dnWinId = selenium
					.getEval("{var windowId; for(var x in selenium.browserbot.openedWindows ) {windowId=x;} }");
			if (!shouldContains && dnWinId.equalsIgnoreCase(winId)) {
				exceptionCounter = 0;
				result = dnWinId;
			} else if (shouldContains && dnWinId.contains(winId)) {
				exceptionCounter = 0;
				result = dnWinId;
			} else {
				throw new Exception("Method waits for window: " + winId);
			}
		} catch (Exception e) {
			if (!e.getMessage().contains("Is this HTML page fully loaded")
					&& !e.getMessage().contains("Method waits for window")) {
				throw e;
			} else if (exceptionCounter > EXCEPTIONS_LIMIT) {
				e.printStackTrace();
				throw new Exception("waitWindow method has generated more than " + EXCEPTIONS_LIMIT + " exceptions");
			}
			exceptionCounter++;
			result = waitWindow(winId);
		}
		return result;
	}

	// the method returns the main window id
	/*
	 * protected String getMainWindow() { return
	 * selenium.getEval("{var windowId; for(var x in selenium.browserbot.openedWindows ) {windowId=x;} }"); }
	 */

	// login method (into the CC)
	protected void login(String userName, String password) throws Exception {
		selenium.open(LOGIN_URL, PAGE_TIME_OUT);
		selenium.windowMaximize();
		selenium.windowFocus();

		wait("selenium.isElementPresent(\"//input[@id='username']\")");
		selenium.type("id=username", userName);

		wait("selenium.isElementPresent(\"//input[@id='j_password']\")");
		selenium.type("id=j_password", password);

		selenium.click("//input[@name='submit']");
		Thread.sleep(2000);
		selenium.open(CC_CONTEXT);

		selenium.refresh();
		wait("selenium.isTextPresent(\"Welcome to\")");

		assertTrue(selenium.isTextPresent("Welcome to"));

		try {
			ignoreMarkCRFCompleteMSG = selenium.getCookieByName("ignoreMarkCRFCompleteMSG");
		} catch (Exception e) {
		}
	}

	// the method clicks the hoverStudySubjectEvent button
	protected void hoverStudySubjectEvent(String SSID, String eventName) throws Exception {
		selenium.open(SUBJECT_MATRIX_URL, PAGE_TIME_OUT);

		wait("selenium.isElementPresent(\"//table[@id='findSubjects']//td[text()='" + SSID
				+ "']/parent::tr[starts-with(@id, 'findSubjects_row')]\")");
		String rowId = selenium.getAttribute("//table[@id='findSubjects']//td[text()='" + SSID
				+ "']/parent::tr[starts-with(@id, 'findSubjects_row')]@id");

		String menuId = findMenuId(SSID, rowId, eventName);
		String eventId = menuId.replace("Menu_on_", "Event_");
		wait("selenium.isElementPresent(\"//div[@id='" + eventId
				+ "']/following-sibling::a[starts-with(@onmouseover, 'hideAllTooltips')]\")");
		selenium.mouseOver("//div[@id='" + eventId
				+ "']/following-sibling::a[starts-with(@onmouseover, 'hideAllTooltips')]");
		selenium.mouseMove("//div[@id='" + eventId
				+ "']/following-sibling::a[starts-with(@onmouseover, 'hideAllTooltips')]");

		Thread.sleep(5000);
	}

    protected void openListUserAccountsPage() {
        selenium.open(LIST_USER_ACCOUNTS_URL, PAGE_TIME_OUT);
        selenium.waitForPageToLoad(PAGE_TIME_OUT);
    }

	protected void openSubjectMatrixPage() {
		selenium.open(SUBJECT_MATRIX_URL, PAGE_TIME_OUT);
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
	}

	// the method tries to remove the CRF data (it clicks 'X' button)
	protected void removeCRFData(String crfName) throws Exception {
		wait("selenium.isElementPresent(\"//td[(@class='table_cell' or @class='table_cell_left') and starts-with(text(), '"
				+ crfName + "')]/parent::tr//a[starts-with(@href, 'DeleteEventCRF?')]\")");
		selenium.click("//td[(@class='table_cell' or @class='table_cell_left') and starts-with(text(), '" + crfName
				+ "')]/parent::tr//a[starts-with(@href, 'DeleteEventCRF?')]");
		removeEventCRFData();
	}

	// the method checks can we remove CRF data if it's exist or not (if we can remove data - it returns true)
	protected boolean haveToRemoveCRFData(String crfName) throws Exception {
		Thread.sleep(2000);
		return selenium
				.isElementPresent("//td[(@class='table_cell' or @class='table_cell_left') and starts-with(text(), '"
						+ crfName + "')]/parent::tr//a[starts-with(@href, 'DeleteEventCRF?')]");
	}

	// the method clicks the administrativeEditing button
	protected void administrativeEditing(String crfName) throws Exception {
		wait("selenium.isElementPresent(\"//td[@class='table_cell' and starts-with(text(), '" + crfName
				+ "')]/parent::tr/td/a[contains(@onclick, 'AdministrativeEditing?')]\")");
		selenium.click("//td[@class='table_cell' and starts-with(text(), '" + crfName
				+ "')]/parent::tr/td/a[contains(@onclick, 'AdministrativeEditing?')]");
		initCrfInfo();
	}

	// the method clicks the enterData button
	protected void enterData(String crfName) throws Exception {
		wait("selenium.isElementPresent(\"//td[(@class='table_cell' or @class='table_cell_left') and starts-with(text(), '"
				+ crfName + "')]/parent::tr//img[@alt='Enter Data']\")");
		selenium.click("//td[(@class='table_cell' or @class='table_cell_left') and starts-with(text(), '" + crfName
				+ "')]/parent::tr//img[@alt='Enter Data']");
		initCrfInfo();
	}

	// the method clicks the continueEnteringData button
	protected void continueEnteringData(String crfName) throws Exception {
		wait("selenium.isElementPresent(\"//td[(@class='table_cell' or @class='table_cell_left') and starts-with(text(), '"
				+ crfName + "')]/parent::tr//img[@alt='Continue Entering Data']\")");
		selenium.click("//td[(@class='table_cell' or @class='table_cell_left') and starts-with(text(), '" + crfName
				+ "')]/parent::tr//img[@alt='Continue Entering Data']");
		initCrfInfo();
	}

	// the method adds the new row in the repeating group
	protected void addRow() throws Exception {
		int existingRowsCount = getXpathCount(groupRowSelector);
		waitAndDo("selenium.isElementPresent(\"" + groupRowSelector + "[" + (existingRowsCount + 1) + "]\")",
				"//button[@stype='add']");
	}

	// the method removes the row from the repeating group (rowNumber the serial number of visible row on the page ->
	// from 1 to n)
	protected void removeRow(int rowNumber) throws Exception {
		int existingRowsCount = getXpathCount(groupRowSelector);
		waitAndDo("selenium.isElementPresent(\"" + groupRowSelector + "[" + existingRowsCount + "]\") == false",
				groupRowSelector + "[" + rowNumber + "]//button[@class='button_remove']");
	}

	// the method clicks the radio button with needed value
	protected void setRadio(String inputId, String value) throws Exception {
		wait("selenium.isElementPresent(\"//input[@id='" + inputId + "' and @value='" + value + "']\")");
		selenium.click("//input[@id='" + inputId + "' and @value='" + value + "']");
	}

	// the method sets the needed value in the input on the page
	protected void setValue(String inputId, String value) throws Exception {
		wait("selenium.isElementPresent(\"//input[@id='" + inputId + "']\")");
		selenium.type("//input[@id='" + inputId + "']", value);
	}

	// the method clicks the radio button in the repeating group with needed value (row - is the serial number of
	// visible row on the page -> from 1 to n)
	protected void setInputRadio(int row, String inputId, String value) throws Exception {
		--row;
		String id = groupId + "_" + (row == 0 ? row : ("manual" + row)) + inputId;
		wait("selenium.isElementPresent(\"//input[@id='" + id + "' and @value='" + value + "']\")");
		selenium.click("//input[@id='" + id + "' and @value='" + value + "']");
	}

	// the method sets the needed value in the input in the repeating group (row - is the serial number of visible row
	// on the page -> from 1 to n)
	protected void setInputValue(int row, String inputId, String value) throws Exception {
		--row;
		String id = groupId + "_" + (row == 0 ? row : ("manual" + row)) + inputId;
		wait("selenium.isElementPresent(\"//input[@id='" + id + "']\")");
		selenium.type("//input[@id='" + id + "']", value);
	}

	// the method sets the needed value in the select in the repeating group (row - is the serial number of visible row
	// on the page -> from 1 to n)
	protected void setSelectValue(int row, String inputId, String value) throws Exception {
		--row;
		String id = groupId + "_" + (row == 0 ? row : ("manual" + row)) + inputId;
		wait("selenium.isElementPresent(\"//select[@id='" + id + "']\")");
		selenium.select("//select[@id='" + id + "']", value);
	}

	protected void setSelectValue(String inputId, String value) throws Exception {
		wait("selenium.isElementPresent(\"//select[@id='" + inputId + "']\")");
		selenium.select("//select[@id='" + inputId + "']", value);
	}

	// the method clicks the radio button in the repeating group with needed value (row - is the internal number that
	// the javascript has generated for this new added element before saving of the CRF form)
	protected void setNewInputRadio(int row, String inputId, String value) throws Exception {
		--row;
		String id = groupId + "_" + row + "" + inputId;
		wait("selenium.isElementPresent(\"//input[@id='" + id + "' and @value='" + value + "']\")");
		selenium.click("//input[@id='" + id + "' and @value='" + value + "']");
	}

	// the method sets the needed value in the input in the repeating group (row - is the internal number that the
	// javascript has generated for this new added element before saving of the CRF form)
	protected void setNewInputValue(int row, String inputId, String value) throws Exception {
		--row;
		String id = groupId + "_" + row + "" + inputId;
		wait("selenium.isElementPresent(\"//input[@id='" + id + "']\")");
		selenium.type("//input[@id='" + id + "']", value);
	}

	// the method sets the needed value in the select in the repeating group (row - is the internal number that the
	// javascript has generated for this new added element before saving of the CRF form)
	protected void setNewSelectValue(int row, String inputId, String value) throws Exception {
		--row;
		String id = groupId + "_" + row + "" + inputId;
		wait("selenium.isElementPresent(\"//select[@id='" + id + "']\")");
		selenium.select("//select[@id='" + id + "']", value);
	}

	// method opens new tab (tabNumber - is the number of the tab from 1 to n)
	protected void openTab(int tabNumber) throws Exception {
		if (tabNumber > 1) {
			Thread.sleep(5000);
			wait("selenium.isElementPresent(\"//div[@id='Tab" + tabNumber + "NotSelected']/div/div/div/a\")");
			selenium.click("//div[@id='Tab" + tabNumber + "NotSelected']/div/div/div/a");
			wait("selenium.isElementPresent(\"//div[@id='Tab" + tabNumber + "NotSelected']//div[@class='tab_BG_h']\")");
			initCrfInfo();
		}
	}

	// method waits until the tab will be opened (tabNumber - is the number of the tab from 1 to n)
	protected void waitForNextTab(int tabNumber) throws Exception {
		if (tabNumber > 1) {
			selenium.waitForPageToLoad(PAGE_TIME_OUT);
			wait("selenium.isElementPresent(\"//div[@id='Tab" + tabNumber + "NotSelected']//div[@class='tab_BG_h']\")");
			initCrfInfo();
		}
	}

	// method saves the CRF (it click the save button)
	protected void saveCrf() throws Exception {
		selenium.click("//input[@id='srl']");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
	}

	// exit the CRF (it click the exit button)
	protected void exitCrf() throws Exception {
		selenium.click("//input[@id='seh']");
	}

	// method tries to mark the CRF as complete
	protected void markCRFComplete() throws Exception {
		Thread.sleep(2000);
		selenium.createCookie("ignoreMarkCRFCompleteMSG=yes", "");
		int c = 0;
		boolean wereTheConfirmation = false;
		Thread.sleep(2000);
		wait("selenium.isElementPresent(\"//input[@id='markCompleteId']\")");
		selenium.click("//input[@id='markCompleteId']");
		while (c < 5 && !wereTheConfirmation) {
			Thread.sleep(DELAY);
			wereTheConfirmation = selenium.isConfirmationPresent();
			c++;
		}
		if (wereTheConfirmation) {
			assertTrue(selenium.getConfirmation().contains("Marking this CRF complete will finalize data entry."));
		}
	}

	// method checks / waits for needed DN
	protected void checkDn(String inputId, String txt) throws Exception {
		checkDn(inputId, txt, null, null, false);
	}

	// method checks / waits for needed DN + method checks type, status and assignment
	protected void checkDn(String inputId, String txt, String type, String status, boolean shouldBeAssigned)
			throws Exception {
		String id = "flag_" + inputId;
		wait("selenium.isElementPresent(\"//img[@id='" + id + "']\")");
		selenium.click("//img[@id='" + id + "']");
		selenium.selectWindow(waitWindow("dnote_win"));
		wait("selenium.isTextPresent(\"" + txt + "\")");

		if (type != null) {
			wait("selenium.isElementPresent(\"//td[@class='aka_header_border'][2]\")");
			assertTrue(selenium.getText("//td[@class='aka_header_border'][2]").contains(type));
		}
		if (status != null) {
			wait("selenium.isElementPresent(\"//td[@class='aka_header_border'][3]\")");
			assertTrue(selenium.getText("//td[@class='aka_header_border'][3]").contains(status));
		}
		if (shouldBeAssigned) {
			assertEquals(selenium.isTextPresent("Assigned to:"), shouldBeAssigned);
		}

		wait("selenium.isElementPresent(\"//a[@onclick='javascript:window.close();']\")");
		selenium.click("//a[@onclick='javascript:window.close();']");
		selectMainWindow();
	}

	protected void checkDn(String inputId, String txt, String type, String status, String assignedTo) throws Exception {
		String id = "flag_" + inputId;
		wait("selenium.isElementPresent(\"//img[@id='" + id + "']\")");
		selenium.click("//img[@id='" + id + "']");
		selenium.selectWindow(waitWindow("dnote_win"));
		wait("selenium.isTextPresent(\"" + txt + "\")");

		if (assignedTo != null) {
			wait("selenium.isElementPresent(\"//td[@class='aka_header_border'][1]\")");
			assertTrue(selenium.getText("//td[@class='aka_header_border'][1]").contains(assignedTo));
		}
		if (type != null) {
			wait("selenium.isElementPresent(\"//td[@class='aka_header_border'][2]\")");
			assertTrue(selenium.getText("//td[@class='aka_header_border'][2]").contains(type));
		}
		if (status != null) {
			wait("selenium.isElementPresent(\"//td[@class='aka_header_border'][3]\")");
			assertTrue(selenium.getText("//td[@class='aka_header_border'][3]").contains(status));
		}

		wait("selenium.isElementPresent(\"//a[@onclick='javascript:window.close();']\")");
		selenium.click("//a[@onclick='javascript:window.close();']");
		selectMainWindow();
	}

	// method checks / waits for needed DN (row - is the internal number that the javascript has generated for this new
	// added element before saving of the CRF form)
	protected void checkDn(int row, String inputId, String txt) throws Exception {
		--row;
		String id = "flag_" + groupId + "_" + (row == 0 ? row : ("manual" + row)) + inputId;
		wait("selenium.isElementPresent(\"//img[@id='" + id + "']\")");
		selenium.click("//img[@id='" + id + "']");
		selenium.selectWindow(waitWindow("dnote_win"));
		wait("selenium.isTextPresent(\"" + txt + "\")");
		wait("selenium.isElementPresent(\"//a[@onclick='javascript:window.close();']\")");
		selenium.click("//a[@onclick='javascript:window.close();']");
		selectMainWindow();
	}

	// method adds DN for element with id == ("flag_" + inputId)
	protected void addDirectDn(String inputId, String txt) throws Exception {
		addDirectDn(inputId, txt, null);
	}

	// method adds DN for element with id == ("flag_" + inputId) + this method closes the DN with needed type
	protected void addDirectDn(String inputId, String txt, DiscrepancyNoteType dnType) throws Exception {
		assertTrue(selenium.isElementPresent("//img[@id='flag_" + inputId + "' and @src='images/icon_noNote.gif']"));
		selenium.click("//img[@id='flag_" + inputId + "']");
		selenium.selectWindow(waitWindow("dnote_win"));

		wait("selenium.isElementPresent(\"//input[@name='description']\")");
		wait("selenium.isElementPresent(\"//textarea[@name='detailedDes']\")");
		wait("selenium.isElementPresent(\"//input[@type='submit' and (@name='Submit' or @name='SubmitExit')]\")");

		selenium.type("//input[@name='description']", txt);
		selenium.type("//textarea[@name='detailedDes']", "description: " + txt);
		setDnType(dnType);
		// setDNResolutionStatus();
		wait("selenium.isElementPresent(\"//input[@type='submit' and (@name='Submit' or @name='SubmitExit')]\")");
		selenium.click("//input[@type='submit' and (@name='Submit' or @name='SubmitExit')]");
		wait("selenium.isTextPresent(\"ATTENTION: You must submit/save the form\") || selenium.isTextPresent(\"Your discrepancy note\")");
		String popupTitle = selenium.getTitle();
		selectMainWindow();
		wait("selenium.isElementPresent(\"//img[@id='flag_" + inputId
				+ "' and (@src='images/icon_Note.gif' or @src='images/icon_flagWhite.gif')]\")");
		selenium.selectWindow(popupTitle);
		wait("selenium.isElementPresent(\"//a[@onclick='javascript:window.close();']\")");
		selenium.click("//a[@onclick='javascript:window.close();']");
		selectMainWindow();
	}

	// method adds DN for element in repeating group (row - is the internal number that the javascript has generated for
	// this new added element before saving of the CRF form)
	protected void addDnForNewInput(int row, String inputId, String txt) throws Exception {
		--row;
		String id = "flag_" + groupId + "_" + row + "" + inputId;
		assertTrue(selenium.isElementPresent("//img[@id='" + id + "' and @src='images/icon_noNote.gif']"));
		selenium.click("//img[@id='" + id + "']");
		selenium.selectWindow(waitWindow("dnote_win"));

		wait("selenium.isElementPresent(\"//input[@name='description']\")");
		wait("selenium.isElementPresent(\"//textarea[@name='detailedDes']\")");
		wait("selenium.isElementPresent(\"//input[@type='submit' and (@name='Submit' or @name='SubmitExit')]\")");

		selenium.type("//input[@name='description']", txt);
		selenium.type("//textarea[@name='detailedDes']", "description: " + txt);
		wait("selenium.isElementPresent(\"//input[@type='submit' and (@name='Submit' or @name='SubmitExit')]\")");
		selenium.click("//input[@type='submit' and (@name='Submit' or @name='SubmitExit')]");
		wait("selenium.isTextPresent(\"ATTENTION: You must submit/save the form\") || selenium.isTextPresent(\"Your discrepancy note\")");
		String popupTitle = selenium.getTitle();
		selectMainWindow();
		wait("selenium.isElementPresent(\"//img[@id='" + id
				+ "' and (@src='images/icon_Note.gif' or @src='images/icon_flagWhite.gif')]\")");
		selenium.selectWindow(popupTitle);
		wait("selenium.isElementPresent(\"//a[@onclick='javascript:window.close();']\")");
		selenium.click("//a[@onclick='javascript:window.close();']");
		selectMainWindow();
	}

	// method adds DN for element in repeating group (row - is the serial number of visible row on the page -> from 1 to
	// n)
	protected void addDn(int row, String inputId, String txt) throws Exception {
		--row;
		String id = "flag_" + groupId + "_" + (row == 0 ? row : ("manual" + row)) + inputId;
		assertTrue(selenium.isElementPresent("//img[@id='" + id + "' and @src='images/icon_noNote.gif']"));
		selenium.click("//img[@id='" + id + "']");
		selenium.selectWindow(waitWindow("dnote_win"));

		wait("selenium.isElementPresent(\"//input[@name='description']\")");
		wait("selenium.isElementPresent(\"//textarea[@name='detailedDes']\")");
		wait("selenium.isElementPresent(\"//input[@type='submit' and (@name='Submit' or @name='SubmitExit')]\")");

		selenium.type("//input[@name='description']", txt);
		selenium.type("//textarea[@name='detailedDes']", "description: " + txt);
		wait("selenium.isElementPresent(\"//input[@type='submit' and (@name='Submit' or @name='SubmitExit')]\")");
		selenium.click("//input[@type='submit' and (@name='Submit' or @name='SubmitExit')]");
		wait("selenium.isTextPresent(\"ATTENTION: You must submit/save the form\") || selenium.isTextPresent(\"Your discrepancy note\")");
		String popupTitle = selenium.getTitle();
		selectMainWindow();
		wait("selenium.isElementPresent(\"//img[@id='" + id
				+ "' and (@src='images/icon_Note.gif' or @src='images/icon_flagWhite.gif')]\")");
		selenium.selectWindow(popupTitle);
		wait("selenium.isElementPresent(\"//a[@onclick='javascript:window.close();']\")");
		selenium.click("//a[@onclick='javascript:window.close();']");
		selectMainWindow();
	}

	// method checks alert for the input
	protected void checkAlertForInput(String inputId) throws Exception {
		wait("selenium.isElementPresent(\"//input[@id='" + inputId
				+ "']/parent::div//span[@class='aka_exclaim_error']\")");
		assertTrue(getXpathCount("//input[@id='" + inputId + "']/parent::div//span[@class='aka_exclaim_error']") == 1);

	}

	// method checks DN for the input
	protected void checkDNForInput(String inputId) throws Exception {
		wait("selenium.isElementPresent(\"//img[@id='flag_" + inputId
				+ "' and (@src='images/icon_Note.gif' or @src='images/icon_flagWhite.gif')]\")");
		assertTrue(getXpathCount("//img[@id='flag_" + inputId
				+ "' and (@src='images/icon_Note.gif' or @src='images/icon_flagWhite.gif')]") == 1);
	}

	// method checks quantity of alerts on the CRF form
	protected void checkQuantityOfAlertsForRG(int expectedQuantityOfAlerts) throws Exception {
		wait("selenium.getXpathCount(\"" + groupRowSelector
				+ "[@repeat-template!='']//span[@class='aka_exclaim_error']\") == \"" + expectedQuantityOfAlerts + "\"");
		assertTrue(getXpathCount(groupRowSelector + "[@repeat-template!='']//span[@class='aka_exclaim_error']") == expectedQuantityOfAlerts);
	}

	// method checks quantity of added DN's on the CRF form
	protected void checkQuantityOfDNsForRG(int expectedQuantityOfDNs) throws Exception {
		wait("selenium.getXpathCount(\"" + groupRowSelector
				+ "[@repeat-template!='']//img[@alt='Discrepancy Note' and @src='images/icon_Note.gif']\") == \""
				+ expectedQuantityOfDNs + "\"");
		assertTrue(getXpathCount(groupRowSelector
				+ "[@repeat-template!='']//img[@alt='Discrepancy Note' and @src='images/icon_Note.gif']") == expectedQuantityOfDNs);
	}

	// go to list of DNs assigned to current user
	protected void goToAssignedToMePage() throws Exception {
		selenium.click("//a[contains(@href, 'ViewNotes?module=submit&')]");
		wait("selenium.isTextPresent(\"Notes and Discrepancies\")");
	}

	// check status of DN list
	protected void checkSelectedDNListStatus(String status) {
		assertTrue(selenium.getText("//table[@id='listNotes']/thead/tr[3]/td[3]/div").contains(status));
	}

	// check user in filter of DN list
	protected void checkSelectedDNListUser(String user) {
		assertTrue(selenium.getText("//table[@id='listNotes']/thead/tr[3]/td[19]/div").contains(user));
	}

	// method to change and save study status
	protected void changeStudyStatus(String status) throws Exception {
		selenium.open(CC_CONTEXT + "/pages/studymodule");
		// wait("selenium.isElementPresent(\"//select[@name='studyStatus']\")");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		selenium.select("//select[@name='studyStatus']", "label=" + status);
	}

	// method to set and save site's facility name
	protected void setSiteFacilityName(String facilityName) throws Exception {
		selenium.open(CC_CONTEXT + "/ListSite");
		// wait("selenium.isTextPresent(\"Manage All Sites\")");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);

		selenium.click("//tr[2]/td[8]/table/tbody/tr/td[2]/a/img");
		// wait("selenium.isTextPresent(\"Update Site Details\")");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);

		selenium.type("//tr[11]/td[2]/div/input", facilityName);
		selenium.click("//input[@name='Submit']");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
	}

	// method to set and save site name
	protected void setSiteName(String name) throws Exception {
		selenium.open(CC_CONTEXT + "/ListSite");
		wait("selenium.isTextPresent(\"Manage All Sites\")");

		selenium.click("//tr[2]/td[8]/table/tbody/tr/td[2]/a/img");
		wait("selenium.isTextPresent(\"Update Site Details\")");

		selenium.type("//tr[2]/td[2]/div/input", name);
		selenium.click("//input[@name='Submit']");
	}

	// checking site's facility name
	protected void checkSiteFacilityName(String facilityName) throws Exception {
		selenium.open(CC_CONTEXT + "/ListSite");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		// wait("selenium.isTextPresent(\"Manage All Sites\")");

		assertTrue(selenium.getText("//tr[2]/td[5]").contains(facilityName));
	}

	// checking site name
	protected void checkSiteName(String name) throws Exception {
		selenium.open(CC_CONTEXT + "/ListSite");
		// wait("selenium.isTextPresent(\"Manage All Sites\")");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);

		assertTrue(selenium.getText("//table/tbody/tr[2]/td/table/tbody/tr[2]/td").contains(name));
	}

	// returns random facility name
	protected String getRandomName() {
		return Long.toString(new Date().getTime());
	}

	// get name of first site in site list
	protected String getCurrentSiteName() throws Exception {
		selenium.open(CC_CONTEXT + "/ListSite");
		// wait("selenium.isTextPresent(\"Manage All Sites\")");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		return selenium.getText("//table/tbody/tr[2]/td/table/tbody/tr[2]/td");
	}

	protected void fillTab1() throws Exception {
		Thread.sleep(5000);
		enterData("Monthly Office Visit Follow-up Form"); // enter data for 'XForm' CRF
		setValue("input353", "16-Feb-2012");
		setValue("input354", "11031703-1555-SC");
		setValue("input355", "11122101-T013");
		setValue("input356", "10111801-02");
		setInputRadio(1, "input357", "3");
		addDn(1, "input357", "DN 1");
		setValue("input360", "24-Jan-2012");
		addDirectDn("input360", "DN 1");
		setValue("input361", "15-Feb-2012");
		setRadio("input362", "2");
		setRadio("input363", "1");
		setValue("input364", "150");
		setValue("input365", "85");
		setValue("input366", "59");
	}

	protected void fillTab2() throws Exception {
		Thread.sleep(5000);
		String[] arr = new String[] { "08-Feb-2012", "24-Jan-2012", "25-Jan-2012", "26-Jan-2012", "27-Jan-2012",
				"28-Jan-2012", "29-Jan-2012", "30-Jan-2012", "01-Feb-2012", "02-Feb-2012", "03-Feb-2012",
				"04-Feb-2012", "05-Feb-2012", "06-Feb-2012", "07-Feb-2012", "08-Feb-2012", "09-Feb-2012",
				"10-Feb-2012", "11-Feb-2012", "12-Feb-2012", "13-Feb-2012", "14-Feb-2012", "15-Feb-2012" };
		for (int i = 1; i <= 22; i++) {
			addRow();
		}
		setInputValue(1, "input367", arr[0]);
		setInputRadio(1, "input368", "1");
		for (int i = 2; i <= 23; i++) {
			setNewInputValue(i, "input367", arr[i - 1]);
			setNewInputRadio(i, "input368", "1");
		}
	}

	protected void removeCRFData() throws Exception {
		hoverStudySubjectEvent(TEST_SUBJECT_1, "First Office Visit/Screening"); // view/enter datafor: SSID ->
																				// 'TEST_SUBJECT_1' & event -> 'First
																				// Office Visit/Screening'
		removeCRFData("Monthly Office Visit Follow-up Form"); // remove CRF data
		hoverStudySubjectEvent(TEST_SUBJECT_1, "First Office Visit/Screening"); // view/enter datafor: SSID ->
																				// 'TEST_SUBJECT_1' & event -> 'First
																				// Office Visit/Screening'
	}

	protected void waitForText(String text) throws Exception {
		wait("selenium.isTextPresent(\"" + text + "\")");
	}

	protected void openStudySetupPage() throws Exception {
		selenium.open(CC_CONTEXT + "/UpdateStudyNew?id=9");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		// wait("selenium.isElementPresent(\"//input[@name='studySubjectIdLabel']\")");
	}

	protected void openSdvPage() throws Exception {
		selenium.open(CC_CONTEXT + "/pages/viewAllSubjectSDVtmp?studyId=9");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		// waitForText("Source Data Verification for");
	}

	protected void openViewJobsPage() throws Exception {
		selenium.open(CC_CONTEXT + "/ViewAllJobs");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		// waitForText("Administer All Jobs");
	}

	protected void openAddSubjectPage() throws Exception {
		selenium.open(CC_CONTEXT + "/AddNewSubject");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		// waitForText("Add Subject");
	}

	protected void openScheduleEvenPage() throws Exception {
		selenium.open(CC_CONTEXT + "/CreateNewStudyEvent");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		// waitForText("Schedule Study Event");
	}

	protected void openDiscrepancyNotesPage() throws Exception {
		selenium.open(CC_CONTEXT + "/ViewNotes?module=submit");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		// waitForText("Notes and Discrepancies");
	}

	protected void submitStudyOptions() throws Exception {
		selenium.click("//input[@name='Submit']");
		selenium.waitForPageToLoad(PAGE_TIME_OUT);
		// waitForText("AC Study");
	}

	protected void assertElementPresented(String locator) {
		assertTrue(selenium.isElementPresent(locator));
	}

	protected void assertElementNotPresented(String locator) {
		assertFalse(selenium.isElementPresent(locator));
	}

	protected void assertElementVisible(String locator) {
		assertTrue(selenium.isVisible(locator));
	}

	protected void assertElementNotVisible(String locator) {
		assertFalse(selenium.isVisible(locator));
	}

	protected void assertTextPresented(String text) {
		assertTrue(selenium.isTextPresent(text));
	}

	// ************************
	// *** internal methods ***
	// ************************

	// method sets the needed DN type
	private void setDnType(DiscrepancyNoteType dnType) throws Exception {
		if (dnType != null) {
			switch (dnType) {
			case FAILED_VALIDATION_CHECK: {
				setSelectValue("typeId", "label=Failed Validation Check");
				Thread.sleep(1000);
				break;
			}
			case ANNOTATION: {
				// TODO
				break;
			}
			case QUERY: {
				setSelectValue("typeId", "label=Query");
				Thread.sleep(1000);
				break;
			}
			case REASON_FOR_CHANGE: {
				setSelectValue("typeId", "label=Reason for Change");
				Thread.sleep(1000);
				break;
			}
			}
		}
	}

	// method tries to do the logout
	private void logout() throws Exception {
		selenium.open(LOGOUT_URL, PAGE_TIME_OUT);
		Thread.sleep(2000);
	}

	private String findMenuId(String SSID, String rowId, String eventName) throws Exception {
		String menuId = null;
		wait("selenium.isElementPresent(\"//tr[@id='" + rowId + "']//tr[starts-with(@id, 'Menu_on_" + SSID
				+ "')][1]\")");
		String firstMenuId = selenium.getAttribute("//tr[@id='" + rowId + "']//tr[starts-with(@id, 'Menu_on_" + SSID
				+ "')][1]@id");
		for (int i = 1; i <= MAX_EVENT_DEFINITIONS; i++) {
			String part = firstMenuId.substring(0, firstMenuId.lastIndexOf("_"));
			String nextMenuId = part + "_" + i;
			wait("selenium.isElementPresent(\"//tr[@id='" + nextMenuId + "']/parent::tbody/tr/td\")");
			String txt = selenium.getText("//tr[@id='" + nextMenuId + "']/parent::tbody/tr/td");
			if (txt.contains("Event: " + eventName)) {
				menuId = nextMenuId;
				break;
			}
		}
		if (menuId == null) {
			throw new Exception("MenuId is null!");
		}
		return menuId;
	}

	private void removeEventCRFData() throws Exception {
		int c = 0;
		boolean wereTheConfirmation = false;
		Thread.sleep(2000);
		wait("selenium.isElementPresent(\"//form[starts-with(@action, 'DeleteEventCRF?')]/input[@value='Delete Event CRF']\")");
		selenium.click("//form[starts-with(@action, 'DeleteEventCRF?')]/input[@value='Delete Event CRF']");
		while (c < 5 && !wereTheConfirmation) {
			Thread.sleep(DELAY);
			wereTheConfirmation = selenium.isConfirmationPresent();
			c++;
		}
		if (wereTheConfirmation) {
			assertTrue(selenium.getConfirmation().equalsIgnoreCase(
					"This CRF has data. Are you sure you want to delete it?"));
		}
	}

	private int getXpathCount(String selector, Integer delay) throws Exception {
		int count = selenium.getXpathCount(selector).intValue();
		Thread.sleep(delay == null ? DELAY : delay);
		int sCount = selenium.getXpathCount(selector).intValue();
		if (count != sCount) {
			return getXpathCount(selector, delay);
		} else {
			return count;
		}
	}

	private int getXpathCount(String selector) throws Exception {
		return getXpathCount(selector, null);
	}

	private void updateRowsInfo() throws Exception {
		rowsInfo.totalRows = getXpathCount(groupRowSelector, 5000);
		rowsInfo.visibleRows = rowsInfo.totalRows - rowsInfo.hiddenRows;
	}

	private void initCrfInfo() throws Exception {
		updateRowsInfo();
		wait("selenium.isElementPresent(\"" + groupRowSelector + "[@repeat='template']\")");
		Thread.sleep(2000);
		groupId = selenium.getAttribute(groupRowSelector + "[1]@repeat-template");
		mainWinId = selenium.getTitle();
	}

	private void selectMainWindow() {
		selenium.selectWindow(mainWinId);
	}
}
