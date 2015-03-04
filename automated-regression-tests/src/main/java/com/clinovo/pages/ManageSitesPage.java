package com.clinovo.pages;

import org.openqa.selenium.WebDriver;

public class ManageSitesPage extends BasePage {

	public static final String PAGE_NAME = "Manage Sites page";
	public static final String PAGE_URL = "ListSite";
	
    public ManageSitesPage (WebDriver driver) {
        super(driver);
    }

    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}
}