package com.clinovo.pages.crfs;

import com.clinovo.pages.BasePage;
import com.clinovo.pages.MarkCRFCompleteDialogPage;
import com.clinovo.utils.ClinCaptureDate;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by Anton on 21.07.2014.
 */
public class AdverseEventsV1Page extends BasePage {

    public AdverseEventsV1Page (WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "showMoreInfo")
    private WebElementFacade lMoreInfo;

    @FindBy(name = "interviewer")
    private WebElementFacade eInterviewerName;

    @FindBy(id = "interviewDate")
    private WebElementFacade eInterviewDate;

    @FindBy(id = "markCompleteId")
    private WebElementFacade cbMarkCompleted;

    @FindBy(id = "SE_DAT_OD")
    private WebElementFacade parentOnsetDate;

    @FindBy(id = "srl")
    private WebElementFacade bSave;

    @FindBy(id = "seh")
    private WebElementFacade bExit;

    private MarkCRFCompleteDialogPage confirmationDialog = new MarkCRFCompleteDialogPage(getDriver());


    public void fillInCRFData() {
        lMoreInfo.click();
        eInterviewerName.type("Joe Doe");
        eInterviewDate.type(ClinCaptureDate.getTodayDateInCCFormat());
        enterOnsetDate(ClinCaptureDate.getTodayDateInCCFormat());
    }

    public void markCRFComplete() {
        cbMarkCompleted.click();
        confirmationDialog.yes();
    }

    public void clickSaveBtn() {
        bSave.click();
    }

    public void clickExitBtn() {
        bExit.click();
    }

    private void enterOnsetDate(String date) {
        parentOnsetDate.findBy(By.tagName("input")).type(date);
    }
}
