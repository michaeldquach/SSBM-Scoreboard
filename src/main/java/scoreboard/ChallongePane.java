package scoreboard;

import challonge.ChallongeAPI;
import challonge.Match;
import challonge.Tournament;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

public class ChallongePane extends Pane {
    private ScoreboardModel model;
    private ScoreboardView view;
    private ScorePane scorePane;

    private ComboBox<Tournament> tournamentDropDown;
    private ComboBox<Match> matchDropDown;
    private Button loadTournamentButton, loginButton, refreshButton, urlTournamentButton, urlAPIButton;
    private PasswordField API_keyField;
    private EventHandler<ActionEvent> matchDropDownEventHandler;

    public ChallongePane(ScoreboardModel initModel, ScoreboardView initView){
        this.model = initModel;
        this.view = initView;
        this.setPrefSize(600, 95);
        this.setStyle("-fx-border-color: black");

        int row1Y = 15;
        int row2Y = 50;

        tournamentDropDown = new ComboBox<Tournament>();
        tournamentDropDown.setPromptText("Select Tournament");
        tournamentDropDown.setButtonCell(new ListCell<Tournament>() {
            @Override
            public void updateItem(Tournament item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null) {
                    setText(item.toString());
                    setStyle("");       //Resets font to default
                }
                else{
                    setText("Select Tournament");
                    setStyle("-fx-font-style: italic;" + "-fx-text-fill: grey;");
                }
            }
        });
        tournamentDropDown.setPrefSize(155,25);
        tournamentDropDown.relocate(215, row1Y);

        matchDropDown = new ComboBox<Match>();
        matchDropDown.setCellFactory(lv -> new ListCell<Match>(){
            public void updateItem(Match match, boolean selectable){
                super.updateItem(match, selectable);
                if(match != null){
                    setText(match.toString());
                    if(match.isComplete() || !match.isReady()){     //Disable selection of match if already completed/not ready
                        setDisable(true);
                        setStyle("-fx-text-fill: grey;");       //Change text colour for disabled indication
                    }
                    else{
                        setDisable(false);
                        setStyle("");       //Change text colour to default
                    }
                }
            }
        });
        matchDropDownEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                scorePane.updatePlayersFromMatch(matchDropDown.getValue());
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
        matchDropDown.relocate(215, row2Y);

        loadTournamentButton = new Button("Load Tournament");
        loadTournamentButton.setPrefSize(115,25);
        loadTournamentButton.relocate(475, row1Y);

        urlTournamentButton = new Button("URL");
        urlTournamentButton.setPrefSize(50,25);
        urlTournamentButton.relocate(380, row1Y);

        refreshButton = new Button("↻");
        refreshButton.setPadding(new Insets(0, 0, 0, 0));
        refreshButton.setStyle("-fx-font: 20px \"Verdana\";");
        refreshButton.setPrefSize(25,25);
        refreshButton.relocate(440, row1Y);

        loginButton = new Button("Login to Challonge");
        loginButton.setPrefSize(145,25);
        loginButton.relocate(10, row2Y);

        urlAPIButton = new Button("API?");
        urlAPIButton.setPrefSize(40,25);
        urlAPIButton.relocate(165, row2Y);

