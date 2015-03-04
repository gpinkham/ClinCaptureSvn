package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

public class ConfirmCreateSitePage extends BasePage {

	public static final String PAGE_NAME = "Confirm Site page";
	public static final String PAGE_URL = "CreateSubStudy";
	
	@FindBy(jquery = "td[class='aka_revised_content']")
    private WebElementFacade tdAkaRevisedContent;
	
    public ConfirmCreateSitePage (WebDriver driver) {
        super(driver);
    }

    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1) && (tdAkaRevisedContent.isCurrentlyEnabled());
	}
}
