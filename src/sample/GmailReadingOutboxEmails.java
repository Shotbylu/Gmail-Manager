package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.*;

import java.io.*;

public class GmailReadingOutboxEmails {
    public Label textReadEmailSender;
    public Label textReadEmailReceiver;
    public Label textReadEmailSubject;
    public VBox vBoxReadEmailContent;
    public TextArea textReadEmailContent = new TextArea();

    // completed => reviewed
    // this method interacts with the ReplyingToOutboxPopup controller to assign values to that controller's variables
    // this method replies to the outbox email popup
    public void replyToEmail(ActionEvent actionEvent) throws IOException {
        textReadEmailContent.setDisable(true);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("GmailReplyingToOutboxPopup.fxml"));
        Parent root = fxmlLoader.load();
        GmailReplyingToOutboxPopup controller = fxmlLoader.getController();

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
