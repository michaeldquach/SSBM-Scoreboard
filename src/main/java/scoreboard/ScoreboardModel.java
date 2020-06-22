package scoreboard;

import challonge.ChallongeAPI;
import challonge.Match;
import challonge.Participant;
import challonge.Tournament;
import obs.OBSOutput;

import java.util.ArrayList;

public class ScoreboardModel {
    private ArrayList<Tournament> tournaments;
    private ArrayList<Participant> participants;
    private ArrayList<Match> matches;

    private String P1Name, P2Name, roundString, tournamentName, commentatorName;
    private int P1Score, P2Score, P1Port, P2Port;
    private boolean toggleChallonge, challongeLoggedIn, readyToPush;
    private Character P1Char, P2Char;

    private Participant player1, player2;
    private Tournament currentTournament;
    private Match currentMatch;

    public static final boolean DEBUG = true;

    public ScoreboardModel(){
        //Fields for OBSOutput
        P1Name = null;
        P2Name = null;

        P1Score = 0;
        P2Score = 0;

        P1Port = 0;
        P2Port = 0;

        P1Char = null;
        P2Char = null;

        roundString = null;
        tournamentName = null;
        commentatorName = null;

        //Fields for challonge
        toggleChallonge = true;
        challongeLoggedIn = false;
        readyToPush = false;
        player1 = null;
        player2 = null;
        currentTournament = null;
        currentMatch = null;

        tournaments = new ArrayList<Tournament>();
        participants = new ArrayList<Participant>();
        matches = new ArrayList<Match>();
    }

    //Generates list of tournaments from JSON. Also checks if logged in to Challonge
    public boolean pullTournamentList(){
        if(ChallongeAPI.getTournamentList() != null){               //Only retrieves null if failed to login. Will retrieve an array of tournaments if logged in
            tournaments = ChallongeAPI.getTournamentList();         //Note: accounts with no tournaments will still retrieve an empty array of tournaments, so that case is covered
            return true;
        }
        return false;           //Canary to flag that we've not been able to log in
    }

    public boolean pullMatchList(){
        if(currentTournament != null){
            matches = ChallongeAPI.getMatchList(currentTournament.getId());         //populate matches list by pulling from API
            int maxRound = 0;
            for(Match x:matches){
                x.findParticipant(this);           //match player ids to names, update in match
                if(x.getRound() >= maxRound){
                    maxRound = x.getRound();
                }
            }
            currentTournament.setMaxRound(maxRound);        //find highest round number, update in tournament
            return true;                                    //updating match and tournament is kinda dumb, but info is not included in their respective JSONs, and is essentially the same as creating a new array
        }
        return false;
    }

    public boolean pullParticipantList(){
        if(currentTournament != null){
            participants = ChallongeAPI.getParticipantList(currentTournament.getId());
            return true;
        }
        return false;
    }

    public Participant findPlayer(Integer playerID){
        for(Participant x:participants){
            if(playerID != null && playerID == x.getId()){
                return x;
            }
        }
        return null;
    }

    public void DEBUGTESTING(){
        if(DEBUG){
            if(readyToPush){
                System.out.println("Fields required for challonge ready to push.");     //debug
            }

            //OUTPUT to OBSOutput
            //Players
            System.out.println("P1: " + this.P1Name);
            System.out.println("P2: " + this.P2Name);

            //Scores
            System.out.println("P1 Score: " + this.P1Score);
            System.out.println("P2 Score: " + this.P2Score);

            //Ports
            System.out.println("P1 Port: " + this.P1Port);
            System.out.println("P2 Port: " + this.P2Port);

            //Characters
            System.out.println("P1 Char: " + this.P1Char);
            System.out.println("P2 Char: " + this.P2Char);

            //Match Info
            System.out.println("Bracket Round: " + this.roundString);
            System.out.println("Tournament: " + this.tournamentName);
            System.out.println("Commentators: " + this.commentatorName);

            //Challonge Info
            System.out.println("Current Tournament: " + this.currentTournament);
            System.out.println("Current Match: " + this.currentMatch);
            System.out.println("Player 1: " + this.player1);
            System.out.println("Player 2: " + this.player2);
            System.out.println();
        }
    }

