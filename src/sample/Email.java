package sample;

import javafx.beans.property.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Email {
    private String recipient, subject, content, fontName = "System", sender = "";
    private boolean isRead;
    private int fontSize = 13;
    private SimpleBooleanProperty isSelected = new SimpleBooleanProperty(false);

    public Email(String recipient, String subject, String content, boolean isRead) {
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
        this.isRead = isRead;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRead(boolean value) {
        isRead = value;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setSelected(boolean value) {
        isSelected.set(value);
    }

    public boolean isSelected() {
        return isSelected.get();
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontSize() {
        return fontSize;
    }

    public SimpleBooleanProperty selectedProperty() {
        return isSelected;
    }

    public void setSender(String sender){ this.sender = sender; }

    public String getSender(){ return sender; }

    public String toString() {
        String result = ((sender + "\t\t" + subject + " - " + content).replaceAll("\n", " "));
        if (result.length() > 70)
            result = result.substring(0, 70);
        if (isRead)
            return result;
        return "UNREAD " + result;
    }

    public void writeToFile(String fileName) throws IOException {
        FileWriter fw = new FileWriter(fileName, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(sender + "\n" + recipient + "\n" + subject + "\n" + content + "\n");
        bw.write(isRead + "\n" + fontName + "\n" + fontSize + "\n");
        bw.write("======\n");
        bw.flush();
        bw.close();
    }
}
