package com.logigear.control.common.imp;

import com.logigear.control.base.imp.BaseControl;
import com.logigear.control.base.imp.Clickable;
import com.logigear.control.common.IComboBox;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotSelectableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ComboBox extends Clickable implements IComboBox {

    private Logger logger = Logger.getLogger(Element.class);

    public ComboBox(By locator) {
        super(locator);
    }

    public ComboBox(String locator) {
        super(locator);
    }

    public ComboBox(String locator, Object... value) {
        super(locator, value);
    }

    public ComboBox(BaseControl parent, String locator) {
        super(parent, locator);
    }

    public ComboBox(BaseControl parent, By locator) {
        super(parent, locator);
    }

    public ComboBox(BaseControl parent, String locator, Object... value) {
        super(parent, locator, value);
    }

    @Override
    public List<String> getOptions() {
        return getOptionElements().stream().map(WebElement::getText).collect(Collectors.toList());
    }

    private List<WebElement> getOptionElements() {
        Select select = new Select(getElement());
        return select.getOptions();
    }

    public void waitForOptionAvailable(String optionText, int timeOutInSecond) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSecond);
            wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(this.getElement(),
                    By.xpath(String.format(".//option[normalize-space(.)=\"%s\"]", optionText))));
        } catch (Exception e) {
            logger.error(String.format("waitForOptionAvailable: Has error with control '%s': %s",
                    getLocator().toString(), e.getMessage().split("\n")[0]));
        }
    }

    @Override
    public int totalOptions() {
        Select select = new Select(getElement());
        List<WebElement> options = select.getOptions();
        return options.size();
    }

    @Override
    public String getSelected() {
        Select select = new Select(getElement());
        return select.getFirstSelectedOption().getText();
    }

    @Override
    public void select(String text) {
        WebElement element = getElement();
        if (!element.isEnabled())
            throw new ElementNotSelectableException(String.format("Element with locator: %s is not selectable", this.getLocator()));
        Select select = new Select(element);
        try {
            select.selectByVisibleText(text);
        } catch (NoSuchElementException e) {
            logger.error(String.format("Option %s is not available. Available options: %s", text, getOptions()));
            throw e;
        }
    }

    @Override
    public void select(int index) {
        Select select = new Select(getElement());
        select.selectByIndex(index);
    }

    public void selectByPartialText(String text) {
        this.getChildElement(String.format(".//option[contains(text(),'%s')]", text)).click();
    }
}
