package com.clinovo.clincapture.selenium.base;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebDriverTestBase {

	public final WebDriver driver = new FirefoxDriver();

	public String tomcatUrl;
	public String contextPath;
	public String username;
	public String password;

	public static final String LIST_STUDY_SUBJECTS = "/ListStudySubjects";
	public static final String J_PASSWORD = "j_password";
	public static final String J_USERNAME = "j_username";
	public static final String SUBMIT = "submit";
	public static final String PAGES_STUDYMODULE = "/pages/studymodule";
	public static final int TIME_OUT_IN_SECONDS = 30;

	private void setTestProperties() throws Exception {
		String resource = "web-test.properties";
		Properties prop = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream(resource);
		prop.load(stream);
		username = prop.getProperty("selenium.username");
		password = prop.getProperty("selenium.password");
		tomcatUrl = prop.getProperty("selenium.tomcatUrl");
		contextPath = prop.getProperty("selenium.contextPath");
	}

	@Before
	public void login() throws Exception {
		setTestProperties();
		driver.manage().timeouts().implicitlyWait(TIME_OUT_IN_SECONDS, TimeUnit.SECONDS);
		driver.get(tomcatUrl + contextPath);
		driver.findElement(By.name(J_USERNAME)).sendKeys(username);
		driver.findElement(By.name(J_PASSWORD)).sendKeys(password);
		driver.findElement(By.name(SUBMIT)).click();
	}

	@After
	public void closeBrowser() {
		driver.quit();
	}

	public void openListStudySubjectsPage() {
		driver.get(tomcatUrl + contextPath + LIST_STUDY_SUBJECTS);
	}

	public void openBuildStudyPage() {
		driver.get(tomcatUrl + contextPath + PAGES_STUDYMODULE);
	}

	public void open(String url) {
		driver.get(tomcatUrl + contextPath + url);
	}

	public void selectRadio(String name, String value) {
		for (WebElement radio : driver.findElements(By.name(name))) {
			if (radio.getText().equalsIgnoreCase(value)) {
				radio.click();
			}
		}
	}
}
