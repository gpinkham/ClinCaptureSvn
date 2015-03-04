package com.clinovo.pages.crfs;

import com.clinovo.pages.BasePage;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import org.openqa.selenium.WebDriver;

/**
 * Created by Anton on 07.07.2014.
 */
public class BiochemistryV1Page extends BasePage {

    @FindBy(xpath = "(//input[@id='input17'])[2]")
    private WebElementFacade rbBiochemistryResultsPerformedNo;

    @FindBy(xpath = "//td[2]/table/tbody/tr/td[3]/div/input")
    private WebElementFacade eLabID;

    @FindBy(id = "markCompleteId")
    private WebElementFacade cbMarkCRFComplete;

    @FindBy(id = "srh")
    private WebElementFacade bSave;

    public BiochemistryV1Page(WebDriver driver) {
        super(driver);
    }

    public void tickBioResultsPerformedNo() {
        rbBiochemistryResultsPerformedNo.click();
    }

    public void enterLabID(String labID) {
        eLabID.type(labID);
    }

    public void markCRFComplete() {
        cbMarkCRFComplete.click();
    }

    public void clickSaveBtn() {
        bSave.click();
    }
}
