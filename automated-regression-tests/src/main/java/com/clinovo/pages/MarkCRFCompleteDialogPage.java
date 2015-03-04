package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by Anton on 22.07.2014.
 */
public class MarkCRFCompleteDialogPage extends BasePage {

	public static final String PAGE_NAME = "Mark CRF Complete page";

    public MarkCRFCompleteDialogPage (WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "confirmation")
    private WebElementFacade confirmationDialog;

    @FindBy(xpath = "//div[@id='confirmation']//input[@value='Yes']")
    private WebElementFacade bYes;

    public void yes() {
        bYes.click();
    }

    public void no() {
        confirmationDialog.findBy(By.xpath(".//input[@value='No']")).click();
    }
}
