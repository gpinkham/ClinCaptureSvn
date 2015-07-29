package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

import com.clinovo.pages.beans.CRF;

public class PreviewCRFPage extends BasePage {

	public static final String PAGE_NAME = "Preview CRF page";
	public static final String PAGE_URL = "CreateCRFVersion?action=confirm";
	
	@FindBy(xpath = ".//span[@class='crf_name']")
    private WebElementFacade spanCRFName;
	
	@FindBy(xpath = ".//span[@class='version_name']")
    private WebElementFacade spanCRFVersion;
	
    public PreviewCRFPage (WebDriver driver) {
        super(driver);
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}

	public void setCRFParameters(CRF crf) {
		crf.setName(spanCRFName.getText());
		crf.setVersion(spanCRFVersion.getText());
	}
}
