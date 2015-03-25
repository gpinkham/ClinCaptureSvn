package com.clinovo.pages;

import com.clinovo.pages.beans.StudySubject;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

public class AddSubjectPage extends BasePage {

	public static final String PAGE_NAME = "Add Subject page";
	public static final String PAGE_URL = "AddNewSubject";

	@FindBy(jquery = "form[action='ChangeStudy']")
    private WebElementFacade formWithData;
	
	@FindBy(name = "label")
    private WebElementFacade iStudySubjectID;

	@FindBy(name = "uniqueIdentifier")
    private WebElementFacade iPersonID;

	@FindBy(name = "secondaryLabel")
    private WebElementFacade iSecondaryID;
	
	@FindBy(name = "enrollmentDate")
    private WebElementFacade iDateOfEnrollment;
	
	@FindBy(name = "gender")
    private WebElementFacade iGender;

    @FindBy(id = "dobField")
    private WebElementFacade iDOB;
    
    @FindBy(name = "dynamicGroupClassId")
    private WebElementFacade sDynamicGroupName;

    
    @FindBy(name = "submitDone")
    private WebElementFacade bSubmit;

    @FindBy(name = "StartDataEntry")
    private WebElementFacade bStartDataEntry;

    public AddSubjectPage (WebDriver driver) {
        super(driver);
    }

    public void selectDynamicGroup(String dynamicGroup) {
    	sDynamicGroupName.selectByVisibleText(dynamicGroup);
    }

    public void clickStartDataEntryButton() {
    	bStartDataEntry.click();
    }

    @Override
    public void clickSubmit() {
        bSubmit.click();
    }
    
    public void fillInAddSubjectPage(StudySubject ssubj) {
		
    	iStudySubjectID.type(ssubj.getStudySubjectID());

    	iPersonID.type(ssubj.getPersonID());
    	
    	iSecondaryID.type(ssubj.getSecondaryID());
    	
    	if (!"".equals(ssubj.getDateOfEnrollmentForStudy())) { 
    		iDateOfEnrollment.type(ssubj.getDateOfEnrollmentForStudy());
    	}

    	iGender.selectByValue(StudySubject.convertGenderNameToValue(ssubj.getGender()));
    	
    	iDOB.type(ssubj.getDateOfBirth());
    	
    	if (!"".equals(ssubj.getDynamicGroupName())) { 
    		sDynamicGroupName.selectByVisibleText(ssubj.getDynamicGroupName());
    	}
	}
}
