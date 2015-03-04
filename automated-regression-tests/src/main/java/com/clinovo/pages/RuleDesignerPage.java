package com.clinovo.pages;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static net.thucydides.core.matchers.BeanMatchers.the;
import static org.hamcrest.Matchers.is;
import net.thucydides.core.pages.components.HtmlTable;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

/**
 * Created by Anton on 11.05.2014.
 */
public class RuleDesignerPage extends BasePage {

	public static final String PAGE_NAME = "Rule Designer page";

    private static WebElement expressionArea;

    @FindBy(css = "input.input-sm")
    private WebElementFacade eTextDataInput;

    // Tiles

    @FindBy(xpath = "/html/body/div/div[2]/div/div[2]/div/div[2]/p[1]")
    private WebElementFacade tileLessThan;

    @FindBy(xpath = "/html/body/div/div[6]/div/div[2]/div/div[2]/p[1]")
    private WebElementFacade tileCurrentDate;

    // Tabs

    @FindBy(id = "studiesLink")
    private WebElementFacade tabStudies;

    @FindBy(id = "eventsLink")
    private WebElementFacade tabEvents;

    @FindBy(id = "crfsLink")
    private WebElementFacade tabCRFs;

    @FindBy(id = "versionsLink")
    private WebElementFacade tabCRFVersions;

    @FindBy(id = "itemsLink")
    private WebElementFacade tabItems;

    // Tabs' Tables

    @FindBy(id = "studies")
    private WebElement tblStudies;

    @FindBy(id = "events")
    private WebElement tblEvents;

    @FindBy(id = "crfs")
    private WebElement tblCRFs;

    @FindBy(id = "versions")
    private WebElement tblCRFVersions;

    @FindBy(id = "items")
    private WebElement tblItems;

    // Rule Details

    @FindBy(id="ruleName")
    private WebElementFacade eRuleName;

    @FindBy(id = "evaluateTrue")
    private WebElementFacade rbEvaluateTrue;

    @FindBy(id = "evaluateFalse")
    private WebElementFacade rbEvaluateFalse;

    @FindBy(id = "ide")
    private WebElementFacade cbIDE;

    @FindBy(id = "ae")
    private WebElementFacade cbAE;

    @FindBy(id="dde")
    private WebElementFacade cbDDE;

    @FindBy(id = "dataimport")
    private WebElementFacade cbDI;

    @FindBy(xpath = "//input[@name='action'][@action='discrepancy']")
    private WebElementFacade rbCreateDiscrepancy;

    @FindBy(css = "textarea.form-control.input-sm")
    private WebElementFacade eDiscrepancyText;

    


    // Constructor

    public RuleDesignerPage(WebDriver driver) {
        super(driver);
    }


    public boolean is_text_data_available() {
        return eTextDataInput.isCurrentlyEnabled();
    }

    @SuppressWarnings("deprecation")
	public void verify_studies_tab() {
        // check that Studies tab is active
        assertEquals(tabStudies.getAttribute("class").toLowerCase(), "active");

        // check that Studies table has some rows
        assertFalse("There are no studies in Studies tab [Rule Studio]", new HtmlTable(tblStudies).getRowElements().isEmpty());
    }

    public void click_on_study(String studyName) { clickItemInTab(tblStudies, studyName); }

    @SuppressWarnings("deprecation")
	public void verify_events_tab() {
        // check that Events tab is active
        assertEquals(tabEvents.getAttribute("class").toLowerCase(), "active");

        // check that Events table has some rows
        assertFalse("There are no events in Events tab [Rule Studio]", new HtmlTable(tblEvents).getRowElements().isEmpty());
    }

    public void click_on_event(String eventName) { clickItemInTab(tblEvents, eventName); }

    @SuppressWarnings("deprecation")
	public void verify_crfs_tab() {
        // check that CRFs tab is active
        assertEquals(tabCRFs.getAttribute("class").toLowerCase(), "active");

        // check that CRFs table has some rows
        assertFalse("There are no events in CRFs tab [Rule Studio]", new HtmlTable(tblCRFs).getRowElements().isEmpty());
    }

    public void click_on_crf(String crfName) { clickItemInTab(tblCRFs, crfName); }

