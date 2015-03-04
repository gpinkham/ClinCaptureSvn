package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

public class ViewUserAccountPage extends BasePage {

	public static final String PAGE_NAME = "View User Account page";
	public static final String PAGE_URL = "ViewUserAccount";
	
	@FindBy(className = "tablebox_center")
    private WebElementFacade dTableWithData;
	
    public ViewUserAccountPage (WebDriver driver) {
        super(driver);
    }

    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf("ViewUserAccount") > -1 ||
    			(getDriver().getCurrentUrl().indexOf(CreateUserAccountPage.PAGE_URL) > -1) && dTableWithData.isCurrentlyEnabled());
	}

    public String getPasswordFromAlertsAndMessages() {
		String str = dAlert.getText();
		str = str.split(":")[1];
		str = str.trim().split("\n")[0];
		return str;
	}
}
