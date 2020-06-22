package scoreboard;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ConsolePane extends Pane {
    private ScoreboardModel model;
    private ScoreboardView view;
    private static TextArea consoleBox;
    private static DateTimeFormatter timeFormatter;

    public ConsolePane(ScoreboardModel initModel, ScoreboardView initView){
        this.model = initModel;
        this.view = initView;
        this.setPrefSize(600, 105);
        this.setStyle("-fx-border-color: black");

        Label label = new Label("Console Log:");
        label.relocate(16, 5);

        timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");        //to format time without dropping :00

        consoleBox = new TextArea();
        consoleBox.setEditable(false);
        consoleBox.setText(String.format("%s: %s", LocalTime.now().format(timeFormatter), "Initializing."));
        consoleBox.setWrapText(true);
        consoleBox.setPrefSize(580, 70);
        consoleBox.relocate(10,25);

        getChildren().addAll(label, consoleBox);
    }

    public static void outputText(String newLine){
        consoleBox.appendText(String.format("\n%s: %s", LocalTime.now().format(timeFormatter), newLine));
    }

    public void toggleConsole(boolean toggle){
        setVisible(toggle);
    }
}
