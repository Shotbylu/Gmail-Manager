package sample;

import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class DiceGame {
    public Label displayWinLose;
    public Label displayBalance;
    public Label numberFirstRoll;
    public Button btnHigh;
    public Button btnLow;
    public Button btnNextRound;
    private String highOrLow;
    private int firstDiceRoll;
    private int balance = 100;

    public void guessHigh(MouseEvent mouseEvent) {
        highOrLow = "high";
        btnHigh.setDisable(true);
        btnLow.setDisable(true);
        winOrLose();
    }

    public void guessLow(MouseEvent mouseEvent) {
        highOrLow = "low";
        btnHigh.setDisable(true);
        btnLow.setDisable(true);
        winOrLose();
    }

    // rolls a dice and display the number rolled
    // lets the user choose whether the second number rolled is bigger or smaller
    public void startsRound(MouseEvent mouseEvent) {
        displayWinLose.setText("");
        firstDiceRoll = (int) (Math.random() * 6 + 1);
        numberFirstRoll.setText(Integer.toString(firstDiceRoll));
        btnNextRound.setText("Next Round");
        btnNextRound.setLayoutX(100.0);
        btnNextRound.setDisable(true);
        btnHigh.setDisable(false);
        btnLow.setDisable(false);
    }

    // determines whether the user picked the right guess
    // if the user's balance is less than 0, ends the game
    public void winOrLose() {
        int secondDiceRoll = (int) (Math.random() * 6 + 1);

        switch (highOrLow) {
            case "high":
                if (secondDiceRoll > firstDiceRoll) {
                    displayWinLose.setText("You won! The second dice had a value of " + secondDiceRoll);
                    balance += 50;
                } else {
                    balance -= 50;
                    if (balance < 0) {
                        disableEverything();
                        return;
                    } else {
                        displayWinLose.setText("You lost! The second dice had a value of " + secondDiceRoll);
                    }
                }
                break;
            case "low":
                if (secondDiceRoll < firstDiceRoll) {
                    displayWinLose.setText("You won! The second dice had a value of " + secondDiceRoll);
                    balance += 50;
                } else {
                    balance -= 50;
                    if (balance < 0) {
                        disableEverything();
                        return;
                    } else {
                        displayWinLose.setText("You lost! The second dice had a value of " + secondDiceRoll);
                    }
                }
                break;
        }
        displayBalance.setText("$" + Integer.toString(balance));
        btnNextRound.setDisable(false);
    }

    public void disableEverything() {
        btnNextRound.setDisable(true);
        btnHigh.setDisable(true);
        btnLow.setDisable(true);
        displayWinLose.setText("You lost! You ran out of balance! Please restart!");
    }
}
