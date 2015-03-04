package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

/**
 * Created by Anton on 17.07.2014.
 */
public class CreateEventDefinitionPage extends BasePage {

	public static final String PAGE_NAME = "Create Event Def page";
	
    public CreateEventDefinitionPage (WebDriver driver) {
        super(driver);
    }

    @FindBy(name = "name")
    private WebElementFacade eEventDefinitionName;

    @FindBy(name = "description")
    private WebElementFacade eEventDefinitionDesc;

    @FindBy(name = "type")
    private WebElementFacade ddlEventDefinitionType;

    @FindBy(name = "Submit")
    private WebElementFacade bContinue;

    @FindBy(name = "isReference")
    private WebElementFacade cbReferenceEvent;

    @FindBy(name = "schDay")
    private WebElementFacade eDaySchedule;

    @FindBy(name = "maxDay")
    private WebElementFacade eDayMax;

    @FindBy(name = "minDay")
    private WebElementFacade eDayMin;

    @FindBy(name = "emailDay")
    private WebElementFacade eDayEmail;

    @FindBy(name = "emailUser")
    private WebElementFacade eUserName;


    public void enterEventDefinitionName(String eventDefinitionName) {
        eEventDefinitionName.type(eventDefinitionName);
    }

    public void enterEventDefinitionDesc(String eventDefinitionDesc) {
        eEventDefinitionDesc.type(eventDefinitionDesc);
    }

    public void selectEventDefinitionType(String eventDefinitionType) {
        ddlEventDefinitionType.selectByVisibleText(eventDefinitionType);
    }

    public void enterEventDefinitionDetails(String eventDefinitionName, String eventDefinitionDesc, String eventDefinitionType) {
        enterEventDefinitionName(eventDefinitionName);
        enterEventDefinitionDesc(eventDefinitionDesc);
        selectEventDefinitionType(eventDefinitionType);
    }

    public void tickReferenceEventChkBox() {
        cbReferenceEvent.click();
    }

    public void enterCalendaredEventDetails(String daySchedule, String dayMax, String dayMin, String dayEmail, String username) {
        if (!ddlEventDefinitionType.getText().equals("Calendared")) {
            System.out.println("[ERROR]: You're trying to enter Calendared Event details for NOT Calendared event. Please check 'Type' field!");
        }

        eDaySchedule.type(daySchedule);
        eDayMax.type(dayMax);
        eDayMin.type(dayMin);
        eDayEmail.type(dayEmail);
        eUserName.type(username);
    }

    public void clickContinueBtn() {
        bContinue.click();
    }
}
