package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

public class AdministerUsersPage extends BasePage {

	public static final String PAGE_NAME = "Administer Users page";
	public static final String PAGE_URL = "ListUserAccounts";
	
    @FindBy(id = "CreateUser")
    private WebElementFacade bCreateUser;

    public AdministerUsersPage (WebDriver driver) {
        super(driver);
    }

    public void clickCreateUserButton() {
    	bCreateUser.click();
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return bCreateUser.isDisplayed();
	}
}
