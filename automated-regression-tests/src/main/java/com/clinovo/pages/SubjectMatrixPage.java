package com.clinovo.pages;

import net.thucydides.core.annotations.findby.By;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.webelements.RadioButtonGroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

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
    
    

    @FindBy(css = "div.dynFilter")
    private WebElementFacade divFindSubjects;

    @FindBy(id = "dynFilterInput")
    private WebElementFacade eFindSubjects;

    @FindBy(linkText = " Apply Filter ")
    private WebElementFacade lApplyFilter;

    @FindBy(id = "sedDropDown")
    private WebElementFacade ddlSelectEvent;

    @FindBy(name = "StartDataEntry")
    private WebElementFacade bStartDataEntry;

    @FindBy(className = "crfListTable")
    private WebElementFacade tblSubjectEventCRFs;

    @FindBy(name = "ndIcon")
    private WebElementFacade iDiscrepancyNoteFlag;

    @FindBy(name = "Schedule")
    private WebElementFacade bScheduleEvent;


    public void clickSubjectEventStatusIcon(String subjectID, String event) {
        getEventStatusIconElement(subjectID, event).click();
    }

    public void clickStartDataEntryBtn() {
        bStartDataEntry.click();
    }

    public void clickStartDataEntryBtn(String crf) {
        WebElement iEnterData = tblSubjectEventCRFs.findElement(By.xpath("//*[text()[contains(.,'" + crf + "')]]/../td//img[@alt='Enter Data']"));
        iEnterData.click();
    }

    public boolean isEventScheduled(String subjectID, String event) {
        WebElement eventStatusIcon = getEventStatusIconElement(subjectID, event);
        WebElement eventStatusIconImage = eventStatusIcon.findElement(By.tagName("img"));

        return eventStatusIconImage.getAttribute("src").contains("icon_Scheduled.gif");
    }

    private WebElement getEventStatusIconElement(String subjectID, String event) {
        WebElementFacade eventInDDL = ddlSelectEvent.selectByVisibleText(event);
        Integer eventIndex = Integer.parseInt(eventInDDL.getAttribute("value"));

        return  getDriver().findElement(By.jquery("[onclick*=" + subjectID + "_" + eventIndex + "]"));
    }

    public void filterSubjectID(String subjectID) {
        enterSubjectIDToFilterField(subjectID);
        clickApplyFilterLink();
    }

    public void enterSubjectIDToFilterField(String subjectID) {
        divFindSubjects.click();
        eFindSubjects.type(subjectID);
    }

    public void clickApplyFilterLink() {
        lApplyFilter.click();
    }

    public boolean isDiscrepancyNoteFlagDisplayed() {
        return iDiscrepancyNoteFlag.isDisplayed();
    }

    public void clickDNIcon() {
        iDiscrepancyNoteFlag.click();
    }

    public boolean isSubjectExists(String subjectID) {
        List<WebElement> subjectIDElements = tblFindSubjects.findElements(By.xpath("./tbody/tr/td[1]"));

        List<String> subjectIDs = new ArrayList<String>();

        for (WebElement w : subjectIDElements)
            subjectIDs.add(w.getText());

        return subjectIDs.contains(subjectID);
    }

    public void clickScheduleEventBtn() {
        bScheduleEvent.click();
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return tblFindSubjects.isDisplayed();
	}

	public void callPopupForSubjectAndEvent(String studySubjectID, String eventName) {
		WebElement trWithSubjectData = tblFindSubjects.findElement(By.xpath(".//td[text()='" + studySubjectID + "']]/.."));
		WebElement eventIcon = trWithSubjectData.findElement(By.xpath("//div[@event_name='" + eventName + "']/..//a"));
		
    	eventIcon.click();
	}
}