    public void outputToOBS(){
        OBSOutput.writeOutputText("P1 Tag", P1Name);
        OBSOutput.writeOutputText("P2 Tag", P2Name);

        OBSOutput.writeOutputText("P1 Score", String.valueOf(P1Score));
        OBSOutput.writeOutputText("P2 Score", String.valueOf(P2Score));

        OBSOutput.writeOutputText("P1 Port", String.valueOf(P1Port));
        OBSOutput.writeOutputImage(String.format("/Images/OBS/Port %d.png", P1Port), "P1 Port Image");
        OBSOutput.writeOutputText("P2 Port", String.valueOf(P2Port));
        OBSOutput.writeOutputImage(String.format("/Images/OBS/Port %d.png", P2Port), "P2 Port Image");

        if(P1Char != null){
            OBSOutput.writeOutputText("P1 Char", P1Char.getName());
            OBSOutput.writeOutputImage(P1Char.getIconPath("OBS"), "P1 Character Icon");
        }
        else{
            OBSOutput.writeOutputText("P1 Char", null);
            OBSOutput.writeOutputImage(null, "P1 Character Icon");
        }
        if(P2Char != null){
            OBSOutput.writeOutputText("P2 Char", P2Char.getName());
            OBSOutput.writeOutputImage(P2Char.getIconPath("OBS"), "P2 Character Icon");
        }
        else{
            OBSOutput.writeOutputText("P2 Char", null);
            OBSOutput.writeOutputImage(null, "P2 Character Icon");
        }

        OBSOutput.writeOutputText("Bracket Round", roundString);
        OBSOutput.writeOutputText("Tournament Name", tournamentName);
        OBSOutput.writeOutputText("Commentator Tag", commentatorName);
    }

    //refreshes match list after pushing info by calling refresh() (refresh() calls loadTournament(currentTournament) in model)
    public boolean uploadMatchInfo(boolean tiesEnabled){
        if(challongeLoggedIn && readyToPush){     //ready to push checks that p1, p2, curTourney, and curMatch all not null
            Integer winnerID;
            if(player1.getId() == currentMatch.getPlayer1_id()){        //if player1's id in the scoreboard matches player1 in challonge
                if(P1Score > P2Score){        //if player1 won the match
                    winnerID = player1.getId();
                    ConsolePane.outputText(String.format("[%s] results uploaded. %s: %d, %s: %d.", currentMatch.getRoundString(currentTournament.getMaxRound()), player1.getName() + " (W)", P1Score, player2.getName(), P2Score));
                }
                else if(P2Score > P1Score){       //if player2 won the match
                    winnerID = player2.getId();
                    ConsolePane.outputText(String.format("[%s] results uploaded. %s: %d, %s: %d.", currentMatch.getRoundString(currentTournament.getMaxRound()), player1.getName(), P1Score, player2.getName() + " (W)", P2Score));
                }
                else{
                    if(tiesEnabled){
                        winnerID = null;
                    }
                    else{
                        ConsolePane.outputText("Scores are tied, and no winner can be selected. Please update the scores and resubmit to Challonge.");
                        return false;
                    }
                }
                if(!DEBUG){
                    ChallongeAPI.putMatchResults(currentTournament.getId(), currentMatch.getId(), P1Score, P2Score, winnerID);
                }
                reset(false);
                return true;
            }

            else if(player1.getId() == currentMatch.getPlayer2_id()){       //if player1's id matches player2 in challonge (because of ordering)
                if(P1Score > P2Score){        //if player1 won the match
                    winnerID = player1.getId();
                    ConsolePane.outputText(String.format("[%s] results uploaded. %s: %d, %s: %d.", currentMatch.getRoundString(currentTournament.getMaxRound()), player1.getName() + " (W)", P1Score, player2.getName(), P2Score));
                }
                else if(P2Score > P1Score){       //if player2 won the match
                    winnerID = player2.getId();
                    ConsolePane.outputText(String.format("[%s] results uploaded. %s: %d, %s: %d.", currentMatch.getRoundString(currentTournament.getMaxRound()), player1.getName(), P1Score, player2.getName() + " (W)", P2Score));
                }
                else{
                    if(tiesEnabled){
                        winnerID = null;
                    }
                    else{
                        ConsolePane.outputText("Scores are tied, and no winner can be selected. Please update the scores and resubmit to Challonge.");
                        return false;
                    }
                }
                if(!DEBUG){
                    ChallongeAPI.putMatchResults(currentTournament.getId(), currentMatch.getId(), P2Score, P1Score, winnerID);
                }
                reset(false);
                return true;
            }
        }
        return false;
    }
    
