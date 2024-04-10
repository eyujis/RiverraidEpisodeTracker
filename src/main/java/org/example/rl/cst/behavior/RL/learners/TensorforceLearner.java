package org.example.rl.cst.behavior.RL.learners;

import org.example.rl.util.RLPercept;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TensorforceLearner extends RLLearner {
    private final String INITIALIZE = "/initialize";
    private final String STEP = "/step";

    private final HttpClient client;
    private final String APIUrl;
    private final String configPath;
    private final String configName;


    private double pastReward;
    private boolean pastTerminal;
    private ArrayList<Double> pastState;

    public TensorforceLearner(String configPath, String APIUrl) throws IOException, InterruptedException {
        this.configPath = configPath;
        this.configName = configPath.substring(configPath.lastIndexOf("\\") + 1);
        this.APIUrl = APIUrl;
        client = HttpClient.newHttpClient();

        // Reads config file
        String jsonData = new String(Files.readAllBytes(Paths.get(configPath)));

        // Creates initialize request
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(APIUrl + INITIALIZE))
            .POST(HttpRequest.BodyPublishers.ofString(jsonData))
            .build();

        // Sends request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Initialized learning API successfully");
            System.out.println(response.body());
        } else {
            System.out.println("Unable to initialize learning API");
        }
    }

    @Override
    public void rlStep(ArrayList<RLPercept> trial) {
        // Saves trial data so it can be used in selectAction call
        pastState = trial.get(trial.size() - 1).getState();
        pastReward = trial.get(trial.size() - 1).getReward();
        pastTerminal = trial.get(trial.size() - 1).isTerminal();
    }

    @Override
    public ArrayList<Double> selectAction(ArrayList<Double> s) {
        if (pastState == null) {
            pastState = s;
            pastReward = 0.0;
            pastTerminal = false;
        }

        if (!s.equals(pastState)) {
            System.out.println("Something's wrong! s is not the state given in rlStep");
        }

        // Creates body
        String body = "{\"state\": " + pastState.toString() + ", " +
                "\"reward\": " + pastReward + ", " +
                "\"terminal\": " + pastTerminal + "}";

        // Creates step request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(APIUrl + STEP))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        // Sends request
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Converts response to ArrayList
        JSONObject data = new JSONObject(response.body());
        JSONArray actionData = data.getJSONArray("action");

        ArrayList<Double> action = new ArrayList<Double>();
        for (int i = 0; i < actionData.length(); i++){
            action.add(actionData.getDouble(i));
        }

        return actionSpace.translateAPIAction(action);
    }

    @Override
    public void endEpisode() {
        pastState = null;
    }

    @Override
    public String toString() {
        return "TensorforceLearner(configPath=" + configName + ")";
    }
}
