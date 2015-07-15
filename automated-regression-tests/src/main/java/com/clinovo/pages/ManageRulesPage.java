package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

public class ManageRulesPage extends BasePage {
	
	public static final String PAGE_NAME = "Manage Rules page";
	public static final String PAGE_URL = "ViewRuleAssignment";
	
	@FindBy(name="ImportRule")
	private WebElementFacade bImportRule;


    public ManageRulesPage(WebDriver driver) {
        super(driver);
    }
	
	@Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}

	public void clickImportRulesButton() {
		bImportRule.click();
	}
}
