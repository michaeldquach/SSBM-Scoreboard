package challonge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)     //JSON has more fields than we care about
public class Tournament {
    private String name;
    private int id;
    private int participants_count;
    private int maxRound;

    public Tournament(){
        name = "";
        id = 0;
        participants_count = 0;
        maxRound = 0;
    }

    public Tournament(String name, int id, int participants){
        this.name = name;
        this.id = id;
        this.participants_count = participants;
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

    public void setMaxRound(int maxRound){
        this.maxRound = maxRound;
    }

    public String toString(){
        return name + ", " + participants_count + " participants. ID: " + id;
    }
}
