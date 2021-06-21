package com.logigear.browser.ie;

import com.logigear.driver.manager.RemoteDriverManager;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RemoteIEDriver extends RemoteDriverManager {

    @Override
    public void createWebDriver(String key) throws Exception {
        try {
            InternetExplorerOptions ops = new InternetExplorerOptions();
            ops.merge(getCapabilities());
            String remoteURL = getRemoteUrl().toString();
            System.out.println("Running on remote URL " + remoteURL);
            this.webDrivers.put(key, new RemoteWebDriver(getRemoteUrl(), ops));
        } catch (Exception ex) {
            System.out.println("Getting error when creating Web Driver: " + ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }

}
