package com.clinovo.pages;

import org.openqa.selenium.WebDriver;

/**
 * Created by Anton on 04.06.2014.
 */
public class HomePage extends BasePage {

	public static final String PAGE_NAME = "Home page";
	public static final String PAGE_URL = "MainMenu";
	
    public HomePage (WebDriver driver) {
        super(driver);
    }

    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1) ||
    			((getDriver().getCurrentUrl().indexOf(ResetPasswordPage.PAGE_URL) > -1) && (taskMenuIsVisible()));
	}
}
