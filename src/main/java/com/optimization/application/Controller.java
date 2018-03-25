/*
 * Copyright (c) Lobachevsky University, 2018. All rights reserved.
 * Developed by: Nikolay Komarov.
 */
package com.optimization.application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button processButton;

    @FXML
    private TextField functionField;

    @FXML
    private Pane plotArea;

    // Method Props

    @FXML
    private ComboBox<Method> methodSelector;

    @FXML
    private RadioButton precisionRadio;

    @FXML
    private RadioButton iterationsCountRadio;

    @FXML
    private TextField stopByValueField;

    @FXML
    private Label rLabel;

    @FXML
    private TextField rValueField;

    // Results

    @FXML
    private Label iterationsCountValue;

    @FXML
    private Label argumentValue;

    @FXML
    private Label functionValue;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMethodSelector();
        initResultsSection();
    }

    private void initMethodSelector() {
        ObservableList<Method> items = FXCollections.observableArrayList(
                new Method("scan", "Scan"),
                new Method("piyavskii", "Piyavskii's"),
                new Method("strongin", "Strongin's"));
        methodSelector.getItems().addAll(items);
        methodSelector.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Method>() {
            @Override
            public void changed(ObservableValue<? extends Method> observable, Method oldValue, Method value) {
                boolean isLipschitzMethod = "piyavskii".equals(value.getValue()) || "strongin".equals(value.getValue());
                rLabel.setVisible(isLipschitzMethod);
                rValueField.setVisible(isLipschitzMethod);
            }
        });

    }

    private void initResultsSection() {
        iterationsCountValue.setVisible(false);
        argumentValue.setVisible(false);
        functionValue.setVisible(false);
    }

    private static class Method {
        private String name;
        private String value;

        public Method(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return this.name;
        }
    }
}
