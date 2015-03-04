package com.clinovo.pages;

import org.openqa.selenium.WebDriver;

public class DefineStudyEventSelectedCRFsPage extends BasePage {

	public static final String PAGE_NAME = "Define Study Event - Selected CRF(s) page";
	public static final String PAGE_URL = "DefineStudyEvent";
	
    public DefineStudyEventSelectedCRFsPage (WebDriver driver) {
        super(driver);
    }

    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().endsWith(PAGE_URL));
	}
}