        API_keyField = new PasswordField();
        API_keyField.setPromptText("Challonge API Key");
        API_keyField.setText(ChallongeAPI.readAPIKey());
        API_keyField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                update();
            }
        });
        API_keyField.setPrefSize(195,25);
        API_keyField.relocate(10, row1Y);

        getChildren().addAll(tournamentDropDown, matchDropDown, loadTournamentButton, urlTournamentButton, refreshButton, loginButton, urlAPIButton, API_keyField);
    }

    //called when p1/p2 dropdown selected
    public void updateMatchFromPlayers(ComboBox<String> thisPlayerComboBox, ComboBox<String> otherPlayerComboBox){
        matchDropDown.setOnAction(null);        //Disable event listener for the match drop down (so it doesn't listen to this programmatic change)

        for(Match x:model.getMatches()){        //Traverse matches
            if(x != null && x.getPlayer1() != null && x.getPlayer2() != null ){
                if(thisPlayerComboBox.getValue() != null && otherPlayerComboBox.getValue() != null){
                    if(thisPlayerComboBox.getValue().equals(x.getPlayer1().getName()) && otherPlayerComboBox.getValue().equals(x.getPlayer2().getName())){      //Check if player 1 and player 2 from the player drop downs matches players 1 and 2 from match x
                        if(!x.isComplete()){        //For double jeopardy cases within brackets - players might meet for a re-match, but this necessitates that one match has already been completed and can thus be overlooked
                            matchDropDown.getSelectionModel().clearAndSelect(matchDropDown.getItems().indexOf(x));      //Select the match within the comboBox if players match
                            matchDropDown.setOnAction(matchDropDownEventHandler);       //Re-enable the event listener
                            return;         //We exit upon finding the match, no need to keep traversing
                        }
                    }
                    else if(thisPlayerComboBox.getValue().equals(x.getPlayer2Name()) && otherPlayerComboBox.getValue().equals(x.getPlayer1Name())){
                        if(!x.isComplete()){
                            matchDropDown.getSelectionModel().clearAndSelect(matchDropDown.getItems().indexOf(x));
                            matchDropDown.setOnAction(matchDropDownEventHandler);
                            return;
                        }
                    }
                    else{
                        matchDropDown.getSelectionModel().clearSelection();         //If players do not match any matches in the list, deselect the match
                    }
                }
            }
        }
        matchDropDown.setOnAction(matchDropDownEventHandler);
    }

    public String getAPIKey(){
        String API_KEY = API_keyField.getText();
        API_keyField.setText(null);     //clear api field
        return API_KEY;
    }

    public void update(){
        if(scorePane != view.getScorePane()){
            scorePane = view.getScorePane();
        }

        loginButton.setDisable(API_keyField.getText() == null || API_keyField.getText().equals(""));      //only enable if text in password field

        tournamentDropDown.setDisable(!model.isChallongeLoggedIn());        //disable if not logged in to challonge
        loadTournamentButton.setDisable(!model.isChallongeLoggedIn());
        matchDropDown.setDisable(!model.isChallongeLoggedIn());

        urlTournamentButton.setDisable(model.getCurrentTournament() == null);       //disable if no tournament loaded
        refreshButton.setDisable(model.getCurrentTournament() == null);
    }

    public void toggleChallonge(boolean toggle){
        setVisible(toggle);
    }

    public void reset(boolean completeReset){
        matchDropDown.getSelectionModel().clearSelection();

        //Complete Reset
        if(completeReset){
            matchDropDown.setItems(null);
        }
    }

    //Updates list of tournaments after logging
    public void challongeLogin(){
        ObservableList<Tournament> tournamentOptions = FXCollections.observableArrayList(model.getTournaments());
        tournamentDropDown.setItems(tournamentOptions);   //populate tournament drop down
    }

    //Updates list of matches from loaded tournament
    public void loadTournament(){
        ObservableList<Match> matchOptions = FXCollections.observableArrayList(model.getMatches());
        matchDropDown.setItems(matchOptions);       //Populate match drop down
    }

    public void refresh(Tournament currentTournament){
        tournamentDropDown.getSelectionModel().select(currentTournament);
    }

    public ComboBox<Tournament> getTournamentDropDown() {
        return tournamentDropDown;
    }

    public ComboBox<Match> getMatchDropDown() {
        return matchDropDown;
    }

    public Button getLoadTournamentButton(){
        return loadTournamentButton;
    }

    public Button getLoginButton(){
        return loginButton;
    }

    public Button getRefreshButton(){
        return refreshButton;
    }

    public Button getUrlTournamentButton() {
        return urlTournamentButton;
    }

    public Button getUrlAPIButton() {
        return urlAPIButton;
    }
}
