package com.clinovo.pages;

import net.thucydides.core.annotations.DefaultUrl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import net.thucydides.core.pages.WebElementFacade;

@DefaultUrl("http://localhost:8080/clincapture")
//@DefaultUrl("https://cc-qa.clincapture.clinovo.com/")
public class LoginPage extends BasePage {
	
	@FindBy(id="username")
	private WebElementFacade login;
	
	@FindBy(id="j_password")
	private WebElementFacade password;
	
	@FindBy(name="submit")
	private WebElementFacade loginButton;

	public static final String PAGE_NAME = "Login page";
	
	public LoginPage(WebDriver driver) {
		super(driver);
	}
	
	public void enterLoginName(String loginName) {
        element(login).type(loginName);
	}
	
	public void enterPassword(String password) {
        element(this.password).type(password);
	}
	
	public void clickLoginBtn() {
		element(loginButton).click();
	}
	
	@Override
	public boolean isOnPage(WebDriver driver) {
    	return loginButton.isDisplayed() && login.isDisplayed() && password.isDisplayed();
	}
}
