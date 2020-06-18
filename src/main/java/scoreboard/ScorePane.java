package scoreboard;

import challonge.Match;
import challonge.Participant;
import challonge.Tournament;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import java.util.ArrayList;

public class ScorePane extends Pane {
    private ScoreboardModel model;
    private ScoreboardView view;
    private ChallongePane challongePane;

    private Spinner<Integer> P1ScoreSpinner, P2ScoreSpinner;
    private ComboBox<String> P1DropDown, P2DropDown, P1CharDropDown, P2CharDropDown;
    private ComboBox<Integer> P1PortDropDown, P2PortDropDown;
    private TextField tournamentRoundField, tournamentNameField, commentatorsField;
    private Button swapButton, toggleChallongeButton, saveButton, uploadButton, resetButton;
    private EventHandler<ActionEvent> P1DropDownEventHandler, P2DropDownEventHandler;

    public ScorePane(ScoreboardModel initModel, ScoreboardView initView){
        this.model = initModel;
        this.view = initView;
        this.setPrefSize(600, 200);
        this.setStyle("-fx-border-color: black");

        //Spinners

        int row1Y = 10;

        P1ScoreSpinner = new Spinner<Integer>(0, 999, 0);
        P1ScoreSpinner.setEditable(true);
        P1ScoreSpinner.getEditor().setTextFormatter(new TextFormatter<Object>(c -> {        //textfield filter to restrict input to only integers, and to only 3 digits.
            if(!c.getControlNewText().matches("\\d*") || c.getControlNewText().length() > 3){
                return null;
            }
            else{
                if(c.getControlNewText().equals("")){
                    P1ScoreSpinner.getValueFactory().setValue(0);         //set default value to 0 if empty string (raises NPE with spinner otherwise, javafx bug)
                }
                return c;
            }
        }));
        P1ScoreSpinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!"".equals(newValue)) {                 //flags if change is made to field required to push to challonge, without saving that change
                if(model.isReadyToPush()){
                    model.setReadyToPush(false);
                    update();
                }
            }
        });
        P1ScoreSpinner.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        P1ScoreSpinner.setPrefSize(60, 25);
        P1ScoreSpinner.relocate(200,row1Y);

        P2ScoreSpinner = new Spinner<Integer>(0, 999, 0);
        P2ScoreSpinner.setEditable(true);
        P2ScoreSpinner.getEditor().setTextFormatter(new TextFormatter<Object>(c -> {
            if(!c.getControlNewText().matches("\\d*") || c.getControlNewText().length() > 3){
                return null;
            }
            else{
                if(c.getControlNewText().equals("")){
                    P2ScoreSpinner.getValueFactory().setValue(0);
                }
                return c;
            }
        }));
        P2ScoreSpinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!"".equals(newValue)) {                 //flags if change is made to field required to push to challonge, without saving that change
                if(model.isReadyToPush()){
                    model.setReadyToPush(false);
                    update();
                }
            }
        });
        P2ScoreSpinner.setPrefSize(60, 25);
        P2ScoreSpinner.relocate(340,row1Y);

        getChildren().addAll(P1ScoreSpinner, P2ScoreSpinner);

        //ComboBoxes

        P1DropDown = new ComboBox<String>();
        P1DropDown.setEditable(true);
        P1DropDownEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //System.out.println("updating from p1listcombo");
                challongePane.updateMatchFromPlayers(P1DropDown, P2DropDown);

                if(model.isReadyToPush()){              //flags if change is made to field required to push to challonge, without saving that change
                    model.setReadyToPush(false);
                    update();
                }
            }
        };
        P1DropDown.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        P1DropDown.setPromptText("Player 1");
        P1DropDown.setOnAction(P1DropDownEventHandler);
        P1DropDown.setPrefSize(180,25);
        P1DropDown.relocate(10, row1Y);

        P2DropDown = new ComboBox<String>();
        P2DropDown.setEditable(true);
        P2DropDownEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //System.out.println("updating from p2listcombo");
                challongePane.updateMatchFromPlayers(P2DropDown, P1DropDown);

                if(model.isReadyToPush()){              //flags if change is made to field required to push to challonge, without saving that change
                    model.setReadyToPush(false);
                    update();
                }
            }
        };
        P2DropDown.setPromptText("Player 2");
        P2DropDown.setOnAction(P2DropDownEventHandler);
        P2DropDown.setPrefSize(180,25);
        P2DropDown.relocate(410, row1Y);

        int row2Y = 45;

        ObservableList<String> characterList = FXCollections.observableArrayList(model.getCharacters());

        P1CharDropDown = new ComboBox<String>();
        P1CharDropDown.setItems(characterList);
        P1CharDropDown.setPromptText("Select P1 Character");            //Extremely dumb scenario. We need prompt text, as setText won't show on launch. But prompt text disappears on noneditable comboboxes
        P1CharDropDown.setButtonCell(new ListCell<String>() {           //But every time selection is cleared, we'll lose the prompt text on noneditable comboboxes
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null) {
                    setText(item);
                    setStyle("");
                }
                else{
                    setText("Select P1 Character");                     //Thus need to set "prompt text" again
                    setStyle("-fx-font-style: italic;" + "-fx-text-fill: grey;");
                }
            }
        });
        P1CharDropDown.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        P1CharDropDown.setPrefSize(175,25);
        P1CharDropDown.relocate(85, row2Y);

        P2CharDropDown = new ComboBox<String>();
        P2CharDropDown.setItems(characterList);
        P2CharDropDown.setPromptText("Select P2 Character");
        P2CharDropDown.setButtonCell(new ListCell<String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null) {
                    setText(item);
                    setStyle("");
                }
                else{
                    setText("Select P2 Character");
                    setStyle("-fx-font-style: italic;" + "-fx-text-fill: grey;");
                }
            }
        });
        P2CharDropDown.setPrefSize(175,25);
        P2CharDropDown.relocate(340, row2Y);

        ObservableList<Integer> portList = FXCollections.observableArrayList(1,2,3,4);
        P1PortDropDown = new ComboBox<Integer>(portList);
        P1PortDropDown.getSelectionModel().select(0);       //Select port 1 by default
        P1PortDropDown.setButtonCell(new ListCell<Integer>() {
            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null) {
                    setText("Port: " + item);
                    setStyle("-fx-padding: -5 -5 -5 5");
                }
            }
        });
        P1PortDropDown.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        P1PortDropDown.setPrefSize(65,25);
        P1PortDropDown.relocate(10, row2Y);

        P2PortDropDown = new ComboBox<Integer>(portList);
        P2PortDropDown.getSelectionModel().select(1);       //Select port 2 by default
        P2PortDropDown.setButtonCell(new ListCell<Integer>() {
            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText("Port: " + item);
                    setStyle("-fx-padding: -5 -5 -5 5");
                }
            }
        });
        P2PortDropDown.setPrefSize(65,25);
        P2PortDropDown.relocate(525, row2Y);

        getChildren().addAll(P1DropDown, P2DropDown, P1CharDropDown, P2CharDropDown, P1PortDropDown, P2PortDropDown);

        //Textfields

        int textFieldY = 100;

        tournamentRoundField = new TextField();
        tournamentRoundField.setPromptText("Enter bracket round.");
        tournamentRoundField.setPrefSize(186, 25);
        tournamentRoundField.relocate(10,textFieldY);

        tournamentNameField = new TextField();
        tournamentNameField.setPromptText("Enter tournament name.");
        tournamentNameField.setPrefSize(188, 25);
        tournamentNameField.relocate(206,textFieldY);

        commentatorsField = new TextField();
        commentatorsField.setPromptText("Enter commentator tags.");
        commentatorsField.setPrefSize(186, 25);
        commentatorsField.relocate(404,textFieldY);

        getChildren().addAll(tournamentRoundField, tournamentNameField, commentatorsField);

        //Labels

        Label tournamentRoundLabel, tournamentNameLabel, commentatorLabel;
        int labelY = 80;

        tournamentRoundLabel = new Label("Bracket Round: ");
        tournamentRoundLabel.relocate(18, labelY);

        tournamentNameLabel = new Label("Tournament: ");
        tournamentNameLabel.relocate(214, labelY);

        commentatorLabel = new Label("Commentators: ");
        commentatorLabel.relocate(412, labelY);

        getChildren().addAll(tournamentRoundLabel, tournamentNameLabel, commentatorLabel);

        //Buttons

        int buttonY = 145;

        swapButton = new Button("Swap");
        swapButton.setPrefSize(60, 60);
        swapButton.relocate(270, 10);

        toggleChallongeButton = new Button("Hide Challonge");
        toggleChallongeButton.setPrefSize(138, 40);
        toggleChallongeButton.relocate(10, buttonY);

        saveButton = new Button("Save and Output");
        saveButton.setPrefSize(138, 40);
        saveButton.relocate(157, buttonY);

        uploadButton = new Button("Upload Match");
        uploadButton.setPrefSize(138, 40);
        uploadButton.relocate(305, buttonY);

        resetButton = new Button("Reset Match");
        resetButton.setPrefSize(138, 40);
        resetButton.relocate(452, buttonY);

        getChildren().addAll(toggleChallongeButton, saveButton, uploadButton, resetButton, swapButton);
    }

    //called when selecting match from match dropdown
    public void updatePlayersFromMatch(Match selectedMatch){
        P1DropDown.setOnAction(null);      //Disable player drop down event listeners, we're changing values programmatically
        P2DropDown.setOnAction(null);

        reset(false);

        if(model.isReadyToPush()){
            model.setReadyToPush(false);
            update();
        }

        if(selectedMatch != null){
            String player1Name = selectedMatch.getPlayer1Name();        //Take player names from selected match
            String player2Name = selectedMatch.getPlayer2Name();

            if(player1Name != null && player2Name != null){
                if(P1DropDown.getItems().contains(player1Name) && P2DropDown.getItems().contains(player2Name)) {      //If player drop downs contain those player names, select them within the combo box
                    P1DropDown.getSelectionModel().clearAndSelect(P1DropDown.getItems().indexOf(player1Name));
                    P2DropDown.getSelectionModel().clearAndSelect(P2DropDown.getItems().indexOf(player2Name));
                }
            }
            else{
                P1DropDown.getSelectionModel().clearSelection();
                P2DropDown.getSelectionModel().clearSelection();
            }

            tournamentRoundField.setText(selectedMatch.getRoundString(model.getCurrentTournament().getMaxRound()));      //Also update tournament round text
        }
        else{
            tournamentRoundField.setText(null);
        }
        P1DropDown.setOnAction(P1DropDownEventHandler);     //Re-enable player drop down event listeners
        P2DropDown.setOnAction(P2DropDownEventHandler);
    }

    public void update(){
        if(challongePane != view.getChallongePane()){
            challongePane = view.getChallongePane();
        }
        uploadButton.setDisable(!model.isReadyToPush() || !model.isToggleChallonge());
    }

    public void toggleChallonge(boolean toggle){
        if(toggle){
            toggleChallongeButton.setText("Hide Challonge");
        }
        else{
            toggleChallongeButton.setText("Show Challonge");
        }
    }

    //Swap info in fields for each player
    public void swap(){
        //System.out.println("Swapping");         //debug

        String tempName = P1DropDown.getValue();
        int tempScore = P1ScoreSpinner.getValue();
        String tempChar = P1CharDropDown.getValue();
        Integer tempPort = P1PortDropDown.getValue();       //keep this Integer instead of int, so it doesn't accidentally pass an int arg as an index

        P1DropDown.getSelectionModel().select(P2DropDown.getValue());
        P1ScoreSpinner.getValueFactory().setValue(P2ScoreSpinner.getValue());
        P1CharDropDown.getSelectionModel().select(P2CharDropDown.getValue());
        P1PortDropDown.getSelectionModel().select(P2PortDropDown.getValue());

        P2DropDown.getSelectionModel().select(tempName);
        P2ScoreSpinner.getValueFactory().setValue(tempScore);
        P2CharDropDown.getSelectionModel().select(tempChar);
        P2PortDropDown.getSelectionModel().select(tempPort);
    }

    public void save(){
        model.saveMatchInfo(P1DropDown.getValue(), P2DropDown.getValue(),
                            P1ScoreSpinner.getValue(), P2ScoreSpinner.getValue(),
                            P1PortDropDown.getValue(), P2PortDropDown.getValue(),
                            P1CharDropDown.getSelectionModel().getSelectedItem(), P2CharDropDown.getSelectionModel().getSelectedItem(),
                            tournamentRoundField.getText(), tournamentNameField.getText(), commentatorsField.getText(), challongePane.getMatchDropDown().getValue());
    }

    public void reset(boolean completeReset){
        //Players and Match
        tournamentRoundField.setText(null);
        P1DropDown.getSelectionModel().clearSelection();
        P1DropDown.setValue(null);      //for clearing the combobox's text field
        P2DropDown.getSelectionModel().clearSelection();
        P2DropDown.setValue(null);

        //Scores
        P1ScoreSpinner.getValueFactory().setValue(0);
        P2ScoreSpinner.getValueFactory().setValue(0);

        //Ports
        P1PortDropDown.getSelectionModel().select(0);
        P2PortDropDown.getSelectionModel().select(1);

        //Characters
        P1CharDropDown.getSelectionModel().clearSelection();
        P2CharDropDown.getSelectionModel().clearSelection();

        //Complete Reset
        if(completeReset){
            tournamentNameField.setText(null);
            commentatorsField.setText(null);
        }
    }

    //Updates namefield and player dropdowns from loaded tournament
    public void loadTournament(Tournament loadedTournament){
        if(loadedTournament != null){
            tournamentNameField.setText(loadedTournament.getName());        //update field for tournament name
        }

        ArrayList<String> participantNames = new ArrayList<String>();       //Make array list of player strings rather than players
        for(Participant x:model.getParticipants()){
            participantNames.add(x.getName());
        }

        ObservableList<String> participantOptions = FXCollections.observableArrayList(participantNames);
        P1DropDown.setItems(participantOptions);        //Populate player drop downs
        P2DropDown.setItems(participantOptions);

        if(model.isReadyToPush()){          //only called if the model bool is already true
            model.setReadyToPush(false);
            update();           //only needs to update this pane
        }
    }

    public Button getSwapButton(){
        return swapButton;
    }

    public Button getToggleChallongeButton(){
        return toggleChallongeButton;
    }

    public Button getSaveButton(){
        return saveButton;
    }

    public Button getUploadButton(){
        return uploadButton;
    }

    public Button getResetButton(){
        return resetButton;
    }
}
