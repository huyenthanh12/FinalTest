package com.logigear.control.base.imp;

import com.logigear.control.base.IBaseControl;
import com.logigear.control.common.imp.Element;
import com.logigear.driver.DriverUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BaseControl implements IBaseControl {

    private Logger logger = Logger.getLogger(Element.class);
    private String locator;
    private By byLocator;
    private String dynamicLocator;
    private BaseControl parent;

    public BaseControl(String locator) {
        this.locator = locator;
        this.dynamicLocator = locator;
        this.byLocator = getByLocator();
    }

    public BaseControl(By byLocator) {
        this.byLocator = byLocator;
    }

    public BaseControl(String locator, Object... args) {
        this.dynamicLocator = locator;
        this.locator = String.format(dynamicLocator, args);
        this.byLocator = getByLocator();
    }

    public BaseControl(BaseControl parent, String locator) {
        this.locator = locator;
        this.dynamicLocator = locator;
        this.byLocator = getByLocator();
        this.parent = parent;
    }

    public BaseControl(BaseControl parent, By byLocator) {
        this.byLocator = byLocator;
        this.parent = parent;
    }

    public BaseControl(BaseControl parent, String locator, Object... args) {
        this.dynamicLocator = locator;
        this.locator = String.format(dynamicLocator, args);
        this.byLocator = getByLocator();
        this.parent = parent;
    }

    protected WebDriver getDriver() {
        return DriverUtils.getWebDriver();
    }

    protected JavascriptExecutor jsExecutor() {
        return (JavascriptExecutor) getDriver();
    }

    private By getByLocator() {
        String body = this.locator.replaceAll("[\\w\\s]*=(.*)", "$1").trim();
        String type = this.locator.replaceAll("([\\w\\s]*)=.*", "$1").trim();
        switch (type.toLowerCase()) {
            case "css":
                return By.cssSelector(body);
            case "id":
                return By.id(body);
            case "class":
                return By.className(body);
            case "link":
                return By.linkText(body);
            case "xpath":
                return By.xpath(body);
            case "text":
                return By.xpath(String.format("//*[contains(text(), '%s')]", body));
            case "name":
                return By.name(body);
            default:
                return By.xpath(locator);
        }
    }

    private WebElement getParent() {
        return parent.getElement();
    }

    @Override
    public void dragAndDrop(int xOffset, int yOffset) {
        Actions actions = new Actions(getDriver());
        actions.dragAndDropBy(getElement(), xOffset, yOffset).build().perform();
    }

    @Override
    public void dragAndDrop(BaseControl target) {
        Actions actions = new Actions(getDriver());
        actions.dragAndDrop(getElement(), target.getElement()).build().perform();
    }

    public void focus() {
        DriverUtils.execJavaScript("arguments[0].focus();", getElement());
    }

    @Override
    public String getAttribute(String attributeName) {
        try {
            logger.debug(String.format("Get attribute '%s' of element %s", attributeName, getLocator().toString()));
            return getElement().getAttribute(attributeName);
        } catch (Exception e) {
            logger.error(String.format("Has error with control '%s': %s", getLocator().toString(), e.getMessage().split("\n")[0]));
            throw e;
        }

    }

    @Override
    public WebElement getChildElement(String xpath) {
        return getElement().findElement(By.xpath(xpath));
    }

    @Override
    public List<WebElement> getChildElements() {
        return getChildElements("./*");
    }

    @Override
    public List<WebElement> getChildElements(String xpath) {
        return getElement().findElements(By.xpath(xpath));
    }

    @Override
    public String getClassName() {
        return getAttribute("class");
    }

    @Override
    public WebElement getElement() {
        WebElement element = null;
        try {
            if (parent != null) {
                WebElement eleParent = parent.getElement();
                element = eleParent.findElement(getLocator());
            } else {
                element = getDriver().findElement(getLocator());
            }
            return element;
        } catch (StaleElementReferenceException e) {
            logger.error(
                    String.format("StaleElementReferenceException '%s': %s", getLocator().toString(), e.getMessage().split("\n")[0]));
            return getElement();
        }
    }

    @Override
    public List<WebElement> getElements() {
        if (parent != null)
            return parent.getElement().findElements(getLocator());
        return getDriver().findElements(getLocator());
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseControl> List<T> getListElements(Class<?> clazz) {
        String js = "function getElementTreeXPath(e){for(var n=[];e&&1==e.nodeType;e=e.parentNode){for(var o=0,r=e.previousSibling;r;r=r.previousSibling)r.nodeType!=Node.DOCUMENT_TYPE_NODE&&r.nodeName==e.nodeName&&++o;var t=e.nodeName.toLowerCase(),a=o?'['+(o+1)+']':'[1]';n.splice(0,0,t+a)}return n.length?'/'+n.join('/'):null} return getElementTreeXPath(arguments[0]);";
        List<T> result = new ArrayList<T>();
        List<WebElement> list = getElements();
        for (WebElement webEle : list) {
            try {
                String xpath = (String) DriverUtils.execJavaScript(js, webEle);
                Constructor<?> ctor = clazz.getDeclaredConstructor(By.class);
                ctor.setAccessible(true);
                T element = (T) ctor.newInstance(By.xpath(xpath));
                result.add(element);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public By getLocator() {
        return this.byLocator;
    }

    @Override
    public String getLocatorString() {
        return this.locator;
    }

    @Override
    public String getTagName() {
        return getElement().getTagName();
    }

    @Override
    public String getText() {
        try {
            logger.debug(String.format("Get text of element %s", getLocator().toString()));
            return getElement().getText();
        } catch (Exception e) {
            logger.error(String.format("Has error with control '%s': %s", getLocator().toString(), e.getMessage().split("\n")[0]));
            throw e;
        }
    }

    public List<String> getListText() {
        return getElements().stream().map(WebElement::getText).collect(Collectors.toList());
    }

    @Override
    public String getValue() {
        try {
            logger.debug(String.format("Get value of element %s", getLocator().toString()));
            return getElement().getAttribute("value");
        } catch (Exception e) {
            logger.error(String.format("Has error with control '%s': %s", getLocator().toString(), e.getMessage().split("\n")[0]));
            throw e;
        }

    }

    @Override
    public boolean isClickable() {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), DriverUtils.getTimeOut());
            return (wait.until(ExpectedConditions.elementToBeClickable(getLocator())) != null);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isDynamicLocator() {
        return this.locator != null && this.locator.toLowerCase().matches("(.*)%[s|d](.*)");
    }

    @Override
    public boolean isEnabled() {
        try {
            logger.debug(String.format("is control enabled or not: %s", getLocator().toString()));
            return getElement().isEnabled();
        } catch (Exception e) {
            logger.error(String.format("IsEnabled: Has error with control '%s': %s", getLocator().toString(),
                    e.getMessage().split("\n")[0]));
            return false;
        }
    }

    @Override
    public boolean isExist() {
        return isExist(DriverUtils.getTimeOut());
    }

    @Override
    public boolean isExist(int timeOutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSeconds);
            return (wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(getLocator())) != null);
        } catch (Exception e) {
            logger.error(String.format("IsExisted: Has error with control '%s': %s", this.getLocator().toString(), e.getMessage()));
            return false;
        }
    }

    @Override
    public boolean isSelected() {
        try {
            logger.debug(String.format("is control selected or not: %s", getLocator().toString()));
            return getElement().isSelected();
        } catch (Exception e) {
            logger.error(String.format("IsSelected: Has error with control '%s': %s", getLocator().toString(),
                    e.getMessage().split("\n")[0]));
            return false;
        }
    }

    @Override
    public boolean isVisible() {
        return isVisible(DriverUtils.getTimeOut());
    }

    @Override
    public boolean isVisible(int timeOutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSeconds);
            return (wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(getLocator())) != null);
        } catch (Exception e) {
            logger.error(String.format("IsVisible: Has error with control '%s': %s", this.getLocator().toString(), e.getMessage()));
            return false;
        }
    }

    @Override
    public void mouseHoverJScript() {
        String mouseOverScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover', true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
        jsExecutor().executeScript(mouseOverScript, getElement());

    }

    @Override
    public void moveTo() {
        Actions actions = new Actions(getDriver());
        actions.moveToElement(getElement()).build().perform();
    }

    @Override
    public void moveTo(int x, int y) {
        WebElement element = getElement();
        int absX = element.getLocation().x + x;
        int absY = element.getLocation().y + y;

        Actions actions = new Actions(getDriver());
        actions.moveByOffset(absX, absY).build().perform();
    }

    @Override
    public void moveToCenter() {
        WebElement element = getElement();
        int x = element.getLocation().x + element.getSize().width / 2;
        int y = element.getLocation().y + element.getSize().height / 2;

        Actions actions = new Actions(getDriver());
        actions.moveByOffset(x, y).build().perform();
    }

    @Override
    public void scrollElementToCenterScreen() {
        DriverUtils.waitForAutoScrollingStopped();
        String js = "Element.prototype.documentOffsetTop=function(){return this.offsetTop+(this.offsetParent?this.offsetParent.documentOffsetTop():0)};var top=arguments[0].documentOffsetTop()-window.innerHeight/2;window.scrollTo(0,top);";
        DriverUtils.execJavaScript(js, getElement());
    }

    @Override
    public void scrollToView() {
        try {
            jsExecutor().executeScript("arguments[0].scrollIntoView(true);", getElement());
        } catch (JavascriptException e) {
            WebElement element = getElement();
            int x = element.getRect().x;
            int y = element.getRect().y;
            String js = String.format("window.scrollTo(%s, %s);", x, y);
            jsExecutor().executeScript(js);
        }
    }

    @Override
    public void scrollToView(int offsetX, int offsetY) {
        try {
            jsExecutor().executeScript("arguments[0].scrollIntoView(true);", getElement());
        } catch (JavascriptException e) {
            WebElement element = getElement();
            int x = element.getRect().x + offsetX;
            int y = element.getRect().y + offsetY;
            String js = String.format("window.scrollTo(%s, %s);", x, y);
            jsExecutor().executeScript(js);
        }
    }

    @Override
    public void setAttributeJS(String attributeName, String value) {
        try {
            logger.debug(String.format("Set attribute for %s", getLocator().toString()));
            jsExecutor().executeScript(String.format("arguments[0].setAttribute('%s','%s');", attributeName, value),
                    getElement());
        } catch (Exception e) {
            logger.error(String.format("Has error with control '%s': %s", getLocator().toString(), e.getMessage().split("\n")[0]));
            throw e;
        }

    }

    /**
     * Set value for dynamic control.
     *
     * @param args
     * @Example TextBox myTextBox = new TextBox("//input[@value='%s']"); </br>
     * myTextBox.setDynamicValue("example");
     */
    @Override
    public void setDynamicValue(Object... args) {
        this.locator = String.format(this.dynamicLocator, args);
        this.byLocator = getByLocator();
    }

    @Override
    public void submit() {
        getElement().submit();
    }

    @Override
    public void waitForDisappear() {
        waitForDisappear(DriverUtils.getTimeOut());
    }

    @Override
    public void waitForDisappear(int timeOutInSeconds) {
        if (getElements().size() == 0) {
            return;
        }
        try {
            logger.info(String.format("Wait for control disappear %s", getLocator().toString()));
            WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSeconds);
            wait.until(ExpectedConditions.not(ExpectedConditions.presenceOfElementLocated(getLocator())));
        } catch (Exception e) {
            logger.error(String.format("waitForDisappear: Has error with control '%s': %s", getLocator().toString(),
                    e.getMessage().split("\n")[0]));
        }
    }

    @Override
    public void waitForDisplay() {
        waitForDisplay(DriverUtils.getTimeOut());
    }

    @Override
    public void waitForDisplay(int timeOutInSeconds) {
        try {
            logger.info(String.format("Wait for control display %s", getLocator().toString()));
            WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSeconds);
            wait.until(ExpectedConditions.presenceOfElementLocated(getLocator()));
        } catch (Exception e) {
            logger.error(String.format("WaitForDisplay: Has error with control '%s': %s", getLocator().toString(),
                    e.getMessage().split("\n")[0]));
        }
    }

    @Override
    public void waitForElementClickable() {
        waitForElementClickable(DriverUtils.getTimeOut());
    }

    @Override
    public void waitForElementClickable(int timeOutInSecond) {
        try {
            logger.info(String.format("Wait for element clickable %s", getLocator().toString()));
            WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSecond);
            wait.until(ExpectedConditions.elementToBeClickable(getLocator()));
        } catch (Exception e) {
            logger.error(String.format("WaitForElementClickable: Has error with control '%s': %s",
                    getLocator().toString(), e.getMessage().split("\n")[0]));
        }
    }

    @Override
    public void waitForElementDisabled(int timeOutInSecond) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSecond);
            wait.until(driver -> !getElement().isEnabled());
        } catch (Exception e) {
            logger.error(String.format("waitForElementDisabled: Has error with control '%s': %s",
                    getLocator().toString(), e.getMessage().split("\n")[0]));
        }
    }

    @Override
    public void waitForElementDisabled() {
        waitForElementDisabled(DriverUtils.getTimeOut());
    }

    @Override
    public void waitForElementEnabled(int timeOutInSecond) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSecond);
            wait.until(driver -> getElement().isEnabled());
        } catch (Exception e) {
            logger.error(String.format("waitForElementEnabled: Has error with control '%s': %s",
                    getLocator().toString(), e.getMessage().split("\n")[0]));
        }
    }

    @Override
    public void waitForElementEnabled() {
        waitForElementEnabled(DriverUtils.getTimeOut());
    }

    @Override
    public void waitForInvisibility() {
        waitForInvisibility(DriverUtils.getTimeOut());
    }

    @Override
    public void waitForInvisibility(int timeOutInSeconds) {
        try {
            logger.info(String.format("Wait for invisibility of %s", getLocator().toString()));
            WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSeconds);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(getLocator()));
        } catch (Exception e) {
            logger.error(String.format("waitForInvisibility: Has error with control '%s': %s", getLocator().toString(),
                    e.getMessage().split("\n")[0]));
        }
    }

    @Override
    public void waitForTextToBeNotPresent(String text, int timeOutInSecond) {
        try {
            logger.info(String.format("Wait for text not to be present in %s", getLocator().toString()));
            WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSecond);
            wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(getElement(), text)));
        } catch (Exception e) {
            logger.error(String.format("waitForTextToBeNotPresent: Has error with control '%s': %s",
                    getLocator().toString(), e.getMessage().split("\n")[0]));
        }
    }

    @Override
    public void waitForTextToBePresent(String text, int timeOutInSecond) {
        try {
            logger.info(String.format("Wait for text to be present in %s", getLocator().toString()));
            WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSecond);
            wait.until(ExpectedConditions.textToBePresentInElement(getElement(), text));
        } catch (Exception e) {
            logger.error(String.format("waitForTextToBePresent: Has error with control '%s': %s",
                    getLocator().toString(), e.getMessage().split("\n")[0]));
        }
    }

    @Override
    public void waitForValueNotPresentInAttribute(String attribute, String value, int timeOutInSecond) {
        try {
            logger.info(String.format("Wait for %s not to be present in %s attribute of %s", value, attribute, getLocator().toString()));
            WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSecond);
            wait.until(ExpectedConditions.not(ExpectedConditions.attributeToBe(getLocator(), attribute, value)));
        } catch (Exception e) {
            logger.error(String.format("waitForValueNotPresentInAttribute: Has error with control '%s': %s",
                    getLocator().toString(), e.getMessage().split("\n")[0]));
        }
    }

    @Override
    public void waitForValuePresentInAttribute(String attribute, String value, int timeOutInSecond) {
        try {
            logger.info(String.format("Wait for %s to be present in %s attribute of %s", value, attribute, getLocator().toString()));
            WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSecond);
            wait.until(ExpectedConditions.attributeToBe(getLocator(), attribute, value));
        } catch (Exception e) {
            logger.error(String.format("waitForValuePresentInAttribute: Has error with control '%s': %s",
                    getLocator().toString(), e.getMessage().split("\n")[0]));
        }
    }

    @Override
    public void waitForVisibility() {
        waitForVisibility(DriverUtils.getTimeOut());
    }

    @Override
    public void waitForVisibility(int timeOutInSeconds) {
        try {
            logger.info(String.format("Wait for control's visibility %s", getLocator().toString()));
            WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSeconds);
            wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator()));
        } catch (Exception e) {
            logger.error(String.format("waitForVisibility: Has error with control '%s': %s", getLocator().toString(),
                    e.getMessage().split("\n")[0]));
        }
    }

    @Override
    public void waitForStalenessOfElement() {
        waitForStalenessOfElement(DriverUtils.getTimeOut());
    }

    @Override
    public void waitForStalenessOfElement(int timeOutInSeconds) {
        try {
            logger.info(String.format("Wait for control's staleness %s", getLocator().toString()));
            WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSeconds);
            wait.until(ExpectedConditions.stalenessOf(getElement()));
        } catch (Exception e) {
            logger.error(String.format("waitForStalenessOfElement: Has error with control '%s': %s", getLocator().toString(),
                    e.getMessage().split("\n")[0]));
        }
    }


    public boolean isStale() {
        try {
            //INFO: If element is stale accessing any property should throw exception
            getElement().getTagName();
            return false;
        } catch (StaleElementReferenceException e) {
            return true;
        }
    }
}
