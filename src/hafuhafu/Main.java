package hafuhafu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Image Picker");
        primaryStage.setScene(new Scene(getRoot()));
        primaryStage.show();
    }

    public static Parent getRoot() throws IOException {
        return FXMLLoader.load(Main.class.getResource("controller/home.fxml"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
