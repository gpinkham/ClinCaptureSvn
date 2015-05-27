package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

import com.clinovo.pages.beans.User;

public class SignStudyEventPage extends BasePage {

	public static final String PAGE_NAME = "Sign Study Event page";
	public static final String PAGE_URL = "UpdateStudyEvent";
	
    public SignStudyEventPage (WebDriver driver) {
        super(driver);
    }
    @FindBy(jquery = "form[action='UpdateStudyEvent']")
    private WebElementFacade formWithData;
    
    @FindBy(xpath = ".//form//input[@name='j_user']")
    private WebElementFacade iUserName;
    
    @FindBy(xpath = ".//form//input[@name='j_pass']")
    private WebElementFacade iPassword;
    
    @FindBy(name = "BTN_Sign")
    private WebElementFacade bSign;
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}
    
    public void enterCredentials(User user) {
		iUserName.type(user.getUserName());
		iPassword.type(user.getPassword());
	}
    
	public void clickSignButton() {
		bSign.click();
	}
}
