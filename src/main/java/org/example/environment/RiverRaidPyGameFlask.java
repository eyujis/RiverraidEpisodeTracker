package org.example.environment;

import org.example.util.RawEnvInput;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;

public class RiverRaidPyGameFlask implements RiverRaidEnv {
    private final String INITIALIZE = "/initialize";
    private final String STEP = "/step";
    private final String GET = "/get";

    private final String url;
    private final HttpClient client;
    int nStep = 0;

    public RiverRaidPyGameFlask(String url) {
        client = HttpClient.newHttpClient();
        this.url = url;

        // Creates initialize request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + INITIALIZE))
                .POST(HttpRequest.BodyPublishers.ofString( ""))
                .build();

        // Sends request
        try {
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

    @Override
    public RawEnvInput step() {
        nStep++;

        // Creates step request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + GET))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        // Sends request
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject data = new JSONObject(response.body());

            JSONArray jsonArray = data.getJSONArray("state");
            byte[] bytes = new byte[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                bytes[i]=(byte)(((int)jsonArray.get(i)) & 0xFF);
            }

            InputStream in = new ByteArrayInputStream(bytes);
            BufferedImage bImageFromConvert = ImageIO.read(in);

            return new RawEnvInput(bImageFromConvert, data.getDouble("reward"), data.getBoolean("terminal"));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void communicateAction(int action) {
        // Creates step request
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url + STEP))
            .POST(HttpRequest.BodyPublishers.ofString("{\"action\": " + String.valueOf(action) + "}"))
            .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getNStep() {
        return nStep;
    }
}
