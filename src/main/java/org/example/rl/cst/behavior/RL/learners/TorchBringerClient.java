package org.example.rl.cst.behavior.RL.learners;

import org.example.rl.util.RLPercept;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TorchBringerClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public TorchBringerClient(int port) {
        try {
            clientSocket = new Socket(InetAddress.getLocalHost(), port);
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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

            out.println(request.toString());
            System.out.println(in.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Double> step(RLPercept percept) {
        JSONObject request = new JSONObject();
        request.put("method", "step");
        request.put("state", percept.getState());
        request.put("reward", percept.getReward());
        request.put("terminal", percept.isTerminal());

        out.println(request.toString());
        try {
            return ((ArrayList<ArrayList<Double>>) new JSONObject(in.readLine()).get("action")).get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
