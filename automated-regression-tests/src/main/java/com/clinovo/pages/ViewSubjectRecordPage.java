package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

public class ViewSubjectRecordPage extends BasePage {

	public static final String PAGE_NAME = "View Subject Record page";
	public static final String PAGE_URL = "ViewStudySubject";
	
	@FindBy(xpath = ".//a[contains(@href, 'studySubjectRecord')]")
    private WebElementFacade lStudySubjectRecord;
	
	@FindBy(id = "flag_gender")
    private WebElementFacade lGenderFlag;
	
	@FindBy(id = "flag_uniqueIdentifier")
    private WebElementFacade lPersonIDFlag;
	
	@FindBy(id = "flag_dob")
    private WebElementFacade lDOBFlag;
	
	@FindBy(id = "flag_enrollmentDate")
    private WebElementFacade lEnrollmentDateFlag;
	
    public ViewSubjectRecordPage (WebDriver driver) {
        super(driver);
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}

	public void clickStudySubjectRecordLink() {
		lStudySubjectRecord.click();
	}
	
	public void clickEnrollmentDateFlag() {
		lEnrollmentDateFlag.click();
	}
}
