package scoreboard;

import challonge.ChallongeAPI;
import challonge.Match;
import challonge.Participant;
import challonge.Tournament;
import java.util.ArrayList;

public class ScoreboardModel {
    private String tournamentName;
    private String player1Name;
    private String player2Name;
    private int player1Score, player2Score, player1Port, player2Port, roundNumber;

    private ArrayList<Tournament> tournaments;
    private ArrayList<Participant> participants;
    private ArrayList<Match> matches;
    private Tournament currentTournament;
    private Match currentMatch;
    private Participant player1, player2;


    public ScoreboardModel(){
        tournamentName = "";
        player1 = null;
        player2 = null;
        //player1Name = "Player 1";
        //player2Name = "Player 2";
        player1Score = 0;
        player2Score = 0;
        player1Port = 1;
        player2Port = 2;
        roundNumber = 0;
        currentTournament = null;
        currentMatch = null;
        tournaments = new ArrayList<Tournament>();
        participants = new ArrayList<Participant>();
        matches = new ArrayList<Match>();
    }

    //might make sense for this function to exist in view only?
    public void swapPlayers(){
        Participant tempPlayer = player1;
        int tempScore = player1Score;
        int tempPort = player1Port;

        player1 = player2;
        player1Score = player2Score;
        player1Port = player2Port;

        player2 = tempPlayer;
        player2Score = tempScore;
        player2Port = tempPort;
    }

    public boolean pushMatchInfo(){
        if(currentMatch != null && player1 != null && player2 != null){     //Might make function to grab match when match is null, but players are not (e.g. selected from drop down)
            Integer winnerID;
            if(player1.getId() == currentMatch.getPlayer1_id()){        //if player1's id in the scoreboard matches player1 in challonge
                //System.out.println("they match");

                if(player1Score > player2Score){        //if player1 won the match
                    winnerID = player1.getId();
                }
                else if(player2Score > player1Score){       //if player2 won the match
                    winnerID = player2.getId();
                }
                else{
                    winnerID = null;        //Tie, this shouldn't occur todo maybe make this default to player 1
                }
                ChallongeAPI.putMatchResults(currentTournament.getId(), currentMatch.getId(), player1Score, player2Score, winnerID);
                return true;
            }

            else if(player1.getId() == currentMatch.getPlayer2_id()){       //if player1's id matches player2 in challonge (because of ordering)

                if(player1Score > player2Score){        //if player1 won the match
                    winnerID = player1.getId();
                }
                else if(player2Score > player1Score){       //if player2 won the match
                    winnerID = player2.getId();
                }
                else{
                    winnerID = null;        //Tie, this shouldn't occur
                }
                ChallongeAPI.putMatchResults(currentTournament.getId(), currentMatch.getId(), player2Score, player1Score, winnerID);
                return true;
            }
        }
        return false;
    }

    public boolean pullMatchInfo(){
        if(currentMatch != null){
            player1 = currentMatch.getPlayer1();
            player2 = currentMatch.getPlayer2();
            roundNumber = currentMatch.getRound();
            return true;
        }
        return false;
    }

    public void resetMatchInfo(){
        player1Score = 0;
        player2Score = 0;
        player1Port = 1;
        player2Port = 2;
        roundNumber = 0;
    }

    public void pullTournamentList(){
        tournaments = ChallongeAPI.getTournamentList();
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
            return true;
        }
        return false;
    }

    public boolean pullParticipantList(){
        if(currentTournament != null){
            setParticipants(ChallongeAPI.getParticipantList(currentTournament.getId()));
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

    public Participant findPlayer(String playerName){
        for(Participant x:participants){
            if(playerName != null && playerName == x.getName()){
                return x;
            }
        }
        return null;
    }

    public String findPlayerName(Integer playerID){
        for(Participant x:participants){
            if(playerID != null && playerID == x.getId()){
                return x.getName();
            }
        }
        return null;
    }

    public Integer findPlayerID(String playerName){
        for(Participant x:participants){
            if(playerName != null && playerName.equals(x.getName())){
                return x.getId();
            }
        }
        return null;
    }

    //Method for initializing model from a config file
    //todo make it read from a config file
    public static ScoreboardModel loadModel(){
        ScoreboardModel model = new ScoreboardModel();
        ChallongeAPI.setCredentials("Sasquach_", "0jjHgAoqDH85DjAdKnWdRIRCecr5CktpePisHH7d");
        model.pullTournamentList();

        return model;
    }

    //Getters and Setters

    public String getTournamentName(){
        return tournamentName;
    }

    public Participant getPlayer1(){
        return player1;
    }

    public Participant getPlayer2() {
        return player2;
    }

    public String getPlayer1Name(){
        return player1Name;
    }

    public String getPlayer2Name(){
        return player2Name;
    }

    public int getPlayer1Score(){
        return player1Score;
    }

    public int getPlayer2Score(){
        return player2Score;
    }

    public int getPlayer1Port() {
        return player1Port;
    }

    public int getPlayer2Port() {
        return player2Port;
    }

    public int getRoundNumber(){
        return roundNumber;
    }

    public Tournament getCurrentTournament() {
        return currentTournament;
    }

    public Match getCurrentMatch(){
        return currentMatch;
    }

    public ArrayList<Tournament> getTournaments(){
        return tournaments;
    }

    public ArrayList<Match> getMatches(){
        return matches;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setTournamentName(String tournamentName){
        this.tournamentName = tournamentName;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public void setCurrentTournament(Tournament currentTournament){
        this.currentTournament = currentTournament;
        if(currentTournament != null){
            setTournamentName(currentTournament.getName());
        }
    }

    public void setCurrentMatch(Match currentMatch){
        this.currentMatch = currentMatch;
        //Set round number here too
    }

    public void setParticipants(ArrayList<Participant> participants){
        this.participants = participants;
    }
}
