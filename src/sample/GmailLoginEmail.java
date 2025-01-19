package sample;

import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class GmailLoginEmail implements Initializable{
    public TextField textGetGoogleLoginEmail;
    public Label labelEmailDoesNotExist;
    private static Stage mainStage;

    public static void assignStage(Stage stage) {
        mainStage = stage;
    }

    // switches current scene to create a new google account
    public void createGoogleAccount(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GoogleAccountCreation.fxml"));
        Parent root = fxmlLoader.load();
        mainStage.setTitle("Google Account Creation");
        mainStage.setScene(new Scene(root));
    }

    // if the Google user exists in the system's record, loads the google account from our record
    public void loginEmailComplete(ActionEvent actionEvent) throws IOException {
        String temp, gmailAddress = textGetGoogleLoginEmail.getText();
        FXMLLoader fxmlLoaderCreation = new FXMLLoader(getClass().getResource("GoogleAccountCreation.fxml"));
        fxmlLoaderCreation.load();
        GoogleAccountCreation creationController = fxmlLoaderCreation.getController();

        FXMLLoader fxmlLoaderLoginPassword = new FXMLLoader(getClass().getResource("GmailLoginPassword.fxml"));
        Parent root = fxmlLoaderLoginPassword.load();
        GmailLoginPassword loginPasswordController = fxmlLoaderLoginPassword.getController();

        if (gmailAddress.contains("@gmail.com"))
            temp = "";
        else
            temp = "@gmail.com";
        if (creationController.whetherThisAccountExists(gmailAddress + temp)) {
            labelEmailDoesNotExist.setOpacity(0);
            textGetGoogleLoginEmail.clear();

            mainStage.setScene(new Scene(root));

            loginPasswordController.labelLoginEmail.setText(gmailAddress + temp);
            int index = Main.getGoogleAccountList().indexOf(gmailAddress + temp);
            Gmail loginGmail = new Gmail(new GoogleAccount(Main.getGoogleAccountList().get(index).replaceAll("@gmail.com",""), Main.getGoogleAccountList().get(index + 1), Main.getGoogleAccountList().get(index + 2), Main.getGoogleAccountList().get(index + 3)), false);

            GmailComposeEmailScene.assignGmail(loginGmail);
            GmailInboxScene.assignGmail(loginGmail);
            GmailOutboxScene.assignGmail(loginGmail);
            GmailDraftsScene.assignGmail(loginGmail);
            GmailTrashScene.assignGmail(loginGmail);
            GmailReplyingToOutboxPopup.assignGmail(loginGmail);
            GmailReplyingToInboxPopup.assignGmail(loginGmail);
        } else {
            labelEmailDoesNotExist.setOpacity(1);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void validateEmail(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            loginEmailComplete(new ActionEvent());
        }
    }
}
