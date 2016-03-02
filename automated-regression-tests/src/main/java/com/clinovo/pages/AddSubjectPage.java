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

    @FindBy(id = "GoToPreviousPage")
    private WebElementFacade bBack;
    
    @FindBy(name = "submitDone")
    private WebElementFacade bSubmit;
    
    @FindBy(name = "StartDataEntry")
    private WebElementFacade bStartDataEntry;
    
    @FindBy(name = "submitEvent")
    private WebElementFacade bSscheduleEvents;
    
    @FindBy(name = "submitEnroll")
    private WebElementFacade bAddNextSubject;

    @FindBy(name = "cancel")
    private WebElementFacade bCancel;

    public AddSubjectPage (WebDriver driver) {
        super(driver);
    }

    public void selectDynamicGroup(String dynamicGroup) {
    	sDynamicGroupName.selectByVisibleText(dynamicGroup);
    }

    public void clickStartDataEntryButton() {
    	bStartDataEntry.click();
    }
    
    public void clickAddNextSubjectButton() {
    	bAddNextSubject.click();
    }

    @Override
    public void clickSubmit() {
        bSubmit.click();
    }
    
    public void fillInAddSubjectPage(StudySubject ssubj) {
		
    	iStudySubjectID.type(ssubj.getStudySubjectID());

    	if (!ssubj.getPersonID().isEmpty()) { 
    		iPersonID.type(ssubj.getPersonID());
    	}
    	
    	if (!ssubj.getSecondaryID().isEmpty()) { 
    		iSecondaryID.type(ssubj.getSecondaryID());
    	}
    	
    	if (!ssubj.getDateOfEnrollmentForStudy().isEmpty()) { 
    		iDateOfEnrollment.type(ssubj.getDateOfEnrollmentForStudy());
    	}

    	if (!ssubj.getGender().isEmpty()) {  
    		iGender.selectByValue(StudySubject.convertGenderNameToValue(ssubj.getGender()));
    	}
    	
    	if (!ssubj.getDateOfBirth().isEmpty()) { 
    		iDOB.type(ssubj.getDateOfBirth());
    	}
    	
    	if (!ssubj.getDynamicGroupName().isEmpty()) { 
    		sDynamicGroupName.selectByVisibleText(ssubj.getDynamicGroupName());
    	}
	}
}
