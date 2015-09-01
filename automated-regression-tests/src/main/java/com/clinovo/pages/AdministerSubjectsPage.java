package com.clinovo.pages;

import java.util.Map;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

public class AdministerSubjectsPage extends BasePage {

	public static final String PAGE_NAME = "Administer Subjects page";
	public static final String PAGE_URL = "ListSubject";
    
	@FindBy(id = "listSubjects")
    private WebElementFacade tListSubjects;
    
    @FindBy(xpath = ".//div[contains(@onclick,'studySubjectIdAndStudy')][@class='dynFilter']")
    private WebElementFacade divProtocolStudySubjectID;
    
    @FindBy(xpath = ".//input[@id='dynFilterInput']")
    private WebElementFacade iFilterField;

    @FindBy(xpath = ".//tr[@class='filter']//a[contains(@href,\"onInvokeAction('listSubjects','filter')\")]")
    private WebElementFacade lApplyFilter;
    
    @FindBy(xpath = ".//tr[@class='filter']//a[contains(@href,\"onInvokeAction('listSubjects','clear')\")]")
    private WebElementFacade lClearFilter;
    
    @FindBy(xpath = ".//*[@name='bt_Edit1']")
    private WebElementFacade lEditSubject;
    
    @FindBy(xpath = ".//*[@name='bt_View1']")
    private WebElementFacade lViewSubject;
    
    @FindBy(xpath = ".//*[@name='bt_Remove1']")
    private WebElementFacade lRemoveSubject;
    
    public AdministerSubjectsPage (WebDriver driver) {
        super(driver);
    }

    public void enterStudyIDStudySubjectIDToFilterField(String studyIDStSubjectID) {
    	divProtocolStudySubjectID.waitUntilVisible();
    	divProtocolStudySubjectID.click();
    	iFilterField.type(studyIDStSubjectID);
    }
    
	public void filterAdministerSubjectsPage(Map<String, String> map) {
		if (map.containsKey("Protocol-Study Subject IDs")) {
			enterStudyIDStudySubjectIDToFilterField(map.get("Protocol-Study Subject IDs"));
		}
		
		clickApplyFilterLink();
	}
	
	public void clickApplyFilterLink() {
        lApplyFilter.click();
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}

	public void clickEditIconForSubject(String studyIDStSubjectID) {
		lEditSubject.click();
	}
}
