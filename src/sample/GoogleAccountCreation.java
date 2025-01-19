package sample;

import javafx.beans.value.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Callback;

import java.io.*;

public class GoogleAccountCreation {
    public Label labelUsernameWasTaken;
    public TextField textGetAccountFirstName;
    public TextField textGetAccountLastName;
    public TextField textGetAccountUsername;
    public Label labelPasswordIsInvalid;
    public HBox hboxSignupViewPasswords;
    public PasswordField passwordFieldCreateAccount;
    public PasswordField passwordFieldCreateAccountConfirm;
    public CheckBox checkboxShowPasswordCreateAccount;
    public TextField textGoogleSignupRevealConfirmedPassword = new TextField(),  // this isn't declared in any FXML files
            textGoogleSignupRevealPassword = new TextField();  // this isn't declared in any FXML files
    private boolean setOnce = false;
    private static Stage mainStage;
    private static Gmail currentGmail;

    public static void assignStage(Stage stage) {
        mainStage = stage;
    }

    public static void assignGmail(Gmail gmail) {
        currentGmail = gmail;
    }

    // two-way binds on the TextFields and PasswordFields to display the user input passwords
    // only binds once
    public void showCreateAccountPassword(ActionEvent actionEvent) {
        if (passwordFieldCreateAccount.getText().equals(""))
            return;

        if (!setOnce) {
            setOnce = true;
            textGoogleSignupRevealPassword.setMaxWidth(150);
            textGoogleSignupRevealPassword.setMaxHeight(32);
            textGoogleSignupRevealPassword.setManaged(false);
            textGoogleSignupRevealPassword.setVisible(false);
            textGoogleSignupRevealPassword.managedProperty().bind(checkboxShowPasswordCreateAccount.selectedProperty());
            textGoogleSignupRevealPassword.visibleProperty().bind(checkboxShowPasswordCreateAccount.selectedProperty());
            textGoogleSignupRevealPassword.textProperty().bindBidirectional(passwordFieldCreateAccount.textProperty());

            passwordFieldCreateAccountConfirm.setVisible(true);
            textGoogleSignupRevealConfirmedPassword.setMaxWidth(150);
            textGoogleSignupRevealConfirmedPassword.setMaxHeight(32);
            textGoogleSignupRevealConfirmedPassword.setManaged(false);
            textGoogleSignupRevealConfirmedPassword.setVisible(false);
            textGoogleSignupRevealConfirmedPassword.managedProperty().bind(checkboxShowPasswordCreateAccount.selectedProperty());
            textGoogleSignupRevealConfirmedPassword.visibleProperty().bind(checkboxShowPasswordCreateAccount.selectedProperty());
            textGoogleSignupRevealConfirmedPassword.textProperty().bindBidirectional(passwordFieldCreateAccountConfirm.textProperty());

            VBox formatting = new VBox();
            formatting.setMinWidth(46);
            hboxSignupViewPasswords.getChildren().addAll(textGoogleSignupRevealPassword, textGoogleSignupRevealConfirmedPassword, formatting);
        }
    }

    // interacts with Gmail login class to switch scene
    public void signinInstead(ActionEvent actionEvent) throws IOException {
        clearCreateAccountTextFields();
        FXMLLoader signInEmail = new FXMLLoader(getClass().getResource("GmailLoginEmail.fxml"));
        Parent root = signInEmail.load();
        mainStage.setTitle("Login Gmail");
        mainStage.setScene(new Scene(root));
    }

    // creates a new google account if user inputs meet the requirements
    // sets up the new user's inbox
    public void createAccountComplete(ActionEvent actionEvent) throws IOException {
        String password = passwordFieldCreateAccount.getText(), confirmedPassword = passwordFieldCreateAccountConfirm.getText();

        if (whetherThisAccountExists(textGetAccountUsername.getText() + "@gmail.com")) {
            labelUsernameWasTaken.setOpacity(1);
        } else if (password.equals(password.replaceAll("[a-z]", "")) || password.equals(password.replaceAll("[A-Z]", "")) || password.equals(password.replaceAll("[0-9]", "")) || password.length() < 8) {
            labelPasswordIsInvalid.setOpacity(1);
        } else if (!password.equals(confirmedPassword)) {
            labelPasswordIsInvalid.setOpacity(1);
            labelPasswordIsInvalid.setText("                   The passwords don't match");
        } else {
            labelPasswordIsInvalid.setOpacity(0);
            labelPasswordIsInvalid.setText("                   Your password does not meet the requirement");
            GoogleAccount newGoogleAccount = new GoogleAccount(textGetAccountUsername.getText(), passwordFieldCreateAccount.getText(), textGetAccountFirstName.getText(), textGetAccountLastName.getText());
            newGoogleAccount.writeToFile();
            clearCreateAccountTextFields();

            FXMLLoader fxmlLoaderInbox = new FXMLLoader(getClass().getResource("GmailInboxScene.fxml"));
            Parent root = fxmlLoaderInbox.load();
            GmailInboxScene inboxController = fxmlLoaderInbox.getController();

            Gmail assignGmail = new Gmail(newGoogleAccount, true);
            currentGmail = assignGmail;
            GmailComposeEmailScene.assignGmail(assignGmail);
            GmailInboxScene.assignGmail(assignGmail);
            GmailOutboxScene.assignGmail(assignGmail);
            GmailDraftsScene.assignGmail(assignGmail);
            GmailTrashScene.assignGmail(assignGmail);
            GmailReplyingToInboxPopup.assignGmail(assignGmail);
            GmailReplyingToOutboxPopup.assignGmail(assignGmail);
            inboxController.checkInbox(actionEvent);
        }
    }

    public void clearCreateAccountTextFields() {
        textGetAccountFirstName.clear();
        textGetAccountLastName.clear();
        textGetAccountUsername.clear();
        passwordFieldCreateAccount.clear();
        passwordFieldCreateAccountConfirm.clear();
    }

    // reads our google account record and adds to an ArrayList called googleAccountList in Main.java
    public boolean whetherThisAccountExists(String gmailUsername) throws IOException {
        FileReader fr = new FileReader("Google Accounts.txt");
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
            Main.getGoogleAccountList().add(line);
        }
        br.close();
        return Main.getGoogleAccountList().contains(gmailUsername);
    }

    // part of the emailSelection ComboBox setup
    public void setSelectEmails(String option, ObservableList<Email> data) {
        switch (option) {
            case "None":
                for (Email email : data)
                    email.setSelected(false);
                break;
            case "Read":
                for (Email email : data)
                    if (email.isRead())
                        email.setSelected(true);
                break;
            case "Unread":
                for (Email email : data)
                    if (!email.isRead())
                        email.setSelected(true);
                break;
            case "All":
                for (Email email : data)
                    email.setSelected(true);
                break;
        }
    }
}
