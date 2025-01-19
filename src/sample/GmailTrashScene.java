package sample;

import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.ResourceBundle;

public class GmailTrashScene implements Initializable {
    public TextField textGetSearchBox;
    public Label trashCount;
    public ComboBox<String> selectEmails;
    public ComboBox<String> recoverEmailTo;
    public ListView<Email> listTrashedEmails;
    private ObservableList<String> emailSelectionOptions = FXCollections.observableArrayList("None", "Read", "Unread", "All");
    private ObservableList<String> recoverOptions = FXCollections.observableArrayList("Move to Inbox", "Move to Outbox", "Move to Drafts");
    public ObservableList<Email> trashData = FXCollections.observableArrayList();
    private static Stage mainStage;
    private static Gmail currentGmail;
    public CheckBox cbTrash = CheckBoxBuilder.create().build();
    private String username;

    // collects emails from Gmail into the trash data, add listeners to trash emails, and then add trash data to List View
    public void initialize(URL location, ResourceBundle resources) {
        if (currentGmail == null)
            return;

        trashData.addAll(currentGmail.getTrash());
        username = currentGmail.getGoogleAccount().getUsername();
        trashCount.setText(trashData.size() + "");
        selectEmails.setItems(emailSelectionOptions);
        selectEmails.setPromptText("Select Emails");
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
                            for (Email email : trashData)
                                email.setSelected(false);
                            break;
                        case "Read":
                            for (Email email : trashData)
                                if (email.isRead())
                                    email.setSelected(true);
                            break;
                        case "Unread":
                            for (Email email : trashData)
                                if (!email.isRead())
                                    email.setSelected(true);
                            break;
                        case "All":
                            for (Email email : trashData)
                                email.setSelected(true);
                            break;
                    }
                }
            });
            return cell;
        });
        recoverEmailTo.setItems(recoverOptions);
        recoverEmailTo.setPromptText("Recover Options");
        FXMLLoader inboxLoader = new FXMLLoader(getClass().getResource("GmailInboxScene.fxml"));
        FXMLLoader outboxLoader = new FXMLLoader(getClass().getResource("GmailOutboxScene.fxml"));
        FXMLLoader draftsLoader = new FXMLLoader(getClass().getResource("GmailDraftsScene.fxml"));
        try {
            inboxLoader.load();
            outboxLoader.load();
            draftsLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        GmailInboxScene inboxController = inboxLoader.getController();
        GmailOutboxScene outboxController = outboxLoader.getController();
        GmailDraftsScene draftsController = draftsLoader.getController();
        recoverEmailTo.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            cell.setOnMousePressed(e -> {
                if (!cell.isEmpty()) {
                    try {
                        switch (cell.getItem()) {
                            case "Move to Inbox":
                                moveEmail(trashData, inboxController.inboxData, listTrashedEmails, inboxController.listInboxEmails, "trash", "inbox", currentGmail.getTrash(), currentGmail.getInbox());
                                inboxController.inboxCount.setText(inboxController.inboxData.size() + "");
                                break;
                            case "Move to Outbox":
                                moveEmail(trashData, outboxController.outboxData, listTrashedEmails, outboxController.listOutboxEmails, "trash", "outbox", currentGmail.getTrash(), currentGmail.getOutbox());
                                outboxController.sentCount.setText(outboxController.outboxData.size() + "");
                                break;
                            case "Move to Drafts":
                                moveEmail(trashData, draftsController.draftsData, listTrashedEmails, draftsController.listDraftEmails, "trash", "drafts", currentGmail.getTrash(), currentGmail.getDrafts());
                                draftsController.draftCount.setText(draftsController.draftsData.size() + "");
                                break;
                        }
                        trashCount.setText(trashData.size() + "");
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            });
            return cell;
        });
        ChangeListener<Boolean> inboxListener = (paramObservableValue, paramT1, selected) -> {
            if (selected) {
                cbTrash.setSelected(true);
            } else
                cbTrash.setSelected(false);
        };
        Callback<Email, ObservableValue<Boolean>> getProperty = Email::selectedProperty;
        Callback<ListView<Email>, ListCell<Email>> forListView = CheckBoxListCell.forListView(getProperty);
        listTrashedEmails.setCellFactory(forListView);

        for (Email email : trashData)
            email.selectedProperty().addListener(inboxListener);
        listTrashedEmails.setItems(trashData);
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

    //public ObservableList<Email> getTrashData() { return trashData; }

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
        GmailInboxScene inboxController = fxmlLoader.getController();
        inboxController.checkInbox(actionEvent);
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

    // sets up the selectEmails and recoverEmails Combo Boxes
    // loads the trash scene
    public void checkTrash(ActionEvent actionEvent) throws IOException {
        FXMLLoader trashLoader = new FXMLLoader(getClass().getResource("GmailTrashScene.fxml"));
        Parent root = trashLoader.load();
        mainStage.setTitle("Gmail - Trash");
        mainStage.setScene(new Scene(root));
    }

    // accesses a method from the inbox controller
    // filters and displays outbox emails containing the search keyword
    public void searchForMail(KeyEvent keyEvent) throws IOException {
        FXMLLoader inboxScene = new FXMLLoader(getClass().getResource("GmailInboxScene.fxml"));
        inboxScene.load();
        GmailInboxScene inboxController = inboxScene.getController();
        inboxController.searchForEmail(textGetSearchBox, listTrashedEmails, trashData, keyEvent.getCode());
    }

    // deletes an email permanently from view and from the trash emails .txt file
    public void deleteTrashedEmails(ActionEvent actionEvent) throws IOException {
        int tempIndex = 0;

        while (tempIndex < trashData.size()) {
            if (trashData.get(tempIndex).isSelected())
                currentGmail.getTrash().remove(trashData.remove(tempIndex));
            else
                tempIndex++;
        }

        eraseInformationFromFile(username + "'s trash.txt");

        for (Email email : trashData)
            email.writeToFile(username + "'s trash.txt");

        FXMLLoader trashLoader = new FXMLLoader(getClass().getResource("GmailTrashScene.fxml"));
        mainStage.setScene(new Scene(trashLoader.load()));
    }

    // removes all selected emails from trash and trash .txt file
    // moves the removed emails to somewhere else in the user's Gmail
    public void moveEmail(ObservableList<Email> fromData, ObservableList<Email> toData, ListView<Email> fromListView, ListView<Email> toListView, String fromLocation, String toLocation, ArrayList<Email> fromArr, ArrayList<Email> toArr) throws IOException {
        int tempIndex = 0;
        while (tempIndex < fromData.size()) {
            Email tempEmail = fromData.get(tempIndex);
            if (tempEmail.isSelected()) {
                tempEmail.setSelected(false);
                tempEmail.writeToFile(username + "'s " + toLocation + ".txt");

                toData.add(tempEmail);
                toListView.getItems().add(tempEmail);
                toArr.add(tempEmail);

                fromListView.getItems().remove(tempEmail);
                fromData.remove(tempEmail);
                fromArr.remove(tempEmail);
            } else {
                tempIndex++;
            }
        }

        eraseInformationFromFile(username + "'s " + fromLocation + ".txt");

        for (Email email : fromData)
            email.writeToFile(username + "'s " + fromLocation + ".txt");

        if (mainStage.getTitle().contains("Trash")) {
            FXMLLoader trashScene = new FXMLLoader(getClass().getResource("GmailTrashScene.fxml"));
            mainStage.setScene(new Scene(trashScene.load()));
        } else if (mainStage.getTitle().contains("Inbox")) {
            FXMLLoader inboxScene = new FXMLLoader(getClass().getResource("GmailInboxScene.fxml"));
            mainStage.setScene(new Scene(inboxScene.load()));
        } else if (mainStage.getTitle().contains("Drafts")) {
            FXMLLoader draftsScene = new FXMLLoader(getClass().getResource("GmailDraftsScene.fxml"));
            mainStage.setScene(new Scene(draftsScene.load()));
        } else {
            FXMLLoader outboxScene = new FXMLLoader(getClass().getResource("GmailOutboxScene.fxml"));
            mainStage.setScene(new Scene(outboxScene.load()));
        }
    }

    // clears everything in a file because the file needs to be updated
    public void eraseInformationFromFile(String fileName) throws IOException {
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
