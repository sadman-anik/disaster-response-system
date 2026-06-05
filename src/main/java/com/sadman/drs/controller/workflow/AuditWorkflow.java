package com.sadman.drs.controller.workflow;

import com.sadman.drs.client.bridge.DRSClientService;
import com.sadman.drs.controller.ui.AlertHelper;
import com.sadman.drs.controller.ui.AuditViewHelper;
import com.sadman.drs.controller.validation.FormValueHelper;
import com.sadman.drs.model.AuditRecord;
import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

/**
 * Handles audit search and chart refresh for the main JavaFX view.
 */
public class AuditWorkflow {
    private final Supplier<DRSClientService> clientServiceSupplier;
    private final TextField auditSearchField;
    private final TableView<AuditRecord> auditTable;
    private final BarChart<String, Number> auditActionChart;

    public AuditWorkflow(Supplier<DRSClientService> clientServiceSupplier,
                         TextField auditSearchField,
                         TableView<AuditRecord> auditTable,
                         BarChart<String, Number> auditActionChart) {
        this.clientServiceSupplier = clientServiceSupplier;
        this.auditSearchField = auditSearchField;
        this.auditTable = auditTable;
        this.auditActionChart = auditActionChart;
    }

    public void searchAuditRecords() {
        loadAuditRecords(auditSearchField == null ? "" : auditSearchField.getText());
    }

    public void refreshAuditData() {
        loadAuditRecords(auditSearchField == null ? "" : auditSearchField.getText());
    }

    private void loadAuditRecords(String keyword) {
        try {
            List<AuditRecord> auditRecords = FormValueHelper.isBlank(keyword)
                    ? clientServiceSupplier.get().findAllAuditEvents()
                    : clientServiceSupplier.get().searchAuditEvents(keyword.trim());
            auditTable.setItems(FXCollections.observableArrayList(auditRecords));
            AuditViewHelper.updateAuditActionChart(auditActionChart, auditRecords);
        } catch (IOException | ClassNotFoundException exception) {
            AlertHelper.showError("Audit Refresh Error", exception.getMessage());
        }
    }

}
