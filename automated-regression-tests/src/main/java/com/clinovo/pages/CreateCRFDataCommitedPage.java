package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

public class CreateCRFDataCommitedPage extends BasePage {

	public static final String PAGE_NAME = "Create a New CRF Version - Data Committed Successfully page";
	public static final String PAGE_URL = "CreateCRFVersion?action=confirmsql&";
	
    @FindBy(id = "GoToCRFList")
    private WebElementFacade iExit;
    
    @FindBy(id = "ViewCRF")
    private WebElementFacade iViewCRF;
    
    @FindBy(id = "ViewCRFMetadata")
    private WebElementFacade iViewCRFMetadata;
	
    public CreateCRFDataCommitedPage (WebDriver driver) {
        super(driver);
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}

	public void clickExitButton() {
		iExit.click();
	}

	public void clickViewCRFButton() {
		iViewCRF.click();
	}

	public void clickViewCRFMetadataButton() {
		iViewCRFMetadata.click();
	}
	
	
}