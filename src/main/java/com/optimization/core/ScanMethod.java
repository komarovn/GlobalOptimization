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
    public void processStep() {

    }
}
