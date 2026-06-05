package com.sadman.drs.controller.ui;

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
        severityComboBox.setItems(FXCollections.observableArrayList("Low", "Medium", "High", "Critical"));
        damageLevelComboBox.setItems(FXCollections.observableArrayList("Minor", "Moderate", "Major", "Severe"));
        activityTypeComboBox.setItems(FXCollections.observableArrayList(
                "Warning/Evacuation", "Search and Rescue", "Immediate Assistance",
                "Damage Assessment", "Continuing Assistance", "Infrastructure Restoration", "Debris Removal"));
        taskPriorityComboBox.setItems(FXCollections.observableArrayList("Low", "Medium", "High", "Critical"));
        reportStatusComboBox.setItems(FXCollections.observableArrayList(
                "Reported", "Assessed", "In Progress", "Completed", "Closed"));

        if (reportTaskStatusComboBox != null) {
            reportTaskStatusComboBox.setItems(FXCollections.observableArrayList(
                    "Pending", "In Progress", "Completed"));
        }

        if (departmentTaskStatusComboBox != null) {
            departmentTaskStatusComboBox.setItems(FXCollections.observableArrayList(
                    "Pending", "In Progress", "Completed"));
        }
    }
}
