package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

public class ImportRuleDataPage extends BasePage {
	
	public static final String PAGE_NAME = "Import Rule Data page";
	public static final String PAGE_URL = "ImportRule";
	
	@FindBy(xpath=".//input[@class='button_medium medium_continue' and @type='submit']")
	private WebElementFacade bContinue;

	@FindBy(xpath=".//*[@id='dlgBtnYes']")
	private WebElementFacade bYesInPopup;
	
	@FindBy(xpath=".//*[@id='dlgBtnNo']")
	private WebElementFacade bNoInPopup;
	
	@FindBy(name="xml_file")
	private WebElementFacade iBrowseFile;
	
    public ImportRuleDataPage(WebDriver driver) {
        super(driver);
    }
	
	public void clickContinue() {
		bContinue.click();
	}
	
	@Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}

	public void browseRuleFile(String filepath) {
		upload(filepath).to(iBrowseFile);
	}

	public void clickYesButtonInPopup() {
		bYesInPopup.waitUntilVisible();
		bYesInPopup.click();
	}
}
