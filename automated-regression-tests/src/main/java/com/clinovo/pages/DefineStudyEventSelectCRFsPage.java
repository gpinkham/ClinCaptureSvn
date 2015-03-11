package com.clinovo.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.thucydides.core.annotations.findby.By;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.webelements.Checkbox;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.clinovo.utils.ItemsUtil;

public class DefineStudyEventSelectCRFsPage extends BasePage {

	public static final String PAGE_NAME = "Define Study Event - Select CRF(s) page";
	public static final String PAGE_URL = "DefineStudyEvent";
	
	@FindBy(jquery = "form[name='crfForm']")
    private WebElementFacade formWithData;
	
	@FindBy(jquery = "img[src$='/arrow_next.gif']")
    private WebElementFacade lNextPage;
	
	@FindBy(jquery = "img[src$='/arrow_back.gif']")
    private WebElementFacade lPreviousPage;
	
	@FindBy(jquery = "img[src$='/arrow_last.gif']")
    private WebElementFacade lLastPage;
	
	@FindBy(jquery = "img[src$='/arrow_first.gif']")
    private WebElementFacade lFirstPage;
	
    public DefineStudyEventSelectCRFsPage (WebDriver driver) {
        super(driver);
    }

    public void selectCRFs(List<String> listCRFs) {
    	Map<String, Boolean> nameCRFToSelectedMap = new HashMap<String, Boolean>();
    	for (String eCRF: listCRFs) {
    		nameCRFToSelectedMap.put(eCRF, false);
    	}
    	
    	for (int numOfLeftCRFs = listCRFs.size(); numOfLeftCRFs > 0;) {
    		for (String eCRF: listCRFs) {
        		if (!nameCRFToSelectedMap.get(eCRF)) {
        			numOfLeftCRFs = numOfLeftCRFs - selectCRFIfPresentInTable(eCRF, nameCRFToSelectedMap);
        		}
        	}
    		
    		if (lNextPage.isCurrentlyVisible()) {
    			lNextPage.click();
    		} else {
    			assert(numOfLeftCRFs == 0);
    			break;
    		}
    	} 
    }
    
    private int selectCRFIfPresentInTable(String eCRF, Map<String, Boolean> nameCRFToSelectedMap) {
    	if (isElementVisible(By.xpath(".//td[text()='"+eCRF+"']/..//input[@type='checkbox']"))) {
    		WebElement chbox = formWithData.findBy(By.xpath(".//td[text()='"+eCRF+"']/..//input[@type='checkbox']"));
    		ItemsUtil.fillCheckbox(new Checkbox(chbox), "true");
    		nameCRFToSelectedMap.put(eCRF, true);
    		return 1;
    	}
    	
		return 0;
	}

	@Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().endsWith(PAGE_URL));
	}
}
