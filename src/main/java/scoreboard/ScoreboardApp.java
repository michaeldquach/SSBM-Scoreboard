package scoreboard;

import challonge.ChallongeAPI;
import challonge.Tournament;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScoreboardApp extends Application {
    private ScoreboardModel model;
    private ScoreboardView view;

    public void start(Stage primaryStage){
        ChallongeAPI.initKeyring();
        model = ScoreboardModel.loadModel();
        view = new ScoreboardView(model);

        ScorePane scorePane = view.getScorePane();
        ChallongePane challongePane = view.getChallongePane();

        //View handling code

            //ScorePane Buttons
        scorePane.getSwapButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                handleSwap();
            }
        });

        scorePane.getToggleChallongeButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                handleToggleChallonge();
                primaryStage.sizeToScene();
            }
        });

        scorePane.getSaveButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                handleSave();
            }
        });

        scorePane.getUploadButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                handleUpload();
            }
        });

        scorePane.getResetButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                handleReset(false);
            }
        });

            //ChallongePane Buttons
        challongePane.getLoginButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                handleChallongeLogin("Sasquach_", challongePane.getAPIKey());
            }
        });

        challongePane.getLoadTournamentButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                handleLoadTournament(challongePane.getTournamentDropDown().getSelectionModel().getSelectedItem());
            }
        });

        challongePane.getRefreshButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                handleRefresh();
            }
        });

        challongePane.getUrlTournamentButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(model.getCurrentTournament() != null){
                    handleOpenURL(model.getCurrentTournament().getFull_challonge_url());
                }
            }
        });

        challongePane.getUrlAPIButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                handleOpenURL("https://challonge.com/settings/developer");
            }
        });

        //Non-view handling code

        primaryStage.setTitle("SSBM Scoreboard Manager");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(view));
        primaryStage.centerOnScreen();
        primaryStage.show();
        view.update();
    }

    public static void main(String[] args){
        launch(args);
    }

    public void handleSwap(){
        view.swap();
    }

    public void handleToggleChallonge(){
        model.toggleChallonge();
        view.toggleChallonge();
    }

    public void handleSave(){
        view.save();
    }

    public void handleUpload(){
        if(model.uploadMatchInfo(false, false)){
            handleRefresh();        //updates tournament matches after pushing data
        }
    }

    public void handleReset(boolean completeReset){
        model.reset(completeReset);
        view.reset(completeReset);
    }

    public void handleChallongeLogin(String username, String password){
        handleReset(true);
        model.challongeLogin(username, password);
        view.challongeLogin();
    }

    public void handleLoadTournament(Tournament loadedTournament){
        model.loadTournament(loadedTournament);        //loads the selected tournament from the comboBox and pulls data from it
        view.loadTournament(loadedTournament);
    }

    public void handleRefresh(){
        handleLoadTournament(model.getCurrentTournament());
        view.refresh(model.getCurrentTournament());
    }

    public void handleOpenURL(String url){
        ChallongeAPI.openURL(url);
    }
}