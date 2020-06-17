package scoreboard;

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
        model = ScoreboardModel.loadModel();
        view = new ScoreboardView(model);

        ScorePane scorePane = view.getScorePane();
        ChallongePane challongePane = view.getChallongePane();

        //View handling code

            //Buttons
        scorePane.getSwapButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                handleSwap();
            }
        });

        scorePane.getSaveButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                handleSave();
            }
        });

        scorePane.getResetButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                handleReset(false);
            }
        });

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

        scorePane.getTestButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                handleTest();
            }
        });

        //Non-view handling code

        primaryStage.setTitle("SSBM Scoreboard Manager");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(view, 700, 450));
        primaryStage.show();
        view.update();
    }

    public static void main(String[] args){
        launch(args);
    }

    public void handleSwap(){
        view.swap();
    }

    public void handleSave(){
        view.save();
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

    public void handleTest(){
        /*
        System.out.println(Font.getDefault());
        System.out.println(model.getPlayer1() + ": " + model.getPlayer1Score());
        System.out.println(model.getPlayer2() + ": " + model.getPlayer2Score());
        //System.out.println(model.getCurrentMatch().getState());
        System.out.println(model.getParticipants());
        System.out.println(model.getMatches());
        System.out.println(model.getCurrentMatch());
        System.out.println(model.getCurrentTournament().getMaxRound());

         */
    }
}

/*
setCredentials("Sasquach_", "0jjHgAoqDH85DjAdKnWdRIRCecr5CktpePisHH7d");

    ScoreboardModel testing = new ScoreboardModel();

        testing.pullTournamentList();
                testing.setCurrentTournament(testing.getTournaments().get(0));
                testing.pullParticipantList();
                System.out.println(testing.getParticipants());
                testing.pullMatchList();
                System.out.println(testing.getMatches());
                testing.setCurrentMatch(testing.getMatches().get(3));
                testing.pullMatchInfo();


                //testing swaps
                System.out.println(testing.getPlayer1());
                System.out.println(testing.getPlayer1Score());
                System.out.println(testing.getPlayer2());
                System.out.println(testing.getPlayer2Score());
                testing.swapPlayers();
                System.out.println(testing.getPlayer1());
                System.out.println(testing.getPlayer1Score());
                System.out.println(testing.getPlayer2());
                System.out.println(testing.getPlayer2Score());
                //testing.swapPlayers();

                //testing.pushMatchInfo();
                }

 */