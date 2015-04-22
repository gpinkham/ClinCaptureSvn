package com.clinovo.pages;

import net.thucydides.core.annotations.findby.By;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.webelements.RadioButtonGroup;

import org.openqa.selenium.WebDriver;

import com.clinovo.pages.beans.Study;

public class UpdateStudyDetailsPage extends BasePage {

	public static final String PAGE_NAME = "Update Study Details page";
	public static final String PAGE_URL = "UpdateStudyNew";
	
	@FindBy(jquery = "form[action='UpdateStudyNew']")
    private WebElementFacade formWithData;
	
	@FindBy(id = "excl_section1")
    private WebElementFacade lStudyDescriptionAndStatus;
	
	@FindBy(id = "excl_section3")
    private WebElementFacade lConditionsAndEligibility;
	
	@FindBy(id = "excl_section7")
    private WebElementFacade  lStudyParameterConfiguration;
	
	@FindBy(name = "description")
    private WebElementFacade tBriefSummary;
	
	@FindBy(name = "expectedTotalEnrollment")
    private WebElementFacade iExpectedTotalEnrollment;

	@FindBy(name = "autoCodeDictionaryName")
    private WebElementFacade iAutoCodeDictionaryName;

	private RadioButtonGroup rAllowMedicalCoding ;
	
	private RadioButtonGroup rCodeWithContext;
	
	// Data Entry Parameters:
	private RadioButtonGroup rCollectInterviewerName;
	
	private RadioButtonGroup rInterviewerNameDefault;
	    
	private RadioButtonGroup rInterviewerNameEditable;
		
	private RadioButtonGroup rCollectInterviewDate;
		
	private RadioButtonGroup rInterviewDateDefault;
	    
	private RadioButtonGroup rInterviewDateEditable;

	private RadioButtonGroup rUseAutotabbing ;
	
        
    private RadioButtonGroup rAllowCRFEvaluation;
    
    private RadioButtonGroup rEvaluateWithContext;

	private RadioButtonGroup rSASNameAnnotation;
	
	private RadioButtonGroup rApprovalNeeded;

	
    public UpdateStudyDetailsPage (WebDriver driver) {
        super(driver);
    }

    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}
    
    public void disableCRFEvaluation() {
        defineRadioButtons();
        rAllowCRFEvaluation.selectByValue("no");
    }
    
    public void enableCRFEvaluation() {
        defineRadioButtons();
        rAllowCRFEvaluation.selectByValue("yes");
    }
    
    public void disableEvaluateWithContext() {
        defineRadioButtons();
        rEvaluateWithContext.selectByValue("no");
    }
    
    public void enableEvaluateWithContext() {
        defineRadioButtons();
        rEvaluateWithContext.selectByValue("yes");
    }
    
    public void disableMedicalCoding() {
        defineRadioButtons();
        rAllowMedicalCoding.selectByValue("no");
    }
    
    public void enableMedicalCoding() {
        defineRadioButtons();
        rAllowMedicalCoding.selectByValue("yes");
    }
    
    public void fillBriefSummary(String summary) {
    	tBriefSummary.type(summary);
    }
    
    public void fillExpectedTotalEnrollment(String value) {
    	iExpectedTotalEnrollment.type(value);
    }
    
    public void defineRadioButtons() {
    	//Data Entry Parameters: 
    	rCollectInterviewerName = new RadioButtonGroup(formWithData.findElements(By.name("interviewerNameRequired")));
    	rInterviewerNameDefault = new RadioButtonGroup(formWithData.findElements(By.name("interviewerNameDefault")));
    	rInterviewerNameEditable = new RadioButtonGroup(formWithData.findElements(By.name("interviewerNameEditable")));
    	rCollectInterviewDate = new RadioButtonGroup(formWithData.findElements(By.name("interviewDateRequired")));
    	rInterviewDateDefault = new RadioButtonGroup(formWithData.findElements(By.name("interviewDateDefault")));
    	rInterviewDateEditable = new RadioButtonGroup(formWithData.findElements(By.name("interviewDateEditable")));
    	rUseAutotabbing = new RadioButtonGroup(formWithData.findElements(By.name("autoTabbing")));
    	
    	rAllowMedicalCoding = new RadioButtonGroup(formWithData.findElements(By.name("allowCodingVerification")));
    	rAllowCRFEvaluation = new RadioButtonGroup(formWithData.findElements(By.name("allowCrfEvaluation")));
    	rEvaluateWithContext = new RadioButtonGroup(formWithData.findElements(By.name("evaluateWithContext")));
    	rSASNameAnnotation = new RadioButtonGroup(formWithData.findElements(By.name("annotatedCrfSasItemNames")));
    	rApprovalNeeded = new RadioButtonGroup(formWithData.findElements(By.name("medicalCodingApprovalNeeded")));
    	rCodeWithContext = new RadioButtonGroup(formWithData.findElements(By.name("medicalCodingContextNeeded")));
    }

	public void fillInStudyDetailsPage(Study study) {

		//lStudyDescriptionAndStatus.click();
		lConditionsAndEligibility.click();
		defineRadioButtons();
		
		if (!study.getBriefSummary().equals("")) {
			tBriefSummary.type(study.getBriefSummary());
		}
		if (!study.getExpectedTotalEnrollment().equals("")) {
			iExpectedTotalEnrollment.type(study.getExpectedTotalEnrollment());
		}
		
		//Medical Coding
		if (!study.getAllowMedicalCoding().equals("")) {
			rAllowMedicalCoding.selectByValue(study.getAllowMedicalCoding());
		}
		if (!study.getApprovalNeeded().equals("")) {
			rApprovalNeeded.selectByValue(study.getAllowCRFEvaluation());
		}
		if (!study.getCodeWithContext().equals("")) {
			rCodeWithContext.selectByValue(study.getEvaluateWithContext());
		}	
		if (!study.getAutoCodeDictionaryName().equals("")) {
			iAutoCodeDictionaryName.type(study.getAutoCodeDictionaryName());
		}
		
		//CRF Evaluation
		if (!study.getAllowCRFEvaluation().equals("")) {
			rAllowCRFEvaluation.selectByValue(study.getAllowCRFEvaluation());
		}
		if (!study.getEvaluateWithContext().equals("")) {
			rEvaluateWithContext.selectByValue(study.getEvaluateWithContext());
		}	
		if (!study.getSASNameAnnotation().equals("")) {
			rSASNameAnnotation.selectByValue(study.getSASNameAnnotation());
		}
		
		//Data Entry Parameters
		if (!study.getCollectInterviewerName().equals("")) {
			rCollectInterviewerName.selectByValue(study.getCollectInterviewerName());
		}
		if (!study.getInterviewerNameDefault().equals("")) {
			rInterviewerNameDefault.selectByValue(study.getInterviewDateDefault());
		}
		if (!study.getInterviewerNameEditable().equals("")) {
			rInterviewerNameEditable.selectByValue(study.getInterviewerNameEditable());
		}	
		if (!study.getCollectInterviewDate().equals("")) {
			rCollectInterviewDate.selectByValue(study.getCollectInterviewDate());
		}
		if (!study.getInterviewDateDefault().equals("")) {
			rInterviewDateDefault.selectByValue(study.getInterviewDateDefault());
		}
		if (!study.getInterviewDateEditable().equals("")) {
			rInterviewDateEditable.selectByValue(study.getInterviewDateEditable());
		}	
		if (!study.getUseAutotabbing ().equals("")) {
			rUseAutotabbing.selectByValue(study.getUseAutotabbing());
		}
		    	
	}
}
