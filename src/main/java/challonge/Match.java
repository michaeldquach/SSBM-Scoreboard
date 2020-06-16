package challonge;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import scoreboard.ScoreboardModel;

import java.util.HashMap;

import static java.lang.Math.abs;

@JsonIgnoreProperties(ignoreUnknown = true)     //JSON string has more fields than we care about
public class Match {
    private final int id, round;
    private final Integer player1_id, player2_id;
    private Participant player1, player2;
    private String player1Name, player2Name, state;

    public Match(){
        id = 0;
        player1_id = null;
        player2_id = null;
        player1 = null;
        player2 = null;
        player1Name = null;
        player2Name = null;
        round = 0;
        state = null;
    }

    public Match(int id, Integer player1_id, Integer player2_id, int round, String state){
        this.id = id;
        this.player1_id = player1_id;
        this.player2_id = player2_id;
        this.round = round;
        this.state = state;
    }

    //Some JANK SHIT mane. Used to iterate through this array and update participants fields. same as just making a new arraylist of this + the fields
    //I only do this because the api for matches doesn't give player names...
    public void findParticipant(ScoreboardModel model){
        if(player1_id != null){
            player1 = model.findPlayer(player1_id);
            player1Name = player1.getName();
        }
        if(player2_id != null){
            player2 = model.findPlayer(player2_id);
            player2Name = player2.getName();
        }
    }

    public int getId(){
        return id;
    }

    public Integer getPlayer1_id(){
        return player1_id;
    }

    public Integer getPlayer2_id(){
        return player2_id;
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

    public int getRound(){
        return round;
    }

    public String getRoundString(int maxRound){
        String prefix, suffix;
        HashMap<Integer, String> suffixList = new HashMap<>();
        suffixList.put(maxRound - 1, "Final");
        suffixList.put(maxRound - 2, "Semi-Final");
        suffixList.put(maxRound - 3, "Quarter-Final");


        if(this.round == maxRound) {
            return "Grand Finals";
        }

        if(this.round >= 0){
            prefix = "Winners ";
        }
        else {
            prefix = "Losers ";
        }

        if(suffixList.containsKey(this.round)){
            suffix = suffixList.get(this.round);
        }
        else{
            suffix = "Round " + this.round;
        }
        return prefix + suffix;
    }

    public String getState(){
        return state;
    }

    //Checks if the match has both player ids
    public boolean isReady(){
        return player1_id != null && player2_id != null;
    }

    public boolean isComplete(){
        return this.state.equals("complete");
    }

    public String toString(){
        String player1Display, player2Display, roundString, stateString, playersString, finalString;

        if(player1Name != null){
            if(player1Name.length() > 12){
                player1Display = player1Name.substring(0, 9) + "...";
            }
            else{
                player1Display = player1Name;
            }
        }
        else{
            player1Display = "TBD";
        }
        if(player2Name != null){
            if(player2Name.length() > 12){
                player2Display = player2Name.substring(0, 9) + "...";
            }
            else{
                player2Display = player2Name;
            }
        }
        else{
            player2Display = "TBD";
        }

        playersString = String.format("%12s vs %-12s", player1Display, player2Display);

        if(round > 0){
            roundString = String.format("%-2s:%2d", "WR" , round);
        }
        else{
            roundString = String.format("%-2s:%2d", "LR" , round*-1);
        }

        if(isComplete()){
            stateString = "COMPLETE";
        }
        else if(!isReady()){
            stateString = "NOT READY";
        }
        else {
            stateString = "READY";
        }

        finalString = String.format("%s || %-9s || %s", roundString, stateString, playersString);

        return finalString;
    }
}
