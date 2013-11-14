package com.clinovo.clincapture.selenium;

import com.clinovo.clincapture.selenium.base.BaseTest;

import java.util.Date;

import org.junit.Test;

public class EditUserTest extends BaseTest {

    @Test
    public void testPhoneNumberChange() throws Exception {
        login(ROOT, ROOT_PASSWORD);

        openListUserAccountsPage();

        String phone = "" + new Date().getTime();
        phone = "+" + (phone.length() > 15 ? phone.substring(0, 15) : phone);

        wait("selenium.isElementPresent(\"//input[@name='ebl_filterKeyword']\")");
        wait("selenium.isElementPresent(\"//input[@type='submit' and @class='button_search']\")");

        wait("selenium.isElementPresent(\"//input[@name='ebl_filterKeyword']\")");
        selenium.type("//input[@name='ebl_filterKeyword']", ROOT);
        selenium.click("//input[@type='submit' and @class='button_search']");

        wait("selenium.isElementPresent(\"//a[contains(@href, 'EditUserAccount?userId=1')]\")");
        selenium.click("//a[contains(@href, 'EditUserAccount?userId=1')]");

        wait("selenium.isElementPresent(\"//input[@type='text' and @id='phone']\")");
        wait("selenium.isElementPresent(\"//input[@type='submit' and @name='continue']\")");
        selenium.type("//input[@id='phone']", phone);
        selenium.click("//input[@type='submit' and @name='continue']");

        wait("selenium.isElementPresent(\"//input[@type='submit' and @name='submit']\")");
        selenium.click("//input[@type='submit' and @name='submit']");

        wait("selenium.isElementPresent(\"//a[contains(@href, 'ViewUserAccount?userId=1')]\")");
        selenium.click("//a[contains(@href, 'ViewUserAccount?userId=1')]");

        wait("selenium.isElementPresent(\"//input[@type='button' and @name='BTN_Smart_Back']\")");

        assertTrue(selenium.getText("//div[@class='tablebox_center']").contains(phone));
    }
}
