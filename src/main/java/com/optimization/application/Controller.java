/*
 * Copyright (c) Lobachevsky University, 2018. All rights reserved.
 * Developed by: Nikolay Komarov.
 */
package com.optimization.application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private final String INVALID_FIELD_CLS = "text-field__invalid";

    private AppModel model;

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

    public void setModel(AppModel model) {
        this.model = model;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMethodSelector();
        initFunctionField();
        initStopParamField();
        initRValueField();
        initResultsSection();
    }

    private void initMethodSelector() {
        methodSelector.getItems().addAll(FXCollections.observableArrayList(
                new Method("scan", "Scan"),
                new Method("piyavskii", "Piyavskii's"),
                new Method("strongin", "Strongin's")));
        methodSelector.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Method>() {
            @Override
            public void changed(ObservableValue<? extends Method> observable, Method oldValue, Method value) {
                model.setMethodName(value.getValue());

                boolean isLipschitzMethod = "piyavskii".equals(value.getValue()) || "strongin".equals(value.getValue());
                rLabel.setVisible(isLipschitzMethod);
                rValueField.setVisible(isLipschitzMethod);
            }
        });

    }

    private void initFunctionField() {
        functionField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String value) {
                model.setFunctionExpression(value);
            }
        });
        functionField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isFocused) {
                if (!isFocused) {
                    if (!model.validateFunctionExpression()) {
                        if (!functionField.getStyleClass().contains(INVALID_FIELD_CLS)) {
                            functionField.getStyleClass().add(INVALID_FIELD_CLS);
                        }
                    } else {
                        functionField.getStyleClass().remove(INVALID_FIELD_CLS);
                    }
                }
            }
        });
    }

    private void initStopParamField() {
        precisionRadio.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean selected) {
                if (selected) {
                    stopByValueField.setText(String.valueOf(model.getPrecision()));
                }
            }
        });
        iterationsCountRadio.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean selected) {
                if (selected) {
                    stopByValueField.setText(String.valueOf(model.getIterationsCount()));
                }
            }
        });
        stopByValueField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String value) {

            }
        });
        stopByValueField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isFocused) {
                if (!isFocused && !stopByValueField.getText().isEmpty()) {
                    if (precisionRadio.isSelected()) {
                        model.setPrecision(Double.valueOf(stopByValueField.getText()));
                        stopByValueField.setText(String.valueOf(model.getPrecision()));
                    } else if (iterationsCountRadio.isSelected()) {
                        model.setIterationsCount(Integer.valueOf(stopByValueField.getText()));
                        stopByValueField.setText(String.valueOf(model.getIterationsCount()));
                    }
                }
            }
        });
    }

    private void initRValueField() {
        rValueField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String value) {

            }
        });
        rValueField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isFocused) {
                if (!isFocused && !rValueField.getText().isEmpty()) {
                    model.setrParameter(Double.valueOf(rValueField.getText()));
                    rValueField.setText(String.valueOf(model.getrParameter()));
                }
            }
        });
    }

    private void initResultsSection() {
        setVisibilityResultsSection(false);
    }

    public void updateResultSection() {
        iterationsCountValue.setText(String.valueOf(model.getIterationsCountResult()));
        argumentValue.setText(String.valueOf(model.getArgumentValueResult()));
        functionValue.setText(String.valueOf(model.getFunctionValueResult()));
    }

    public void setVisibilityResultsSection(boolean isVisible) {
        iterationsCountValue.setVisible(isVisible);
        argumentValue.setVisible(isVisible);
        functionValue.setVisible(isVisible);
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
