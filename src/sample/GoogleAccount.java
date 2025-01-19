package sample;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GoogleAccount {
    private String username, password, firstName, lastName;

    public GoogleAccount(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGmailAddress() {
        return username + "@gmail.com";
    }

    public void writeToFile() throws IOException {
        FileWriter fw = new FileWriter("Google Accounts.txt", true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(getGmailAddress() + "\n" + password + "\n" + firstName + "\n" + lastName + "\n");
        bw.write("======\n");
        bw.close();
    }
}
