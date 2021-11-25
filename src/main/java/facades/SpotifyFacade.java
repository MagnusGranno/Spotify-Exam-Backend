package facades;


import DTO.CategoryDTOS.CategoryObjectDTO;
import DTO.CategoryDTOS.ItemsDTO;
import DTO.PlaylistsDTOS.PlaylistDTO;
import DTO.PlaylistsDTOS.PlaylistObjectDTO;
import DTO.PlaylistsDTOS.PlaylistsDTO;
import DTO.TracksDTOS.TrackItemsDTO;
import DTO.TracksDTOS.TracksObjectDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;


public class SpotifyFacade {
    private static SpotifyFacade instance;
    private String expiresIn;
    private Instant tokenExpireTime;
    private String accessToken;
    private String refreshToken;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();



    private SpotifyFacade() {
    }

    public static SpotifyFacade getSpotifyFacade() {
        if (instance == null) {

            instance = new SpotifyFacade();
        }
        return instance;
    }




    public String getTokenFromSpotify() throws IOException {
        String clientID = Secrets.clientID;
        String clientSecret = Secrets.clientSecret;
        String Response = null;
        String tokenURL = "https://accounts.spotify.com/api/token";

        URL url = new URL(tokenURL);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

        String data = "grant_type=client_credentials&client_id=" + clientID + "&client_secret=" + clientSecret + "";

        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = http.getOutputStream();
        stream.write(out);

        BufferedReader Lines = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String currentLine = Lines.readLine();
        String response = "";
        while (currentLine != null) {
            response = currentLine;
            currentLine = Lines.readLine();
        }

        this.accessToken = String.valueOf(JsonParser.parseString(response).getAsJsonObject().getAsJsonPrimitive("access_token"));
        this.expiresIn = String.valueOf(JsonParser.parseString(response).getAsJsonObject().getAsJsonPrimitive("expires_in"));
        Duration d = Duration.ofSeconds(Long.parseLong(expiresIn));
        this.tokenExpireTime = Instant.now().plus(d);
        this.accessToken = accessToken.substring(1, accessToken.length() - 1);
        http.disconnect();

        return response;
    }

    public void getTokenIfNeeded() throws IOException {
        if(tokenExpireTime == null || tokenExpireTime.isBefore(Instant.now())){
            getTokenFromSpotify();
        }
    }

    public List<ItemsDTO> getCategories() throws IOException {
        getTokenIfNeeded();
        String browseUrl = "https://api.spotify.com/v1/browse/categories?locale=en_US";

        URL url = new URL(browseUrl);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setRequestProperty("content-type", "application/json");
        http.setRequestProperty("Authorization", "Bearer " +accessToken);

        BufferedReader Lines = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String currentLine = Lines.readLine();
        String response = "";
        //TODO: 1
        while (currentLine != null) {
            response += currentLine;
            currentLine = Lines.readLine();
        }
//        System.out.println(response);
        return gson.fromJson(response, CategoryObjectDTO.class).getCategories().getItems();
    }

    public List<PlaylistDTO> getPlaylists (String genre) throws IOException {
        getTokenIfNeeded();
        String browseUrl = "https://api.spotify.com/v1/browse/categories/" + genre + "/playlists";

        URL url = new URL(browseUrl);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setRequestProperty("content-type", "application/json");
        http.setRequestProperty("Authorization", "Bearer " +accessToken);

        BufferedReader Lines = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String currentLine = Lines.readLine();
        String response = "";
        while (currentLine != null) {
            response += currentLine;
            currentLine = Lines.readLine();
        }

        PlaylistsDTO playlistDTO = gson.fromJson(response, PlaylistObjectDTO.class).getPlaylistDTO();
        playlistDTO.moveImageUrlForEachItem();

        return playlistDTO.getPlaylistDTO();
    }
    public List<TrackItemsDTO> getTracks(String playlistId) throws IOException {
        getTokenIfNeeded();
        String browseUrl = "https://api.spotify.com/v1/playlists/" + playlistId + "/tracks?offset=0&limit=20";

        URL url = new URL(browseUrl);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setRequestProperty("content-type", "application/json");
        http.setRequestProperty("Authorization", "Bearer " +accessToken);

        BufferedReader Lines = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String currentLine = Lines.readLine();
        String response = "";
        while (currentLine != null) {
            response += currentLine;
            currentLine = Lines.readLine();
        }

//        System.out.println(response);

        TracksObjectDTO trackItemsDTO = gson.fromJson(response, TracksObjectDTO.class);
        trackItemsDTO.moveDataToTrackItem();



        return trackItemsDTO.getItems();
    }

}











