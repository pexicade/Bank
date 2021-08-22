package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static Connection connection;
    public static boolean isConnected = false;
    public static Parent root;
    public static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception{
        root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        primaryStage.setTitle("ورود");
        primaryStage.setScene(new Scene(root,900,606));
        primaryStage.show();
    }

    public static void main(String[] args) {
        System.out.println("Start");
        try {
            connection = new Connection(8000);
            System.out.println("CONNECTED");
            isConnected = true;
        } catch(IOException e) {
//            e.printStackTrace();
            System.out.println("ERR: Couldn't connect to server");
        }
        launch(args);
    }

}
