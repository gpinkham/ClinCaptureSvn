package com.clinovo.clincapture.selenium;

import static org.junit.Assert.assertFalse;

import com.clinovo.clincapture.selenium.base.WebDriverTestBase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class TestStudyParameterEventLocationRequired extends WebDriverTestBase {

	@Test
	public void testThatLocationFieldOnTheViewEventPageIsNotDisplayed() {
		openBuildStudyPage();

		new Select(driver.findElement(By.name("studyStatus"))).selectByValue("4");// Design

		driver.findElement(By.name("saveStudyStatus")).click();

		open("/UpdateStudyNew?id=1"); // open Study Configuration page

		selectRadio("eventLocationRequired", "not_used");

		WebElement element = driver.findElement(By.name("description"));
		if (element.getText().trim().equals("")) {
			element.clear();
			element.sendKeys("summary");
		}

		((JavascriptExecutor) driver).executeScript("document.getElementById('section3').setAttribute('style', '');");

		element = driver.findElement(By.name("expectedTotalEnrollment"));
		if (element.getAttribute("value").trim().equals("0")) {
			element.clear();
			element.sendKeys("500");
		}

		driver.findElement(By.name("Submit")).submit();

		new Select(driver.findElement(By.name("studyStatus"))).selectByValue("1");// Available

		driver.findElement(By.name("saveStudyStatus")).click();

		openListStudySubjectsPage();

		open("/EnterDataForStudyEvent?eventId=1"); // open View Event page

		assertFalse(driver.findElement(By.id("globalRecord")).getText().toLowerCase().contains("location"));
	}
}
