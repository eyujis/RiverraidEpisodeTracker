package org.example.rl.cst.behavior.RL.learners;

import org.example.rl.util.RLPercept;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TorchBringerFlaskClient extends TorchBringerClient {
    private static final String INITIALIZE = "/initialize";
    private static final String STEP = "/step";

    private final HttpClient client;
    private final String url;


    public TorchBringerFlaskClient(String url) {
        this.url = url;

        client = HttpClient.newHttpClient();
    }

    public void initialize(String configPath) {
        try {
            // Reads config file
            String jsonData = new String(Files.readAllBytes(Paths.get(configPath)));

            // Creates initialize request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + INITIALIZE))
                    .POST(HttpRequest.BodyPublishers.ofString( "{\"config\": "+ jsonData + "}"))
                    .build();

            // Sends request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println(response.body());
            } else {
                System.out.println("Unable to initialize learning API - Error code " + String.valueOf(response.statusCode()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Double> step(RLPercept percept) {
        System.out.println(percept + "\n");

        // Creates body
        String body = "{\"state\": " + List.of(percept.getState()).toString() + ", " +
                "\"reward\": " + percept.getReward() + ", " +
                "\"terminal\": " + percept.isTerminal() + "}";

        // Creates step request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + STEP))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        // Sends request
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        // Converts response to ArrayList
        JSONObject data = new JSONObject(response.body());

        if (data.has("info")) {
            System.out.println(data.getString("info"));
            return new ArrayList<>();
        }

        JSONArray actionData = data.getJSONArray("action").getJSONArray(0);

        ArrayList<Double> action = new ArrayList<Double>();
        for (int i = 0; i < actionData.length(); i++){
            action.add(actionData.getDouble(i));
        }

        return action;
    }
}
