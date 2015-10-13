package com.clinovo.pages;

import net.thucydides.core.annotations.findby.By;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.webelements.RadioButtonGroup;

import org.openqa.selenium.WebDriver;

import com.clinovo.pages.beans.Study;

/**
 * UpdateStudyNew page.
 */
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
	private WebElementFacade lStudyParameterConfiguration;

	@FindBy(name = "description")
	private WebElementFacade tBriefSummary;

	@FindBy(name = "expectedTotalEnrollment")
	private WebElementFacade iExpectedTotalEnrollment;

	@FindBy(name = "autoCodeDictionaryName")
	private WebElementFacade iAutoCodeDictionaryName;

	private RadioButtonGroup rAllowMedicalCoding;

	private RadioButtonGroup rCodeWithContext;

	// Subject Parameters: 
	private RadioButtonGroup rHowToGenerateSubjectID;

	private RadioButtonGroup rCollectDateOfEnrollmentForStudy;

	private RadioButtonGroup rCollectGender;

	private RadioButtonGroup rCollectSubjectDateOfBirth;

	private RadioButtonGroup rCollectPersonID;

	// Data Entry Parameters:
	private RadioButtonGroup rCollectInterviewerName;

	private RadioButtonGroup rInterviewerNameDefault;

	private RadioButtonGroup rInterviewerNameEditable;

	private RadioButtonGroup rCollectInterviewDate;

	private RadioButtonGroup rInterviewDateDefault;

	private RadioButtonGroup rInterviewDateEditable;

	private RadioButtonGroup rUseAutotabbing;

	private RadioButtonGroup rAllowCRFEvaluation;

	private RadioButtonGroup rEvaluateWithContext;

	private RadioButtonGroup rSASNameAnnotation;

	private RadioButtonGroup rApprovalNeeded;

	//Event Parameters
	private RadioButtonGroup rCollectEventLocation;

	private RadioButtonGroup rCollectStartDate;

	private RadioButtonGroup rCollectStopDate;

	/**
	 * Set driver to this page.
	 * @param driver WebDriver
	 */
	public UpdateStudyDetailsPage(WebDriver driver) {
		super(driver);
	}

	@Override
	public boolean isOnPage(WebDriver driver) {
		return (driver.getCurrentUrl().contains(PAGE_URL));
	}

	/**
	 * Disable CRF evaluation.
	 */
	public void disableCRFEvaluation() {
		defineRadioButtons();
		rAllowCRFEvaluation.selectByValue("no");
	}

	/**
	 * Enable CRF evaluation.
	 */
	public void enableCRFEvaluation() {
		defineRadioButtons();
		rAllowCRFEvaluation.selectByValue("yes");
	}

	/**
	 * Disable evaluate with context.
	 */
	public void disableEvaluateWithContext() {
		defineRadioButtons();
		rEvaluateWithContext.selectByValue("no");
	}

	/**
	 * Enable evaluate with context.
	 */
	public void enableEvaluateWithContext() {
		defineRadioButtons();
		rEvaluateWithContext.selectByValue("yes");
	}

	/**
	 * Disable medical coding.
	 */
	public void disableMedicalCoding() {
		defineRadioButtons();
		rAllowMedicalCoding.selectByValue("no");
	}

	/**
	 * Enable medical coding.
	 */
	public void enableMedicalCoding() {
		defineRadioButtons();
		rAllowMedicalCoding.selectByValue("yes");
	}

	/**
	 * Fill brief summary.
	 * @param summary String
	 */
	public void fillBriefSummary(String summary) {
		tBriefSummary.type(summary);
	}

	/**
	 * Fill expected total enrollment.
	 * @param value String
	 */
	public void fillExpectedTotalEnrollment(String value) {
		iExpectedTotalEnrollment.type(value);
	}

	/**
	 * Define radio buttons on this page.
	 */
	public void defineRadioButtons() {
		//Data Entry Parameters:
		rCollectInterviewerName = new RadioButtonGroup(formWithData.findElements(By.name("interviewerNameRequired")));
		rInterviewerNameDefault = new RadioButtonGroup(formWithData.findElements(By.name("interviewerNameDefault")));
		rInterviewerNameEditable = new RadioButtonGroup(formWithData.findElements(By.name("interviewerNameEditable")));
		rCollectInterviewDate = new RadioButtonGroup(formWithData.findElements(By.name("interviewDateRequired")));
		rInterviewDateDefault = new RadioButtonGroup(formWithData.findElements(By.name("interviewDateDefault")));
		rInterviewDateEditable = new RadioButtonGroup(formWithData.findElements(By.name("interviewDateEditable")));
		rUseAutotabbing = new RadioButtonGroup(formWithData.findElements(By.name("autoTabbing")));

		//Subject Parameters:
		rHowToGenerateSubjectID = new RadioButtonGroup(formWithData.findElements(By.name("subjectIdGeneration")));
		rCollectDateOfEnrollmentForStudy = new RadioButtonGroup(formWithData.findElements(By.name("dateOfEnrollmentForStudyRequired")));
		rCollectGender = new RadioButtonGroup(formWithData.findElements(By.name("genderRequired")));
		rCollectSubjectDateOfBirth = new RadioButtonGroup(formWithData.findElements(By.name("collectDob")));
		rCollectPersonID = new RadioButtonGroup(formWithData.findElements(By.name("subjectPersonIdRequired")));

		rAllowMedicalCoding = new RadioButtonGroup(formWithData.findElements(By.name("medicalCoding")));
		rAllowCRFEvaluation = new RadioButtonGroup(formWithData.findElements(By.name("studyEvaluator")));
		rEvaluateWithContext = new RadioButtonGroup(formWithData.findElements(By.name("evaluateWithContext")));
		rSASNameAnnotation = new RadioButtonGroup(formWithData.findElements(By.name("annotatedCrfSasItemNames")));
		rApprovalNeeded = new RadioButtonGroup(formWithData.findElements(By.name("medicalCodingApprovalNeeded")));
		rCodeWithContext = new RadioButtonGroup(formWithData.findElements(By.name("medicalCodingContextNeeded")));

		//Event Parameters
		rCollectEventLocation = new RadioButtonGroup(formWithData.findElements(By.name("eventLocationRequired")));
		rCollectStartDate = new RadioButtonGroup(formWithData.findElements(By.name("startDateTimeRequired")));
		rCollectStopDate = new RadioButtonGroup(formWithData.findElements(By.name("endDateTimeRequired")));
	}

	/**
	 * Fill in study details page.
	 * @param study Study
	 */
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
		//Medical Coding:
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
		//CRF Evaluation:
		if (!study.getAllowCRFEvaluation().equals("")) {
			rAllowCRFEvaluation.selectByValue(study.getAllowCRFEvaluation());
		}
		if (!study.getEvaluateWithContext().equals("")) {
			rEvaluateWithContext.selectByValue(study.getEvaluateWithContext());
		}
		if (!study.getSASNameAnnotation().equals("")) {
			rSASNameAnnotation.selectByValue(study.getSASNameAnnotation());
		}
		//Data Entry Parameters:
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
		if (!study.getUseAutotabbing().equals("")) {
			rUseAutotabbing.selectByValue(study.getUseAutotabbing());
		}
		//Subject Parameters:
		if (!study.getHowToGenerateSubjectID().equals("")) {
			rHowToGenerateSubjectID.selectByValue(study.getHowToGenerateSubjectID());
		}
		if (!study.getCollectDateOfEnrollmentForStudy().equals("")) {
			rCollectDateOfEnrollmentForStudy.selectByValue(study.getCollectDateOfEnrollmentForStudy());
		}
		if (!study.getCollectGender().equals("")) {
			rCollectGender.selectByValue(study.getCollectGender());
		}
		if (!study.getCollectSubjectDateOfBirth().equals("")) {
			rCollectSubjectDateOfBirth.selectByValue(study.getCollectSubjectDateOfBirth());
		}
		if (!study.getCollectPersonID().equals("")) {
			rCollectPersonID.selectByValue(study.getCollectPersonID());
		}
		//Event Parameters:
		if (!study.getCollectEventLocation().equals("")) {
			rCollectEventLocation.selectByValue(study.getCollectEventLocation());
		}
		if (!study.getCollectStartDate().equals("")) {
			rCollectStartDate.selectByValue(study.getCollectStartDate());
		}
		if (!study.getCollectStopDate().equals("")) {
			rCollectStopDate.selectByValue(study.getCollectStopDate());
		}
	}
}
