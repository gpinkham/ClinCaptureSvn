package com.clinovo.pages;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.pages.components.HtmlTable;

import org.openqa.selenium.WebDriver;

/**
 * Created by Anton on 07.07.2014.
 */
public class ViewStudyLogPage extends BasePage {

	public static final String PAGE_NAME = "View Study Log page";

    public ViewStudyLogPage (WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "studyAuditLogs")
    private WebElementFacade tblStudyAuditLogs;

    @FindBy(css = ".first_level_header")
    private WebElementFacade textHeading;

    public boolean isTblStudyAuditLogsAvailable() {
        return tblStudyAuditLogs.isCurrentlyVisible();
    }

    public boolean isTblStudyAuditLogsEmpty() {
        return new HtmlTable(tblStudyAuditLogs).getRowElements().isEmpty();
    }

    public boolean isOnPage() {
        return textHeading.getText().contains("View Study");
    }
}
