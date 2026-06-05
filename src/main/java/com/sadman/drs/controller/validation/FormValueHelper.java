package com.sadman.drs.controller.validation;

import javafx.scene.control.ComboBox;

/**
 * Shared helpers for reading JavaFX form values safely.
 */
public final class FormValueHelper {
    private FormValueHelper() {
    }

    public static String getValue(ComboBox<String> comboBox) {
        return comboBox.getValue() == null ? "" : comboBox.getValue();
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
