/*
 * Copyright (c) Lobachevsky University, 2018. All rights reserved.
 * Developed by: Nikolay Komarov.
 */
package com.optimization.core;

import net.objecthunter.exp4j.Expression;

public class ScanMethod extends AbstractMethod {

    public ScanMethod(Expression functionExpression, String variable, double leftBound, double rightBound) {
        super(functionExpression, variable, leftBound, rightBound);
    }

    @Override
    protected double calculateCharacteristic(int i) {
        return points.get(i + 1) - points.get(i);
    }

    @Override
    protected double calculateNextPoint() {
        int bestCharacteristicIndex = getBestCharacteristicIndex();
        return 0.5 * (points.get(bestCharacteristicIndex + 1) + points.get(bestCharacteristicIndex));
    }
}
