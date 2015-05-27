package com.clinovo.pages;

import net.thucydides.core.annotations.findby.By;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.clinovo.pages.beans.StudyEventDefinition;

import java.util.List;
import java.util.Map;

/**
 * Created by Anton on 18.07.2014.
 */
public class SubjectMatrixPage extends BasePage {

	public static final String PAGE_NAME = "SM page";
	public static final String PAGE_URL = "ListStudySubjects";
	
    public SubjectMatrixPage (WebDriver driver) {
        super(driver);
    }
    
    @FindBy(id = "findSubjects")
    private WebElementFacade tblFindSubjects;
    
    @FindBy(xpath = ".//div[starts-with(@id, 'eventScheduleWrapper')]//*[@id='startdateField']")
    private WebElementFacade iStartDate;
	
    @FindBy(xpath = ".//div[starts-with(@id, 'eventScheduleWrapper')]//*[@id='enddateField']")
    private WebElementFacade iEndDate;
    
    @FindBy(xpath = ".//div[starts-with(@id, 'eventScheduleWrapper')]//*[@name='Schedule']")
    private WebElementFacade bScheduleEvent;
    
    @FindBy(xpath = ".//*[@class='crfListTable']//a[contains(@href,'UpdateStudyEvent')]/img[contains(@src,'icon_SignedBlue.gif')]")
    private WebElementFacade bSignEvent;
    
    @FindBy(xpath = ".//*[@class='crfListTable']//a[contains(@href,'SignStudySubject')]/img[contains(@src,'icon_SignedBlue.gif')]")
    private WebElementFacade bSignSubject;
    
    @FindBy(className = "crfListTable")
    private WebElementFacade tCRFList;
    
    @FindBy(css = "div.dynFilter")
    private WebElementFacade divFindSubjects;
    
    @FindBy(id = "dynFilterInput")
    private WebElementFacade iFindSubjects;

    @FindBy(xpath = ".//tr[@class='filter']//a[contains(@href,\"onInvokeAction('findSubjects','filter')\")]")
    private WebElementFacade lApplyFilter;
    
    @FindBy(xpath = ".//tr[@class='filter']//a[contains(@href,\"onInvokeAction('findSubjects','clear')\")]")
    private WebElementFacade lClearFilter;

    @Override
	public boolean isOnPage(WebDriver driver) {
    	return tblFindSubjects.isCurrentlyVisible();
	}
    
    public void filterSMByStudySubjectID(String sSubjectID) {
    	enterStudySubjectIDToFilterField(sSubjectID);
        clickApplyFilterLink();
    }

    public void enterStudySubjectIDToFilterField(String sSubjectID) {
    	divFindSubjects.waitUntilVisible();
    	divFindSubjects.click();
        iFindSubjects.type(sSubjectID);
    }

    public void clickApplyFilterLink() {
        lApplyFilter.click();
    }
    
    public void clickClearFilterLink() {
        lClearFilter.click();
    }
    
	public void callPopupForSubjectAndEvent(String studySubjectID, String eventName) {
		List<WebElement> eventIcons = tblFindSubjects.findElements(By.xpath(".//td[text()='" + studySubjectID + "']"));
		if (eventIcons.size() == 0) {
			filterSMByStudySubjectID(studySubjectID);
		}
		WebElementFacade eventIcon = tblFindSubjects.findBy(By.xpath(".//td[text()='" + studySubjectID + "']/..//div[@event_name='" + eventName + "']/../a"));		
    	eventIcon.click();
	}
	
	private void initElementsInPopup() {
		iStartDate.waitUntilVisible();
		iEndDate.waitUntilVisible();
		bScheduleEvent.waitUntilVisible();
	}

	public void fillInPopupToScheduleEvent(StudyEventDefinition event) {
		initElementsInPopup();
		if (!event.getStartDateTime().isEmpty()) {
			iStartDate.type(event.getStartDateTime());
		}
		
		iEndDate.type(event.getEndDateTime());
	}

	public void clickScheduleEventButtonInPopup() {
		bScheduleEvent.waitUntilVisible();
		bScheduleEvent.click();
	}

	public void eventIsScheduled(StudyEventDefinition event) {
		WebElementFacade eventIcon = findEventIconOnSM(event.getStudySubjectID(), event.getName());		
		assert(eventIcon.getAttribute("src").endsWith("icon_Scheduled.gif"));
	}

	public void clickEnterDataButtonInPopup(String aCRFName) { 
		tCRFList.waitUntilVisible();
		List<WebElement> tds = tblFindSubjects.findElements(By.xpath(".//div[starts-with(@id, 'crfListWrapper')]//td[contains(text(), '"+ aCRFName +"')]"));
		for (WebElement td: tds) {
			if (td.getText().replaceFirst(aCRFName, "").trim().replace("*", "").isEmpty()) {
	    		td.findElement(By.xpath("./..//img[contains(@name,'bt_EnterData')]")).click();
	    		break;
	    	}
	    }
	}

	public void clickSignEventButton() {
		bSignEvent.waitUntilVisible();
		bSignEvent.click();
	}

	public void filterSMPage(Map<String, String> map) {
		if (map.containsKey("Study Subject ID")) {
			enterStudySubjectIDToFilterField(map.get("Study Subject ID"));
		}
		
		clickApplyFilterLink();
	}

	public void checkSignEventStatus(Map<String, String> values) {
		if (values.containsKey("Study Subject ID") && values.containsKey("Event Name")) {
			WebElementFacade eventIcon = findEventIconOnSM(values.get("Study Subject ID"), values.get("Event Name"));		
			assert(eventIcon.getAttribute("src").endsWith("icon_Signed.gif"));
		}
	}
	
	public WebElementFacade findEventIconOnSM(String studySubjectID, String eventName) {
		return tblFindSubjects.findBy(By.xpath(".//td[text()='" + studySubjectID + "']/..//div[@event_name='" + eventName + "']/../a/img"));
	}
}
