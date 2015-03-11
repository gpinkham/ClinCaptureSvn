package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

public class ConfirmChangeStudyPage extends BasePage {

	public static final String PAGE_NAME = "Confirm Changing Your Current Study/Site ";
	public static final String PAGE_URL = "ChangeStudy";

	@FindBy(name = "Submit")
    private WebElementFacade bSubmit;
	
    public ConfirmChangeStudyPage (WebDriver driver) {
        super(driver);
    }
    
    @Override
	public void clickSubmit() {
    	bSubmit.click();
	}
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1) && (bSubmit.isCurrentlyVisible());
	}
}
