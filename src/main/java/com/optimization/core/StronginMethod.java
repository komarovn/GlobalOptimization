/*
 * Copyright (c) Lobachevsky University, 2018. All rights reserved.
 * Developed by: Nikolay Komarov.
 */
package com.optimization.core;

import net.objecthunter.exp4j.Expression;

public class StronginMethod extends AbstractMethod {
    private double rParam;

    public StronginMethod(Expression functionExpression, String variable, double leftBound, double rightBound, double rParam) {
        super(functionExpression, variable, leftBound, rightBound);
        this.rParam = rParam;
    }

    @Override
    protected double calculateCharacteristic(int i) {
        return 0.0;
    }

    @Override
    protected double calculateNextPoint() {
        return 0;
    }
}
