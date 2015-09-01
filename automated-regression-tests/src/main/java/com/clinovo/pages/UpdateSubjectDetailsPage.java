package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

/**
 * Created by Igor.
 */
public class UpdateSubjectDetailsPage extends BasePage {

	public static final String PAGE_NAME = "Update Subject Details page";
	public static final String PAGE_URL = "UpdateSubject";
	
	@FindBy(name = "flag_uniqueIdentifier")
    private WebElementFacade lPersonIDFlag;
	
	@FindBy(name = "flag_gender")
    private WebElementFacade lGenderFlag;
	
	@FindBy(name = "flag_dob")
    private WebElementFacade lDOBFlag;
	
    public UpdateSubjectDetailsPage (WebDriver driver) {
        super(driver);
    }

    @Override
	public boolean isOnPage(WebDriver driver) {
    	return driver.getCurrentUrl().indexOf(PAGE_URL) > -1;
	}
    
    public void clickPersonIDFlag() {
    	lPersonIDFlag.click();
	}
    
    public void clickGenderFlag() {
    	lGenderFlag.click();
	}
    
    public void clickDOBFlag() {
    	lDOBFlag.click();
	}
}