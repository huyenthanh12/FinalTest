package com.logigear.browser.ie;

import com.logigear.driver.manager.LocalDriverManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;

public class LocalIEDriver extends LocalDriverManager {

    @Override
    public void createWebDriver(String key) throws Exception {
        try {
            WebDriverManager.iedriver().arch32().setup();
            InternetExplorerOptions ops = new InternetExplorerOptions();
            ops.merge(getCapabilities());
            WebDriver webDriver = new InternetExplorerDriver(ops);
            webDriver.manage().window().maximize();
            this.webDrivers.put(key, webDriver);
        } catch (Exception ex) {
            System.out.println("Getting error when creating Web Driver: " + ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }
}
