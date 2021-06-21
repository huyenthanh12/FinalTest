package com.common;

import com.constant.Constant;
import com.logigear.driver.DriverUtils;
import org.testng.annotations.*;

public class TestBase {

    @Parameters({"browser"})
    @BeforeMethod
    public void beforeMethod(@Optional String browser) {
        BrowserHelper.setCurrentBrowser(browser);
        BrowserHelper.startBrowser("default");
        DriverUtils.navigate(Constant.DASHBOARD_URL);
    }

    @AfterMethod
    public void afterMethod() {
        DriverUtils.quitBrowser();
    }

}
