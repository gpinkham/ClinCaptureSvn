package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static net.thucydides.core.matchers.BeanMatchers.the;
import static net.thucydides.core.pages.components.HtmlTable.filterRows;
import static org.hamcrest.Matchers.is;

/**
 * Created by Anton on 07.07.2014.
 */
public class ViewEventPage extends BasePage {

	public static final String PAGE_NAME = "View Event Def page";

    @FindBy(name = "bt_EnterData0")
    private WebElementFacade iEnterDataBioV1CRF;

    @FindBy(xpath = "/html/body/table/tbody/tr[1]/td/table[2]/tbody/tr/td[2]/div[3]/div/div/div/div/div/div/div/div/div/table")
    private WebElementFacade tblCRFs;

    @FindBy(css = ".first_level_header")
    private WebElementFacade textHeading;

    public ViewEventPage(WebDriver driver) {
        super(driver);
    }

    public void clickOnEnterDataBioV1Icon() {
        iEnterDataBioV1CRF.click();
    }

    public boolean isCRFStatusIconDEStarted(String crfName, String status) {
        List<WebElement> row = filterRows(tblCRFs, the("CRF Name", is(crfName)));

        return row.get(0).findElement(By.cssSelector("img")).getAttribute("alt").trim().equalsIgnoreCase(status);
    }

    public boolean isOnPage(WebDriver driver) {
        return textHeading.isDisplayed() && tblCRFs.isDisplayed();
    }
}
