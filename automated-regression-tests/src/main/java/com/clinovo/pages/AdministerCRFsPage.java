package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

import com.clinovo.pages.beans.CRF;

public class AdministerCRFsPage extends BasePage {

	public static final String PAGE_NAME = "Administer CRFs page";
	public static final String PAGE_URL = "CreateCRFVersion";
	
    @FindBy(name = "Create CRF")
    private WebElementFacade bCreateCRF;

    @FindBy(name = "ebl_filterKeyword")
    private WebElementFacade iFind;
    
    @FindBy(xpath = ".//input[@class='button_search']")
    private WebElementFacade iFindButton;
    
    @FindBy(xpath = ".//*[@id='contentTable']/tbody/tr[2]")
    private WebElementFacade rowFirst;
    
    public AdministerCRFsPage (WebDriver driver) {
        super(driver);
    }

    public void clickCreateCRFButton() {
    	bCreateCRF.click();
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return bCreateCRF.isDisplayed();
	}

	public void filter(CRF crf) {
		iFind.type(crf.getName());
		iFindButton.click();
	}

	public void checkCRFRowIsPresent(CRF crf) {
		rowFirst.isCurrentlyVisible();
	}
}
