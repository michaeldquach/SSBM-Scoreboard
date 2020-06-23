package scoreboard;

import challonge.Tournament;
import javafx.scene.layout.Pane;

public class ScoreboardView extends Pane {
    private ScoreboardModel model;
    private ScorePane scorePane;
    private ChallongePane challongePane;
    private ConsolePane consolePane;

    public ScoreboardView(ScoreboardModel initModel){
        model = initModel;
        this.setPrefSize(610,415);      //default size

        consolePane = new ConsolePane(model, this);     //gotta initialize this first, has message box for errors
        consolePane.relocate(5, 305);
        consolePane.managedProperty().bind(consolePane.visibleProperty());

        scorePane = new ScorePane(model, this);
        scorePane.relocate(5, 0);

        challongePane = new ChallongePane(model, this);
        challongePane.relocate(5, 205);
        challongePane.managedProperty().bind(challongePane.visibleProperty());

        getChildren().addAll(consolePane, scorePane, challongePane);
        update();
    }

    public void resizeWindow(){
        double totalHeight, scorePaneHeight, challongePaneHeight, consolePaneHeight;

        scorePaneHeight = scorePane.getHeight() + 5;

        if(challongePane.isVisible()){
            challongePaneHeight = challongePane.getHeight() + 5;
        }
        else{
            challongePaneHeight = 0;
        }

        if(consolePane.isVisible()){
            consolePaneHeight = consolePane.getHeight() + 5;
        }
        else{
            consolePaneHeight = 0;
        }

        totalHeight = scorePaneHeight + challongePaneHeight + consolePaneHeight;

        challongePane.relocate(5, scorePaneHeight);     //always directly below the scorepane
        consolePane.relocate(5, scorePaneHeight + challongePaneHeight);     //always at the very bottom

        this.setPrefSize(610, totalHeight);
    }

    public void update(){
        scorePane.update();
        challongePane.update();
    }

    public void swap(){
        scorePane.swap();
        update();
    }

    public void toggleConsole(boolean toggle){
        scorePane.toggleConsole(toggle);
        consolePane.toggleConsole(toggle);
        resizeWindow();
        update();
    }

    public void toggleChallonge(boolean toggle){
        scorePane.toggleChallonge(toggle);
        challongePane.toggleChallonge(toggle);
        resizeWindow();
        update();
    }

    public void save(){
        scorePane.save();
        ConsolePane.outputText("Saving match results and updating OBS overlay.");
        update();
    }

    public void reset(boolean completeReset){
        scorePane.reset(completeReset);
        challongePane.reset(completeReset);
        update();
    }

    public void challongeLogin(){
        challongePane.challongeLogin();
        if(model.isChallongeLoggedIn()){
            ConsolePane.outputText("Logged in successfully.");
        }
        else{
            ConsolePane.outputText("Could not log in. Try entering the API key again.");
        }
        update();
    }

    public void loadTournament(Tournament loadedTournament, boolean refresh){
        scorePane.loadTournament(loadedTournament);
        challongePane.loadTournament();
        if(!refresh){
            ConsolePane.outputText("Loading tournament: [" + loadedTournament + "].");
        }
        reset(false);
        update();
    }

    public void refresh(Tournament currentTournament){
        challongePane.refresh(currentTournament);
        ConsolePane.outputText("Refreshing [" + currentTournament + "] match list.");
    }

    public ScorePane getScorePane() {
        return scorePane;
    }

    public ChallongePane getChallongePane(){
        return challongePane;
    }
}
