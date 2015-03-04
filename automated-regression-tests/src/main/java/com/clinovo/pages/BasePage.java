package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import org.openqa.selenium.WebDriver;

/**
 * Created by Anton on 07.07.2014.
 */
public class BasePage extends AbstractPage {
	@FindBy(jquery = "a[href$='j_spring_security_logout']")
    private WebElementFacade lLogOut;
	
	@FindBy(jquery = "a[href$='system']")
    private WebElementFacade lSystem;
	
	@FindBy(id="nav_Tasks_link")
	private WebElementFacade lTasksMenu;

	@FindBy(jquery = "a[href$='ListUserAccounts']")
    private WebElementFacade lUsers;
	
    @FindBy(linkText="Rules")
    private WebElementFacade lRules;

    @FindBy(linkText = "Add Subject")
    private WebElementFacade lAddSubject;

    @FindBy(jquery = "a[href$='studymodule']")
    private WebElementFacade lBuildStudy;

    @FindBy(linkText = "Study Audit Log")
    private WebElementFacade lStudyAuditLog;

    @FindBy(linkText = "Subject Matrix")
    private WebElementFacade lSubjectMatrix;

    @FindBy(id = "Submit")
    private WebElementFacade bSubmitA;
    
    @FindBy(jquery = "input[name$='ubmit'],input[id$='ubmit'],input[name$='onfirm']")
    private WebElementFacade bSubmitU;
    
    @FindBy(jquery = "input[name$='ontinue'],input[id$='ontinue'],input[name$='onfirm']")
    private WebElementFacade bContinueU;
    
    @FindBy(className = "alert")
    protected WebElementFacade dAlert;

    public boolean taskMenuIsVisible() { 
    	return lTasksMenu.isVisible(); 
    }

    public void goToManageRulesPage() {
        lTasksMenu.click();
        lRules.click();
    }

    public void goToAddSubjectPage() {
        lTasksMenu.click();
        lAddSubject.click();
    }

    public void goToBuildStudyPage() {
        lTasksMenu.click();
        lBuildStudy.click();
    }
    
    public void goToConfigureSystemPropertiesPage() {
        lTasksMenu.click();
        lSystem.click();
    }
    
    public void goToStudyAuditLog() {
        lTasksMenu.click();
        lStudyAuditLog.click();
    }

    public void goToSubjectMatrix() {
        lSubjectMatrix.click();
    }
    
    public void goToAdministerUsersPage() {
    	lTasksMenu.click();
    	lUsers.click();
    }
    
    public void clickSubmit() {
    	if (bSubmitA.isCurrentlyVisible()) {
    		bSubmitA.click();
    	} else {
    		bSubmitU.click();
    	}
    }
    
    public void clickContinue() {
    	bContinueU.click();
    }
    
    protected String currSubjectID;

    public void setCurrSubjectID(String subjectID) {
        this.currSubjectID = subjectID;
    }

    public String getCurrentSubjectID() {
        return currSubjectID;
    }

    public BasePage(WebDriver driver) {
        super(driver);
    }

    public void logOut() {
        lLogOut.click();
    }

	@Override
	public boolean isOnPage(WebDriver driver) {
		return false;
	}
}
