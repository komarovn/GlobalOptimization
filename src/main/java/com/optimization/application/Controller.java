/*
 * Copyright (c) Lobachevsky University, 2018. All rights reserved.
 * Developed by: Nikolay Komarov.
 */
package com.optimization.application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    private final String INVALID_FIELD_CLS = "text-field__invalid";

    private AppModel model;
    private Set<Control> invalidControls = new HashSet<Control>();

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

    @FXML
    private TextField leftBoundIntervalField;

    @FXML
    private TextField rightBoundIntervalField;

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
        initIntervalFields();
        initProcessButton();
        initResultsSection();
    }

    private void initMethodSelector() {
        invalidControls.add(methodSelector);
        methodSelector.setPromptText(StringResources.CHOOSE_METHOD);
        methodSelector.getItems().addAll(FXCollections.observableArrayList(
                new Method("scan", StringResources.SCAN_METHOD),
                new Method("piyavskii", StringResources.PIYAVSKII_METHOD),
                new Method("strongin", StringResources.STRONGIN_METHOD)));
        methodSelector.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Method>() {
            @Override
            public void changed(ObservableValue<? extends Method> observable, Method oldValue, Method value) {
                invalidControls.remove(methodSelector);
                model.setMethodName(value.getValue());

                boolean isLipschitzMethod = "piyavskii".equals(value.getValue()) || "strongin".equals(value.getValue());

                if (isLipschitzMethod) {
                    invalidControls.add(rValueField);
                    processRValue();
                } else {
                    invalidControls.remove(rValueField);
                }

                rLabel.setVisible(isLipschitzMethod);
                rValueField.setVisible(isLipschitzMethod);
                validateProcessButton();
            }
        });
    }

    private void initFunctionField() {
        invalidControls.add(functionField);
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
                    changeFieldValidation(functionField, model.validateFunctionExpression());
                    validateProcessButton();
                }
            }
        });
    }

    private void initStopParamField() {
        invalidControls.add(stopByValueField);
        precisionRadio.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean selected) {
                if (selected) {
                    stopByValueField.setText(String.valueOf(model.getPrecision()));
                } else {
                    changeFieldValidation(stopByValueField, true);
                }
            }
        });
        iterationsCountRadio.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean selected) {
                if (selected) {
                    stopByValueField.setText(String.valueOf(model.getIterationsCount()));
                } else {
                    changeFieldValidation(stopByValueField, true);
                }
            }
        });
        stopByValueField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isFocused) {
                if (!isFocused) {
                    boolean isValid = true;

                    if (precisionRadio.isSelected()) {
                        isValid = validateDoubleValue(stopByValueField.getText()) &&
                                Double.valueOf(stopByValueField.getText()) >= 0;

                        if (isValid) {
                            model.setPrecision(Double.valueOf(stopByValueField.getText()));
                            stopByValueField.setText(String.valueOf(model.getPrecision()));
                        }
                    } else if (iterationsCountRadio.isSelected()) {
                        isValid = validateIntValue(stopByValueField.getText()) &&
                                Integer.valueOf(stopByValueField.getText()) >= 0;

                        if (isValid) {
                            model.setIterationsCount(Integer.valueOf(stopByValueField.getText()));
                            stopByValueField.setText(String.valueOf(model.getIterationsCount()));
                        }
                    }

                    changeFieldValidation(stopByValueField, isValid);
                    validateProcessButton();
                }
            }
        });
    }

    private void initRValueField() {
        rValueField.setVisible(false);
        rLabel.setVisible(false);
        rValueField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isFocused) {
                if (!isFocused) {
                    processRValue();
                }
            }
        });
    }

    private void initIntervalFields() {
        invalidControls.add(leftBoundIntervalField);
        invalidControls.add(rightBoundIntervalField);
        leftBoundIntervalField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isFocused) {
                if (!isFocused) {
                    processLeftBound();
                    processRightBound();
                    validateProcessButton();
                }
            }
        });
        rightBoundIntervalField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isFocused) {
                if (!isFocused) {
                    processRightBound();
                    processLeftBound();
                    validateProcessButton();
                }
            }
        });
    }

    private void changeFieldValidation(TextField field, boolean isValid) {
        if (isValid) {
            field.getStyleClass().remove(INVALID_FIELD_CLS);
            invalidControls.remove(field);
        } else {
            invalidControls.add(field);

            if (!field.getStyleClass().contains(INVALID_FIELD_CLS)) {
                field.getStyleClass().add(INVALID_FIELD_CLS);
            }
        }
    }

    private boolean validateDoubleValue(String value) {
        try {
            Double.valueOf(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean validateIntValue(String value) {
        try {
            Integer.valueOf(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void processRValue() {
        boolean isValid = validateDoubleValue(rValueField.getText());

        if (isValid) {
            model.setrParameter(Double.valueOf(rValueField.getText()));
            rValueField.setText(String.valueOf(model.getrParameter()));
        }

        changeFieldValidation(rValueField, isValid);
        validateProcessButton();
    }

    private void processLeftBound() {
        boolean isValid = false;

        if (validateDoubleValue(leftBoundIntervalField.getText())) {
            double left = Double.valueOf(leftBoundIntervalField.getText());
            double right = model.getRightBoundInterval();

            if (left <= right) {
                model.setLeftBoundInterval(left);
                leftBoundIntervalField.setText(String.valueOf(model.getLeftBoundInterval()));
                isValid = true;
            }
        }

        changeFieldValidation(leftBoundIntervalField, isValid);
    }

    private void processRightBound() {
        boolean isValid = false;

        if (validateDoubleValue(rightBoundIntervalField.getText())) {
            double left = model.getLeftBoundInterval();
            double right = Double.valueOf(rightBoundIntervalField.getText());

            if (left <= right) {
                model.setRightBoundInterval(right);
                rightBoundIntervalField.setText(String.valueOf(model.getRightBoundInterval()));
                isValid = true;
            }
        }

        changeFieldValidation(rightBoundIntervalField, isValid);
    }

    private void validateProcessButton() {
        processButton.setDisable(invalidControls.size() != 0);
    }

    private void initProcessButton() {
        processButton.setDisable(true);
        processButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                validateProcessButton();

                if (!processButton.isDisabled()) {
                    //TODO: call process
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
