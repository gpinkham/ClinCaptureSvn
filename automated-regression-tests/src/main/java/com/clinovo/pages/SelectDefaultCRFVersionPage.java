package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

/**
 * Created by Anton on 18.07.2014.
 */
public class SelectDefaultCRFVersionPage extends BasePage {

	public static final String PAGE_NAME = "Select Default CRF Version page";

    public SelectDefaultCRFVersionPage (WebDriver driver) {
        super(driver);
    }

    @FindBy(name = "Submit")
    private WebElementFacade bContinue;

    public void clickContinueBtn() {
        bContinue.click();
    }
}
