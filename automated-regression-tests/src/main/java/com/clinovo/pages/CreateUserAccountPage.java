package com.clinovo.pages;

import java.util.List;

import net.thucydides.core.annotations.findby.By;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.webelements.Checkbox;
import net.thucydides.core.webelements.RadioButtonGroup;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.clinovo.utils.ItemsUtil;
import com.clinovo.pages.beans.User;

public class CreateUserAccountPage extends BasePage {

	public static final String PAGE_NAME = "Create User Account page";
	public static final String PAGE_URL = "CreateUserAccount";
	
    @FindBy(id = "userName")
    private WebElementFacade iUserName;

    @FindBy(id = "firstName")
    private WebElementFacade iFirstName;

    @FindBy(id = "lastName")
    private WebElementFacade iLastName;

    @FindBy(id = "email")
    private WebElementFacade iEmail;

    @FindBy(id = "phone")
    private WebElementFacade iPhone;

    @FindBy(id = "institutionalAffiliation")
    private WebElementFacade iInstitutionalAffiliation;

    @FindBy(id = "activeStudy")
    private WebElementFacade sActiveStudy;
    
    @FindBy(id = "role")
    private WebElementFacade sRole;
    
    @FindBy(id = "type")
    private WebElementFacade sType;
    
    @FindBy(id = "runWebServices")
    private WebElementFacade chRunWebServices;
    
    @FindBy(className = "aka_revised_content")
    private WebElementFacade tdWithForm;
    
    @FindBy(jquery = "form")
    private WebElementFacade formWithData;

    public CreateUserAccountPage (WebDriver driver) {
        super(driver);
    }
    
    public void fillInCreateUserAccountPage(User user) {
		fillUserNameField(user.getUserName());
		fillFirstNameField(user.getFirstName());
		fillLastNameField(user.getLastName());
		fillEmailField(user.getEmail());
		fillPhoneField(user.getPhone());
		fillInstitutionalAffilationField(user.getInstitutionalAffiliation());
		
		fillActiveStudySelect(user);
		fillRoleSelect(user.getRoleValue());
		fillUserType(user);
		fillAuthorizeSOAP(user.getAuthorizeSOAP());
		fillShowUserPassword(user.getShowUserPasswordValue());
	}

	private void fillShowUserPassword(String value) {
		List<WebElement> list = tdWithForm.findElements(By.name("displayPwd"));
		RadioButtonGroup rButton = new RadioButtonGroup(list);
		rButton.selectByValue(value);
	}

	private void fillAuthorizeSOAP(String string) {
		ItemsUtil.fillCheckbox(new Checkbox(chRunWebServices.getWrappedElement()), string);
	}

	private void fillUserType(User user) {
		
		if (user.getUserTypeName() == null || 
				user.getUserTypeName().trim().isEmpty()) {
			sType.selectByValue(user.getUserTypeValue());
		} else {
			for (String optionText: sType.getSelectOptions()) {
				if (optionText.trim().equals(user.getUserTypeName())) {
					sType.selectByVisibleText(optionText);
				}
			}
		}
	}

	private void fillRoleSelect(String value) {
		sRole.selectByValue(value);
	}

	private void fillActiveStudySelect(User user) {
		if (user.getActiveStudyName() == null || 
				user.getActiveStudyName().trim().isEmpty()) {
			sActiveStudy.selectByIndex(user.getActiveStudyIndex());
		} else {
			for (String optionText: sActiveStudy.getSelectOptions()) {
				if (optionText.trim().equals(user.getActiveStudyName())) {
					sActiveStudy.selectByVisibleText(optionText);
				}
			}
		}
	}

	private void fillInstitutionalAffilationField(
			String institutionalAffiliation) {
		iInstitutionalAffiliation.type(institutionalAffiliation);
	}

	private void fillPhoneField(String phone) {
		iPhone.type(phone);
	}

	private void fillEmailField(String email) {
		iEmail.type(email);
	}

	private void fillLastNameField(String lastName) {
		iLastName.type(lastName);		
	}

	private void fillFirstNameField(String firstName) {
		iFirstName.type(firstName);
	}

	private void fillUserNameField(String userName) {
		iUserName.type(userName);
	}
	
	@Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1) &&
    			formWithData.isCurrentlyEnabled();
	}
}
