package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

/**
 * Created by Anton on 01.07.2014.
 */
public class ScheduleEventPage extends BasePage {

	public static final String PAGE_NAME = "Schedule Event page";

    @FindBy(name = "studyEventDefinition")
    private WebElementFacade ddlStudyEventDefinition;

    @FindBy(name = "Schedule")
    private WebElementFacade bScheduleEvent;

    public ScheduleEventPage (WebDriver driver) {
        super(driver);
    }

    public void selectStudyDefinition(String studyDefinition) {
        ddlStudyEventDefinition.selectByVisibleText(studyDefinition);
    }

    public void clickOnScheduleEventButton() {
        bScheduleEvent.click();
    }
}
