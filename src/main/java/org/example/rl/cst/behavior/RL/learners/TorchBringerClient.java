package org.example.rl.cst.behavior.RL.learners;

import org.example.rl.util.RLPercept;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TorchBringerClient {
    private Socket clientSocket;
    private OutputStreamWriter out;
    private InputStreamReader in;

    public TorchBringerClient(int port) {
        try {
            clientSocket = new Socket("localhost", port);
            out = new OutputStreamWriter(clientSocket.getOutputStream(), "UTF8");
            in = new InputStreamReader(clientSocket.getInputStream(), "UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialize(String configPath) {
        try {
            String jsonData = new String(Files.readAllBytes(Paths.get(configPath)));
            JSONObject request = new JSONObject();
            request.put("method", "initialize");
            request.put("config", new JSONObject(jsonData));

            out.write(request.toString());
            out.flush();
            System.out.println(readInput());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Double> step(RLPercept percept) {
        JSONObject request = new JSONObject();
        request.put("method", "step");
        request.put("state", List.of(percept.getState()));
        request.put("reward", percept.getReward());
        request.put("terminal", percept.isTerminal());


        try {
            out.write(request.toString());
            out.flush();

            JSONObject jsonObject = new JSONObject(readInput());
            JSONArray jsonArray = (JSONArray) jsonObject.get("action");
            ArrayList<Double> result = new ArrayList<Double>();
            result.add((jsonArray.getJSONArray(0)).getDouble(0));
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readInput() {
        char[] buffer = new char[1024];
        try {
            in.read(buffer, 0, 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.valueOf(buffer);
    }
}
