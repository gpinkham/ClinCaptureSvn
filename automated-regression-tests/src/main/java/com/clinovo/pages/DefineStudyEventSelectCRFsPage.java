package com.clinovo.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.thucydides.core.annotations.findby.By;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.webelements.Checkbox;
import net.thucydides.core.webelements.RadioButtonGroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.clinovo.utils.ItemsUtil;

public class DefineStudyEventSelectCRFsPage extends BasePage {

	public static final String PAGE_NAME = "Define Study Event - Select CRF(s) page";
	public static final String PAGE_URL = "DefineStudyEvent";
	
	@FindBy(jquery = "form")
    private WebElementFacade formWithData;
	
	@FindBy(jquery = "img[src$='/arrow_next.gif'].parent()")
    private WebElementFacade lNextPage;
	
	@FindBy(jquery = "img[src$='/arrow_back.gif'].parent()")
    private WebElementFacade lPreviousPage;
	
	@FindBy(jquery = "img[src$='/arrow_last.gif'].parent()")
    private WebElementFacade lLastPage;
	
	@FindBy(jquery = "img[src$='/arrow_first.gif'].parent()")
    private WebElementFacade lFirstPage;
	
    public DefineStudyEventSelectCRFsPage (WebDriver driver) {
        super(driver);
    }

    public void selectCRFs(List<String> listCRFs) {
    	Map<String, Boolean> nameCRFToSelectedMap = new HashMap<String, Boolean>();
    	for (String aCRF: listCRFs) {
    		nameCRFToSelectedMap.put(aCRF, false);
    	}
    	
    	do {
    		for (String aCRF: listCRFs) {
        		if (!nameCRFToSelectedMap.get(aCRF)) selectCRFIfPresentInTable(aCRF, nameCRFToSelectedMap);
        	}
    		
    		if (lNextPage.isCurrentlyVisible()) {
    			lNextPage.click();
    		}
    	} while (lNextPage.isCurrentlyVisible());
    }
    
    private void selectCRFIfPresentInTable(String aCRF, Map<String, Boolean> nameCRFToSelectedMap) {
    	WebElement chbox = formWithData.findElement(By.jquery("td.filter(function(){ return $(this).text() === '" + 
    			aCRF + "';}).parent()")).findElement(By.jquery("input[type='checkbox']"));
    	if (chbox != null) ItemsUtil.fillCheckbox(new Checkbox(chbox), "true");
	}

	@Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().endsWith(PAGE_URL));
	}
}
