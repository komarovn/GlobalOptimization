/*
 * Copyright (c) Lobachevsky University, 2018. All rights reserved.
 * Developed by: Nikolay Komarov.
 */
package com.optimization.application;

import javafx.beans.InvalidationListener;
import javafx.beans.NamedArg;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.shape.Line;
import javafx.util.Callback;

public class ExtendedLineChart extends LineChart<Double, Double> {
    private double increment = 0.01;

    private ObservableList<Data<Double, Double>> ticks;
    private Data<Double, Double> leftBoundMarker;
    private Data<Double, Double> rightBoundMarker;

    public ExtendedLineChart(@NamedArg("xAxis") Axis<Double> xAxis,
                             @NamedArg("yAxis") Axis<Double> yAxis) {
        super(xAxis, yAxis);
        initPlot();
    }

    public ExtendedLineChart(@NamedArg("xAxis") Axis<Double> xAxis,
                             @NamedArg("yAxis") Axis<Double> yAxis,
                             @NamedArg("data") ObservableList<Series<Double, Double>> data) {
        super(xAxis, yAxis, data);
        initPlot();
    }

    public void initPlot() {
        setCursor(Cursor.CROSSHAIR);
        getXAxis().setAutoRanging(false);

        ticks = FXCollections.observableArrayList(new Callback<Data<Double, Double>, Observable[]>() {
            @Override
            public Observable[] call(Data<Double, Double> param) {
                return new Observable[] { param.XValueProperty() };
            }
        });
        ticks.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                layoutPlotChildren();
            }
        });

        leftBoundMarker = new Data<Double, Double>(0.0, 0.0);
        rightBoundMarker = new Data<Double, Double>(0.0, 0.0);
        addMarkerLine(leftBoundMarker, "interval-marker");
        addMarkerLine(rightBoundMarker, "interval-marker");
    }

    public void setInterval(double leftBound, double rightBound) {
        double delta = rightBound - leftBound;
        this.increment = Math.max(delta * 0.005, 0.01);
        double epsilon = delta * 0.1;
        epsilon = -0.0001 < epsilon && epsilon < 0.0001 ? 10.0 : epsilon;

        leftBoundMarker.setXValue(leftBound);
        rightBoundMarker.setXValue(rightBound);

        // Expand visible interval of X-axis by 10 per cent of length to both sides.
        leftBound -= epsilon;
        rightBound += epsilon;

        ((ValueAxis<Double>) getXAxis()).setLowerBound(leftBound);
        ((ValueAxis<Double>) getXAxis()).setUpperBound(rightBound);
    }

    public double getLeftBoundInterval() {
        return ((ValueAxis<Double>) getXAxis()).getLowerBound();
    }

    public double getRightBoundInterval() {
        return ((ValueAxis<Double>) getXAxis()).getUpperBound();
    }

    public double getIncrement() {
        return increment;
    }

    public void clearSeries(Series<Double, Double> series) {
        this.setAnimated(false);
        series.getData().clear();
        this.setAnimated(true);
    }

    public void addTickMarker(double tick) {
        Data<Double, Double> marker = new Data<Double, Double>(tick, 0.0);
        addMarkerLine(marker, "tick");
        ticks.add(marker);
    }

    public void clearTicks() {
        ticks.clear();
    }

    private void addMarkerLine(Data<Double, Double> marker, String cls) {
        Line line = new Line();

        if (cls != null && cls.length() != 0) {
            line.getStyleClass().add("plot__" + cls);
        }

        marker.setNode(line);
        getPlotChildren().add(line);
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();

        drawVerticalLine(leftBoundMarker);
        drawVerticalLine(rightBoundMarker);

        for (Data<Double, Double> marker : ticks) {
            drawVerticalLine(marker);
        }
    }

    private void drawVerticalLine(Data<Double, Double> marker) {
        Line line = (Line) marker.getNode();
        line.setStartX(getXAxis().getDisplayPosition(marker.getXValue()));
        line.setEndX(line.getStartX());
        line.setStartY(0.0);
        line.setEndY(getBoundsInLocal().getHeight());
        line.toFront();
    }
}
