package com.clinovo.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.webelements.Checkbox;
import net.thucydides.core.webelements.RadioButtonGroup;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.clinovo.pages.beans.CRF;
import com.clinovo.utils.Common;
import com.clinovo.utils.ItemsUtil;

public class CRFPage extends BasePage {
	public static final String PAGE_NAME = "CRF page";
	public static final String PAGE_URL = "InitialDataEntry/AdministrativeEditing";
	
	@FindBy(id = "mainForm")
    private WebElementFacade formWithData;
	
	@FindBy(name = "submittedResume")
    private WebElementFacade bSave;
	
	@FindBy(id = "markCompleteId")
    private WebElementFacade cMarkCRFComplete;
	
	// 'Mark CRF Complete' dialog 
	@FindBy(id = "confirmation")
    private WebElementFacade divConfirmation;
	
	@FindBy(id = "ignoreMarkCRFCompleteMSG")
    private WebElementFacade cIgnoreMarkCRFCompleteMSG;
	
	@FindBy(xpath = ".//*[@id='confirmation']//input[contains(@onclick, 'markCRFCompleteOk')]")
    private WebElementFacade bMarkCRFCompleteYes;
	
	public CRFPage(WebDriver driver) {
		super(driver);
	}
	
	@Override
	public boolean isOnPage(WebDriver driver) {
		return true;
	}

	public void fillInCRF(CRF crf) {
		List<String> names = new ArrayList<String>(crf.getFieldNameToValueMap().keySet());
		Collections.sort(names, CRF.comparatorForItemOIDs);
		for (String fieldName: names) {
			if (crf.getFieldNameToValueMap().get(fieldName).isEmpty()) continue;
			WebElementFacade element = formWithData.find(By.xpath(".//*[@name='"+Common.removeType(fieldName)+"']"));
			switch (Common.getType(fieldName)){
				case "T":
					// text (or date) field
					element.type(crf.getFieldNameToValueMap().get(fieldName));
					break;
				case "R":
					// radio button group
					RadioButtonGroup rBgroup = new RadioButtonGroup(formWithData.findElements(By.xpath(".//*[@name='"+Common.removeType(fieldName)+"']")));
					rBgroup.selectByValue(crf.getFieldNameToValueMap().get(fieldName));
					break;
				case "S": 
					// single or multiple select
					element.selectByVisibleText(crf.getFieldNameToValueMap().get(fieldName));
					break;
				case "C": 
					// checkbox
					ItemsUtil.fillCheckbox(new Checkbox(element), crf.getFieldNameToValueMap().get(fieldName));
					break;
				case "F": 
					// upload file 
					upload(crf.getFieldNameToValueMap().get(fieldName)).to(element);
					break;
				
				default: 
					element.type(crf.getFieldNameToValueMap().get(fieldName));
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

	public void clickSaveButton() {
		bSave.click();
	}
}
