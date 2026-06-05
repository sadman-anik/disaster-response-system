package com.sadman.drs.controller.workflow;

import com.sadman.drs.client.bridge.DRSClientService;
import com.sadman.drs.controller.ui.AlertHelper;
import com.sadman.drs.model.DisasterReport;
import com.sadman.drs.model.Resource;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Handles resource recommendation and allocation workflow for the main JavaFX view.
 */
public class ResourceWorkflow {
    private final Supplier<DRSClientService> clientServiceSupplier;
    private final ComboBox<DisasterReport> resourceReportComboBox;
    private final ComboBox<Resource> resourceComboBox;
    private final TextField quantityField;
    private final TextArea resourceOutputArea;
    private final Runnable refreshAllData;
    private final Runnable showResources;

    public ResourceWorkflow(Supplier<DRSClientService> clientServiceSupplier,
                            ComboBox<DisasterReport> resourceReportComboBox,
                            ComboBox<Resource> resourceComboBox,
                            TextField quantityField,
                            TextArea resourceOutputArea,
                            Runnable refreshAllData,
                            Runnable showResources) {
        this.clientServiceSupplier = clientServiceSupplier;
        this.resourceReportComboBox = resourceReportComboBox;
        this.resourceComboBox = resourceComboBox;
        this.quantityField = quantityField;
        this.resourceOutputArea = resourceOutputArea;
        this.refreshAllData = refreshAllData;
        this.showResources = showResources;
    }

    public void recommendResourcesForSelectedReport() {
        DisasterReport report = resourceReportComboBox.getValue();

        if (report == null) {
            AlertHelper.showWarning("Select a disaster report first.");
            return;
        }

        try {
            String recommendation = clientServiceSupplier.get().recommendResources(report);
            resourceOutputArea.setText("Recommended resources for " + report + ":\n\n" + recommendation
                    + "\n\nThese recommendations are generated automatically based on disaster type and severity.");
        } catch (IOException | ClassNotFoundException exception) {
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

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException exception) {
            AlertHelper.showWarning("Quantity must be a valid number.");
            return;
        }

        try {
            clientServiceSupplier.get().allocateResource(report.getReportId(), resource, quantity,
                    "Allocated from Resources page");
            resourceOutputArea.setText("Allocated " + quantity + " x " + resource.getResourceName()
                    + " to " + report.getReportDisplayName() + ".");
            quantityField.clear();
            refreshAllData.run();
            showResources.run();
        } catch (IOException | ClassNotFoundException | IllegalArgumentException exception) {
            AlertHelper.showError("Resource Allocation Error", exception.getMessage());
        }
    }
}
