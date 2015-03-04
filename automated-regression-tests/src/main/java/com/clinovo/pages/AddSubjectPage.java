package com.clinovo.pages;

import com.clinovo.utils.ClinCaptureDate;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

/**
 * Created by Anton on 01.07.2014.
 */
public class AddSubjectPage extends BasePage {

    @FindBy(name = "dynamicGroupClassId")
    private WebElementFacade ddlDynamicGroupClassId;

    @FindBy(name = "submitDone")
    private WebElementFacade bSubmit;

    @FindBy(name = "StartDataEntry")
    private WebElementFacade bStartDataEntry;

    @FindBy(name = "submitEvent")
    private WebElementFacade bScheduleEvent;

    @FindBy(xpath = "/html/body/table/tbody/tr[1]/td/table[2]/tbody/tr/td[2]/form/div[1]/div/div/div/div/div/div/div/div/div/table/tbody/tr[1]/td[2]/table/tbody/tr[1]/td[1]/div/input[1]")
    private WebElementFacade eSubjectID;

    @FindBy(name = "uniqueIdentifier")
    private WebElementFacade ePersonID;

    @FindBy(name = "gender")
    private WebElementFacade ddlSex;

    @FindBy(id = "dobField")
    private WebElementFacade eDOB;

    
    public static final String PAGE_NAME = "Add New Subject page";
    
    public AddSubjectPage (WebDriver driver) {
        super(driver);
    }

    public void selectDynamicGroup(String dynamicGroup) {
        ddlDynamicGroupClassId.selectByVisibleText(dynamicGroup);
    }

    public void enterPersonID(String personID) {
        ePersonID.type(personID);
    }

    public void selectSex(String sex) {
        ddlSex.selectByVisibleText(sex);
    }

    // dob parameter should be in the following format: dd-mm-yyyy (e.g. 01-01-1979)
    public void enterDOB(String dob) {
        eDOB.type(ClinCaptureDate.getDateInCCFormat(dob));
    }

    public void clickOnScheduleEventButton() {
        bScheduleEvent.click();
    }

    public void saveSubjectID() {
        setCurrSubjectID(eSubjectID.getAttribute("value"));
    }

    public void clickSubmitBtn() {
        bSubmit.click();
    }
}
