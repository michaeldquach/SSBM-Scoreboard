package challonge;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)     //JSON string has more fields than we care about
public class Participant {
    private final String name;
    private final int seed;
    private final int id;

    public Participant(){
        name = "";
        seed = 0;
        id = 0;
    }

    public Participant(String name, int seed, int id){
        this.name = name;
        this.seed = seed;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public int getSeed(){
        return seed;
    }

    public int getId(){
        return id;
    }

    public String toString(){
        return name;
    }
}