    public void saveMatchInfo(String P1Name, String P2Name, int P1Score, int P2Score, int P1Port, int P2Port, Character P1Char, Character P2Char, String roundString, String tournamentName, String commentatorName, Match currentMatch){
        this.P1Name = P1Name;
        this.P2Name = P2Name;
        this.P1Score = P1Score;
        this.P2Score = P2Score;
        this.P1Port = P1Port;
        this.P2Port = P2Port;
        this.P1Char = P1Char;
        this.P2Char = P2Char;
        this.roundString = roundString;
        this.tournamentName = tournamentName;
        this.commentatorName = commentatorName;

        this.currentMatch = currentMatch;
        if(currentMatch != null){
            if(currentMatch.getPlayer1Name().equals(P1Name) && currentMatch.getPlayer2Name().equals(P2Name)){       //player1 and player2 are only used to push data to challonge, and aren't accessed otherwise
                this.player1 = currentMatch.getPlayer1();                                                           //so we only need to save p1 and p2 when it exactly matches the match data (ready for pushing)
                this.player2 = currentMatch.getPlayer2();                                                           //p1name and p2name are still saved for output to OBSOutput
            }
            else if(currentMatch.getPlayer1Name().equals(P2Name) && currentMatch.getPlayer2Name().equals(P1Name)){      //check that order of player names matches order of Participant Players
                this.player2 = currentMatch.getPlayer1();                                                               //so we don't accidentally attribute the scores backwards (e.g. after player swaps in GUI)
                this.player1 = currentMatch.getPlayer2();
            }
        }
        else{
            this.player1 = null;
            this.player2 = null;
        }
        //check if all fields are present, flags ready for pushing. Only instance where readyToPush can be set to true
        readyToPush = this.currentTournament != null && this.currentMatch != null && this.player1 != null && this.player2 != null;

        DEBUGTESTING();
        outputToOBS();      //write to text files for OBS
    }

    public void reset(boolean completeReset){
        P1Name = null;
        P2Name = null;

        P1Score = 0;
        P2Score = 0;

        P1Port = 0;
        P2Port = 0;

        P1Char = null;
        P2Char = null;

        roundString = null;
        tournamentName = null;
        commentatorName = null;

        player1 = null;
        player2 = null;
        currentMatch = null;
        readyToPush = false;

        if(completeReset){
            currentTournament = null;
            tournaments = new ArrayList<Tournament>();
            participants = new ArrayList<Participant>();
            matches = new ArrayList<Match>();
        }
    }

    //logs into challonge and pulls tournament list
    public void challongeLogin(String username, String password){
        ChallongeAPI.saveAPIKey(username, password);        //saves api key to keyring todo only save on successful login
        challongeLoggedIn = pullTournamentList();        //Populates the tournament list. If we pulled the tournamentList successfully, then it means we've logged in
        readyToPush = false;        //we aren't pushing regardless of if logged in or not
    }

    //generates participant and match list from selected tournament to load
    public void loadTournament(Tournament loadedTournament){
        this.currentTournament = loadedTournament;
        if(currentTournament != null){
            tournamentName = currentTournament.getName();
        }
        pullParticipantList();
        pullMatchList();
    }

    //Method for initializing model from a config file
    public static ScoreboardModel loadModel(){
        ScoreboardModel model = new ScoreboardModel();
        return model;
    }

    //Getters and Setters

    public ArrayList<Tournament> getTournaments(){
        return tournaments;
    }

    public ArrayList<Match> getMatches(){
        return matches;
    }

    public ArrayList<Participant> getParticipants(){
        return participants;
    }

    public boolean isToggleChallonge() {
        return toggleChallonge;
    }

    public boolean isChallongeLoggedIn(){
        return challongeLoggedIn;
    }

    public boolean isReadyToPush(){
        return readyToPush;
    }

    public Tournament getCurrentTournament(){
        return currentTournament;
    }

    public void toggleChallonge(){
        toggleChallonge = !toggleChallonge;         //todo make other functions care if challonge toggled, trim some fat
    }

    public void setReadyToPush(boolean flag){
        readyToPush = flag;
        if(ScoreboardModel.DEBUG){System.out.println("Setting flag: " + readyToPush); }     //debug
    }
}
