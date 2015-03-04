package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

public class ConfirmSystemPropertiesPage extends BasePage {

	public static final String PAGE_NAME = "Confirm System Properties page";
	public static final String PAGE_URL = "pages/system";
	
	@FindBy(id = "systemConfirmationTable")
    private WebElementFacade tSystemConfirmation;
	
	@FindBy(id = "confirm")
    private WebElementFacade bSubmit;
	
    public ConfirmSystemPropertiesPage (WebDriver driver) {
        super(driver);
    }

    public void clickSubmit() {
    	bSubmit.click();
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1) 
    			&& (tSystemConfirmation.isCurrentlyVisible());
	}
}