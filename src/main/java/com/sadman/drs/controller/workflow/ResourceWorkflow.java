package com.sadman.drs.controller.workflow;

import com.sadman.drs.client.bridge.DRSClientService;
import com.sadman.drs.controller.ui.AlertHelper;
import com.sadman.drs.model.DepartmentResourceAlert;
import com.sadman.drs.model.DisasterReport;
import com.sadman.drs.model.Resource;
import com.sadman.drs.model.StatusValues;
import com.sadman.drs.server.service.DepartmentResourceAlertService;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

/**
 * Handles resource recommendation and allocation workflow for the main JavaFX view.
 */
public class ResourceWorkflow {
    private final DepartmentResourceAlertService resourceAlertService = new DepartmentResourceAlertService();
    private final Supplier<DRSClientService> clientServiceSupplier;
    private final ComboBox<DisasterReport> resourceReportComboBox;
    private final ComboBox<Resource> resourceComboBox;
    private final TextField quantityField;
    private final TextArea resourceOutputArea;
    private final Label resourceCriticalAlertLabel;
    private final Runnable refreshAllData;
    private final Runnable showResources;

    public ResourceWorkflow(Supplier<DRSClientService> clientServiceSupplier,
                            ComboBox<DisasterReport> resourceReportComboBox,
                            ComboBox<Resource> resourceComboBox,
                            TextField quantityField,
                            TextArea resourceOutputArea,
                            Label resourceCriticalAlertLabel,
                            Runnable refreshAllData,
                            Runnable showResources) {
        this.clientServiceSupplier = clientServiceSupplier;
        this.resourceReportComboBox = resourceReportComboBox;
        this.resourceComboBox = resourceComboBox;
        this.quantityField = quantityField;
        this.resourceOutputArea = resourceOutputArea;
        this.resourceCriticalAlertLabel = resourceCriticalAlertLabel;
        this.refreshAllData = refreshAllData;
        this.showResources = showResources;
    }

    public void recommendResourcesForSelectedReport() {
        DisasterReport report = resourceReportComboBox.getValue();

        if (report == null) {
            AlertHelper.showWarning("Select a disaster report first.");
            return;
        }
        if (StatusValues.isTerminalReportStatus(report.getStatus())) {
            AlertHelper.showWarning("Completed reports cannot receive resource recommendations.");
            return;
        }

        try {
            clearCriticalAlertLabel();
            String recommendation = clientServiceSupplier.get().recommendResources(report);
            resourceOutputArea.setText("Recommended resources for " + report + ":\n\n" + recommendation
                    + "\n\nThese recommendations are generated automatically based on disaster type and severity.");
        } catch (IOException | ClassNotFoundException | IllegalStateException exception) {
            AlertHelper.showError("Resource Recommendation Error", exception.getMessage());
        }
    }

    public void allocateSelectedResource() {
        DisasterReport report = resourceReportComboBox.getValue();
        Resource resource = resourceComboBox.getValue();

        if (report == null || resource == null) {
            AlertHelper.showWarning("Select report and resource first.");
            return;
        }
        if (StatusValues.isTerminalReportStatus(report.getStatus())) {
            AlertHelper.showWarning("Completed reports cannot receive resource allocations.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException exception) {
            AlertHelper.showWarning("Quantity must be a valid number.");
            return;
        }

        try {
            DRSClientService clientService = clientServiceSupplier.get();
            clientService.allocateResource(report.getReportId(), resource, quantity,
                    "Allocated from Resources page");
            List<DepartmentResourceAlert> alerts = findResourceAlerts(clientService);
            resourceOutputArea.setText("Allocated " + quantity + " x " + resource.getResourceName()
                    + " to " + report.getReportDisplayName() + ".");
            updateCriticalAlertLabel(alerts);
            quantityField.clear();
            refreshAllData.run();
            showResources.run();
        } catch (IOException | ClassNotFoundException | IllegalArgumentException | IllegalStateException exception) {
            AlertHelper.showError("Resource Allocation Error", exception.getMessage());
        }
    }

    private String buildCriticalAlertText(List<DepartmentResourceAlert> alerts) {
        if (alerts.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder("Critical department resource alerts:\n");
        alerts.stream()
                .limit(5)
                .forEach(alert -> builder.append("- ")
                        .append(alert.getAlertMessage())
                        .append("\n"));

        if (alerts.size() > 5) {
            builder.append("- ").append(alerts.size() - 5).append(" more alert(s) shown in the Resources table.\n");
        }
        return builder.toString();
    }

    private List<DepartmentResourceAlert> findResourceAlerts(DRSClientService clientService)
            throws IOException, ClassNotFoundException {
        return resourceAlertService.findCriticalAlerts(
                clientService.findAllDepartments(),
                clientService.findAllResources());
    }

    private void updateCriticalAlertLabel(List<DepartmentResourceAlert> alerts) {
        String alertText = buildCriticalAlertText(alerts);
        resourceCriticalAlertLabel.setText(alertText);
        boolean hasCriticalAlerts = !alertText.isBlank();
        resourceCriticalAlertLabel.setVisible(hasCriticalAlerts);
        resourceCriticalAlertLabel.setManaged(hasCriticalAlerts);
    }

    private void clearCriticalAlertLabel() {
        resourceCriticalAlertLabel.setText("");
        resourceCriticalAlertLabel.setVisible(false);
        resourceCriticalAlertLabel.setManaged(false);
    }
}
