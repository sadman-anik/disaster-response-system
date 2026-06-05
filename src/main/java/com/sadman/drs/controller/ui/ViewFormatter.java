package com.sadman.drs.controller.ui;

import com.sadman.drs.model.DisasterReport;
import com.sadman.drs.model.Resource;
import com.sadman.drs.model.ResponseTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.util.List;
import java.util.stream.Collectors;

public class ViewFormatter {

    public static String buildReportResult(DisasterReport report) {
        StringBuilder builder = new StringBuilder();

        builder.append("Disaster report saved successfully.\n\n");
        builder.append("Report: ").append(report.getReportDisplayName()).append("\n");
        builder.append("Title: ").append(report.getReportTitle()).append("\n");
        builder.append("Type: ").append(report.getDisasterType()).append("\n");
        builder.append("Severity: ").append(report.getSeverity()).append("\n");
        builder.append("Location: ").append(report.getLocation()).append("\n");
        builder.append("Initial Priority: ").append(report.getPriorityLevel()).append("\n\n");
        builder.append("Evacuation Advice:\n").append(report.getEvacuationAdvice()).append("\n\n");
        builder.append("Recommended Resources:\n").append(report.getRecommendedResources()).append("\n\n");
        builder.append("Next step: open Assessment & Priority, assess this report, and the system will automatically create the standard response tasks.");

        return builder.toString();
    }

    public static String buildReportDetails(DisasterReport report, List<ResponseTask> tasks) {
        StringBuilder builder = new StringBuilder();

        builder.append("Report: ").append(report.getReportDisplayName())
                .append("\nTitle: ").append(report.getReportTitle())
                .append("\nType: ").append(report.getDisasterType())
                .append("\nSeverity: ").append(report.getSeverity())
                .append("\nLocation: ").append(report.getLocation())
                .append("\nStatus: ").append(report.getStatus())
                .append("\nPriority: ").append(report.getPriorityLevel())
                .append("\nReporter: ").append(report.getReportedBy())
                .append("\nContact: ").append(report.getContactNumber())
                .append("\nCreated: ").append(report.getCreatedAt())
                .append("\n\nDescription:\n").append(report.getDescription())
                .append("\n\nEvacuation Advice:\n").append(report.getEvacuationAdvice())
                .append("\n\nRecommended Resources:\n").append(report.getRecommendedResources())
                .append("\n\nResponse Tasks:\n");

        if (tasks == null || tasks.isEmpty()) {
            builder.append("No response tasks created yet.");
        } else {
            for (ResponseTask task : tasks) {
                builder.append("• Task #").append(task.getTaskId())
                        .append(" | ").append(task.getActivityType())
                        .append(" | ").append(task.getDepartmentName())
                        .append(" | ").append(task.getStatus())
                        .append("\n");
            }
        }

        return builder.toString();
    }

    public static ObservableList<PieChart.Data> createReportStatusChartData(List<DisasterReport> reports) {
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

        for (String status : List.of("Reported", "Assessed", "In Progress", "Completed", "Closed")) {
            long count = reports.stream()
                    .filter(report -> status.equalsIgnoreCase(report.getStatus()))
                    .count();
            if (count > 0) {
                chartData.add(new PieChart.Data(status, count));
            }
        }

        if (chartData.isEmpty()) {
            chartData.add(new PieChart.Data("No reports yet", 1));
        }

        return chartData;
    }

    public static XYChart.Series<String, Number> createTaskDepartmentSeries(List<ResponseTask> tasks) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("In Progress Tasks");

        tasks.stream()
                .filter(task -> "In Progress".equalsIgnoreCase(task.getStatus()))
                .collect(Collectors.groupingBy(ResponseTask::getDepartmentName, Collectors.counting()))
                .forEach((department, count) -> series.getData().add(new XYChart.Data<>(department, count)));

        if (series.getData().isEmpty()) {
            series.getData().add(new XYChart.Data<>("No in-progress tasks", 0));
        }

        return series;
    }

    public static XYChart.Series<String, Number> createResourceAvailabilitySeries(List<Resource> resources) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Available Resources");

        for (Resource resource : resources) {
            series.getData().add(new XYChart.Data<>(resource.getResourceName(), resource.getQuantityAvailable()));
        }

        if (series.getData().isEmpty()) {
            series.getData().add(new XYChart.Data<>("No resources", 0));
        }

        return series;
    }
}
