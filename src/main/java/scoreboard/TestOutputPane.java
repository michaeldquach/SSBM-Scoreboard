package scoreboard;

import javafx.scene.layout.Pane;

public class TestOutputPane extends Pane {
    private ScoreboardModel model;

    public TestOutputPane(ScoreboardModel initModel){
        this.model = initModel;
        this.setPrefSize(600, 100);
    }
}
