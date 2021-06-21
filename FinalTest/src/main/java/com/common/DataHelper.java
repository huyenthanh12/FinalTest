package com.common;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;

public class DataHelper {
    private static Faker faker = new Faker();

    public static String getRandomCharacters() {
        return faker.bothify("?????#####");
    }

    public static String getRandomPageName() {
        return getRandomCharacters() + "_Page";
    }

    public static String getRandomPanelName() {
        return getRandomCharacters() + "_Panel";
    }

    public static String getRandomDataProfileName() {
        return getRandomCharacters() + "_DataProfile";
    }

    public static String convertSpaceToNonBreakingSpaceOnText(String text) {
        return text.replace(" ", " ");
    }
}
