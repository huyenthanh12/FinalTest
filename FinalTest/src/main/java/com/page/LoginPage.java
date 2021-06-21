package com.page;


import com.logigear.control.common.imp.Button;
import com.logigear.control.common.imp.ComboBox;
import com.logigear.control.common.imp.Element;
import com.logigear.control.common.imp.TextBox;

public class LoginPage extends GeneralPage {
    private final ComboBox cbbRepository = new ComboBox("id=repository");
    private final TextBox txtUserName = new TextBox("id=username");
    private final TextBox txtPassWord = new TextBox("id=password");
    private final Button btnLogin = new Button("class=btn-login");
    private final Element mainMenuPageName = new Element("//div[@id='main-menu']//li/a[normalize-space()='%s']");

    //Action

    public void selectCbbRepository(String value) {
        cbbRepository.select(value);
    }

    public void inputUsername(String userName) {
        txtUserName.enter(userName);
    }

    public void inputPassWord(String passWord) {
        txtPassWord.enter(passWord);
    }

    public void clickLoginBtn() {
        btnLogin.click();
    }

    //Verify action
    public boolean isPageNameVisible(String pageName) {
        mainMenuPageName.setDynamicValue(pageName);
        return mainMenuPageName.isExist() && mainMenuPageName.isVisible();
    }
}
