/*
 * Copyright (c) Lobachevsky University, 2018. All rights reserved.
 * Developed by: Nikolay Komarov.
 */
package com.optimization.core;

import net.objecthunter.exp4j.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractMethod {
    private Expression functionExpression;
    private String variable;
    private double leftBound;
    private double rightBound;

    protected List<Double> characteristics; // R_i
    protected List<Double> points; // x_k
    protected List<Double> functionValues; // z_k

    private double currentArgumentValue = 0.0;
    private double currentFunctionValue = Double.MAX_VALUE;
    private double previousFunctionValue = Double.MAX_VALUE;

    public AbstractMethod(Expression functionExpression, String variable, double leftBound, double rightBound) {
        this.functionExpression = functionExpression;
        this.variable = variable;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.characteristics = new ArrayList<Double>();
        this.points = new ArrayList<Double>();
        this.functionValues = new ArrayList<Double>();
    }

    protected double getLeftBound() {
        return leftBound;
    }

    protected double getRightBound() {
        return rightBound;
    }

    public void processStep() {
        // First two values are left and right bounds of interval.
        if (points.size() == 0) {
            currentArgumentValue = getLeftBound();
        } else if (points.size() == 1) {
            currentArgumentValue = getRightBound();
        } else {
            calculateCharacteristics();
            currentArgumentValue = calculateNextPoint();
        }

        points.add(currentArgumentValue);
        Collections.sort(points);
        updateFunctionValues();
        previousFunctionValue = currentFunctionValue;
        currentFunctionValue = evaluateFunctionValue(currentArgumentValue);
    }

    private void calculateCharacteristics() {
        characteristics.clear();

        for (int i = 0; i < points.size() - 1; i++) {
            characteristics.add(calculateCharacteristic(i));
        }
    }

    protected abstract double calculateCharacteristic(int i);

    protected abstract double calculateNextPoint();

    protected int getBestCharacteristicIndex() {
        int index = 0;

        for (int i = 0; i < characteristics.size(); i++) {
            if (characteristics.get(i) > characteristics.get(index)) {
                index = i;
            }
        }

        return index;
    }

    public double evaluateFunctionValue(double arg) {
        if (variable != null) {
            return functionExpression.setVariable(variable, arg).evaluate();
        } else {
            return functionExpression.evaluate();
        }
    }

    private void updateFunctionValues() {
        functionValues.clear();

        for (Double arg : points) {
            functionValues.add(evaluateFunctionValue(arg));
        }
    }

    public double getCurrentArgumentValue() {
        return currentArgumentValue;
    }

    public double getCurrentFunctionValue() {
        return currentFunctionValue;
    }

    public double getArgmin() {
        int argminIndex = 0;

        for (int i = 0; i < functionValues.size(); i++) {
            if (functionValues.get(i) < functionValues.get(argminIndex)) {
                argminIndex = i;
            }
        }

        return points.get(argminIndex);
    }

    protected double computeM() {
        double M = 0.0;

        for (int i = 0; i < points.size() - 1; i++) {
            double deltaZ = Math.abs(functionValues.get(i + 1) - functionValues.get(i));
            double deltaX = points.get(i + 1) - points.get(i);
            double newM = deltaZ / deltaX;

            if (newM > M) {
                M = newM;
            }
        }

        return M;
    }

    public double calculatePrecision() {
        return Math.abs(previousFunctionValue - currentFunctionValue);
    }
}
