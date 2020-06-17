package challonge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ChallongeAPI {
    private static String USERNAME = null;
    private static String API_KEY = null;
    private static ObjectMapper mapper = new ObjectMapper();

    public static String send(String requestURL, String method, String payload, int timeout){
        try{
            URL url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeout);

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod(method.toUpperCase());
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(payload);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer jsonString = new StringBuffer();

            String line;
            while((line = br.readLine()) != null){
                jsonString.append(line);
            }

            br.close();
            connection.disconnect();

            return jsonString.toString();
        }

        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Bare minimum REST methods (POST/GET/PUT/DELETE) to interface with challonge's API

    public static String post(String url, String payload, int timeout){
        return send(url + "?api_key=" + API_KEY, "POST", payload, timeout);
    }

    //no payload required when getting tournament info
    public static String get(String url, int timeout){
        try{
            URL website = new URL(url + "?api_key=" + API_KEY);
            //System.out.println(website);      //Debug to get url used to interface with API
            URLConnection connection = website.openConnection();
            connection.setConnectTimeout(timeout);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String put(String url, String payload, int timeout){
        return send(url + "?api_key=" + API_KEY, "PUT", payload, timeout);
    }

    public static String delete(String url, int timeout){
        return send(url + "?api_key=" + API_KEY, "DELETE", "{}", timeout);
    }

    //More friendly API interfacing methods

    public static ArrayList<Tournament> getTournamentList(){
        ArrayList<Tournament> tournamentList = new ArrayList<Tournament>();

        try{
            String tournamentsJSON = ChallongeAPI.get("https://api.challonge.com/v1/tournaments.json", 30*1000);
            TournamentWrapper[] tournamentsJSONParsed = mapper.readValue(tournamentsJSON, TournamentWrapper[].class);

            for(TournamentWrapper x:tournamentsJSONParsed){
                tournamentList.add(x.getTournament());
            }
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            //todo more detailed error message
        }

        return tournamentList;
    }

    public static ArrayList<Match> getMatchList(int tournamentID){
        ArrayList<Match> matchList = new ArrayList<Match>();

        try{
            String matchesJSON = ChallongeAPI.get("https://api.challonge.com/v1/tournaments/" + tournamentID + "/matches.json", 30*1000);
            MatchWrapper[] matchesJSONParsed = mapper.readValue(matchesJSON, MatchWrapper[].class);

            for(MatchWrapper x:matchesJSONParsed){
                matchList.add(x.getMatch());
            }
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return matchList;
    }

    public static ArrayList<Participant> getParticipantList(int tournamentID){
        ArrayList<Participant> participantList = new ArrayList<Participant>();

        try{
            String participantsJSON = ChallongeAPI.get("https://api.challonge.com/v1/tournaments/" + tournamentID + "/participants.json" , 30*1000);
            ParticipantWrapper[] participantsJSONParsed = mapper.readValue(participantsJSON, ParticipantWrapper[].class);

            for(ParticipantWrapper x:participantsJSONParsed){
                participantList.add(x.getParticipant());
            }
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            //todo more detailed error message
        }
        return participantList;
    }

    //No issue if match already has results inputted. putMatchResults() simply overwrites values within that match
    public static String putMatchResults(int tournamentID, int matchID, int player1Score, int player2Score, Integer winnerID){
        return put("https://api.challonge.com/v1/tournaments/" + tournamentID + "/matches/" + matchID + ".json","{\"match\": {\"scores_csv\": \"" + player1Score + "-" + player2Score + "\", \"winner_id\": " + winnerID + "}}", 30*1000);
    }

    public static void setCredentials(String username, String password){
        USERNAME = username;
        API_KEY = password;
    }

    //Opens url in browser
    public static void openURL(String url){
        try {
            if(url != null){
                Desktop.getDesktop().browse(new URL(url).toURI());
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}

