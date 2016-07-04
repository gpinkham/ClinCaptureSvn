package com.clinovo.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import com.clinovo.pages.beans.CRF;
import com.clinovo.utils.Common;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

public class ViewCRFPage extends BasePage {

	public static final String PAGE_NAME = "View CRF page";

	@FindBy(id = "mainForm")
	private WebElementFacade formWithData;

	@FindBy(xpath = ".//*[@id='centralContainer']/input[@type='button']")
	private WebElementFacade bExit;

	public ViewCRFPage(WebDriver driver) {
		super(driver);
	}

	public void clickExit() {
		bExit.click();
	}

	/**
	 * Method checks if data in the fields from CRF correspond with data from
	 * this CRF bean on View CRF page.
	 *
	 * @param crf
	 *            CRF
	 * @return Map<String, Boolean>
	 */
	public Map<String, Boolean> checkDataInCRF(CRF crf) {
		Map<String, Boolean> result = new HashMap<String, Boolean>();
		List<String> names = new ArrayList<String>(crf.getFieldNameToValueMap().keySet());
		Collections.sort(names, CRF.comparatorForItemOIDs);
		for (String fieldName : names) {
			if (crf.getFieldNameToValueMap().get(fieldName).isEmpty())
				continue;
 
			JavascriptExecutor js = (JavascriptExecutor) getDriver();
			String expectedValue = crf.getFieldNameToValueMap().get(fieldName);
			String actualValue;
			Set<String> expectedSetOfValues;
			Set<String> actualSetOfValues;
			String jsCommand;
			switch (Common.getType(fieldName)) {
			case "T":
				// text (or date) field
				jsCommand = "return document.getElementById('" + Common.removeOrderAndType(fieldName) + "').value;";
				actualValue = (String)js.executeScript(jsCommand);
				result.put(fieldName, expectedValue.equals(actualValue));
				break;
			case "R":
				// radio button group
				jsCommand = "var radioButtons = document.getElementsByName('" + Common.removeOrderAndType(fieldName) + "');" +
							"var value = '';" +
								"for(var i = 0; i < radioButtons.length; i++){" +
									"if(radioButtons[i].checked){" +
										"value = radioButtons[i].value;" +
									"}" +
								"};" +
							"return value.toString();";
				actualValue = (String)js.executeScript(jsCommand);
				result.put(fieldName, expectedValue.equals(actualValue));
				break;
			case "S":
				// single select, by visible text
				jsCommand = "var e = document.getElementById('" + Common.removeOrderAndType(fieldName) + "');" +
							"var visibleText = e.options[e.selectedIndex].text;" +
							"return visibleText;";
				actualValue = (String)js.executeScript(jsCommand);
				result.put(fieldName, expectedValue.equals(actualValue));
				break;
			case "Sv":
				// single select, by value
				jsCommand = "var e = document.getElementById('" + Common.removeOrderAndType(fieldName) + "');" +
							"var value = e.options[e.selectedIndex].value;" +
							"return value;";
				actualValue = (String)js.executeScript(jsCommand);
				result.put(fieldName, expectedValue.equals(actualValue));
				break;
			case "M":
				// or multiple select, by visible text
				jsCommand = "var ms = document.getElementById('" + Common.removeOrderAndType(fieldName) + "');" +
							"var textValues = [];" +
							"for (var i = 0; i < ms.length; i++) {" +
								"if (ms.options[i].selected) textValues.push(ms.options[i].text);" +
							"}" +
							"return textValues.toString();";
				actualValue = (String)js.executeScript(jsCommand);
				expectedSetOfValues = new HashSet<String>(Arrays.asList(expectedValue.split(",")));
				actualSetOfValues = new HashSet<String>(Arrays.asList(actualValue.split(",")));
				result.put(fieldName, expectedSetOfValues.equals(actualSetOfValues));
				break;
			case "Mv":
				// or multiple select, by value
				jsCommand = "var ms = document.getElementById('" + Common.removeOrderAndType(fieldName) + "');" +
							"var values = [];" +
							"for (var i = 0; i < ms.length; i++) {" +
								"if (ms.options[i].selected) values.push(ms.options[i].value);" +
							"}" +
							"return values.toString();";
				actualValue = (String)js.executeScript(jsCommand);
				expectedSetOfValues = new HashSet<String>(Arrays.asList(expectedValue.split(",")));
				actualSetOfValues = new HashSet<String>(Arrays.asList(actualValue.split(",")));
				result.put(fieldName, expectedSetOfValues.equals(actualSetOfValues));
				break;
			case "C":
				// checkbox
				jsCommand = "var checkboxes = document.getElementsByName('" + Common.removeOrderAndType(fieldName) + "');" +
							"var checkboxesChecked = [];" +
							"for (var i=0; i<checkboxes.length; i++) {" +
								"if (checkboxes[i].checked) checkboxesChecked.push(checkboxes[i].value);" +
							"}" +
							"return checkboxesChecked.length > 0 ? checkboxesChecked.toString() : ''";
				actualValue = (String)js.executeScript(jsCommand);
			
				expectedSetOfValues = new HashSet<String>(Arrays.asList(expectedValue.split(",")));
				actualSetOfValues = new HashSet<String>(Arrays.asList(actualValue.split(",")));
				result.put(fieldName, expectedSetOfValues.equals(actualSetOfValues));
				break;
			case "F":
				// upload file
				// result.put(fieldName, fieldValue.equals(element.getValue()));
				break;

			default:
				
			}
		}

		return result;
	}

}