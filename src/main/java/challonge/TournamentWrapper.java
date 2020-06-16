package challonge;

public class TournamentWrapper {
    private Tournament tournament;

    public TournamentWrapper(){
        tournament = null;
    }

    public Tournament getTournament(){
        return tournament;
    }

    public void setTournaments(Tournament tournament){
        this.tournament = tournament;
    }
}
