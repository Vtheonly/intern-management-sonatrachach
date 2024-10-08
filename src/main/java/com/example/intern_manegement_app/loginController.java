package com.example.intern_manegement_app;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

import static com.example.intern_manegement_app.toolkit.hashIt;

public class loginController {
  @FXML private TextField username;
  @FXML private PasswordField password;
  @FXML private AnchorPane loginP;
  Stage loginWindow ;
  public void onLoginClick() throws NoSuchAlgorithmException {
    loginWindow = (Stage) loginP.getScene().getWindow();
    if (oracleConnector.login(username.getText(), hashIt(password.getText()))) {
      JOptionPane.showMessageDialog(null, "Access Granted. ", "Information", JOptionPane.INFORMATION_MESSAGE);
      // admin
      if (oracleConnector.isAdmin(username.getText(),  hashIt(password.getText()))) {
        try {
          FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("user_insertion.fxml"));
          Parent root = fxmlLoader.load();
          Stage stage = new Stage();
          stage.setScene(new Scene(root));
          stage.show();
          loginWindow.close();
        } catch (IOException e) {
          e.printStackTrace();
        }}
      // chief
      else if (oracleConnector.isChief(username.getText(),  hashIt(password.getText()))){
        try {
          FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("decision_Intern.fxml"));
          Parent root = fxmlLoader.load();
          Stage stage = new Stage();
          stage.setTitle("The Chief of Department");
          stage.setScene(new Scene(root));
          stage.show();
          loginWindow.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      // secretary
      else {
        try {
          FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("intern_insertion.fxml"));
          Parent root = fxmlLoader.load();
          insertionInternController controller = fxmlLoader.getController();
          int accessLevel =oracleConnector.getUserAccessLevel(username.getText(), hashIt(password.getText()));
          controller.setAccessLevel(accessLevel);
          Stage stage = new Stage();
          stage.setScene(new Scene(root));
          stage.setTitle("The Clerk");
          stage.show();
          loginWindow.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }else{
      JOptionPane.showMessageDialog(null, "Invalid username or password. ", "Information", JOptionPane.INFORMATION_MESSAGE);
    }
  }
}