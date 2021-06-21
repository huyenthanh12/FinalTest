package com.logigear.control.base;

public interface IClickable extends IBaseControl {

    void click();

    void click(boolean waitUntilClickable);

    void click(int time);

    void click(int x, int y);

    void clickByJs();

    void doubleClick();

    void clickUntilInteractable(int times);
}
