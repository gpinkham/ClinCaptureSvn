package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

public class ManageEventDefinitionsPage extends BasePage {

	public static final String PAGE_NAME = "Manage All Event Definitions in Study page";
	public static final String PAGE_URL = "ListEventDefinition";
	
    @FindBy(name = "Create Event")
    private WebElementFacade bCreateEvent;

    public ManageEventDefinitionsPage (WebDriver driver) {
        super(driver);
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1) || bCreateEvent.isCurrentlyVisible();
	}
}
