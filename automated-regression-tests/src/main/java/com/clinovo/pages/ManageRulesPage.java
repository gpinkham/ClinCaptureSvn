package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

public class ManageRulesPage extends BasePage {
	
	public static final String PAGE_NAME = "Manage Rules page";

	@FindBy(name="createRule")
	private WebElementFacade bCreateRule;


    public ManageRulesPage(WebDriver driver) {
        super(driver);
    }
	
	public void click_create_rule_button() {
		bCreateRule.click();
	}
}
