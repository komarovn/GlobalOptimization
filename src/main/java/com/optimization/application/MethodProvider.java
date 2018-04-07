/*
 * Copyright (c) Lobachevsky University, 2018. All rights reserved.
 * Developed by: Nikolay Komarov.
 */
package com.optimization.application;

import com.optimization.core.AbstractMethod;
import com.optimization.core.PiyavskiiMethod;
import com.optimization.core.ScanMethod;
import com.optimization.core.StronginMethod;
import javafx.application.Platform;

public class MethodProvider {
    private AppModel data;
    private Controller app;
    private AbstractMethod method;
    private double currentPrecision = Double.MAX_VALUE;
    private int currentIterationsCount = 0;

    public void process() {
        switch (data.getMethodName()) {
            case "piyavskii":
                method = new PiyavskiiMethod(data.getFunctionExpression(), data.getVariableName(),
                        data.getLeftBoundInterval(), data.getRightBoundInterval(), data.getrParameter());
                break;
            case "strongin":
                method = new StronginMethod(data.getFunctionExpression(), data.getVariableName(),
                        data.getLeftBoundInterval(), data.getRightBoundInterval(), data.getrParameter());
                break;
            case "scan":
            default:
                method = new ScanMethod(data.getFunctionExpression(), data.getVariableName(),
                        data.getLeftBoundInterval(), data.getRightBoundInterval());
        }

        if (data.isStopByPrecision()) {
            double precision = data.getPrecision();

        } else {
            int iterationsCount = data.getIterationsCount();

            for (int i = 0; i < iterationsCount; i++) {
                method.processStep();
                currentIterationsCount++;
                makeTick();
            }
        }

        publishResults();
    }

    public void setData(AppModel data) {
        this.data = data;
    }

    public void setApp(Controller app) {
        this.app = app;
    }

    private void makeTick() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                app.getPlotArea().addTickMarker(method.getCurrentArgumentValue());
            }
        });
    }

    private void publishResults() {
        data.setIterationsCountResult(currentIterationsCount);
        data.setArgumentValueResult(method.getCurrentArgumentValue());
        data.setFunctionValueResult(method.getCurrentFunctionValue());

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                app.updateResultSection();
                app.setVisibilityResultsSection(true);
            }
        });
    }

    public void reset() {
        currentPrecision = Double.MAX_VALUE;
        currentIterationsCount = 0;
        method = null;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                app.getPlotArea().clearTicks();
                app.setVisibilityResultsSection(false);
            }
        });
    }
}
