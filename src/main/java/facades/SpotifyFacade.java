package facades;


import DTO.CategoriesDTO;
import DTO.CategoryDTO;
import DTO.ItemsDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import org.eclipse.yasson.YassonJsonb;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.*;
import java.net.HttpURLConnection;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class SpotifyFacade {
    private static SpotifyFacade instance;
    private String expiresIn;
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

        accessToken = String.valueOf(JsonParser.parseString(response).getAsJsonObject().getAsJsonPrimitive("access_token"));
        expiresIn = String.valueOf(JsonParser.parseString(response).getAsJsonObject().getAsJsonPrimitive("expires_in"));
        accessToken = accessToken.substring(1, accessToken.length() - 1);
        http.disconnect();


        return response;

    }
    public List<ItemsDTO> getCategories() throws IOException {
        getTokenFromSpotify();
        String browseUrl = "https://api.spotify.com/v1/browse/categories?locale=dk_US";

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
        System.out.println(response);
        return gson.fromJson(response, CategoryDTO.class).getCategories().getItems();
    }
}