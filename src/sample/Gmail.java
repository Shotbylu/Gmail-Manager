package sample;

import java.io.*;
import java.util.*;

public class Gmail {
    private ArrayList<Email> inbox = new ArrayList<>(),
            outbox = new ArrayList<>(),
            drafts = new ArrayList<>(),
            trash = new ArrayList<>();
    private GoogleAccount googleAccount;

    public Gmail(GoogleAccount googleAccount, boolean isNew) throws IOException {
        this.googleAccount = googleAccount;
        if (!isNew) {
            createAllEmails(inbox, "inbox");
            createAllEmails(outbox, "outbox");
            createAllEmails(drafts, "drafts");
            createAllEmails(trash, "trash");
        } else {
            initializeInboxTextFiles("outbox");
            initializeInboxTextFiles("drafts");
            initializeInboxTextFiles("trash");
            Email welcomeToGoogle = new Email(this.getGoogleAccount().getGmailAddress(), googleAccount.getFirstName() + ", welcome to your new Google Account",
                    "Hi " + googleAccount.getFirstName() + ",\n\nWelcome to Google. Your new account comes with access to Google products, apps, and services. Here are a few tips to get you started."
                            + "\n\n\nConfirm your options are right for you\nReview and change your privacy and security options to make Google work better for you."
                            + "\n\n\nStay in the know with the Google app.\nFind quick answers, explore your interests, and stay up to date!\n\n\nGoogle Community Team", false);
            welcomeToGoogle.writeToFile(getGoogleAccount().getUsername() + "'s inbox.txt");
            welcomeToGoogle.setSender("Google Community Team");
            inbox.add(welcomeToGoogle);
        }
    }

    public void initializeInboxTextFiles(String sectionOfGmail) throws IOException {
        FileWriter fw = new FileWriter(getGoogleAccount().getUsername() + "'s " + sectionOfGmail + ".txt", true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("");
        bw.close();
    }

    public void createAllEmails(ArrayList<Email> emails, String sectionOfGmail) throws IOException {
        FileReader fr = new FileReader(getGoogleAccount().getUsername() + "'s " + sectionOfGmail + ".txt");
        BufferedReader br = new BufferedReader(fr);

        String line;
        ArrayList<String> lines = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if (line.equals("======")) {
                String sender = lines.remove(0), recipient = lines.remove(0), subject = lines.remove(0);
                int booleanIndex = Math.max(lines.lastIndexOf("true"), lines.lastIndexOf("false"));
                boolean isRead = Boolean.parseBoolean(lines.remove(booleanIndex));
                String fontFamily = lines.remove(booleanIndex);
                int fontSize = Integer.parseInt(lines.remove(booleanIndex));
                String content = "";
                for (int i = 0; i < lines.size(); i++) {
                    if (i == lines.size() - 1)
                        content += lines.get(i);
                    else
                        content += lines.get(i) + "\n";
                }
                Email tempEmail = new Email(recipient, subject, content, isRead);
                tempEmail.setFontName(fontFamily);
                tempEmail.setFontSize(fontSize);
                tempEmail.setSender(sender);
                emails.add(tempEmail);
                lines.clear();
            } else
                lines.add(line);
        }
    }

    public ArrayList<Email> getInbox() {
        return inbox;
    }

    public void addToInbox(Email email) {
        inbox.add(email);
    }

    public ArrayList<Email> getOutbox() {
        return outbox;
    }

    public void addToOutbox(Email email) {
        outbox.add(email);
    }

    public ArrayList<Email> getDrafts() {
        return drafts;
    }

    public ArrayList<Email> getTrash() {
        return trash;
    }

    public GoogleAccount getGoogleAccount() {
        return googleAccount;
    }
}
