/*
 * Copyright (c) Lobachevsky University, 2018. All rights reserved.
 * Developed by: Nikolay Komarov.
 */
package com.optimization.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/forms/main.fxml"));
        primaryStage.setTitle("Global Optimization");
        primaryStage.setScene(new Scene(root, 1000, 460));
        primaryStage.show();
    }

    public static void startApp(String[] args) {
        launch(args);
    }
}
