package com.clinovo.pages;

import net.thucydides.core.annotations.findby.By;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.webelements.RadioButtonGroup;

import org.openqa.selenium.WebDriver;

import com.clinovo.utils.SystemProperties;

public class ConfigureSystemPropertiesPage extends BasePage {

	public static final String PAGE_NAME = "Configure System Properties page";
	public static final String PAGE_URL = "pages/system";
	
	@FindBy(id = "systemForm")
    private WebElementFacade formWithData;
	
	@FindBy(jquery = "img[id='img_group_id_10']")
    private WebElementFacade lMedicalCoding;
		
		@FindBy(id = "systemPropertyGroups'8'.systemProperties'0'.value")
		private WebElementFacade iBioontologyURL;
		
		@FindBy(id = "systemPropertyGroups'8'.systemProperties'1'.value")
		private WebElementFacade iBioontologyAPIKey;
		
		@FindBy(id = "systemPropertyGroups'8'.systemProperties'2'.value")
		private WebElementFacade iAutoCodeDictionaryName;
		
	@FindBy(jquery = "img[id='img_group_id_13']")
    private WebElementFacade lCRFEvaluation;
		
		@FindBy(jquery = "div[id='div_group_id_13']")
		private WebElementFacade dCRFEvaluation;
		
		private RadioButtonGroup rAllowCRFEvaluation;
	    
	    private RadioButtonGroup rEvaluateWithContext;
	
	
    public ConfigureSystemPropertiesPage (WebDriver driver) {
        super(driver);
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}

	public void fillInConfigureSystemPropertiesPage(SystemProperties prop) {
		
		lMedicalCoding.click();
		lCRFEvaluation.click();
		
		defineRadioButtons();
		
		//Medical Coding
		if (!prop.getBioontologyURL().equals("")) {
			iBioontologyURL.type(prop.getBioontologyURL());
		}
		if (!prop.getBioontologyAPIKey().equals("")) {
			iBioontologyAPIKey.type(prop.getBioontologyAPIKey());
		}
		if (!prop.getAutoCodeDictionaryName().equals("")) {
			iAutoCodeDictionaryName.type(prop.getAutoCodeDictionaryName());
		}
		
		//CRF Evaluation
		if (!prop.getAllowCRFEvaluationValue().equals("")) {
			rAllowCRFEvaluation.selectByValue(prop.getAllowCRFEvaluationValue());
		}
		if (!prop.getEvaluateWithContextValue().equals("")) {
			rEvaluateWithContext.selectByValue(prop.getEvaluateWithContextValue());
		}
	}
	
	private void defineRadioButtons() {
	   	rAllowCRFEvaluation = new RadioButtonGroup(formWithData.findElements(By.name("systemPropertyGroups['9'].systemProperties['0'].value")));
	   	rEvaluateWithContext = new RadioButtonGroup(formWithData.findElements(By.name("systemPropertyGroups['9'].systemProperties['1'].value")));
	}
}
