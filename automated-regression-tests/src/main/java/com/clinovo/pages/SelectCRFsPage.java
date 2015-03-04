package com.clinovo.pages;

import net.thucydides.core.annotations.findby.By;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.pages.components.HtmlTable;

import org.openqa.selenium.WebDriver;

import static net.thucydides.core.matchers.BeanMatchers.the;
import static org.hamcrest.Matchers.is;

/**
 * Created by Anton on 17.07.2014.
 */
public class SelectCRFsPage extends BasePage {

	public static final String PAGE_NAME = "Select CRFs page";

    public SelectCRFsPage (WebDriver driver) {
        super(driver);
    }

    @FindBy(name = "Submit")
    private WebElementFacade bContinue;

    @FindBy(jquery = ".tablebox_center>table>tbody>tr:nth-child(2)>td>table")
    private WebElementFacade tblCRFs;


    public void selectCRF(String[] crfNames) {
        for (String crfName : crfNames)
            new HtmlTable(tblCRFs).findFirstRowWhere(the("CRF Name", is(crfName))).findElement(By.tagName("input")).click();

        System.out.println();
    }

    public void clickContinueBtn() {
        bContinue.click();
    }
}
