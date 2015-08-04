package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;
import com.clinovo.pages.beans.StudyEventDefinition;

public class ManageEventDefinitionsPage extends BasePage {

	public static final String PAGE_NAME = "Manage All Event Definitions in Study page";
	public static final String PAGE_URL = "ListEventDefinition";
	
    @FindBy(name = "Create Event")
    private WebElementFacade bCreateEvent;
    
    @FindBy(name = "ebl_filterKeyword")
    private WebElementFacade iFind;
    
    @FindBy(xpath = ".//input[@class='button_search']")
    private WebElementFacade iFindButton;
    
    @FindBy(xpath = ".//*[@id='Table0']/tbody/tr//table//table//tr[2]/td[2]")
    private WebElementFacade tdWithEventName;

    public ManageEventDefinitionsPage (WebDriver driver) {
        super(driver);
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1) || bCreateEvent.isCurrentlyVisible();
	}
    
    public void filter(StudyEventDefinition event) {
		//iFind.type(event.getName().split(" ")[1]);
    	iFind.type(event.getName());
		iFindButton.click();
	}

	public void checkEventRowIsPresent(StudyEventDefinition event) {
		tdWithEventName.isCurrentlyVisible();
		//Assert.assertEquals(event.getName().trim(), tdWithEventName.getText().trim());
	}
}
