package scoreboard;

import challonge.Tournament;
import javafx.scene.layout.Pane;

public class ScoreboardView extends Pane {
    private ScoreboardModel model;
    private ScorePane scorePane;
    private ChallongePane challongePane;

    public ScoreboardView(ScoreboardModel initModel){
        model = initModel;
        scorePane = new ScorePane(model, this);
        scorePane.relocate(50, 25);

        challongePane = new ChallongePane(model, this);
        challongePane.relocate(50, 225);

        getChildren().addAll(scorePane, challongePane);
        this.update();

        TestOutputPane testPane = new TestOutputPane(model);
        //testPane.relocate();
    }

    public void update(){
        scorePane.update();
        challongePane.update();
    }

    public void swap(){
        scorePane.swap();
    }

    public void save(){
        scorePane.save();
    }

    public void reset(boolean completeReset){
        /*//System.out.println("Resetting.");       //debug
        if(completeReset){
            System.out.println("Complete Reset");       //debug
        }         */
        scorePane.reset(completeReset);
        challongePane.reset(completeReset);
    }

    public void challongeLogin(){
        challongePane.challongeLogin();
        update();
    }

    public void loadTournament(Tournament loadedTournament){
        scorePane.loadTournament(loadedTournament);
        challongePane.loadTournament();
        reset(false);
    }

    public ScorePane getScorePane() {
        return scorePane;
    }

    public ChallongePane getChallongePane(){
        return challongePane;
    }
}
