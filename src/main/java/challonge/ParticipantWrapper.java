package challonge;

public class ParticipantWrapper {           //JSON gives an array of *things* that each contain a participant, rather than an array of participants
    private Participant participant;

    public ParticipantWrapper(){
        participant = null;
    }

    public Participant getParticipant(){
        return participant;
    }

    public void setParticipant(Participant participant){
        this.participant = participant;
    }
}
