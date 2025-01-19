package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class GmailLoginPassword {
    public Label labelLoginEmail;
    public VBox vboxLoginViewPassword;
    public PasswordField passwordFieldGoogleLogin;
    public TextField textGoogleLoginRevealPassword = new TextField(); // this isn't declared in any FXML files
    public CheckBox checkboxShowPasswordLogin;
    public Label labelIncorrectPassword;
    private boolean setOnce = false;

    // two-way binds on the TextFields and PasswordFields to display the user input passwords
    // only binds once
    public void showLoginPassword(ActionEvent actionEvent) {
        if (!setOnce) {
            setOnce = true;
            textGoogleLoginRevealPassword.setMaxHeight(47);
            textGoogleLoginRevealPassword.setManaged(false);
            textGoogleLoginRevealPassword.setVisible(false);
            textGoogleLoginRevealPassword.managedProperty().bind(checkboxShowPasswordLogin.selectedProperty());
            textGoogleLoginRevealPassword.visibleProperty().bind(checkboxShowPasswordLogin.selectedProperty());
            textGoogleLoginRevealPassword.textProperty().bindBidirectional(passwordFieldGoogleLogin.textProperty());
            vboxLoginViewPassword.getChildren().add(textGoogleLoginRevealPassword);
        }
    }

    // if the user enters the correct password, switch the current scene to the user's inbox
    public void loginPasswordComplete(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GmailInboxScene.fxml"));
        fxmlLoader.load();
        GmailInboxScene inboxController = fxmlLoader.getController();
        if (Main.getGoogleAccountList().get(Main.getGoogleAccountList().indexOf(labelLoginEmail.getText()) + 1).equals(passwordFieldGoogleLogin.getText())) {
            labelIncorrectPassword.setOpacity(0);
            passwordFieldGoogleLogin.clear();

            inboxController.checkInbox(actionEvent);
        } else {
            labelIncorrectPassword.setOpacity(1);
        }
    }

    public void validatePassword(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            loginPasswordComplete(new ActionEvent());
        }
    }
}
