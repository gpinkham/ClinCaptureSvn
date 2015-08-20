package com.clinovo.pages;

import java.util.concurrent.TimeUnit;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.webelements.Checkbox;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.clinovo.pages.beans.DNote;
import com.clinovo.utils.Common;
import com.clinovo.utils.ItemsUtil;

/**
 * Created by Igor on 04.06.2015.
 * This is class for CreateDiscrepancyNote and ViewDiscrepancyNote pages 
 */
public class DNPage extends BasePage {

	public static final String PAGE_NAME = "DN page";
	public static final String PAGE_URL = "DiscrepancyNote";
	
	@FindBy(xpath = ".//body")
    private WebElementFacade bodyWithData;
	
	// create DN
	@FindBy(xpath = ".//*[@name='SubmitExit']")
    private WebElementFacade bSubmitClose;
	
	@FindBy(xpath = ".//*[@name='Submit']")
    private WebElementFacade bSubmit;
	
	@FindBy(id = "inputDescription")
    private WebElementFacade iDescription;
	
	@FindBy(id = "selectDescription")
    private WebElementFacade sDescription;
	
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
	
	@FindBy(id = "selectDescription0")
    private WebElementFacade sDescription0;
	
	@FindBy(name = "detailedDes0")
    private WebElementFacade tDetailedNote0;
	
	@FindBy(id = "typeId0")
    private WebElementFacade sType0;
	
	@FindBy(id = "userAccountId0")
    private WebElementFacade sAssignToUser0;
	
	@FindBy(name = "sendEmail0")
    private WebElementFacade cEmailAssignedUser0;
	
	// popup for event DN
	@FindBy(id = "confirmation")
    private WebElementFacade divDialogMSG;
	
	@FindBy(id = "ignoreBoxMSG")
    private WebElementFacade chIgnoreBoxMSG;
	
	@FindBy(xpath = ".//input[@onclick = 'clickOkInMessageBox();']")
    private WebElementFacade bOKDialog;
	
    public DNPage (WebDriver driver) {
        super(driver);
    }

    @Override
	public boolean isOnPage(WebDriver driver) {
    	return (driver.getCurrentUrl().indexOf(PAGE_URL) > -1);
	}

    public void fillInAndSaveDN(DNote dn, String currentWindowId) {
    	bodyWithData.withTimeoutOf(60, TimeUnit.SECONDS).isCurrentlyVisible();
		if (!beginNewThread.isCurrentlyVisible() && !bSubmitClose0.isCurrentlyVisible()) {
			if (iDescription.isCurrentlyVisible() && !dn.getType().equals("RFC") && !dn.getType().equals("FVC")) {
				iDescription.type(dn.getDescription());
			} else {
				sDescription.selectByVisibleText(dn.getDescription());
			}

			tDetailedNote.type(dn.getDetailedNote());
			
			if (sType.isCurrentlyVisible() || dn.isQuery()) sType.selectByValue(DNote.getValueByDNType(dn.getType()));
			
			if (sAssignToUser.isCurrentlyVisible()) sAssignToUser.selectByVisibleText(findOptionByUserName(dn.getAssignToUser(), ""));
			
			if (cEmailAssignedUser.isCurrentlyVisible()) ItemsUtil.fillCheckbox(new Checkbox(cEmailAssignedUser), dn.getEmailAssignedUser());
		
			if (bSubmitClose.isCurrentlyVisible()) {
				bSubmitClose.click();
			} else {
				bSubmit.click();
			}
			
			// popup dialog event DN 
			Common.waitABit(2000);
			if (getDriver().getWindowHandles().contains(currentWindowId) && divDialogMSG.isCurrentlyVisible()) {
				ItemsUtil.fillCheckbox(new Checkbox(chIgnoreBoxMSG), "true");
				bOKDialog.click();
			}
		} else {
			
			if (beginNewThread.isCurrentlyVisible()) {
				beginNewThread.click();
			}
			
			if (iDescription0.isCurrentlyVisible() && !dn.getType().equals("RFC") && !dn.getType().equals("FVC")) {
				iDescription0.type(dn.getDescription());
			} else {
				sDescription0.selectByVisibleText(dn.getDescription());
			}
			
			tDetailedNote0.type(dn.getDetailedNote());
			
			if (sType0.isCurrentlyVisible() || dn.isQuery()) sType0.selectByValue(DNote.getValueByDNType(dn.getType()));
			
			if (sAssignToUser0.isCurrentlyVisible()) sAssignToUser0.selectByVisibleText(findOptionByUserName(dn.getAssignToUser(), ""));
			
			if (cEmailAssignedUser0.isCurrentlyVisible()) ItemsUtil.fillCheckbox(new Checkbox(cEmailAssignedUser0), dn.getEmailAssignedUser());
			
			bSubmitClose0.click();
		}
	}

