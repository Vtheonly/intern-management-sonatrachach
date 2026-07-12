package com.example.intern_manegement_app;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
public class main extends Application {
    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage stage) throws IOException {
//      user_insertion update_worker_user update_intern login_page intern_insertion decision_Intern
        FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("login_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setTitle("login");
        stage.setResizable(true);
        stage.show();
    }
}
