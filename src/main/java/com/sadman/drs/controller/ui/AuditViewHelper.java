package com.sadman.drs.controller.ui;

import com.sadman.drs.model.AuditRecord;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper methods for configuring and updating the audit log view.
 */
public class AuditViewHelper {

    public static void initializeAuditTable(
            TableView<AuditRecord> auditTable,
            TableColumn<AuditRecord, Integer> auditIdColumn,
            TableColumn<AuditRecord, String> auditWhenColumn,
            TableColumn<AuditRecord, String> auditUserColumn,
            TableColumn<AuditRecord, String> auditActionColumn,
            TableColumn<AuditRecord, String> auditEntityTypeColumn,
            TableColumn<AuditRecord, String> auditEntityLabelColumn,
            TableColumn<AuditRecord, String> auditDetailsColumn) {
        if (auditTable == null) {
            return;
        }
        auditIdColumn.setCellValueFactory(new PropertyValueFactory<>("auditId"));
        auditWhenColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        auditUserColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        auditActionColumn.setCellValueFactory(new PropertyValueFactory<>("actionType"));
        auditEntityTypeColumn.setCellValueFactory(new PropertyValueFactory<>("entityType"));
        auditEntityLabelColumn.setCellValueFactory(new PropertyValueFactory<>("entityLabel"));
        auditDetailsColumn.setCellValueFactory(new PropertyValueFactory<>("changeDetails"));
    }

    public static void updateAuditActionChart(BarChart<String, Number> auditActionChart,
                                              List<AuditRecord> auditRecords) {
        if (auditActionChart == null) {
            return;
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        auditRecords.stream()
                .collect(Collectors.groupingBy(AuditRecord::getActionType, Collectors.counting()))
                .entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        auditActionChart.getData().setAll(series);
    }
}
