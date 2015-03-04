package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

/**
 * Created by Anton on 25.07.2014.
 */
public class ViewDiscrepancyNotePage extends BasePage {

	public static final String PAGE_NAME = "View DN page";

    public ViewDiscrepancyNotePage (WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "resStatus41")
    private WebElementFacade bCloseNote;

    @FindBy(id = "selectCloseDescription1")
    private WebElementFacade ddlDescription;

    @FindBy(name = "detailedDes1")
    private WebElementFacade taDetailedNote;

    @FindBy(id = "resStatusId1")
    private WebElementFacade ddlSetToStatus;

    @FindBy(id = "userAccountId1")
    private WebElementFacade ddlAssignToUser;

    @FindBy(id = "box1")
    private WebElementFacade tblDNDetails;

    @FindBy(name = "SubmitExit1")
    private WebElementFacade bSubmitAndExit;


    public void clickCloseNoteBtn() {
        bCloseNote.click();
    }

    public void enterDNDetails(String description, String message, String status, String username) {
        if (tblDNDetails.isPresent()) {
            selectDNDescription(description);
            enterDetailedNote(message);
            selectSetToStatus(status);
        }
    }

    public void enterMandatoryDNDetails(String description, String status) {
        if (tblDNDetails.isPresent()) {
            selectDNDescription(description);
            selectSetToStatus(status);
        }
    }

    public void selectDNDescription(String description) {
        ddlDescription.selectByVisibleText(description);
    }

    public void enterDetailedNote(String message) {
        taDetailedNote.type(message);
    }

    public void selectSetToStatus(String status) {
        ddlSetToStatus.selectByVisibleText(status);
    }

    public void selectAssignToUser(String username) {
        ddlAssignToUser.selectByVisibleText(username);
    }

    public void clickSubmitAndExitBtn() {
        bSubmitAndExit.click();
    }
}
