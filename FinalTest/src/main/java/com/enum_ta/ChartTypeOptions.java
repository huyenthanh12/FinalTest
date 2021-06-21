package com.enum_ta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ChartTypeOptions {
    PIE("Pie"), SINGLE_BAR("Single Bar"), STACKED_BAR("Stacked Bar"), GROUP_BAR("Group Bar"), LINE("Line");

    private final String value;

    ChartTypeOptions(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static List<String> getListOfChartTypeOptions() {
        return Arrays.stream(values()).map(ChartTypeOptions::getValue).collect(Collectors.toList());
    }
}
