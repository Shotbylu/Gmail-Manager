package Tests;

import org.junit.*;
import sample.Email;

import java.io.*;

import static org.junit.Assert.*;

public class EmailTest {
    Email testEmail;

    // I can't use JUnit to test JavaFX GUI, so I tested regular Java classes instead.
    // JUnit outputs "java.lang.ExceptionInInitializerError" for initializing a CheckBox!

    @Before
    public void setUp(){
        testEmail = new Email("fred@gmail.com", "test", "testing", false);
    }

    @Test
    // checks Email class' toString() method
    // if the toString output changes after an Email is read, then toString() works properly
    public void testToString() {
        assertEquals(testEmail.toString().substring(0, 6), "UNREAD");
        testEmail.setRead(true);
        assertNotEquals(testEmail.toString().substring(0, 6), "UNREAD");
    }

    @Test
    // checks whether getters (accessor methods) and setters (mutator methods) work
    public void testGetters(){
        assertEquals("fred@gmail.com", testEmail.getRecipient());
        assertEquals("test", testEmail.getSubject());
        assertEquals("testing", testEmail.getContent());
        assertFalse(testEmail.isSelected());
        assertEquals("System", testEmail.getFontName());
        testEmail.setFontSize(15);
        assertEquals(15, testEmail.getFontSize());
        testEmail.selectedProperty().set(true);
        assertTrue(testEmail.selectedProperty().get());
    }

    @Test
    // checks whether Email class' writeToFile() method writes to a file in this order:
    // recipient, subject, content, isRead, fontName, fontSize
    // elements above are separated by a new line
    public void writeToFile() throws IOException{
        FileReader fr = new FileReader("Email Test.txt");
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter("Email Test.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("");

        testEmail.writeToFile("Email Test.txt");
        String[] lines = new String[6];
        int index = 0;
        String line;
        while ((line = br.readLine()) != null){
            if (!line.equals("======")){
                lines[index] = line;
                index++;
            }
        }
        assertEquals(lines[0], testEmail.getRecipient());
        assertEquals(lines[1], testEmail.getSubject());
        assertEquals(lines[2], testEmail.getContent());
        assertEquals(Boolean.parseBoolean(lines[3]), testEmail.isRead());
        assertEquals(lines[4], testEmail.getFontName());
        assertEquals(Integer.parseInt(lines[5]), testEmail.getFontSize());
    }
}
