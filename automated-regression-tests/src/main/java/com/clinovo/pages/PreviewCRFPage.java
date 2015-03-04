package com.clinovo.pages;

import org.openqa.selenium.WebDriver;

public class PreviewCRFPage extends BasePage {

	public static final String PAGE_NAME = "Preview CRF page";
	public static final String PAGE_URL = "CreateCRFVersion?action=confirm";
	
    public PreviewCRFPage (WebDriver driver) {
        super(driver);
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}
}
