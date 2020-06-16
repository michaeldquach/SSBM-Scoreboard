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
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class ScorePane extends Pane {
    private ScoreboardModel model;
    private ScoreboardView view;

    private Spinner<Integer> P1ScoreSpinner, P2ScoreSpinner;
    private ComboBox<Tournament> tournamentDropDown;
    private ComboBox<Match> matchDropDown;
    private ComboBox<String> P1DropDown, P2DropDown, P1CharDropDown, P2CharDropDown;
    private ComboBox<Integer> P1PortDropDown, P2PortDropDown;
    private TextField tournamentRoundField, tournamentNameField, commentatorsField;
    private Button refreshButton, saveButton, uploadButton, resetButton, swapButton, loadTournamentButton, testButton;
    private EventHandler<ActionEvent> matchDropDownEventHandler, P1DropDownEventHandler, P2DropDownEventHandler;
    private PasswordField API_keyField;

    public ScorePane(ScoreboardModel initModel, ScoreboardView initView){
        this.model = initModel;
        this.view = initView;
        this.setPrefSize(600, 200);
        this.setStyle("-fx-border-color: black");

        //Spinners
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
        P1ScoreSpinner.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        P1ScoreSpinner.setPrefSize(60, 25);
        P1ScoreSpinner.relocate(200,10);

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
        P2ScoreSpinner.setPrefSize(60, 25);
        P2ScoreSpinner.relocate(340,10);

        getChildren().addAll(P1ScoreSpinner, P2ScoreSpinner);

        //ComboBoxes
        ObservableList<Tournament> tournamentOptions = FXCollections.observableArrayList(model.getTournaments());
        tournamentDropDown = new ComboBox<Tournament>(tournamentOptions);   //populate tournament drop down todo move function to when program launches/challonge logged in
        //tournamentListComboBox.getSelectionModel().selectFirst();
        tournamentDropDown.setPromptText("Select Tournament");
        tournamentDropDown.setButtonCell(new ListCell<Tournament>() {
            @Override
            public void updateItem(Tournament item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null) {
                    setText(item.toString());
                    setStyle("");       //Resets font to default
                    //setTextFill(null);
                }
                else{
                    setText("Select Tournament");
                    setStyle("-fx-font-style: italic;" + "-fx-text-fill: grey;");
                    //setTextFill(Color.GRAY);
                }
            }
        });
        tournamentDropDown.setPrefSize(250,25);
        tournamentDropDown.relocate(10, 250);

        matchDropDown = new ComboBox<Match>();
        matchDropDown.setCellFactory(lv -> new ListCell<Match>(){
            public void updateItem(Match match, boolean selectable){
                super.updateItem(match, selectable);
                if(match != null){
                    this.setText(match.toString());
                    if(match.isComplete() || !match.isReady()){     //Disable selection of match if already completed/not ready
                        this.setDisable(true);
                        this.setTextFill(Color.GRAY);       //Change text colour for disabled indication
                    }
                    else{
                        this.setDisable(false);
                        this.setTextFill(Color.BLACK);
                    }
                }
            }
        });
        matchDropDownEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //System.out.println("updating from matchlist action");
                updatePlayersFromMatch(matchDropDown.getValue(), P1DropDown, P2DropDown);
            }
        };
        matchDropDown.setOnAction(matchDropDownEventHandler);
        matchDropDown.setStyle("-fx-font: 11px \"Monospaced\";");
        matchDropDown.setPromptText("Select Bracket Match");
        matchDropDown.setButtonCell(new ListCell<Match>() {
            @Override
            public void updateItem(Match item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null) {
                    setText(item.toString());
                    setStyle("-fx-font: 11px \"Monospaced\";"+ "-fx-font-style: normal;");
                }
                else{
                    setText("Select Bracket Match");
                    setStyle("-fx-font: 12px \"System Regular\";" + "-fx-font-style: italic;" + "-fx-text-fill: grey;");
                }
            }
        });
        matchDropDown.setPrefSize(375, 25);
        matchDropDown.relocate(10, 300);

        P1DropDown = new ComboBox<String>();
        P1DropDown.setEditable(true);
        P1DropDownEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //System.out.println("updating from p1listcombo");
                updateMatchFromPlayers(P1DropDown, P2DropDown, matchDropDown);
            }
        };
        P1DropDown.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        P1DropDown.setPromptText("Player 1");
        P1DropDown.setOnAction(P1DropDownEventHandler);
        P1DropDown.setPrefSize(180,25);
        P1DropDown.relocate(10, 10);

        P2DropDown = new ComboBox<String>();
        P2DropDown.setEditable(true);
        P2DropDownEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //System.out.println("updating from p2listcombo");
                updateMatchFromPlayers(P2DropDown, P1DropDown, matchDropDown);
            }
        };
        P2DropDown.setPromptText("Player 2");
        P2DropDown.setOnAction(P2DropDownEventHandler);
        P2DropDown.setPrefSize(180,25);
        P2DropDown.relocate(410, 10);

        //todo make this pull the array from elsewhere
        ArrayList<String> charactersSSBM = new ArrayList<String>();
        charactersSSBM.add("Fox");
        charactersSSBM.add("Falco");
        charactersSSBM.add("Captain Falcon");
        charactersSSBM.add("Mr. Game & Watch");
        ObservableList<String> characterList = FXCollections.observableArrayList(charactersSSBM);

        P1CharDropDown = new ComboBox<String>();
        P1CharDropDown.setItems(characterList);
        P1CharDropDown.setPromptText("Select P1 Character");            //Extremely dumb scenario. We need prompt text, as setText won't show on launch
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
        P1CharDropDown.relocate(85, 45);

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
        P2CharDropDown.relocate(340, 45);

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
        P1PortDropDown.relocate(10, 45);

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
        P2PortDropDown.relocate(525, 45);

        getChildren().addAll(tournamentDropDown, matchDropDown, P1DropDown, P2DropDown, P1CharDropDown, P2CharDropDown, P1PortDropDown, P2PortDropDown);

        //Textfields
        tournamentRoundField = new TextField();
        tournamentRoundField.setPromptText("Enter bracket round.");
        tournamentRoundField.setPrefSize(186, 25);
        tournamentRoundField.relocate(10,100);

        tournamentNameField = new TextField();
        tournamentNameField.setPromptText("Enter tournament name.");
        tournamentNameField.setPrefSize(188, 25);
        tournamentNameField.relocate(206,100);

        commentatorsField = new TextField();
        commentatorsField.setPromptText("Enter commentator tags.");
        commentatorsField.setPrefSize(186, 25);
        commentatorsField.relocate(404,100);

        API_keyField = new PasswordField();
        API_keyField.setPromptText("Challonge API Key");
        API_keyField.relocate(395, 300);

        getChildren().addAll(tournamentRoundField, tournamentNameField, commentatorsField, API_keyField);

        //Labels
        Label tournamentRoundLabel, tournamentNameLabel, commentatorLabel;

        tournamentRoundLabel = new Label("Bracket Round: ");
        tournamentRoundLabel.relocate(18, 80);

        tournamentNameLabel = new Label("Tournament: ");
        tournamentNameLabel.relocate(214, 80);

        commentatorLabel = new Label("Commentators: ");
        commentatorLabel.relocate(412, 80);

        getChildren().addAll(tournamentRoundLabel, tournamentNameLabel, commentatorLabel);

        //Buttons
        refreshButton = new Button("Refresh");
        refreshButton.setPrefSize(138, 40);
        refreshButton.relocate(10, 140);

        saveButton = new Button("Save and Output");
        saveButton.setPrefSize(138, 40);
        saveButton.relocate(157, 140);

        uploadButton = new Button("Upload Match");
        uploadButton.setPrefSize(138, 40);
        uploadButton.relocate(305, 140);

        resetButton = new Button("Reset Match");
        resetButton.setPrefSize(138, 40);
        resetButton.relocate(452, 140);

        swapButton = new Button("Swap");
        swapButton.setPrefSize(60, 60);
        swapButton.relocate(270, 10);

        loadTournamentButton = new Button("Load Tournament");
        loadTournamentButton.setPrefSize(115,25);
        loadTournamentButton.relocate(270, 250);

        testButton = new Button("Test");
        testButton.relocate(450, 250);

        getChildren().addAll(refreshButton, saveButton, uploadButton, resetButton, swapButton, loadTournamentButton, testButton);
    }

    public void updateMatchFromPlayers(ComboBox<String> thisPlayerComboBox, ComboBox<String> otherPlayerComboBox, ComboBox<Match> matchComboBox){
        matchComboBox.setOnAction(null);        //Disable event listener for the match drop down (so it doesn't listen to this programmatic change)

        for(Match x:model.getMatches()){        //Traverse matches
            if(x != null && x.getPlayer1() != null && x.getPlayer2() != null ){
                if(thisPlayerComboBox.getValue() != null && otherPlayerComboBox.getValue() != null){
                    if(thisPlayerComboBox.getValue().equals(x.getPlayer1().getName()) && otherPlayerComboBox.getValue().equals(x.getPlayer2().getName())){      //Check if player 1 and player 2 from the player drop downs matches players 1 and 2 from match x
                        if(!x.isComplete()){        //For double jeopardy cases within brackets - players might meet for a re-match, but this necessitates that one match has already been completed and can thus be overlooked
                            //System.out.println(x);
                            matchComboBox.getSelectionModel().clearAndSelect(matchComboBox.getItems().indexOf(x));      //Select the match within the comboBox if players match
                            matchComboBox.setOnAction(matchDropDownEventHandler);       //Re-enable the event listener
                            return;         //We exit upon finding the match, no need to keep traversing
                        }
                    }
                    else if(thisPlayerComboBox.getValue().equals(x.getPlayer2Name()) && otherPlayerComboBox.getValue().equals(x.getPlayer1Name())){
                        if(!x.isComplete()){
                            //System.out.println(x);
                            matchComboBox.getSelectionModel().clearAndSelect(matchComboBox.getItems().indexOf(x));
                            matchComboBox.setOnAction(matchDropDownEventHandler);
                            return;
                        }
                    }
                    else{
                        //System.out.println("couldn't find matching match");
                        matchComboBox.getSelectionModel().clearSelection();         //If players do not match any matches in the list, deselect the match
                    }
                }
            }
        }
        matchComboBox.setOnAction(matchDropDownEventHandler);
    }

    public void updatePlayersFromMatch(Match selectedMatch, ComboBox<String> P1DropDown, ComboBox<String> P2DropDown){
        P1DropDown.setOnAction(null);      //Disable player drop down event listeners, we're changing values programmatically
        P2DropDown.setOnAction(null);

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

            tournamentRoundField.setText(matchDropDown.getValue().getRoundString(model.getCurrentTournament().getMaxRound()));      //Also update tournament round text
        }
        else{
            tournamentRoundField.setText(null);
        }
        P1DropDown.setOnAction(P1DropDownEventHandler);     //Re-enable player drop down event listeners
        P2DropDown.setOnAction(P2DropDownEventHandler);
    }

    //todo
    public void update(){

    }

    //Separate update function as we don't need this to fire each time we update
    public void pullChallongeData(){
        tournamentNameField.setText(model.getTournamentName());     //update field for tournament name

        ObservableList<Match> matchOptions = FXCollections.observableArrayList(model.getMatches());
        matchDropDown.setItems(matchOptions);       //Populate match drop down

        ArrayList<String> participantNames = new ArrayList<String>();       //Make array list of player strings rather than players
        for(Participant x:model.getParticipants()){
            participantNames.add(x.getName());
        }
        ObservableList<String> participantOptions = FXCollections.observableArrayList(participantNames);
        P1DropDown.setItems(participantOptions);        //Populate player drop downs
        P2DropDown.setItems(participantOptions);
    }

    //todo
    public void save(){
        //INPUT to model
        if(matchDropDown.getValue() != null){            //if a match is selected
            if(matchDropDown.getValue().getPlayer1() != null && matchDropDown.getValue().getPlayer2() != null){            //if the players in the match aren't null
                model.setCurrentMatch(matchDropDown.getValue());
                model.pullMatchInfo();      //todo update player names in the model as well in this function
            }
        }
        else{       //if no match is selected, but players have been manually selected or typed
            if(P1DropDown.getValue() != null && P2DropDown.getValue() != null){
                System.out.println("testing");
                //shouldn't update the players in the model, but should update the player names
            }
        }

        //inputs are already filtered to integers only, should be ok to get value
        model.setPlayer1Score(P1ScoreSpinner.getValue());
        model.setPlayer2Score(P2ScoreSpinner.getValue());


        //OUTPUT to OBS
            //Players
        System.out.println("P1: " + P1DropDown.getValue());
        System.out.println("P2: " + P2DropDown.getValue());

            //Scores
        System.out.println("P1 Score: " + P1ScoreSpinner.getValue());
        System.out.println("P2 Score: " + P2ScoreSpinner.getValue());

            //Ports
        System.out.println("P1 Port: " + P1PortDropDown.getValue());
        System.out.println("P2 Port: " + P2PortDropDown.getValue());

            //Characters
        System.out.println("P1 Char: " + P1CharDropDown.getSelectionModel().getSelectedItem());
        System.out.println("P2 Char: " + P2CharDropDown.getSelectionModel().getSelectedItem());

            //Match Info
        System.out.println("Bracket Round: " + tournamentRoundField.getText());
        System.out.println("Tournament: " + tournamentNameField.getText());
        System.out.println("Commentators: " + commentatorsField.getText());

        //TESTING

        //Check if both combobox values match the ids of the match
        //if so we can select the match within the combobox
        //if not, deselect the value within the match combobox
            //can only submit match results if match is selected within combobox
    }

    //todo
    public void reset(){
        System.out.println("Resetting.");       //debug

        //Players and Match
        matchDropDown.getSelectionModel().clearSelection();
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

    }

    public void loadTournament(){
        System.out.println("Loading tournament");       //debug
        pullChallongeData();
        reset();
    }

    public void swap(){
        System.out.println("Swapping");         //debug

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

    public ComboBox<Tournament> getTournamentDropDown(){
        return tournamentDropDown;
    }

    public Button getSaveButton(){
        return saveButton;
    }

    public Button getLoadTournamentButton() {
        return loadTournamentButton;
    }

    public Button getResetButton(){
        return resetButton;
    }

    public Button getSwapButton(){
        return swapButton;
    }

    public Button getTestButton(){
        return testButton;
    }

}
