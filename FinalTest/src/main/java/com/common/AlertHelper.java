package com.common;

import com.constant.Constant;
import com.logigear.driver.DriverUtils;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AlertHelper {

    /**
     * To click on the 'Cancel' button of the alert.
     */
    public static void dismissAlert() {
        waitForAlertVisible();
        DriverUtils.getWebDriver().switchTo().alert().dismiss();
    }

    /**
     * To click on the 'OK' button of the alert.
     */
    public static void acceptAlert() {
        waitForAlertVisible();
        DriverUtils.getWebDriver().switchTo().alert().accept();
    }

    /**
     * To get the alert message.
     */
    public static String getTextAlert() {
        waitForAlertVisible();
        return DriverUtils.getWebDriver().switchTo().alert().getText();
    }


    /**
            * To wait for the alert appear.
     */
    public static void waitForAlertVisible() {
        WebDriverWait wait = new WebDriverWait(DriverUtils.getWebDriver(), Constant.TIMEOUT_IN_SECONDS);
        wait.until(ExpectedConditions.alertIsPresent());
    }
}
