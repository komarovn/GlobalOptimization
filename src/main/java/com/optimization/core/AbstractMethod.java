/*
 * Copyright (c) Lobachevsky University, 2018. All rights reserved.
 * Developed by: Nikolay Komarov.
 */
package com.optimization.core;

import net.objecthunter.exp4j.Expression;

public abstract class AbstractMethod {
    private Expression functionExpression;
    private String variable;
    private double leftBound;
    private double rightBound;

    private double currentArgumentValue = 0.0;
    private double currentFunctionValue = 0.0;

    public AbstractMethod(Expression functionExpression, String variable, double leftBound, double rightBound) {
        this.functionExpression = functionExpression;
        this.variable = variable;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    protected double getLeftBound() {
        return leftBound;
    }

    protected double getRightBound() {
        return rightBound;
    }

    protected Expression getFunctionExpression() {
        return functionExpression;
    }

    public abstract void processStep();

    public double getCurrentArgumentValue() {
        return currentArgumentValue;
    }

    public double getCurrentFunctionValue() {
        return currentFunctionValue;
    }
}
