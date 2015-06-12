package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.webelements.Checkbox;
import org.openqa.selenium.WebDriver;

import com.clinovo.pages.beans.DNote;
import com.clinovo.utils.ItemsUtil;

/**
 * Created by Igor on 04.06.2015.
 * This is class for CreateDiscrepancyNote and ViewDiscrepancyNote pages 
 */
public class DNPage extends BasePage {

	public static final String PAGE_NAME = "DN page";
	public static final String PAGE_URL = "DiscrepancyNote";
	
	@FindBy(id = "mainForm")
    private WebElementFacade formWithData;
	
	// create DN
	@FindBy(xpath = ".//*[@name='SubmitExit']")
    private WebElementFacade bSubmitClose;
	
	@FindBy(id = "inputDescription")
    private WebElementFacade iDescription;
	
	@FindBy(name = "detailedDes")
    private WebElementFacade tDetailedNote;
	
	@FindBy(id = "typeId")
    private WebElementFacade sType;
	
	@FindBy(id = "userAccountId")
    private WebElementFacade sAssignToUser;
	
	@FindBy(name = "sendEmail")
    private WebElementFacade cEmailAssignedUser;
	
	// view DNs
	@FindBy(xpath = ".//*[@name='SubmitExit0']")
    private WebElementFacade bSubmitClose0;
	
	@FindBy(xpath = ".//a[@id='a0']")
    private WebElementFacade beginNewThread;
	
	@FindBy(id = "inputDescription0")
    private WebElementFacade iDescription0;
	
	@FindBy(name = "detailedDes0")
    private WebElementFacade tDetailedNote0;
	
	@FindBy(id = "typeId0")
    private WebElementFacade sType0;
	
	@FindBy(id = "userAccountId0")
    private WebElementFacade sAssignToUser0;
	
	@FindBy(name = "sendEmail0")
    private WebElementFacade cEmailAssignedUser0;
	
    public DNPage (WebDriver driver) {
        super(driver);
    }

    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}

    public void fillInAndSaveDN(DNote dn) {
		
		if (!beginNewThread.isCurrentlyVisible()) {
			
			iDescription.type(dn.getDescription());
			
			tDetailedNote.type(dn.getDetailedNote());
			
			if (sType.isCurrentlyVisible() || dn.isQuery()) sType.selectByValue(getValueByDNType(dn.getType()));
			
			if (sAssignToUser.isCurrentlyVisible()) sAssignToUser.selectByVisibleText(findOptionByUserName(dn.getAssignToUser()));
			
			if (cEmailAssignedUser.isCurrentlyVisible()) ItemsUtil.fillCheckbox(new Checkbox(cEmailAssignedUser), dn.getEmailAssignedUser());
		
			bSubmitClose.click();
			
		} else {
			
			beginNewThread.click();
			
			iDescription0.type(dn.getDescription());
			
			tDetailedNote0.type(dn.getDetailedNote());
			
			if (sType0.isCurrentlyVisible() || dn.isQuery()) sType0.selectByValue(getValueByDNType(dn.getType()));
			
			if (sAssignToUser0.isCurrentlyVisible()) sAssignToUser0.selectByVisibleText(findOptionByUserName(dn.getAssignToUser()));
			
			if (cEmailAssignedUser0.isCurrentlyVisible()) ItemsUtil.fillCheckbox(new Checkbox(cEmailAssignedUser0), dn.getEmailAssignedUser());
			
			bSubmitClose0.click();
		}
	}

	private String findOptionByUserName(String assignToUserName) {
		WebElementFacade assignToUser = sAssignToUser.isCurrentlyVisible()? sAssignToUser : sAssignToUser0;
		if (assignToUserName.isEmpty()) return assignToUser.getSelectedValue();
		for (String option: assignToUser.getSelectOptions()) {
			if (option.contains("("+assignToUserName+")")) {
				return option;
			}
		}
		return "";
	}

	private String getValueByDNType(String type) {
		switch (type){
		case "Annotation":
			return "2";
		case "Query":
			return "3";
		default: 
			return "2";
		}
	}
}
