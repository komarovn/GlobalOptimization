/*
 * Copyright (c) Lobachevsky University, 2018. All rights reserved.
 * Developed by: Nikolay Komarov.
 */
package com.optimization.application;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.PopupWindow;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    private final String INVALID_FIELD_CLS = "text-field__invalid";

    private AppModel model;
    private MethodProvider provider;
    private Set<Control> invalidControls = new HashSet<Control>();
    private XYChart.Series<Double, Double> functionSeries;

    @FXML
    private Button processButton;

    @FXML
    private TextField functionField;

    @FXML
    private ExtendedLineChart plotArea;

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
    private Pane rValuePane;

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

    public void setProvider(MethodProvider provider) {
        this.provider = provider;
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
        initPlot();
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
                model.setMethodName(value.getValue());
                changeFieldValidation(methodSelector, true, null);

                boolean isLipschitzMethod = "piyavskii".equals(value.getValue()) || "strongin".equals(value.getValue());

                if (isLipschitzMethod) {
                    invalidControls.add(rValueField);
                    processRValue();
                } else {
                    changeFieldValidation(rValueField, true, null);
                }

                rValuePane.setManaged(isLipschitzMethod);
                rValuePane.setVisible(isLipschitzMethod);
            }
        });
    }

    private void initFunctionField() {
        invalidControls.add(functionField);
        functionField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isFocused) {
                if (!isFocused) {
                    processFunctionField();
                }
            }
        });
    }

    private void initStopParamField() {
        invalidControls.add(stopByValueField);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                precisionRadio.setSelected(model.isStopByPrecision());
                iterationsCountRadio.setSelected(!model.isStopByPrecision());
            }
        });
        precisionRadio.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean selected) {
                if (selected) {
                    model.setStopByPrecision(true);
                    stopByValueField.setText(String.valueOf(model.getPrecision()));
                } else {
                    changeFieldValidation(stopByValueField, true, null);
                }
            }
        });
        iterationsCountRadio.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean selected) {
                if (selected) {
                    model.setStopByPrecision(false);
                    stopByValueField.setText(String.valueOf(model.getIterationsCount()));
                } else {
                    changeFieldValidation(stopByValueField, true, null);
                }
            }
        });
        stopByValueField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isFocused) {
                if (!isFocused) {
                    boolean isValid = true;
                    String validationText = StringResources.VALIDATION_INVALID;

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
                        validationText = StringResources.VALIDATION_POSITIVE_INT;

                        if (isValid) {
                            model.setIterationsCount(Integer.valueOf(stopByValueField.getText()));
                            stopByValueField.setText(String.valueOf(model.getIterationsCount()));
                        }
                    }

                    changeFieldValidation(stopByValueField, isValid, validationText);
                }
            }
        });
    }

    private void initRValueField() {
        rValuePane.setManaged(false);
        rValuePane.setVisible(false);
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
        leftBoundIntervalField.setText(String.valueOf(0.0));
        rightBoundIntervalField.setText(String.valueOf(0.0));
        leftBoundIntervalField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isFocused) {
                if (!isFocused) {
                    processLeftBound();
                    processRightBound();
                }
            }
        });
        rightBoundIntervalField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isFocused) {
                if (!isFocused) {
                    processRightBound();
                    processLeftBound();
                }
            }
        });
    }

    private void changeFieldValidation(Control field, boolean isValid, String validationText) {
        if (isValid) {
            field.getStyleClass().remove(INVALID_FIELD_CLS);
            field.setTooltip(null);
            invalidControls.remove(field);
        } else {
            field.setTooltip(produceValidationTooltip(validationText));
            invalidControls.add(field);

            if (!field.getStyleClass().contains(INVALID_FIELD_CLS)) {
                field.getStyleClass().add(INVALID_FIELD_CLS);
            }
        }

        validateProcessButton();
    }

    private Tooltip produceValidationTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.getStyleClass().add("validation-tooltip");
        tooltip.setMaxHeight(24);
        tooltip.setMinHeight(24);
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_BOTTOM_LEFT);
        return tooltip;
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

    private void processFunctionField() {
        boolean isValid = model.validateFunctionExpression(functionField.getText());

        changeFieldValidation(functionField, isValid, StringResources.VALIDATION_INVALID);

        if (isValid) {
            model.setFunctionExpression(functionField.getText());
            updateChartData();
        }
    }

    private void processRValue() {
        boolean isValid = validateDoubleValue(rValueField.getText()) &&
                Double.valueOf(rValueField.getText()) > 1;

        if (isValid) {
            model.setrParameter(Double.valueOf(rValueField.getText()));
            rValueField.setText(String.valueOf(model.getrParameter()));
        }

        changeFieldValidation(rValueField, isValid, StringResources.VALIDATION_R_PARAM);
    }

    private void processLeftBound() {
        boolean isValid = false;

        if (validateDoubleValue(leftBoundIntervalField.getText())) {
            double left = Double.valueOf(leftBoundIntervalField.getText());
            double right = model.getRightBoundInterval();

            if (left <= right) {
                isValid = true;
                model.setLeftBoundInterval(left);
                leftBoundIntervalField.setText(String.valueOf(model.getLeftBoundInterval()));
                updateChartData();
            }
        }

        changeFieldValidation(leftBoundIntervalField, isValid, StringResources.VALIDATION_INVALID);
    }

    private void processRightBound() {
        boolean isValid = false;

        if (validateDoubleValue(rightBoundIntervalField.getText())) {
            double left = model.getLeftBoundInterval();
            double right = Double.valueOf(rightBoundIntervalField.getText());

            if (left <= right) {
                isValid = true;
                model.setRightBoundInterval(right);
                rightBoundIntervalField.setText(String.valueOf(model.getRightBoundInterval()));
                updateChartData();
            }
        }

        changeFieldValidation(rightBoundIntervalField, isValid, StringResources.VALIDATION_INVALID);
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
                    // Execute task in background.
                    new Thread() {
                        @Override
                        public void run() {
                            provider.reset();
                            provider.process();
                        }
                    }.start();
                }
            }
        });
    }

    private void initResultsSection() {
        setVisibilityResultsSection(false);
    }

    private void initPlot() {
        functionSeries = new XYChart.Series<Double, Double>();
        plotArea.getData().add(functionSeries);
    }

    public void updateChartData() {
        plotArea.clearSeries(functionSeries);
        plotArea.setInterval(model.getLeftBoundInterval(), model.getRightBoundInterval());
        double left = plotArea.getLeftBoundInterval();
        double right = plotArea.getRightBoundInterval();

        if (!invalidControls.contains(functionField)) {
            for (double x = left; x <= right; x += plotArea.getIncrement()) {
                XYChart.Data<Double, Double> data = new XYChart.Data<Double, Double>(x, model.getFunctionValue(x));
                functionSeries.getData().add(data);
                data.getNode().setVisible(false);
            }
        }
    }

    public ExtendedLineChart getPlotArea() {
        return plotArea;
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
