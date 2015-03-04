package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

/**
 * Created by Anton on 18.07.2014.
 */
public class ConfirmEventDefinitionCreationPage extends BasePage {

	public static final String PAGE_NAME = "Confirm Event Def Creation page";
	public static final String PAGE_URL = "DefineStudyEvent";
	
   
    public ConfirmEventDefinitionCreationPage (WebDriver driver) {
        super(driver);
    }

    @FindBy(name = "submit")
    private WebElementFacade bSubmit;

    public void clickSubmitBtn() {
        bSubmit.click();
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().endsWith(PAGE_URL));
	}
} 
