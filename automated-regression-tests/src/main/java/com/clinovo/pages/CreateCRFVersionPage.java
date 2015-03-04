package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;

/**
 * 
 */
public class CreateCRFVersionPage extends BasePage {

	public static final String PAGE_NAME = "Create a New CRF page";
	public static final String PAGE_URL = "CreateCRFVersion";
	
    public CreateCRFVersionPage (WebDriver driver) {
        super(driver);
    }

    @FindBy(jquery = "input[type='submit'][class='button_medium']")
    private WebElementFacade bContinue;
    
    @FindBy(id = "excel_file_path")
    private WebElementFacade bBrowseFile;
    
    public void clickBrowseFile() {
    	bBrowseFile.click();
    }
    
    public void browseCRFFile(String filepath) {
    	upload(filepath).to(bBrowseFile);
    }
    
    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}
    
    @Override
	public void clickContinue() {
    	bContinue.click();
	}
}
