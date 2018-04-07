/*
 * Copyright (c) Lobachevsky University, 2018. All rights reserved.
 * Developed by: Nikolay Komarov.
 */
package com.optimization.core;

import net.objecthunter.exp4j.Expression;

public class StronginMethod extends AbstractMethod {
    private double rParam;
    private double m;

    public StronginMethod(Expression functionExpression, String variable,
                          double leftBound, double rightBound, double rParam) {
        super(functionExpression, variable, leftBound, rightBound);
        this.rParam = rParam;
    }

    @Override
    protected double calculateCharacteristic(int i) {
        double M = computeM();
        m = M > 0 ? rParam * M : 1;
        double deltaX = points.get(i + 1) - points.get(i);
        double deltaZ = functionValues.get(i + 1) - functionValues.get(i);
        return m * deltaX + deltaZ * deltaZ / (m * deltaX) - 2 * (functionValues.get(i + 1) + functionValues.get(i));
    }

    @Override
    protected double calculateNextPoint() {
        int index = getBestCharacteristicIndex();
        return 0.5 * (points.get(index + 1) + points.get(index) -
                (functionValues.get(index + 1) - functionValues.get(index)) / m);
    }
}
