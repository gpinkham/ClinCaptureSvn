package com.clinovo.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.clinovo.pages.beans.CRF;
import com.clinovo.utils.Common;
import com.clinovo.utils.ItemsUtil;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.webelements.Checkbox;
import net.thucydides.core.webelements.MultipleSelect;
import net.thucydides.core.webelements.RadioButtonGroup;

/**
 * CRF page.
 */
public class CRFPage extends BasePage {
	public static final String PAGE_NAME = "CRF page";
	public static final String PAGE_URL = "InitialDataEntry/AdministrativeEditing";

	@FindBy(id = "mainForm")
	private WebElementFacade formWithData;

	@FindBy(name = "submittedResume")
	private WebElementFacade bSave;

	@FindBy(name = "submittedExit")
	private WebElementFacade bExit;

	@FindBy(id = "markCompleteId")
	private WebElementFacade cMarkCRFComplete;

	// 'Mark CRF Complete' dialog
	@FindBy(id = "confirmation")
	private WebElementFacade divConfirmation;

	@FindBy(id = "ignoreMarkCRFCompleteMSG")
	private WebElementFacade cIgnoreMarkCRFCompleteMSG;

	@FindBy(xpath = ".//*[@id='confirmation']//input[contains(@onclick, 'markCRFCompleteOk')]")
	private WebElementFacade bMarkCRFCompleteYes;

	@FindBy(name = "interviewer")
	private WebElementFacade iInterviewer;

	@FindBy(id = "showMoreInfo")
	private WebElementFacade lMoreInfo;

	@FindBy(id = "errorMessagesContainer")
	private WebElementFacade iErrorMessage;

	@FindBy(xpath = "//*[@name='submittedExit' or @id='sel']")
	private WebElementFacade bCancel;

	@FindBy(xpath = "//div[@id='errorMessagesContainer']//label")
	private WebElementFacade dError;
	
	@FindBy(id = "dlgBtnYes")
	private WebElementFacade bDlgYes;

	/**
	 * Constructor
	 * @param driver
	 * 			WebDriver
	 */
	public CRFPage(WebDriver driver) {
		super(driver);
	}

	@Override
	public boolean isOnPage(WebDriver driver) {
		return true;
	}

	public void expandMoreInfo() {
		lMoreInfo.click();
	}

	public void clickYesAtDlg() {
		if (bDlgYes.isCurrentlyVisible()) {
			bDlgYes.click();
		}
	}

	public void clickCancelButton() {
		bCancel.click();
	}

	/**
	 * Method checks if data in the fields from CRF correspond with data from this CRF bean.
	 *
	 * @param crf
	 * 			CRF
	 * @return Map<String, Boolean>
	 */
	public Map<String, Boolean> checkDataInCRF(CRF crf) {
		Map<String, Boolean> result = new HashMap<String, Boolean>();
		List<String> names = new ArrayList<String>(crf.getFieldNameToValueMap().keySet());
		Collections.sort(names, CRF.comparatorForItemOIDs);
		for (String fieldName : names) {
			if (crf.getFieldNameToValueMap().get(fieldName).isEmpty())
				continue;

			WebElementFacade element = formWithData
					.find(By.xpath(".//*[@name='" + Common.removeOrderAndType(fieldName) + "']"));
			String fieldValue = crf.getFieldNameToValueMap().get(fieldName);
			MultipleSelect ms;
			Set<String> set;
			switch (Common.getType(fieldName)) {
			case "T":
				// text (or date) field
				result.put(fieldName, fieldValue.equals(element.getTextValue()));
				break;
			case "R":
				// radio button group
				RadioButtonGroup rBgroup = new RadioButtonGroup(formWithData
						.findElements(By.xpath(".//*[@name='" + Common.removeOrderAndType(fieldName) + "']")));
				result.put(fieldName, fieldValue.equals(rBgroup.getSelectedValue().get()));
				break;
			case "S":
				// single select, by visible text
				result.put(fieldName, fieldValue.equals(element.getSelectedVisibleTextValue()));
				break;
			case "Sv":
				// single select, by value
				result.put(fieldName, fieldValue.equals(element.getSelectedValue()));
				break;
			case "M":
				// or multiple select, by visible text
				ms = new MultipleSelect(element);
				set = new HashSet<String>(Arrays.asList(fieldValue.split(",")));
				result.put(fieldName,
						ms.getSelectedOptionLabels().containsAll(set) && set.containsAll(ms.getSelectedOptionLabels()));
				break;
			case "Mv":
				// or multiple select, by value
				ms = new MultipleSelect(element);
				set = new HashSet<String>(Arrays.asList(fieldValue.split(",")));
				result.put(fieldName,
						ms.getSelectedOptionValues().containsAll(set) && set.containsAll(ms.getSelectedOptionValues()));
				break;
			case "C":
				// checkbox
				set = new HashSet<String>();
				Set<String> setOfCheckedItems = new HashSet<String>();
				// check if all needed checkboxes are checked
				for (String value : fieldValue.split(",")) {
					element = formWithData.find(By.xpath(
							".//*[@name='" + Common.removeOrderAndType(fieldName) + "'][@value='" + value + "']"));
					Checkbox cBox = new Checkbox(element);
					if (cBox.isChecked())
						set.add(value);
				}
				// check unneeded checkboxes are not checked
				for (WebElement el : formWithData
						.findElements(By.xpath(".//*[@name='" + Common.removeOrderAndType(fieldName) + "']"))) {
					Checkbox cBox = new Checkbox(el);
					if (cBox.isChecked())
						setOfCheckedItems.add(el.getAttribute("value"));
				}

				result.put(fieldName, setOfCheckedItems.containsAll(set) && set.containsAll(setOfCheckedItems));
				break;
			case "F":
				// upload file
				result.put(fieldName, fieldValue.equals(element.getValue()));
				break;

			default:
				element.type(fieldValue);
			}
		}

		return result;
	}

