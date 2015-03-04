package com.clinovo.pages;

import net.thucydides.core.annotations.findby.By;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.webelements.Checkbox;
import net.thucydides.core.webelements.RadioButtonGroup;

import org.openqa.selenium.WebDriver;
import com.clinovo.utils.ItemsUtil;
import com.clinovo.utils.StudyEventDefinition;

/**
 * 
 */
public class CreateStudyEventDefinitionPage extends BasePage {

	public static final String PAGE_NAME = "Create Study Event Definition page";
	public static final String PAGE_URL = "DefineStudyEvent?actionName=init";
	
	@FindBy(jquery = "form")
    private WebElementFacade formWithData;
	
	@FindBy(name = "name")
    private WebElementFacade iStEventDefName;

    @FindBy(name = "description")
    private WebElementFacade iDescription;

    @FindBy(name = "category")
    private WebElementFacade iCategory;

    @FindBy(name = "schDay")
    private WebElementFacade iDaySchedule;

	@FindBy(name = "maxDay")
    private WebElementFacade iDayMax;

	@FindBy(name = "minDay")
    private WebElementFacade iDayMin;

	@FindBy(name = "emailDay")
    private WebElementFacade iDayEmail;

	@FindBy(name = "emailUser")
    private WebElementFacade iUserName;
	
	@FindBy(id = "isReference")
    private WebElementFacade chReferenceEvent; 
	
	@FindBy(id = "type")
    private WebElementFacade sType;
	
    private RadioButtonGroup rRepeating;
	
    public CreateStudyEventDefinitionPage (WebDriver driver) {
        super(driver);
    }

    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	} 
    
    private void fillReferenceEvent(String string) {
    	ItemsUtil.fillCheckbox(new Checkbox(chReferenceEvent.getWrappedElement()), string);
	}

	private void fillRepeating(String value) {
		rRepeating = new RadioButtonGroup(formWithData.findElements(By.name("repeating")));
		rRepeating.selectByValue(value);
	}

	public void fillInStudyEventDefinitionPage(StudyEventDefinition event) {
				
		iStEventDefName.type(event.getName());

		iDescription.type(event.getDescription());

		iCategory.type(event.getCategory());
		
		iDaySchedule.type(event.getDaySchedule());

		iDayMax.type(event.getDayMax());

		iDayMin.type(event.getDayMin());
		
		iDayEmail.type(event.getDayEmail());

		iUserName.type(event.getUserName());
		
		fillReferenceEvent(event.getReferenceEvent());
		
		sType.selectByValue(StudyEventDefinition.convertTypeNameToTypeValue(event.getType()));

		fillRepeating(event.getRepeating());
	}
}
