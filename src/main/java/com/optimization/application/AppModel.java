package com.optimization.application;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;
import net.objecthunter.exp4j.tokenizer.UnknownFunctionOrVariableException;

public class AppModel {
    private String functionExpression;
    private String methodName;
    private int iterationsCount;
    private double precision;
    private double rParameter;
    private double leftBoundInterval;
    private double rightBoundInterval;

    private int iterationsCountResult;
    private double argumentValueResult;
    private double functionValueResult;

    public String getFunctionExpression() {
        return functionExpression;
    }

    public void setFunctionExpression(String functionExpression) {
        this.functionExpression = functionExpression;
    }

    public boolean validateFunctionExpression() {
        boolean validationResult = false;

        try {
            Expression exp = (new ExpressionBuilder(getFunctionExpression())).build();
            ValidationResult result = exp.validate();
            validationResult = result.isValid();
        } catch (UnknownFunctionOrVariableException e1) {
            // We can pass only one variable in expression.
            String var = e1.getToken();
            try {
                Expression exp = (new ExpressionBuilder(getFunctionExpression())).variable(var).build();
                exp.setVariable(var, 0);
                ValidationResult result = exp.validate();
                validationResult = result.isValid();
            } catch (Exception e2) {
                // Validation is failed.
                // Second variable is found or invalid expression.
            }
        } catch (IllegalArgumentException e) {
            // Validation is failed.
        }

        return validationResult;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public int getIterationsCount() {
        return iterationsCount;
    }

    public void setIterationsCount(int iterationsCount) {
        this.iterationsCount = iterationsCount;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getrParameter() {
        return rParameter;
    }

    public void setrParameter(double r) {
        this.rParameter = r;
    }

    public double getLeftBoundInterval() {
        return leftBoundInterval;
    }

    public void setLeftBoundInterval(double leftBoundInterval) {
        this.leftBoundInterval = leftBoundInterval;
    }

    public double getRightBoundInterval() {
        return rightBoundInterval;
    }

    public void setRightBoundInterval(double rightBoundInterval) {
        this.rightBoundInterval = rightBoundInterval;
    }

    public int getIterationsCountResult() {
        return iterationsCountResult;
    }

    public void setIterationsCountResult(int iterationsCountResult) {
        this.iterationsCountResult = iterationsCountResult;
    }

    public double getArgumentValueResult() {
        return argumentValueResult;
    }

    public void setArgumentValueResult(double argumentValueResult) {
        this.argumentValueResult = argumentValueResult;
    }

    public double getFunctionValueResult() {
        return functionValueResult;
    }

    public void setFunctionValueResult(double functionValueResult) {
        this.functionValueResult = functionValueResult;
    }
}
