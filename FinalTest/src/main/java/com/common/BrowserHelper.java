package com.common;

import com.constant.Constant;
import com.logigear.driver.DriverProperty;
import com.logigear.driver.DriverUtils;
import com.logigear.helper.BrowserSettingHelper;

public class BrowserHelper {

    private static ThreadLocal<String> CURRENT_BROWSER = new InheritableThreadLocal<>();

    private static void startBrowser(String key, DriverProperty property) {

        try {
            DriverUtils.getDriver(key, property);
            DriverUtils.setPageLoadTimeout(Constant.DRIVER_TIMEOUT);
            DriverUtils.setTimeOut(Constant.DRIVER_PAGE_LOAD_TIMEOUT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void startBrowser(String key) {
        try {
            DriverProperty property = BrowserSettingHelper.getDriverProperty(Constant.BROWSER_SETTING_FILE, getCurrentBrowser());
            startBrowser(key, property);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setCurrentBrowser(String browser) {
        Log.info("Running with " + browser);
        CURRENT_BROWSER.set(browser);
    }

    public static String getCurrentBrowser() {
        return CURRENT_BROWSER.get();
    }
}