    @SuppressWarnings("deprecation")
	public void verify_crf_versions_tab() {
        // check that CRF Versions tab is active
        assertEquals(tabCRFVersions.getAttribute("class").toLowerCase(), "active");

        // check that CRF Versions table has some rows
        assertFalse("There are no events in CRF Versions tab [Rule Studio]", new HtmlTable(tblCRFVersions).getRowElements().isEmpty());
    }

    public void click_on_crf_version(String crfVersion) { clickItemInTab(tblCRFVersions, crfVersion); }

    @SuppressWarnings("deprecation")
	public void verify_items_tab() {
        // check that Items tab is active
        assertEquals(tabItems.getAttribute("class").toLowerCase(), "active");

        // check that Items table has some rows
        assertFalse("There are no events in CRF Versions tab [Rule Studio]", new HtmlTable(tblItems).getRowElements().isEmpty());
    }

    public void drag_n_drop_from_items_to_expression_area(String...itemNames) {
        for (int i = 0; i < itemNames.length; i++){
            switch (itemNames[i]) {
                case "<":
                    expressionArea = getDriver().findElement(By.xpath("//div[@id='designSurface']/div/div/div[" + (i+2) + "]"));
                    dragAndDropItem(tileLessThan, expressionArea);
                    break;
                case "Current date":
                    expressionArea = getDriver().findElement(By.xpath("//div[@id='designSurface']/div/div/div[" + (i+2) + "]"));
                    dragAndDropItem(tileCurrentDate, expressionArea);
                    break;
                case "dtmStrokeOnset":
                    expressionArea = getDriver().findElement(By.xpath("//div[@id='designSurface']/div/div/div[" + (i+2) + "]"));
                    dragAndDropItemWithTable(tblItems, itemNames[i], expressionArea);
                    break;
                case "DMSTDAT":
                    expressionArea = getDriver().findElement(By.xpath("//div[@id='designSurface']/div/div/div[" + (i+2) + "]"));
                    dragAndDropItemWithTable(tblItems, itemNames[i], expressionArea);
                    break;
            }
        }
    }

    public void dragAndDropItemTargetTo(String itemName) {
//        dragAndDropItemWithTable(tblItems, itemName, targetToArea);
        HtmlTable tempTable = new HtmlTable(tblItems);
        List<WebElement> matchingRows = tempTable.filterRows(the("Name", is(itemName)));
        WebElement targetElement = matchingRows.get(0);
        new Actions(getDriver()).dragAndDropBy(targetElement, -1200, 135).perform();
    }

    public void enterRuleName(String ruleName) {
        eRuleName.sendKeys(ruleName);
    }

    public void checkEvaluatesToRB(String evaluatesTo) {
        if (evaluatesTo.toLowerCase().equals("true"))
            rbEvaluateTrue.click();
        else if (evaluatesTo.toLowerCase().equals("false"))
            rbEvaluateFalse.click();
    }


    public void checkExecuteUpons(String[] executeUpons) {
        for (String executeUpon : executeUpons) {
            switch (executeUpon) {
                case "Initial data entry":
                    cbIDE.click();
                    break;
                case "Administrative editing":
                    cbAE.click();
                    break;
                case "Double data entry":
                    cbDDE.click();
                    break;
                case "Data import":
                    cbDI.click();
                    break;
            }
        }
    }

    public void checkActionsRB(String action) {
        switch (action) {
            case "Create discrepancy":
                rbCreateDiscrepancy.click();
        }
    }

    public void enterDiscrepancyText(String discrepancyText) {
        if (eDiscrepancyText.isCurrentlyEnabled())
            eDiscrepancyText.sendKeys(discrepancyText);
    }

    private void clickItemInTab(WebElement tabTable, String itemName) {
        HtmlTable tempTable = new HtmlTable(tabTable);
        List<WebElement> matchingRows = tempTable.filterRows(the("Name", is(itemName)));
        WebElement targetRow = matchingRows.get(0);
        targetRow.click();
    }

    private void dragAndDropItemWithTable(WebElement fromTable, String itemName, WebElement destination) {
        HtmlTable tempTable = new HtmlTable(fromTable);
        List<WebElement> matchingRows = tempTable.filterRows(the("Name", is(itemName)));
        WebElement targetElement = matchingRows.get(0);
        dragAndDropItem(targetElement, destination);
    }

    private void dragAndDropItem(WebElement item, WebElement destination) {
        (new Actions(getDriver())).dragAndDrop(item, destination).perform();
    }
}
