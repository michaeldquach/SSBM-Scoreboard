package scoreboard;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class ConsolePane extends Pane {
    private ScoreboardModel model;
    private ScoreboardView view;
    private static TextArea consoleBox;

    public ConsolePane(ScoreboardModel initModel, ScoreboardView initView){
        this.model = initModel;
        this.view = initView;
        this.setPrefSize(600, 105);
        this.setStyle("-fx-border-color: black");

        Label label = new Label("Console Log:");
        label.relocate(16, 5);

        consoleBox = new TextArea();
        consoleBox.setEditable(false);
        consoleBox.setText(String.format("%s: %s", LocalTime.now().truncatedTo(ChronoUnit.SECONDS), "Initializing."));      //todo fix truncation at :00
        consoleBox.setWrapText(true);
        consoleBox.setPrefSize(580, 70);
        consoleBox.relocate(10,25);

        getChildren().addAll(label, consoleBox);
    }

    public static void outputText(String newLine){
        consoleBox.appendText(String.format("\n%s: %s", LocalTime.now().truncatedTo(ChronoUnit.SECONDS), newLine));
    }
}