    public void fillInAndSaveDNForEvent(DNote dn, String currentWindowId) {
    	fillInAndSaveDN(dn, currentWindowId);
		// popup dialog event DN 
		Common.waitABit(2000);
		if (getDriver().getWindowHandles().contains(currentWindowId) && divDialogMSG.isCurrentlyVisible()) {
			ItemsUtil.fillCheckbox(new Checkbox(chIgnoreBoxMSG), "true");
			bOKDialog.click();
		}
	}
    
	private String findOptionByUserName(String assignToUserName, String dnParentID) {
		WebElementFacade assignToUser = sAssignToUser.isCurrentlyVisible()? sAssignToUser : sAssignToUser0;
		assignToUser = assignToUser.isCurrentlyVisible()? assignToUser : bodyWithData.find(By.xpath(".//select[@id='userAccountId"+dnParentID+"']"));;
		if (assignToUserName.isEmpty()) return assignToUser.getSelectedVisibleTextValue();
		for (String option: assignToUser.getSelectOptions()) {
			if (option.contains("("+assignToUserName+")")) {
				return option;
			}
		}
		return "";
	}

	public void findAndFillInAndClickSubmit(DNote dn) {		
		if (dn.getParentID().isEmpty() && !dn.getParentDescription().isEmpty()) {
			for (WebElement div: bodyWithData.findElements(By.xpath(".//b[text()='"+dn.getParentDescription()+"']/../../../../..//td[contains(text(),'ID:')]"))) {
				dn.setParentID(div.getText());
				// take first matching div
				break;
			}
		}
			
		if (dn.getResolutionStatus().equals("Updated")) {
			bodyWithData.findElement(By.xpath(".//input[@id='resStatus2"+dn.getParentID()+"']")).click();				
		} else {
			bodyWithData.findElement(By.xpath(".//input[@id='resStatus4"+dn.getParentID()+"']")).click();
		}
			
		WebElementFacade sDescription1 = dn.getResolutionStatus().equals("Updated")? bodyWithData.find(By.xpath(".//select[@id='selectUpdateDescription"+dn.getParentID()+"']")) :
				bodyWithData.find(By.xpath(".//select[@id='selectCloseDescription"+dn.getParentID()+"']"));
		WebElementFacade tDetailedNote1 = bodyWithData.find(By.xpath(".//textarea[@name='detailedDes"+dn.getParentID()+"']"));
		WebElementFacade sAssignToUser1 = bodyWithData.find(By.xpath(".//select[@id='userAccountId"+dn.getParentID()+"']"));
		WebElementFacade cEmailAssignedUser1 = bodyWithData.find(By.xpath(".//input[@name='sendEmail"+dn.getParentID()+"']"));
				
		sDescription1.selectByValue(dn.getDescription());
		tDetailedNote1.type(dn.getDetailedNote());
		sAssignToUser1.selectByVisibleText(findOptionByUserName(dn.getAssignToUser(), dn.getParentID()));
		ItemsUtil.fillCheckbox(new Checkbox(cEmailAssignedUser1), dn.getEmailAssignedUser());
		
		bodyWithData.findElement(By.xpath(".//input[@name='SubmitExit"+dn.getParentID()+"']")).click();	
	}
}
