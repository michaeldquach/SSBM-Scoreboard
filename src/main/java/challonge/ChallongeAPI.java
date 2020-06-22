package challonge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordAccessException;
import scoreboard.ConsolePane;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class ChallongeAPI {
    private static Keyring keyring;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String send(String requestURL, String method, String payload, int timeout){
        int responseCode = 0;
        try{
            URL url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeout);
            responseCode = connection.getResponseCode();

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
            if(responseCode == 401){        //IOException stems from wrong key. Should really be the only error the user sees
                ConsolePane.outputText(String.format("Error %d: Invalid API Key.", responseCode));
            }
            if(responseCode == 404){
                ConsolePane.outputText(String.format("Error %d: Object not found within account scope.", responseCode));
            }
            if(responseCode == 406){
                ConsolePane.outputText(String.format("Error %d: Request format is not supported.", responseCode));
            }
            if(responseCode == 422){
                ConsolePane.outputText(String.format("Error %d: Validation error for create or update method.", responseCode));
            }
            if(responseCode == 500){
                ConsolePane.outputText(String.format("Error %d: Error on Challonge's end.", responseCode));
            }
            else{
                ConsolePane.outputText(String.format("Error %d.", responseCode));
                //e.printStackTrace();
            }
            return null;
        }
    }

    //Bare minimum REST methods (POST/GET/PUT/DELETE) to interface with challonge's API

    public static String post(String url, String payload, int timeout){
        return send(url + "?api_key=" + readAPIKey(), "POST", payload, timeout);
    }

    //no payload required when getting tournament info
    public static String get(String url, int timeout){
        int responseCode = 0;
        try{
            URL website = new URL(url + "?api_key=" + readAPIKey());
            HttpURLConnection connection = (HttpURLConnection) website.openConnection();
            connection.setConnectTimeout(timeout);
            responseCode = connection.getResponseCode();

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
            if(responseCode == 401){        //IOException stems from wrong key. Should really be the only error the user sees
                ConsolePane.outputText(String.format("Error %d: Invalid API Key.", responseCode));
            }
            if(responseCode == 404){
                ConsolePane.outputText(String.format("Error %d: Object not found within account scope.", responseCode));
            }
            if(responseCode == 406){
                ConsolePane.outputText(String.format("Error %d: Request format is not supported.", responseCode));
            }
            if(responseCode == 422){
                ConsolePane.outputText(String.format("Error %d: Validation error for create or update method.", responseCode));
            }
            if(responseCode == 500){
                ConsolePane.outputText(String.format("Error %d: Error on Challonge's end.", responseCode));
            }
            else{
                ConsolePane.outputText(String.format("Error %d.", responseCode));
                //e.printStackTrace();
            }
            return null;
        }
    }

    public static String put(String url, String payload, int timeout){
        return send(url + "?api_key=" + readAPIKey(), "PUT", payload, timeout);
    }

    public static String delete(String url, int timeout){
        return send(url + "?api_key=" + readAPIKey(), "DELETE", "{}", timeout);
    }

    //More friendly API interfacing methods

    //Challonge doesn't offer any ways to "login/validate" an API key other than getting/pushing/putting and not seeing an error, so this will serve as API login verification
    public static boolean login(String password){
        int responseCode = 0;
        int timeout = 30*1000;
        try{
            URL website = new URL("https://api.challonge.com/v1/tournaments.json?api_key=" + password);
            HttpURLConnection connection = (HttpURLConnection) website.openConnection();
            connection.setConnectTimeout(timeout);
            responseCode = connection.getResponseCode();

            return responseCode == 200;     //200 is response code for successful log in
        }
        catch (IOException e) {
            return false;
        }
    }

    public static ArrayList<Tournament> getTournamentList(){
        ArrayList<Tournament> tournamentList = new ArrayList<Tournament>();

        try{
            String tournamentsJSON = ChallongeAPI.get("https://api.challonge.com/v1/tournaments.json", 30*1000);

            if(tournamentsJSON != null){
                TournamentWrapper[] tournamentsJSONParsed = mapper.readValue(tournamentsJSON, TournamentWrapper[].class);

                for(TournamentWrapper x:tournamentsJSONParsed){
                    tournamentList.add(x.getTournament());
                }
            }
            else{
                return null;        //if login unsuccessful
            }
        }
        catch (JsonProcessingException e) {
            ConsolePane.outputText("Error parsing JSON file.");
            //e.printStackTrace();
        }
        return tournamentList;
    }

    public static ArrayList<Match> getMatchList(int tournamentID){
        ArrayList<Match> matchList = new ArrayList<Match>();

        try{
            String matchesJSON = ChallongeAPI.get("https://api.challonge.com/v1/tournaments/" + tournamentID + "/matches.json", 30*1000);

            if(matchesJSON != null){
                MatchWrapper[] matchesJSONParsed = mapper.readValue(matchesJSON, MatchWrapper[].class);

                for(MatchWrapper x:matchesJSONParsed){
                    matchList.add(x.getMatch());
                }
            }
        }
        catch (JsonProcessingException e) {
            ConsolePane.outputText("Error parsing JSON file.");
            //e.printStackTrace();
        }
        return matchList;
    }

    public static ArrayList<Participant> getParticipantList(int tournamentID){
        ArrayList<Participant> participantList = new ArrayList<Participant>();

        try{
            String participantsJSON = ChallongeAPI.get("https://api.challonge.com/v1/tournaments/" + tournamentID + "/participants.json" , 30*1000);

            if(participantsJSON != null){
                ParticipantWrapper[] participantsJSONParsed = mapper.readValue(participantsJSON, ParticipantWrapper[].class);

                for(ParticipantWrapper x:participantsJSONParsed){
                    participantList.add(x.getParticipant());
                }
            }
        }
        catch (JsonProcessingException e) {
            ConsolePane.outputText("Error parsing JSON file.");
            //e.printStackTrace();
        }
        return participantList;
    }

    //Note: No issue if match already has results inputted. putMatchResults() simply overwrites values within that match
    public static String putMatchResults(int tournamentID, int matchID, int player1Score, int player2Score, Integer winnerID){
        return put("https://api.challonge.com/v1/tournaments/" + tournamentID + "/matches/" + matchID + ".json","{\"match\": {\"scores_csv\": \"" + player1Score + "-" + player2Score + "\", \"winner_id\": " + winnerID + "}}", 30*1000);
    }

    //Opens url in browser
    public static void openURL(String url){
        try {
            if(url != null){
                Desktop.getDesktop().browse(new URL(url).toURI());
            }
        } catch (IOException | URISyntaxException e) {
            ConsolePane.outputText("Error opening URL.");
        }
    }

    //API Key-Handling

    public static void initKeyring(){
        try{
            keyring = Keyring.create();
            try{
                keyring.getPassword("API", "Challonge");    //for first launch, keyring won't have a key stored, so we'll put one in
            } catch (PasswordAccessException e) {
                keyring.setPassword("API", "Challonge", "");
            }
        }
        catch (BackendNotSupportedException | PasswordAccessException e) {
            e.printStackTrace();        //shouldn't print to console pane
        }
    }

    //Saves API key and sets credentials. Note: overwrites everytime, but that functionality is fine.
    public static void saveAPIKey(String username, String password){
        try{
            keyring.setPassword("API", "Challonge", password);
        }
        catch (PasswordAccessException e) {
            e.printStackTrace();
        }
    }

    //Retrieves saved API key, called when sending requests, and on start up to remember password
    public static String readAPIKey(){
        String API_KEY = null;
        try{
            API_KEY = keyring.getPassword("API", "Challonge");
        }
        catch (PasswordAccessException e) {
            e.printStackTrace();
        }
        return API_KEY;
    }
}

