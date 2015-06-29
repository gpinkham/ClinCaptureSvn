package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.clinovo.pages.beans.DNote;

import java.util.Map;

/**
 * Created by Anton on 23.07.2014.
 */
public class NotesAndDiscrepanciesPage extends BasePage {

	public static final String PAGE_NAME = "N&Ds page";
	
    public NotesAndDiscrepanciesPage (WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "dnform")
    private WebElementFacade formWithData;
    
    @FindBy(id = "listNotes")
    private WebElementFacade tDNList;

    @FindBy(id = "listNotes_row1")
    private WebElementFacade firstRow;

    
    @FindBy(xpath = ".//div[contains(@onclick, 'studySubject.label')][@class='dynFilter']")
    private WebElementFacade divFilterBySSubjectId;
    
    @FindBy(xpath = ".//div[contains(@onclick, 'discrepancyNoteBean.disType')][@class='dynFilter']")
    private WebElementFacade divFilterByType;
    
    @FindBy(xpath = ".//div[contains(@onclick, 'discrepancyNoteBean.resolutionStatus')][@class='dynFilter']")
    private WebElementFacade divFilterByResolutionStatus;
    
    @FindBy(xpath = ".//div[contains(@onclick, 'age')][@class='dynFilter']")
    private WebElementFacade divFilterByDaysOpen;
    
    @FindBy(xpath = ".//div[contains(@onclick, 'eventName')][@class='dynFilter']")
    private WebElementFacade divFilterByEventName;
    
    @FindBy(xpath = ".//div[contains(@onclick, 'crfName')][@class='dynFilter']")
    private WebElementFacade divFilterByCRFName;
   
    @FindBy(xpath = ".//div[contains(@onclick, 'entityName')][@class='dynFilter']")
    private WebElementFacade divFilterByEntityName;
    
    @FindBy(xpath = ".//div[contains(@onclick, 'entityValue')][@class='dynFilter']")
    private WebElementFacade divFilterByEntityValue;
    
    @FindBy(xpath = ".//div[contains(@onclick, 'discrepancyNoteBean.description')][@class='dynFilter']")
    private WebElementFacade divFilterByDescription;
    
    @FindBy(xpath = ".//div[contains(@onclick, 'discrepancyNoteBean.user')][@class='dynFilter']")
    private WebElementFacade divFilterByAssignedUser;
    
    @FindBy(id = "dynFilterInput")
    private WebElementFacade iFilterField;
    
    @FindBy(id = "dynFilterDroplist")
    private WebElementFacade sFilterField;

    @FindBy(xpath = ".//tr[@class='filter']//a[contains(@href,\"onInvokeAction('listNotes','filter')\")]")
    private WebElementFacade lApplyFilter;
    
    @FindBy(xpath = ".//tr[@class='filter']//a[contains(@href,\"onInvokeAction('listNotes','clear')\")]")
    private WebElementFacade lClearFilter;

    @Override
   	public boolean isOnPage(WebDriver driver) {
       	return tDNList.isDisplayed();
   	}
       
    public void fillFiltersFromMapOnNDPage(Map<String, String> map) {
		
    	if (map.containsKey("Study Subject ID")) {
			divFilterBySSubjectId.click();
			iFilterField.type(map.get("Study Subject ID"));
		}
		
		if (map.containsKey("Days Open")) {
			divFilterByDaysOpen.click();
			iFilterField.type(map.get("Days Open"));
		}
		
		if (map.containsKey("Entity Name")) {
			divFilterByEntityName.click();
			iFilterField.type(map.get("Entity Name"));
		}
		
		if (map.containsKey("Entity Value")) {
			divFilterByEntityValue.click();
			iFilterField.type(map.get("Entity Value"));
		}
		
		if (map.containsKey("Description") && !map.containsKey("Parent Description")) {
			divFilterByDescription.click();
			iFilterField.type(map.get("Description"));
		}
		
		if (map.containsKey("Parent Description")) {
			divFilterByDescription.click();
			iFilterField.type(map.get("Parent Description"));
		}
		
		if (map.containsKey("Type")) {
			divFilterByType.click();
			sFilterField.selectByValue(map.get("Type"));
		}
		
		if (map.containsKey("Resolution Status")) {
			divFilterByResolutionStatus.click();
			sFilterField.selectByValue(map.get("Resolution Status"));
		}
		
		if (map.containsKey("Event Name")) {
			divFilterByEventName.click();
			sFilterField.selectByValue(map.get("Event Name"));
		}
		
		if (map.containsKey("CRF Name")) {
			divFilterByCRFName.click();
			sFilterField.selectByValue(map.get("CRF Name"));
		}
		
		if (map.containsKey("Assigned User")) {
			divFilterByAssignedUser.click();
			sFilterField.selectByValue(map.get("Assigned User"));
		}
		
		clickApplyFilterLink();
	}
   
    public void fillFiltersFromDNOnNDPage(DNote dn) {
    	fillFiltersFromMapOnNDPage(DNote.getMapWithFields(dn));
    }
    
    public void clickApplyFilterLink() {
        lApplyFilter.click();
    }
    
    public void clickClearFilterLink() {
        lClearFilter.click();
    }

	public void checkDNPresent(DNote dn) {
		firstRow.findBy(By.xpath("//td[1]"));
	}
}
