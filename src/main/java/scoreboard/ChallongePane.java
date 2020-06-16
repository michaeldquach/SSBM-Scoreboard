package scoreboard;

import javafx.scene.layout.Pane;

public class ChallongePane extends Pane {
    private ScoreboardModel model;
    private ScoreboardView view;

    public ChallongePane(ScoreboardModel initModel, ScoreboardView initView){
        this.model = initModel;
        this.view = initView;
        this.setPrefSize(600, 150);
        this.setStyle("-fx-border-color: black");
    }
}
