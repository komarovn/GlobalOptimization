/*
 * Copyright (c) Lobachevsky University, 2018. All rights reserved.
 * Developed by: Nikolay Komarov.
 */
package com.optimization.application;

import javafx.beans.NamedArg;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;

public class ExtendedLineChart<X extends Number, Y extends Number> extends LineChart<X, Y> {
    private double increment = 0.01;

    public ExtendedLineChart(@NamedArg("xAxis") Axis<X> xAxis,
                             @NamedArg("yAxis") Axis<Y> yAxis) {
        super(xAxis, yAxis);
        setCursor(Cursor.CROSSHAIR);
        xAxis.setAutoRanging(false);
    }

    public ExtendedLineChart(@NamedArg("xAxis") Axis<X> xAxis,
                             @NamedArg("yAxis") Axis<Y> yAxis,
                             @NamedArg("data") ObservableList<Series<X, Y>> data) {
        super(xAxis, yAxis, data);
        setCursor(Cursor.CROSSHAIR);
        xAxis.setAutoRanging(false);
    }

    public void setInterval(double leftBound, double rightBound) {
        double delta = rightBound - leftBound;
        this.increment = Math.max(delta * 0.005, 0.01);
        double epsilon = delta * 0.1;
        epsilon = -0.0001 < epsilon && epsilon < 0.0001 ? 10.0 : epsilon;

        // Expand visible interval of X-axis by 10 per cent of length to both sides.
        leftBound -= epsilon;
        rightBound += epsilon;

        ((ValueAxis<X>) getXAxis()).setLowerBound(leftBound);
        ((ValueAxis<X>) getXAxis()).setUpperBound(rightBound);
    }

    public double getLeftBoundInterval() {
        return ((ValueAxis<X>) getXAxis()).getLowerBound();
    }

    public double getRightBoundInterval() {
        return ((ValueAxis<X>) getXAxis()).getUpperBound();
    }

    public double getIncrement() {
        return increment;
    }

    public void clearSeries(Series<X, Y> series) {
        this.setAnimated(false);
        series.getData().clear();
        this.setAnimated(true);
    }
}
