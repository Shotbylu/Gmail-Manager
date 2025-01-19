package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.*;

import java.io.*;

public class GmailReadingInboxEmails {
    public Label textReadEmailSender;
    public Label textReadEmailReceiver;
    public Label textReadEmailSubject;
    public VBox vBoxReadEmailContent;
    public TextArea textReadEmailContent = new TextArea();  // this isn't declared in any FXML file

    // this method interacts with the ReplyingToInboxPopup controller to assign values to that controller's variables
    // this method replies to the inbox email popup
    public void replyToEmail(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GmailReplyingToInboxPopup.fxml"));
        Parent root = fxmlLoader.load();
        GmailReplyingToInboxPopup controller = fxmlLoader.getController();

        controller.textReplyEmailReceiver.setText(textReadEmailSender.getText());
        controller.textReplyEmailSubject.setText("RE: " + textReadEmailSubject.getText());
        controller.textReplyEmailContent.setText("From: " + textReadEmailSender.getText() + "\n" + "To: " + textReadEmailReceiver.getText() + "\n\"" + textReadEmailContent.getText() + "\"");
        controller.textReplyEmailContent.setFont(new Font(textReadEmailContent.getFont().getFamily(), textReadEmailContent.getFont().getSize()));
        controller.vBoxReplyEmailContent.getChildren().addAll(controller.textReplyEmailContent);

        Stage popup = new Stage();
        popup.setTitle("Reply to " + textReadEmailSender.getText());
        popup.setScene(new Scene(root));
        popup.show();
    }
}
