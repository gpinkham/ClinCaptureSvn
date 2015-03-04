package com.clinovo.pages;

import org.openqa.selenium.WebDriver;

public class CreateCRFDataCommitedPage extends BasePage {

	public static final String PAGE_NAME = "Create a New CRF Version - Data Committed Successfully page";
	public static final String PAGE_URL = "CreateCRFVersion?action=confirmsql&";
	
    public CreateCRFDataCommitedPage (WebDriver driver) {
        super(driver);
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}
}