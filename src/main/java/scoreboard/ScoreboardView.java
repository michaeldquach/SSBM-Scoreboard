package scoreboard;

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

        TestOutputPane testPane = new TestOutputPane(model);
        //testPane.relocate();
    }

    public void update(){
        scorePane.update();
    }

    public void handleSave(){
        scorePane.save();
    }

    public ScorePane getScorePane() {
        return scorePane;
    }

    public ChallongePane getChallongePane(){
        return challongePane;
    }
}
