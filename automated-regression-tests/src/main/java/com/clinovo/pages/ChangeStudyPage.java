package com.clinovo.pages;

import java.util.List;
import net.thucydides.core.annotations.findby.By;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.webelements.RadioButtonGroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by Igor
 */
public class ChangeStudyPage extends BasePage {

	public static final String PAGE_NAME = "Change Your Current Study/Site page";
	public static final String PAGE_URL = "ChangeStudy";
	
	@FindBy(jquery = "form[action='ChangeStudy']")
    private WebElementFacade formWithData;
	
	@FindBy(name = "Submit")
    private WebElementFacade bContinue;
	
    public ChangeStudyPage (WebDriver driver) {
        super(driver);
    }

    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}

    @Override
	public void clickContinue() {
    	bContinue.click();
	}
    
	public void selectStudy(String studyName) {
    	List<WebElement> listOfTds = formWithData.findElements(By.xpath(".//td[text()[contains(.,'"+studyName+"')]]"));
    	
    	for (WebElement td: listOfTds) {
    		String text = td.getText().replaceFirst(studyName, "");
    		
    		if (text.trim().split(" ").length == 2) {
    			String value = td.findElement(By.xpath(".//input[@name='studyId']")).getAttribute("value");
    			RadioButtonGroup rStudies = new RadioButtonGroup(formWithData.findElements(By.name("studyId")));
    			rStudies.selectByValue(value);
    			return;
    		}
    	}
	}
}

