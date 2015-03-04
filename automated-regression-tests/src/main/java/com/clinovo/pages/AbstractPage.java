package com.clinovo.pages;

import org.openqa.selenium.WebDriver;

import net.thucydides.core.pages.PageObject;

public abstract class AbstractPage extends PageObject{

	public AbstractPage(WebDriver driver) {
		super(driver);
	}
	
	public abstract boolean isOnPage(WebDriver driver);
}
