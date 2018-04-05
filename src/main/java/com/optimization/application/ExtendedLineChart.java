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

public class ExtendedLineChart<X extends Number, Y extends Number> extends LineChart<X, Y> {

    public ExtendedLineChart(@NamedArg("xAxis") Axis<X> xAxis,
                             @NamedArg("yAxis") Axis<Y> yAxis) {
        super(xAxis, yAxis);
        setCursor(Cursor.CROSSHAIR);
    }

    public ExtendedLineChart(@NamedArg("xAxis") Axis<X> xAxis,
                             @NamedArg("yAxis") Axis<Y> yAxis,
                             @NamedArg("data") ObservableList<Series<X, Y>> data) {
        super(xAxis, yAxis, data);
        setCursor(Cursor.CROSSHAIR);
    }
}
