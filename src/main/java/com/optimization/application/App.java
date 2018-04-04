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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/forms/main.fxml"));
        Parent root = loader.load();

        Controller controller = loader.getController();
        AppModel model = new AppModel();
        controller.setModel(model);

        primaryStage.setTitle(StringResources.APP_NAME);
        primaryStage.setScene(new Scene(root, 1000, 460));
        primaryStage.setMinHeight(380);
        primaryStage.setMinWidth(800);
        primaryStage.show();
    }

    public static void startApp(String[] args) {
        launch(args);
    }
}
