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
import javafx.util.*;

import java.net.URL;

import java.util.*;
import java.io.*;

public class GmailInboxScene implements Initializable {

    public TextField textGetSearchBox;
    public Label inboxCount;
    public ComboBox<String> selectEmails;
    public ListView<Email> listInboxEmails;
    public ObservableList<Email> inboxData = FXCollections.observableArrayList();
    public ObservableList<String> emailSelectionOptions = FXCollections.observableArrayList("None", "Read", "Unread", "All");
    private static Stage mainStage;
    private static Gmail currentGmail;
    public CheckBox cbInbox = CheckBoxBuilder.create().build();

    @Override
    // collects emails from Gmail into the inbox data, add listeners to inbox emails, and then add inbox data to List View
    public void initialize(URL location, ResourceBundle resources) {
        if (currentGmail == null)
            return;

        selectEmails.setPromptText("Select Emails");
        selectEmails.setItems(emailSelectionOptions);
        inboxData.addAll(currentGmail.getInbox());
        inboxCount.setText(inboxData.size() + "");
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
                            for (Email email : inboxData)
                                email.setSelected(false);
                            break;
                        case "Read":
                            for (Email email : inboxData)
                                if (email.isRead())
                                    email.setSelected(true);
                            break;
                        case "Unread":
                            for (Email email : inboxData)
                                if (!email.isRead())
                                    email.setSelected(true);
                            break;
                        case "All":
                            for (Email email : inboxData)
                                email.setSelected(true);
                            break;
                    }
                }
            });
            return cell;
        });
        ChangeListener<Boolean> inboxListener = (paramObservableValue, paramT1, selected) -> {
            if (selected) {
                cbInbox.setSelected(true);
            } else
                cbInbox.setSelected(false);
        };
        Callback<Email, ObservableValue<Boolean>> getProperty = Email::selectedProperty;
        Callback<ListView<Email>, ListCell<Email>> forListView = CheckBoxListCell.forListView(getProperty);
        listInboxEmails.setCellFactory(forListView);

        for (Email email : inboxData)
            email.selectedProperty().addListener(inboxListener);
        listInboxEmails.setItems(inboxData);
    }

    public static void assignStage(Stage stage) {
        mainStage = stage;
    }

    public static void assignGmail(Gmail gmail) {
        currentGmail = gmail;
    }

    //public ObservableList<Email> getInboxData() { return inboxData; }
    //public ObservableList<String> getEmailSelectionOptions() {return emailSelectionOptions; }

    // records the latestScene and latestSceneTitle
    // switches the current scene to the compose email scene
    public void composeNewEmail(ActionEvent actionEvent) throws IOException {
        Main.getLatestGmailScenes().add(mainStage.getScene());
        Main.getLatestSceneTitles().add(Main.getCurrentSceneTitle());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("GmailComposeEmailScene.fxml"));
        Parent root = fxmlLoader.load();
        mainStage.setTitle("New Email");
        mainStage.setScene(new Scene(root));
    }

    // sets up the selectEmails Combo Box and loads the inbox scene
    public void checkInbox(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GmailInboxScene.fxml"));
        inboxCount.setText(inboxData.size() + "");
        Parent root = fxmlLoader.load();
        mainStage.setTitle("Gmail - Inbox");
        mainStage.setScene(new Scene(root));
    }

    // accesses the OutboxScene controller to view the outbox scene
    public void checkSent(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GmailOutboxScene.fxml"));
        fxmlLoader.load();
        GmailOutboxScene outboxController = fxmlLoader.getController();
        outboxController.checkSent(actionEvent);
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
        trashController.listTrashedEmails.refresh();
        trashController.checkTrash(actionEvent);
    }

    public void searchForEmail(KeyEvent keyEvent) {
        searchForEmail(textGetSearchBox, listInboxEmails, inboxData,  keyEvent.getCode());
    }

    // filters and displays inbox emails containing the search keyword
    public void searchForEmail(TextField searchBox, ListView<Email> listView, ObservableList<Email> observableList, KeyCode keyCode){
        if (keyCode == KeyCode.ENTER && searchBox.getText().equals(""))
            listView.setItems(observableList);
        else if (keyCode != KeyCode.ENTER)
            return;

        String key = searchBox.getText();
        ObservableList<Email> tempEmailData = FXCollections.observableArrayList(observableList);

        int index = 0;
        while (index < tempEmailData.size()) {
            Email tempEmail = tempEmailData.get(index);
            if (!(tempEmail.getRecipient().contains(key) || tempEmail.getSubject().contains(key) || tempEmail.getContent().contains(key)))
                tempEmailData.remove(index);
            else
                index++;
        }

        listView.setItems(tempEmailData);
    }

    // accesses the moveEmail method in TrashScene and moves all selected emails to trash
    public void moveInboxEmailsToTrash(ActionEvent actionEvent) throws IOException {
        boolean thereIs = false;
        for (Email email : inboxData) {
            if (email.isSelected())
                thereIs = true;
        }
        if (!thereIs)
            return;

        FXMLLoader trashScene = new FXMLLoader(getClass().getResource("GmailTrashScene.fxml"));
        trashScene.load();
        GmailTrashScene trashController = trashScene.getController();
        trashController.moveEmail(inboxData, trashController.trashData, listInboxEmails, trashController.listTrashedEmails, "inbox", "trash", currentGmail.getInbox(), currentGmail.getTrash());
    }

    // clears everything in a file because the file needs to be updated
    public void clearFile(String fileName) throws IOException {
        FileWriter fw = new FileWriter(fileName);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("");
        bw.close();
    }

    // interacts with the ReadingInboxEmails controller to assign values to that controller's variables
    // pops up the selected email in the inbox
    public void readInboxEmail(MouseEvent mouseEvent) throws IOException {
        FXMLLoader readInbox = new FXMLLoader(getClass().getResource("GmailReadingInboxEmails.fxml"));
        Parent root = readInbox.load();
        GmailReadingInboxEmails controller = readInbox.getController();

        Email selectedEmail = listInboxEmails.getSelectionModel().getSelectedItem();
        if (selectedEmail == null)
            return;

        String gmailAddress = currentGmail.getGoogleAccount().getGmailAddress();
        selectedEmail.setRead(true);
        inboxData.get(inboxData.indexOf(selectedEmail)).setRead(true);
        currentGmail.getInbox().get(currentGmail.getInbox().indexOf(selectedEmail)).setRead(true);

        String fileName = currentGmail.getGoogleAccount().getUsername() + "'s inbox.txt";
        clearFile(fileName);

        for (Email email : inboxData)
            email.writeToFile(fileName);

        controller.textReadEmailSender.setText(selectedEmail.getSender());
        controller.textReadEmailReceiver.setText(gmailAddress);
        controller.textReadEmailSubject.setText(selectedEmail.getSubject());
        controller.textReadEmailContent.setText(selectedEmail.getContent());
        controller.textReadEmailContent.setFont(new Font(selectedEmail.getFontName(), selectedEmail.getFontSize()));
        controller.vBoxReadEmailContent.getChildren().addAll(controller.textReadEmailContent);

        FXMLLoader inboxScene = new FXMLLoader(getClass().getResource("GmailInboxScene.fxml"));
        mainStage.setScene(new Scene(inboxScene.load()));

        Stage popup = new Stage();
        popup.setScene(new Scene(root));
        popup.show();
    }

    // sets up and loads the google account page
    public void loadAccountPage(MouseEvent mouseEvent) throws IOException {
        Main.getLatestGmailScenes().add(mainStage.getScene());
        Main.getLatestSceneTitles().add(Main.getCurrentSceneTitle());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GoogleAccountPage.fxml"));
        Parent root = fxmlLoader.load();
        GoogleAccountPage accountPageController = fxmlLoader.getController();
        GoogleAccount referenceAccount = currentGmail.getGoogleAccount();
        accountPageController.textGetGmailAddress.setText(referenceAccount.getGmailAddress());
        accountPageController.textGetGmailPassword.setText(referenceAccount.getPassword());
        accountPageController.textGetGoogleAccountFullName.setText(referenceAccount.getFirstName() + " " + referenceAccount.getLastName());
        mainStage.setTitle("");
        mainStage.setScene(new Scene(root));
    }
}
