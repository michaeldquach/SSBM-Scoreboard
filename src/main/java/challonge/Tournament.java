package challonge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)     //JSON has more fields than we care about
public class Tournament {
    private final String name;
    private final int id;
    private final int participants_count;
    private int maxRound;
    private final String full_challonge_url;

    public Tournament(){
        name = "";
        id = 0;
        participants_count = 0;
        maxRound = 0;
        full_challonge_url = null;
    }

    public Tournament(String name, int id, int participants, String full_challonge_url){
        this.name = name;
        this.id = id;
        this.participants_count = participants;
        this.full_challonge_url = full_challonge_url;
    }

    public String getName(){
        return name;
    }

    public int getId() {
        return id;
    }

    public int getParticipants_count(){
        return participants_count;
    }

    public int getMaxRound(){
        return maxRound;
    }

    public String getFull_challonge_url(){
        return full_challonge_url;
    }

    public void setMaxRound(int maxRound){
        this.maxRound = maxRound;
    }

    public String toString(){
        return name;
    }
}
