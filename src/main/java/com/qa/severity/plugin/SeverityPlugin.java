package com.qa.severity.plugin;

import io.qameta.allure.CommonJsonAggregator2;
import io.qameta.allure.core.LaunchResults;
import io.qameta.allure.entity.TestResult;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import static io.qameta.allure.util.ResultsUtils.SEVERITY_LABEL_NAME;

public class SeverityPlugin extends CommonJsonAggregator2 {

    private int blocker = 0;
    private int critical = 0;
    private int normal = 0;
    private int minor = 0;
    private int total = 0;

    public SeverityPlugin() {
        super("widgets", "severity-table.json");
    }

    @Override
    public SeverityData getData(List<LaunchResults> launchesResults) {
        launchesResults.stream()
                .flatMap(launch -> launch.getResults().stream())
                .forEach(this::countSeverity);

        return new SeverityData(
                calculatePercentage(blocker),
                calculatePercentage(critical),
                calculatePercentage(normal),
                calculatePercentage(minor),
                total
        );
    }

    private void countSeverity(TestResult testResult) {
        testResult.getLabels().stream()
                .filter(t -> t.getName().equals(SEVERITY_LABEL_NAME))
                .findFirst()
                .ifPresent(severity -> {
                    switch (severity.getValue()) {
                        case "blocker" -> blocker++;
                        case "critical" -> critical++;
                        case "normal" -> normal++;
                        default -> minor++;
                    }
                    total++;
                });
    }

    private double calculatePercentage(int quantity) {
        String formattedValue = String.format("%.2f", (total == 0) ? 0 : (double) quantity / total * 100);
        return Double.parseDouble(formattedValue);
    }

    @Data
    @AllArgsConstructor
    public static class SeverityData {
        double blocker;
        double critical;
        double normal;
        double minor;
        double total;
    }
}