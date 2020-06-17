package scoreboard;

import challonge.ChallongeAPI;
import challonge.Match;
import challonge.Participant;
import challonge.Tournament;
import java.util.ArrayList;

public class ScoreboardModel {
    private ArrayList<Tournament> tournaments;
    private ArrayList<Participant> participants;
    private ArrayList<Match> matches;

    private String P1Name, P2Name, P1Char, P2Char, roundString, tournamentName, commentatorName;
    private int P1Score, P2Score, P1Port, P2Port;
    private boolean challongeLoggedIn;

    private Participant player1, player2;
    private Tournament currentTournament;
    private Match currentMatch;

    public ScoreboardModel(){
        //Fields for OBS
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
        challongeLoggedIn = false;
        player1 = null;
        player2 = null;
        currentTournament = null;
        currentMatch = null;

        tournaments = new ArrayList<Tournament>();
        participants = new ArrayList<Participant>();
        matches = new ArrayList<Match>();
    }

    //generates list of tournaments from JSON
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


    public boolean pushMatchInfo(){
        //todo check if challonge logged in, check if current tournament is null
        //somehow only trigger after saving, with no changes noted

        if(currentMatch != null && player1 != null && player2 != null){     //Might make function to grab match when match is null, but players are not (e.g. selected from drop down)
            Integer winnerID;
            if(player1.getId() == currentMatch.getPlayer1_id()){        //if player1's id in the scoreboard matches player1 in challonge
                //System.out.println("they match");

                if(P1Score > P2Score){        //if player1 won the match
                    winnerID = player1.getId();
                }
                else if(P2Score > P1Score){       //if player2 won the match
                    winnerID = player2.getId();
                }
                else{
                    winnerID = null;        //Tie, this shouldn't occur todo maybe make this default to player 1
                }
                ChallongeAPI.putMatchResults(currentTournament.getId(), currentMatch.getId(), P1Score, P2Score, winnerID);
                return true;
            }

            else if(player1.getId() == currentMatch.getPlayer2_id()){       //if player1's id matches player2 in challonge (because of ordering)

                if(P1Score > P2Score){        //if player1 won the match
                    winnerID = player1.getId();
                }
                else if(P2Score > P1Score){       //if player2 won the match
                    winnerID = player2.getId();
                }
                else{
                    winnerID = null;        //Tie, this shouldn't occur
                }
                ChallongeAPI.putMatchResults(currentTournament.getId(), currentMatch.getId(), P2Score, P1Score, winnerID);
                return true;
            }
        }
        return false;
    }
    
    public void saveInfo(String P1Name, String P2Name, int P1Score, int P2Score, int P1Port, int P2Port, String P1Char, String P2Char, String roundString, String tournamentName, String commentatorName, Match currentMatch){
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
            if(currentMatch.getPlayer1Name().equals(P1Name) && currentMatch.getPlayer2Name().equals(P2Name)){       //check that order of player names matches order of Participant Players
                this.player1 = currentMatch.getPlayer1();                                                           //so we don't accidentally attribute the scores backwards (e.g. after player swaps in GUI)
                this.player2 = currentMatch.getPlayer2();
            }
            else if(currentMatch.getPlayer1Name().equals(P2Name) && currentMatch.getPlayer2Name().equals(P1Name)){
                this.player2 = currentMatch.getPlayer1();
                this.player1 = currentMatch.getPlayer2();
            }
        }
        else{
            this.player1 = null;
            this.player2 = null;
        }

        //OUTPUT to OBS debug
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
    }

    //Is it necessary to reset data within the model? We will save over with null values anyway
    public void reset(boolean completeReset){
        P1Name = null;
        P2Name = null;

        P1Score = 0;
        P2Score = 0;

        P1Port = 1;
        P2Port = 2;

        P1Char = null;
        P2Char = null;

        roundString = null;
        tournamentName = null;
        commentatorName = null;

        if(completeReset){
            player1 = null;
            player2 = null;
            currentTournament = null;
            currentMatch = null;
            tournaments = new ArrayList<Tournament>();
            participants = new ArrayList<Participant>();
            matches = new ArrayList<Match>();
        }
    }

    //logs into challonge and pulls tournament list
    public void challongeLogin(String username, String password){
        ChallongeAPI.setCredentials(username, password);         //Set credentials from api field

        //todo if successful login, save API_KEY to config file
        pullTournamentList();         //populate list of tournaments
        challongeLoggedIn = true;
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
    //todo make it read from a config file
    public static ScoreboardModel loadModel(){
        ScoreboardModel model = new ScoreboardModel();
        //ChallongeAPI.setCredentials("Sasquach_", "0jjHgAoqDH85DjAdKnWdRIRCecr5CktpePisHH7d");     //old
        //model.pullTournamentList();

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

    public boolean isChallongeLoggedIn(){
        return challongeLoggedIn;
    }

    public Tournament getCurrentTournament(){
        return currentTournament;
    }
}
