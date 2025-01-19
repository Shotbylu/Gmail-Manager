package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GmailDraftsScene implements Initializable {
    public TextField textGetSearchBox;
    public Label draftCount;
    public ComboBox<String> selectEmails;
    public ListView<Email> listDraftEmails;
    public ObservableList<Email> draftsData = FXCollections.observableArrayList();
    private ObservableList<String> emailSelectionOptions = FXCollections.observableArrayList("None", "Read", "Unread", "All");
    private static Stage mainStage;
    private static Gmail currentGmail;
    public CheckBox cbDrafts = CheckBoxBuilder.create().build();

    // collects emails from Gmail into the drafts data, add listeners to draft emails, and then add drafts data to List View
    public void initialize(URL location, ResourceBundle resources) {
        if (currentGmail == null)
            return;

        draftsData.addAll(currentGmail.getDrafts());
        selectEmails.setItems(emailSelectionOptions);
        selectEmails.setPromptText("Select Emails");
        draftCount.setText(draftsData.size() + "");
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
                            for (Email email : draftsData)
                                email.setSelected(false);
                            break;
                        case "Read":
                            for (Email email : draftsData)
                                if (email.isRead())
                                    email.setSelected(true);
                            break;
                        case "Unread":
                            for (Email email : draftsData)
                                if (!email.isRead())
                                    email.setSelected(true);
                            break;
                        case "All":
                            for (Email email : draftsData)
                                email.setSelected(true);
                            break;
                    }
                }
            });
            return cell;
        });
        ChangeListener<Boolean> inboxListener = (paramObservableValue, paramT1, selected) -> {
            if (selected) {
                cbDrafts.setSelected(true);
            } else
                cbDrafts.setSelected(false);
        };
        Callback<Email, ObservableValue<Boolean>> getProperty = Email::selectedProperty;
        Callback<ListView<Email>, ListCell<Email>> forListView = CheckBoxListCell.forListView(getProperty);
        listDraftEmails.setCellFactory(forListView);

        for (Email email : draftsData)
            email.selectedProperty().addListener(inboxListener);
        listDraftEmails.setItems(draftsData);
    }

    public static void assignStage(Stage stage) {
        mainStage = stage;
    }

    public static void assignGmail(Gmail gmail) {
        currentGmail = gmail;
    }

    //public ObservableList<Email> getDraftsData() { return draftsData; }

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

    // sets up the selectEmails Combo Box and loads the drafts scene
    public void checkDrafts(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GmailDraftsScene.fxml"));
        Parent root = fxmlLoader.load();
        listDraftEmails.refresh();
        mainStage.setTitle("Gmail - Drafts");
        mainStage.setScene(new Scene(root));
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
        inboxController.searchForEmail(textGetSearchBox, listDraftEmails, draftsData, keyEvent.getCode());
    }

    // accesses the moveEmail method in TrashScene and moves all selected emails to trash
    public void moveDraftEmailsToTrash(ActionEvent actionEvent) throws IOException {
        boolean thereIs = false;
        for (Email email : draftsData) {
            if (email.isSelected())
                thereIs = true;
        }
        if (!thereIs)
            return;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("GmailTrashScene.fxml"));
        fxmlLoader.load();
        GmailTrashScene trashController = fxmlLoader.getController();
        trashController.moveEmail(draftsData, trashController.trashData, listDraftEmails, trashController.listTrashedEmails, "drafts", "trash", currentGmail.getDrafts(), currentGmail.getTrash());
        draftCount.setText(draftsData.size() + "");
        trashController.trashCount.setText(trashController.trashData.size() + "");
    }

    // interacts with the compose email controller to assign properties of a draft email to the compose email scene
    public void readDraftEmail(MouseEvent mouseEvent) throws IOException {
        Email selectedDraft = listDraftEmails.getSelectionModel().getSelectedItem();
        draftsData.remove(selectedDraft);
        listDraftEmails.setItems(draftsData);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GmailComposeEmailScene.fxml"));
        Parent root = fxmlLoader.load();
        GmailComposeEmailScene controller = fxmlLoader.getController();

        controller.textGetEmailRecipient.setText(selectedDraft.getRecipient());
        controller.textGetEmailSubject.setText(selectedDraft.getSubject());
        controller.textGetEmailContent.setText(selectedDraft.getContent());
        controller.textGetEmailContent.setFont(new Font(selectedDraft.getFontName(), selectedDraft.getFontSize()));

        controller.chooseFont.setItems(controller.getFontOptions());
        controller.chooseFont.setPromptText("Choose Font");
        controller.chooseFont.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            cell.setOnMousePressed(e -> {
                if (!cell.isEmpty()) {
                    controller.textGetEmailContent.setFont(new Font(cell.getItem(), controller.textGetEmailContent.getFont().getSize()));
                }
            });
            return cell;
        });
        mainStage.setScene(new Scene(root));
    }

    // accesses a method in InboxScene to load the google account information
    public void loadAccountPage(MouseEvent mouseEvent) throws IOException {
        FXMLLoader inboxScene = new FXMLLoader(getClass().getResource("GmailInboxScene.fxml"));
        inboxScene.load();
        GmailInboxScene inboxController = inboxScene.getController();
        inboxController.loadAccountPage(mouseEvent);
    }
}