	/**
	 * Method fills in data to CRF from this CRF bean.
	 *
	 * @param crf
	 * 			CRF
	 * @return Map<String, Boolean>
	 */
	public void fillInCRF(CRF crf) {
		List<String> names = new ArrayList<String>(crf.getFieldNameToValueMap().keySet());
		Collections.sort(names, CRF.comparatorForItemOIDs);
		if (crf.getAddRows() != null && !crf.getAddRows().isEmpty()) {
			addRowsForRepeatingGroups(crf.getAddRows());
		}
		for (String fieldName : names) {
			if (crf.getFieldNameToValueMap().get(fieldName).isEmpty())
				continue;
			String fieldValue = crf.getFieldNameToValueMap().get(fieldName);
			WebElementFacade element = formWithData
					.find(By.xpath(".//*[@name='" + Common.removeOrderAndType(fieldName) + "']"));
			switch (Common.getType(fieldName)) {
			case "T":
				// text (or date) field
				element.type(fieldValue);
				break;
			case "R":
				// radio button group
				RadioButtonGroup rBgroup = new RadioButtonGroup(formWithData
						.findElements(By.xpath(".//*[@name='" + Common.removeOrderAndType(fieldName) + "']")));
				rBgroup.selectByValue(fieldValue);
				break;
			case "S":
				// single select, by visible text
				element.selectByVisibleText(fieldValue);
				break;
			case "Sv":
				// single select, by value
				element.selectByValue(fieldValue);
				break;
			case "M":
				// or multiple select, by visible text
				for (String value : fieldValue.split(",")) {
					element.selectByVisibleText(value);
				}
				break;
			case "Mv":
				// or multiple select, by value
				for (String value : fieldValue.split(",")) {
					element.selectByValue(value);
				}
				break;
			case "C":
				// checkbox
				for (String value : fieldValue.split(",")) {
					if (!value.matches("\\(.*\\)")) {
						element = formWithData.find(By.xpath(
							".//*[@name='" + Common.removeOrderAndType(fieldName) + "'][@value='" + value + "']"));
						ItemsUtil.fillCheckbox(new Checkbox(element), "checked");
					} else {
						element = formWithData.find(By.xpath(
								".//*[@name='" + Common.removeOrderAndType(fieldName) + "'][@value='" + value.replaceAll("\\(|\\)", "") + "']"));
							ItemsUtil.fillCheckbox(new Checkbox(element), "unchecked");
					}
				}
				break;
			case "F":
				// upload file
				upload(fieldValue).to(element);
				break;

			default:
				element.type(fieldValue);
			}
		}

		if (cMarkCRFComplete.isCurrentlyVisible()) {
			ItemsUtil.fillCheckbox(new Checkbox(cMarkCRFComplete), crf.getMarkComplete());
			if (divConfirmation.isCurrentlyVisible()) {
				ItemsUtil.fillCheckbox(new Checkbox(cIgnoreMarkCRFCompleteMSG), "yes");
				bMarkCRFCompleteYes.click();
			}
		}
	}

	private void addRowsForRepeatingGroups(String value) {
		WebElementFacade element = formWithData.find(By.xpath(".//*[@stype='add']"));
		for (int i = 0; i < Integer.parseInt(value); i++) {
			element.click();
		}
	}

	public void clickSaveButton() {
		bSave.click();
	}

	/**
	 * Method returns DN flag object corresponding to the item on the CRF.
	 * @param itemName
	 * 				String
	 * @return	WebElement
	 */
	public WebElement findFlagIconElementByCRFItem(String itemName) {
		switch (itemName) {
		case "Interviewer Name":
			return formWithData.findElement(By.xpath(".//*[@id='flag_interviewer']"));
		case "Interview Date":
			return formWithData.findElement(By.xpath(".//*[@id='flag_interviewDate']"));
		default:
			return formWithData.findElement(By.xpath(".//*[@id='flag_" + itemName + "']"));
		}
	}

	public void clickExit() {
		bExit.click();
	}

	public void clickSectionTabInCRF(String sectionName) {
		System.out.println("section click");
		formWithData.waitUntilVisible();
		formWithData.findElement(By.xpath("//*[contains(@title,'" + sectionName + "')]")).click();
	}

	public void clickMoreInfoButton() {
		if (!iInterviewer.isCurrentlyVisible()) {
			lMoreInfo.click();
		}
	}

	/**
	 * Verifies if this message is present in error section on CRF.
	 * @param error
	 * 			String
	 */
	public void verifyErrorMessage(String error) {
		String errorMessage;
		errorMessage = dError.getText();
		Assert.assertTrue(errorMessage.contains(error));
	}
}
