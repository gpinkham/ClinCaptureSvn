package com.clinovo.utils;


import org.openqa.selenium.WebDriver;

import java.util.Set;

/**
 * Created by Anton on 25.07.2014.
 */
public class Common {

    private static String parentHandleS;

    public static void switchToLastHandle(WebDriver driver, String parentHandle) {
        Set<String> newWindowHandles = driver.getWindowHandles();

        for (String windowHandle : newWindowHandles)
            if (!windowHandle.equals(parentHandle)) {
                parentHandleS = parentHandle;
                driver.switchTo().window(windowHandle);
            }
    }

    public static void switchBackToParentHandle(WebDriver driver) {
        if (!parentHandleS.isEmpty()) {
            driver.switchTo().window(parentHandleS);
            parentHandleS = "";
        }
    }
}
