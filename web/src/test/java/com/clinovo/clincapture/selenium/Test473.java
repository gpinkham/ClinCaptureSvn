package com.clinovo.clincapture.selenium;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Test473 {
	WebDriver driver;
	
	@Before 
	public void setUp() throws Exception {
		
		driver = new FirefoxDriver();

		driver.get("http://localhost:8080/ClinCapture-1.0.2.11-SNAPSHOT");
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

	@Test
	public void test() throws IOException, InterruptedException {
		//user login
		driver.findElement(By.name("j_username")).sendKeys("root");
		driver.findElement(By.name("j_password")).sendKeys("");
		driver.findElement(By.className("loginbutton")).click();
		
		//read name of Study
		String studyName = driver.findElement(By.xpath(".//*[@id='sidebar_Info_open']/td/div/span/a")).getAttribute("text").trim();
		//go to the Subject Matrix
		driver.findElement(By.xpath(".//*[@id='bt_Home']/div/div/div/table/tbody/tr/td[1]/b/ul/li[2]/a")).click();
		//click Remove/Restore icon on the first subject
		driver.findElement(By.xpath("html/body/table/tbody/tr[1]/td/table[2]/tbody/tr/td[2]/div[1]/form/div/table/tbody[1]/tr[2]/td[15]/div/a[2]/img")).click();
		//read tested name of Study
		String testedStudyName = driver.findElement(By.xpath("html/body/table/tbody/tr[1]/td/table[1]/tbody/tr/td/div[2]/table/tbody/tr/td[1]/b/a")).getAttribute("text").trim();
		
		assertTrue(studyName.contains(testedStudyName.replace("...", "")));
	}

}
