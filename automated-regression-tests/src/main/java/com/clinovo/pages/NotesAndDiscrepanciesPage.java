package com.clinovo.pages;

import net.thucydides.core.annotations.findby.By;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Anton on 23.07.2014.
 */
public class NotesAndDiscrepanciesPage extends BasePage {

	public static final String PAGE_NAME = "N&Ds page";
	
    public NotesAndDiscrepanciesPage (WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "listNotes")
    private WebElementFacade tblDNList;

    @FindBy(id = "listNotes_row1")
    private WebElementFacade row;


    public boolean checkDNDetails(String subjectID, String resolutionStatus, String eventName, String description) {
        //TODO amend checking from IFs and returning of FALSE to smth that will provide more info on what point failed exactly

        Map<String, String> dnTbl = getHeaderAndRowCellsText();

        if (!dnTbl.get("Study Subject ID").equals(subjectID))
            return false;

        if (!dnTbl.get("Resolution Status").equals(resolutionStatus))
            return false;

        if (!dnTbl.get("Event Name").equals(eventName))
            return false;

        return dnTbl.get("Description").contains(description);

    }

    private Map<String, String> getHeaderAndRowCellsText() {
        Map<String, String> result = new HashMap<>();
        List<String> header = getHeaderCellsText();
        List<String> row = getRowCellsText();
        String rowCell;

        if (header.size() != row.size())
            return null;

        int index = 0;
        for (String headerCell : header) {
            rowCell = row.get(index++);
            if (!(headerCell.isEmpty() && rowCell.isEmpty()))
                result.put(headerCell, rowCell);
        }

        return result;
    }

    private List<String> getHeaderCellsText() {
        List<String> textHeadings = new ArrayList<>();

        WebElementFacade header = tblDNList.findBy(By.className("header"));
        List<WebElement> headerCells = header.findElements(By.xpath("./td"));

        for (WebElement cell: headerCells) {
            textHeadings.add(cell.getText().trim());
        }

        return textHeadings;
    }

    private List<String> getRowCellsText() {
        List<String> textRowElements = new ArrayList<>();

        List<WebElement> rowCells = row.findElements(By.xpath("./td"));

        for (WebElement cell: rowCells) {
            textRowElements.add(cell.getText().trim());
        }

        return textRowElements;
    }

    public void clickViewDNIcon() {
        WebElementFacade iViewDN = row.findBy(By.xpath("//img[@alt='View']"));
        iViewDN.click();
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return tblDNList.isDisplayed();
	}
}
