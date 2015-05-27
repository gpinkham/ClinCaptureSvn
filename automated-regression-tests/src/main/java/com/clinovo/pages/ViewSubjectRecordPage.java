package com.clinovo.pages;

import org.openqa.selenium.WebDriver;

public class ViewSubjectRecordPage extends BasePage {

	public static final String PAGE_NAME = "View Subject Record page";
	public static final String PAGE_URL = "ViewStudySubject";
	
    public ViewSubjectRecordPage (WebDriver driver) {
        super(driver);
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}
}
