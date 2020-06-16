package challonge;

public class MatchWrapper {           //JSON gives an array of *things* that each contain a match, rather than an array of match
    private Match match;

    public MatchWrapper(){
        match = null;
    }

    public Match getMatch(){
        return match;
    }

    public void setMatch(Match match){
        this.match = match;
    }
}
