package com.disain.main;

import com.disain.main.resources.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static MainController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/controllers/fxml/MainScene.fxml"));
        Parent root = loader.load();
        mainController = loader.getController();
        primaryStage.setTitle("Cryptographer");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.setOnHiding(event -> System.exit(0));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
