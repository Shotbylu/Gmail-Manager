package sample;

import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

import java.io.*;
import java.util.*;

public class Main extends Application {

    private static Stage stage;
    private static ArrayList<Scene> latestGmailScenes = new ArrayList<>();
    private static ArrayList<String> latestSceneTitles = new ArrayList<>();
    private static ArrayList<String> googleAccountList = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        Parent root = FXMLLoader.load(Main.class.getResource("GmailLoginEmail.fxml"));
        stage.setTitle("Login Gmail");
        stage.setScene(new Scene(root));
        stage.show();
        GmailReplyingToInboxPopup.assignStage(stage);
        GmailReplyingToOutboxPopup.assignStage(stage);
        GmailInboxScene.assignStage(stage);
        GmailOutboxScene.assignStage(stage);
        GmailDraftsScene.assignStage(stage);
        GmailTrashScene.assignStage(stage);
        GmailLoginEmail.assignStage(stage);
        GoogleAccountCreation.assignStage(stage);
        GoogleAccountPage.assignStage(stage);
    }

    // set the scene of the main stage to the latest scene
    public static void setLatestGmailScene() {
        stage.setScene(latestGmailScenes.get(latestGmailScenes.size() - 1));
    }

    public static void setLatestSceneTitle() {
        stage.setTitle(latestSceneTitles.get(latestSceneTitles.size() - 1));
    }

    public static ArrayList<Scene> getLatestGmailScenes() {
        return latestGmailScenes;
    }

    public static ArrayList<String> getLatestSceneTitles() {
        return latestSceneTitles;
    }

    public static ArrayList<String> getGoogleAccountList() {
        return googleAccountList;
    }

    public static String getCurrentSceneTitle() {
        return stage.getTitle();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
