package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

/**
 * 
 */
public class BuildStudyPage extends BasePage {

	public static final String PAGE_NAME = "Build Study page";
	public static final String PAGE_URL = "studymodule";
	
    public BuildStudyPage (WebDriver driver) {
        super(driver);
    }

    @FindBy(jquery = ".contenttable a[href*=DefineStudyEvent]")
    private WebElementFacade lCreateEventDefinition;
    
    @FindBy(jquery = "a[href*='UpdateStudyNew']")
    private WebElementFacade bUpdateStudy;
    
    @FindBy(jquery = "a[href$='CreateSubStudy']")
    private WebElementFacade bCreateSites;
    
    @FindBy(jquery = "a[href$='CreateCRFVersion']")
    private WebElementFacade bCreateCRFVersion;
    
    @FindBy(jquery = "a[href$='DefineStudyEvent?actionName=init']")
    private WebElementFacade bCreateStudyEventDefinition;

    @FindBy(jquery = "a[href$='ListEventDefinition']")
    private WebElementFacade bViewStudyEventDefinitions;
    
    @FindBy(name = "studyStatus")
    private WebElementFacade sStudyStatus;
    
    @FindBy(name = "saveStudyStatus")
    private WebElementFacade bSaveStudyStatus;

    
    public void setStudyStatus(String value) {
    	sStudyStatus.selectByValue(value);
    }
    
    public void clickCreateEventDefinition() {
        lCreateEventDefinition.click();
    }
    
    public void clickAddSite() {
    	bCreateSites.click();
    }
    
    public void clickAddCRF() {
    	bCreateCRFVersion.click();
    }
    
    public void clickAddStudyEventDefinition() {
    	bCreateStudyEventDefinition.click();
    }
    
    public void clickViewStudyEventDefinitions() {
    	bViewStudyEventDefinitions.click();
    }
    
    public void clickUpdateStudy() {
    	bUpdateStudy.click();
    }
    
    public void clickSaveStudyStatusButton() {
    	bSaveStudyStatus.click();
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}
    
    public static String convertStatusNameToStatusValue(String status) {
    	String statusValue;
    	switch (status.replaceAll("'", "")){
			case "Design":
				statusValue = "4";
				break;
			case "Available": 
				statusValue = "1";
				break;
			case "Frozen": 
				statusValue = "9";
				break;
			case "Locked": 
				statusValue = "6";
				break;
			default: 
				statusValue = "1";
    	}
    	
		return statusValue;		 
    }
}
