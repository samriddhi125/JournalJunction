package com.example.javaproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("SignIn.fxml"));
        InputStream inputStream = new FileInputStream("C:\\Users\\ASUS\\IdeaProjects\\JavaProject\\src\\main\\resources\\com\\example\\javaproject\\jjlogo.png");
        Image icon = new Image(inputStream);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("JournalJunction");
        stage.setScene(scene);
        stage.getIcons().add(icon);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
