package com.sadman.drs.controller.ui;

import com.sadman.drs.model.StatusValues;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

public class ComboBoxInitializer {

    public static void initializeComboBoxes(
            ComboBox<String> disasterTypeComboBox,
            ComboBox<String> severityComboBox,
            ComboBox<String> damageLevelComboBox,
            ComboBox<String> activityTypeComboBox,
            ComboBox<String> taskPriorityComboBox,
            ComboBox<String> reportStatusComboBox,
            ComboBox<String> reportTaskStatusComboBox,
            ComboBox<String> departmentTaskStatusComboBox) {

        disasterTypeComboBox.setItems(FXCollections.observableArrayList(
                "Fire", "Flood", "Earthquake", "Hurricane", "Storm", "Chemical Spill", "Other"));
        severityComboBox.setItems(FXCollections.observableArrayList(StatusValues.PRIORITIES));
        damageLevelComboBox.setItems(FXCollections.observableArrayList("Minor", "Moderate", "Major", "Severe"));
        activityTypeComboBox.setItems(FXCollections.observableArrayList(
                "Warning/Evacuation", "Search and Rescue", "Immediate Assistance",
                "Damage Assessment", "Continuing Assistance", "Infrastructure Restoration", "Debris Removal"));
        taskPriorityComboBox.setItems(FXCollections.observableArrayList(StatusValues.PRIORITIES));
        reportStatusComboBox.setItems(FXCollections.observableArrayList(StatusValues.REPORT_STATUSES));

        if (reportTaskStatusComboBox != null) {
            reportTaskStatusComboBox.setItems(FXCollections.observableArrayList(StatusValues.TASK_STATUSES));
        }

        if (departmentTaskStatusComboBox != null) {
            departmentTaskStatusComboBox.setItems(FXCollections.observableArrayList(StatusValues.TASK_STATUSES));
        }
    }
}
