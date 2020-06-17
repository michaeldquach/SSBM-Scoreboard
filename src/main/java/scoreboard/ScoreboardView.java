package scoreboard;

import challonge.Tournament;
import javafx.scene.layout.Pane;

public class ScoreboardView extends Pane {
    private ScoreboardModel model;
    private ScorePane scorePane;
    private ChallongePane challongePane;

    public ScoreboardView(ScoreboardModel initModel){
        model = initModel;
        this.setPrefSize(610,305);      //default size

        scorePane = new ScorePane(model, this);
        scorePane.relocate(5, 0);

        challongePane = new ChallongePane(model, this);
        challongePane.relocate(5, 205);
        challongePane.managedProperty().bind(challongePane.visibleProperty());

        getChildren().addAll(scorePane, challongePane);
        update();
    }

    public void update(){
        scorePane.update();
        challongePane.update();
    }

    public void swap(){
        scorePane.swap();
        update();
    }

    public void toggleChallonge(){
        boolean toggle = model.isToggleChallonge();

        if(toggle){
            setPrefSize(610,305);
        }
        else{
            setPrefSize(610,205);
        }

        scorePane.toggleChallonge(toggle);
        challongePane.toggleChallonge(toggle);
    }

    public void save(){
        scorePane.save();
        update();
    }

    public void reset(boolean completeReset){
        /*//System.out.println("Resetting.");       //debug
        if(completeReset){
            System.out.println("Complete Reset");       //debug
        }         */
        scorePane.reset(completeReset);
        challongePane.reset(completeReset);
        update();
    }

    public void challongeLogin(){
        challongePane.challongeLogin();
        update();
    }

    public void loadTournament(Tournament loadedTournament){
        scorePane.loadTournament(loadedTournament);
        challongePane.loadTournament();
        reset(false);
        update();
    }

    public void refresh(Tournament currentTournament){
        challongePane.refresh(currentTournament);
    }

    public ScorePane getScorePane() {
        return scorePane;
    }

    public ChallongePane getChallongePane(){
        return challongePane;
    }
}
