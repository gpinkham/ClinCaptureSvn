package com.clinovo.pages;

import java.util.Map;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

public class SDVPage extends BasePage {

	public static final String PAGE_NAME = "SDV page";
	public static final String PAGE_URL = "viewAllSubjectSDVtmp";
	
    public SDVPage (WebDriver driver) {
        super(driver);
    }
    
    @FindBy(id = "subjectSDV")
    private WebElementFacade divSubjectSDV;
    
    // filters
    @FindBy(xpath = ".//div[contains(@onclick, 'studySubjectId')][@class='dynFilter']")
    private WebElementFacade divFilterBySSubjectId;
    
    @FindBy(xpath = ".//div[contains(@onclick, 'eventName')][@class='dynFilter']")
    private WebElementFacade divFilterByEventName;
    
    @FindBy(xpath = ".//div[contains(@onclick, 'crfNameVersion')][@class='dynFilter']")
    private WebElementFacade divFilterByCRFName;
    
    @FindBy(id = "dynFilterInput")
    private WebElementFacade iFilterField;
    
    @FindBy(id = "dynFilterDroplist")
    private WebElementFacade sFilterField;
    
    @FindBy(xpath = ".//tr[@class='filter']//a[contains(@href,\"onInvokeAction('sdv','filter')\")]")
    private WebElementFacade lApplyFilter;
    
    @FindBy(xpath = ".//tr[@class='filter']//a[contains(@href,\"onInvokeAction('sdv','clear')\")]")
    private WebElementFacade lClearFilter;

	// for first row
    @FindBy(xpath = ".//*[@id='sdv_row1']//input[contains(@src, 'icon_DoubleCheck_Action.gif')]")
    private WebElementFacade bPerformSDV;

    @FindBy(xpath = ".//*[@id='sdv_row1']//input[@class='sdvCheck']")
    private WebElementFacade cSDVStatus;
    
    @FindBy(xpath = ".//*[@id='sdv_row1']//img[contains(@src, 'icon_DoubleCheck.gif')]")
    private WebElementFacade iconCRFSDVed;
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return divSubjectSDV.isCurrentlyVisible();
	}
    
    public void filterSDVByStudySubjectID(String sSubjectID) {
    	enterStudySubjectIDToFilterField(sSubjectID);
        clickApplyFilterLink();
        
    }

    public void enterStudySubjectIDToFilterField(String sSubjectID) {
    	divFilterBySSubjectId.click();
    	iFilterField.type(sSubjectID);
    }

    public void clickApplyFilterLink() {
        lApplyFilter.click();
    }
    
    public void clickClearFilterLink() {
        lClearFilter.click();
    }

	public void clickPerformSDVButtonForFilteredTable() {
		bPerformSDV.click();
	}

	public void fillFiltersOnSDVPage(Map<String, String> map) {
		if (map.containsKey("Study Subject ID")) {
			divFilterBySSubjectId.click();
			iFilterField.type(map.get("Study Subject ID"));
		}
		
		if (map.containsKey("Event Name")) {
			divFilterByEventName.click();
			sFilterField.selectByValue(map.get("Event Name"));
		}
		
		if (map.containsKey("CRF Name")) {
			divFilterByCRFName.click();
			sFilterField.selectByValue(map.get("CRF Name"));
		}
		
		clickApplyFilterLink();
	}

	public void checkSDVIcon() {
		iconCRFSDVed.isCurrentlyVisible();
	}
}
