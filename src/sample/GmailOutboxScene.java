package sample;

import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.net.URL;
import java.util.*;

public class GmailOutboxScene implements Initializable {

    public TextField textGetSearchBox;
    public Label sentCount;
    public ComboBox<String> selectEmails;
    public ListView<Email> listOutboxEmails;
    public ObservableList<Email> outboxData = FXCollections.observableArrayList();
    private ObservableList<String> emailSelectionOptions = FXCollections.observableArrayList("None", "Read", "Unread", "All");
    private static Stage mainStage;
    private static Gmail currentGmail;
    public CheckBox cbOutbox = CheckBoxBuilder.create().build();

    @Override
    // collects emails from Gmail into the outbox data, add listeners to outbox emails, and then add outbox data to List View
    public void initialize(URL location, ResourceBundle resources) {
        if (currentGmail == null)
            return;

        selectEmails.setPromptText("Select Emails");
        selectEmails.setItems(emailSelectionOptions);
        outboxData.addAll(currentGmail.getOutbox());
        sentCount.setText(outboxData.size() + "");
        selectEmails.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            cell.setOnMousePressed(e -> {
                if (!cell.isEmpty()) {
                    switch (cell.getItem()) {
                        case "None":
                            for (Email email : outboxData)
                                email.setSelected(false);
                            break;
                        case "Read":
                            for (Email email : outboxData)
                                if (email.isRead())
                                    email.setSelected(true);
                            break;
                        case "Unread":
                            for (Email email : outboxData)
                                if (!email.isRead())
                                    email.setSelected(true);
                            break;
                        case "All":
                            for (Email email : outboxData)
                                email.setSelected(true);
                            break;
                    }
                }
            });
            return cell;
        });
        ChangeListener<Boolean> inboxListener = (paramObservableValue, paramT1, selected) -> {
            if (selected) {
                cbOutbox.setSelected(true);
            } else
                cbOutbox.setSelected(false);
        };
        Callback<Email, ObservableValue<Boolean>> getProperty = Email::selectedProperty;
        Callback<ListView<Email>, ListCell<Email>> forListView = CheckBoxListCell.forListView(getProperty);
        listOutboxEmails.setCellFactory(forListView);

        for (Email email : outboxData)
            email.selectedProperty().addListener(inboxListener);
        listOutboxEmails.setItems(outboxData);
    }

    public static void assignStage(Stage stage) {
        mainStage = stage;
    }

    public static void assignGmail(Gmail gmail) {
        currentGmail = gmail;
    }

    public Gmail getCurrentGmail() {
        return currentGmail;
    }

    //public ObservableList<Email> getOutboxData() { return outboxData; }

    // records the latestScene and latestSceneTitle
    // switches the current scene to the compose email scene
    public void composeNewEmail(ActionEvent actionEvent) throws IOException {
        Main.getLatestGmailScenes().add(mainStage.getScene());
        Main.getLatestSceneTitles().add(Main.getCurrentSceneTitle());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("GmailComposeEmailScene.fxml"));
        Parent root = fxmlLoader.load();
        mainStage.setScene(new Scene(root));
    }

    // accesses the InboxScene controller to view the inbox scene
    public void checkInbox(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GmailInboxScene.fxml"));
        fxmlLoader.load();
        GmailInboxScene controller = fxmlLoader.getController();
        controller.checkInbox(actionEvent);
    }

    // sets up the selectEmails Combo Box and loads the outbox scene
    public void checkSent(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GmailOutboxScene.fxml"));
        Parent root = fxmlLoader.load();
        listOutboxEmails.refresh();
        mainStage.setTitle("Gmail - Outbox");
        mainStage.setScene(new Scene(root));
    }

    // accesses the DraftsScene controller to view the drafts scene
    public void checkDrafts(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GmailDraftsScene.fxml"));
        fxmlLoader.load();
        GmailDraftsScene draftsController = fxmlLoader.getController();
        draftsController.checkDrafts(actionEvent);
    }

    // accesses the TrashScene controller to view the trashed emails
    public void checkTrash(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GmailTrashScene.fxml"));
        fxmlLoader.load();
        GmailTrashScene trashController = fxmlLoader.getController();
        trashController.checkTrash(actionEvent);
    }

    // accesses a method from the inbox controller
    // filters and displays outbox emails containing the search keyword
    public void searchForMail(KeyEvent keyEvent) throws IOException {
        FXMLLoader inboxScene = new FXMLLoader(getClass().getResource("GmailInboxScene.fxml"));
        inboxScene.load();
        GmailInboxScene inboxController = inboxScene.getController();
        inboxController.searchForEmail(textGetSearchBox, listOutboxEmails, outboxData, keyEvent.getCode());
    }

    // accesses the moveEmail method in TrashScene and moves all selected emails to trash
    public void moveSentEmailsToTrash(ActionEvent actionEvent) throws IOException {
        boolean thereIs = false;
        for (Email email : outboxData) {
            if (email.isSelected())
                thereIs = true;
        }
        if (!thereIs)
            return;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("GmailTrashScene.fxml"));
        fxmlLoader.load();
        GmailTrashScene trashController = fxmlLoader.getController();
        trashController.moveEmail(outboxData, trashController.trashData, listOutboxEmails, trashController.listTrashedEmails, "outbox", "trash", currentGmail.getOutbox(), currentGmail.getTrash());
    }

    // interacts with the ReadingOutboxEmails controller to assign values to that controller's variables
    // pops up the selected email in the outbox
    public void readSentEmail(MouseEvent mouseEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GmailReadingOutboxEmails.fxml"));
        Parent root = fxmlLoader.load();
        GmailReadingOutboxEmails controller = fxmlLoader.getController();

        Email selectedEmail = listOutboxEmails.getSelectionModel().getSelectedItem();
        if (selectedEmail == null)
            return;

        String gmailAddress = currentGmail.getGoogleAccount().getGmailAddress();
        selectedEmail.setRead(true);
        outboxData.get(outboxData.indexOf(selectedEmail)).setRead(true);
        currentGmail.getOutbox().get(currentGmail.getOutbox().indexOf(selectedEmail)).setRead(true);

        String fileName = currentGmail.getGoogleAccount().getUsername() + "'s outbox.txt";
        clearFile(fileName);

        for (Email email : outboxData)
            email.writeToFile(fileName);

        controller.textReadEmailSender.setText(selectedEmail.getRecipient());
        controller.textReadEmailReceiver.setText(gmailAddress);
        controller.textReadEmailSubject.setText(selectedEmail.getSubject());
        controller.textReadEmailContent.setText(selectedEmail.getContent());
        controller.textReadEmailContent.setFont(new Font(selectedEmail.getFontName(), selectedEmail.getFontSize()));
        controller.vBoxReadEmailContent.getChildren().addAll(controller.textReadEmailContent);

        FXMLLoader outboxScene = new FXMLLoader(getClass().getResource("GmailOutboxScene.fxml"));
        mainStage.setScene(new Scene(outboxScene.load()));

        Stage popup = new Stage();
        popup.setScene(new Scene(root));
        popup.show();
    }

    // clears everything in a file because the file needs to be updated
    public void clearFile(String fileName) throws IOException {
        FileWriter fw = new FileWriter(fileName);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("");
        bw.close();
    }

    // accesses a method in InboxScene to load the google account information
    public void loadAccountPage(MouseEvent mouseEvent) throws IOException {
        FXMLLoader inboxScene = new FXMLLoader(getClass().getResource("GmailInboxScene.fxml"));
        inboxScene.load();
        GmailInboxScene inboxController = inboxScene.getController();
        inboxController.loadAccountPage(mouseEvent);
    }
}
