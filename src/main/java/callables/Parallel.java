package callables;

import utils.HttpUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

public class Parallel implements Callable<String> {

    private String host;
    private String accessToken;

    public Parallel(String host, String accesToken) {
        this.host = host;
        this.accessToken = accesToken;
    }

    @Override
    public String call() throws Exception {
        String response = "";

        HttpURLConnection http = (HttpURLConnection) new URL(host).openConnection();
        http.setRequestMethod("GET");
        http.setRequestProperty("content-type", "application/json");
        http.setRequestProperty("Authorization", "Bearer " + accessToken);

        BufferedReader Lines = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String currentLine = Lines.readLine();
        while (currentLine != null) {
            response += currentLine;
            currentLine = Lines.readLine();
        }
        return response;
    }
}
