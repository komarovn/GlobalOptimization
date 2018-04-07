/*
 * Copyright (c) Lobachevsky University, 2018. All rights reserved.
 * Developed by: Nikolay Komarov.
 */
package com.optimization.core;

import net.objecthunter.exp4j.Expression;

public class PiyavskiiMethod extends AbstractMethod {
    private double r;

    public PiyavskiiMethod(Expression functionExpression, String variable, double leftBound, double rightBound, double r) {
        super(functionExpression, variable, leftBound, rightBound);
        this.r = r;
    }

    @Override
    public void processStep() {

    }
}
