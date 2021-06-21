package com.test.login;

import com.common.Log;
import com.common.TestBase;
import com.constant.Constant;
import com.page.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends TestBase {
    @Test
    public void loginWithValidAccount() {
        LoginPage loginPage = new LoginPage();
        Log.info("Navigate to TA DashBoard");

        Log.info("Choose Sample Repository on Repository ComboBox ");
        loginPage.selectCbbRepository(Constant.SAMPLE_REPOSITORY);

        Log.info("Input Username");
        loginPage.inputUsername(Constant.USER_NAME);

        Log.info("Input Password");
        loginPage.inputPassWord(Constant.PASSWORD);

        Log.info("Click Login Button");
        loginPage.clickLoginBtn();

        Log.info("Verify that Dashboard Main page appears");
        Assert.assertTrue(loginPage.isPageNameVisible(Constant.OVERVIEW_PAGE), "Login failed");
    }
}
