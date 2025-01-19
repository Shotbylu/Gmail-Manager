package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;

public class GmailReplyingToInboxPopup {
    public Label textReplyEmailReceiver;
    public Label textReplyEmailSubject;
    public VBox vBoxReplyEmailContent;
    public TextArea textReplyEmailContent = new TextArea();   // this isn't declared in any FXML file
    private static Stage mainStage;
    public static void assignStage(Stage stage){
        mainStage = stage;
    }
    private static Gmail currentGmail;
    public static void assignGmail(Gmail gmail) {
        currentGmail = gmail;
    }

    // completed => reviewed
    // this method interacts with the OutboxScene controller to 1st record the Email, 2nd add to the outbox, and 3rd if the receiver is the sender, then add to the inbox
    public void sendInboxReply(ActionEvent actionEvent) throws IOException {
        textReplyEmailReceiver.setDisable(false);
        textReplyEmailSubject.setDisable(false);
        textReplyEmailContent.setDisable(false);
        FXMLLoader outboxScene = new FXMLLoader(getClass().getResource("GmailOutboxScene.fxml"));
        outboxScene.load();
        GmailOutboxScene controller = outboxScene.getController();
        Email tempEmail = new Email(textReplyEmailReceiver.getText(), textReplyEmailSubject.getText(), textReplyEmailContent.getText(), true);
        tempEmail.setSender(currentGmail.getGoogleAccount().getGmailAddress());
        tempEmail.writeToFile(controller.getCurrentGmail().getGoogleAccount().getUsername() + "'s outbox.txt");
        controller.getCurrentGmail().addToOutbox(tempEmail);
        if (controller.getCurrentGmail().getGoogleAccount().getGmailAddress().equals(textReplyEmailReceiver.getText())) {
            tempEmail.writeToFile(controller.getCurrentGmail().getGoogleAccount().getUsername() + "'s inbox.txt");
            controller.getCurrentGmail().addToInbox(tempEmail);
        }
        textReplyEmailReceiver.setText("");
        textReplyEmailSubject.setText("");
        textReplyEmailContent.clear();
        textReplyEmailReceiver.setDisable(true);
        textReplyEmailSubject.setDisable(true);
        textReplyEmailContent.setDisable(true);

        FXMLLoader inboxScene = new FXMLLoader(getClass().getResource("GmailInboxScene.fxml"));
        mainStage.setScene(new Scene(inboxScene.load()));
    }
}
