package hacp.histofact;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApplication extends Application {
    public void start(Stage stage) throws Exception {
        //get the main fxml file
        try {
            URL location = getClass().getResource("/hacp/histofact/main.fxml");
            if (location == null) {
                throw new RuntimeException("FXML doc cannot be found please check the doc location.");
            }
            //load the main menu
            Parent root = FXMLLoader.load(location);
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("HistoFact");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args){
        launch(args);
    }
}