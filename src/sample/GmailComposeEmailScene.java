package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.text.Font;

import java.io.File;
import java.io.IOException;

public class GmailComposeEmailScene {
    public TextField textGetEmailRecipient;
    public TextField textGetEmailSubject;
    public TextArea textGetEmailContent;
    public ComboBox<String> chooseFont;
    private static Gmail currentGmail;
    private boolean setOnce = false;
    private ObservableList<String> fontOptions = FXCollections.observableArrayList("SansSerif", "Segoe UI", "System", "Arial", "Bell MT", "Calibri", "Times New Roman");

    public void initialize(){
        if (currentGmail == null)
            return;
        if (!setOnce){
            setOnce = true;
            chooseFont.setPromptText("Choose Font");
            chooseFont.setItems(fontOptions);
        }
    }

    public static void assignGmail(Gmail gmail) {
        currentGmail = gmail;
    }

    public ObservableList<String> getFontOptions(){
        return fontOptions;
    }

    public void increaseFont(ActionEvent actionEvent) {
        textGetEmailContent.setFont(new Font(textGetEmailContent.getFont().getFamily(), textGetEmailContent.getFont().getSize() + 1));
    }

    public void decreaseFont(ActionEvent actionEvent) {
        textGetEmailContent.setFont(new Font(textGetEmailContent.getFont().getFamily(), textGetEmailContent.getFont().getSize() - 1));
    }

    // sends a draft email to the outbox. if the draft email's receiver is the user him/herself, then the method also sends the draft to the inbox.
    public void sendEmail(ActionEvent actionEvent) throws IOException {
        String recipient = textGetEmailRecipient.getText(), subject = textGetEmailSubject.getText(), content = textGetEmailContent.getText();
        if (recipient.equals("") || subject.equals("") || content.equals(""))
            return;

        String username = currentGmail.getGoogleAccount().getUsername();
        Email newEmail = new Email(recipient, subject, content, true);
        newEmail.setFontName(textGetEmailContent.getFont().getFamily());
        newEmail.setFontSize((int) textGetEmailContent.getFont().getSize());
        newEmail.setSender(currentGmail.getGoogleAccount().getGmailAddress());

        FXMLLoader inboxScene = new FXMLLoader(getClass().getResource("GmailInboxScene.fxml"));
        inboxScene.load();
        GmailInboxScene inboxController = inboxScene.getController();

        File checkFile = new File(recipient.replaceAll("@gmail.com","") + "'s inbox.txt");

        if (recipient.equals(currentGmail.getGoogleAccount().getGmailAddress())) {
            newEmail.setRead(false);
            newEmail.writeToFile(username + "'s inbox.txt");
            inboxController.inboxData.add(newEmail);
            currentGmail.getInbox().add(newEmail);
            inboxController.listInboxEmails.setItems(inboxController.inboxData);
            inboxController.inboxCount.setText(inboxController.inboxData.size() + "");
        } else if (checkFile.exists()){
            newEmail.writeToFile(checkFile.getPath());
        }
        newEmail.writeToFile(username + "'s outbox.txt");

        currentGmail.getOutbox().add(newEmail);

        resetFont();
        clearDraft();
        Main.setLatestGmailScene();
        Main.setLatestSceneTitle();
    }

    // clears all fields in an email draft
    public void clearDraft() {
        textGetEmailRecipient.clear();
        textGetEmailSubject.clear();
        textGetEmailContent.clear();
    }

    // resets the font of the draft content
    public void resetFont() {
        textGetEmailContent.setFont(new Font("System", 12));
    }

    // saves a draft email into the user's drafts.txt and updates the drafts scene
    public void saveEmail(ActionEvent actionEvent) throws IOException {
        String recipient = textGetEmailRecipient.getText(), subject = textGetEmailSubject.getText(), content = textGetEmailContent.getText();
        if (recipient.equals("") || subject.equals("") || content.equals(""))
            return;

        Email draft = new Email(recipient, subject, content, true);
        draft.setFontName(textGetEmailContent.getFont().getFamily());
        draft.setFontSize((int) textGetEmailContent.getFont().getSize());
        draft.writeToFile(currentGmail.getGoogleAccount().getUsername() + "'s drafts.txt");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GmailDraftsScene.fxml"));
        fxmlLoader.load();
        GmailDraftsScene controller = fxmlLoader.getController();
        controller.draftsData.add(draft);
        currentGmail.getDrafts().add(draft);
        controller.listDraftEmails.setItems(controller.draftsData);
        controller.draftCount.setText(controller.draftsData.size() + "");

        resetFont();
        clearDraft();
        Main.setLatestGmailScene();
    }

    // discards the email draft
    public void deleteComposedEmail(ActionEvent actionEvent) {
        resetFont();
        clearDraft();
        Main.setLatestGmailScene();
    }

    public void setFamily(ActionEvent actionEvent) {
        textGetEmailContent.setFont(new Font(chooseFont.getValue(), textGetEmailContent.getFont().getSize()));
    }
}
