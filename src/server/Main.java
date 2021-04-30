package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

class ProcessPlayer extends Thread
{
    static void Log(String msg) {
        System.out.println(msg);
    }

    private DataInputStream inPlayer;
    private DataOutputStream outPlayer;
    private Controller controller;
    private String playerName;

    public ProcessPlayer(DataInputStream inPlayer, DataOutputStream outPlayer, Controller controller, String playerName)
    {
        this.inPlayer = inPlayer;
        this.outPlayer = outPlayer;
        this.controller = controller;
        this.playerName = playerName;
    }

    public void run() {
        while(true){
            try
            {
                String request = inPlayer.readUTF();
                String response;

                Log("from "+playerName+": "+request);
                String[] params = request.split("\\|");

                String command = params[0];
                int i = Integer.parseInt(params[1]);
                int j = Integer.parseInt(params[2]);

                switch (command){
                    case "getfield":
                        response = controller.GetFirstPlayerFields();
                        outPlayer.writeUTF(response);
                        Log("to "+playerName+": \n"+ response);
                        break;
                    case "setshoot":
                        boolean setPlayerResult = controller.FirstPlayerShootToSecondPlayer(i,j);

                        response = setPlayerResult == true ? "ok" : "error";

                        outPlayer.writeUTF(response);
                        Log("to "+playerName+": " + response);
                        break;
                }
            }
            catch (IOException e)
            {
                Log("some error: "+ e.getMessage());
            }
        }
    }
}

public class Main
{
    static void Log(String msg) {
        System.out.println(msg);
    }

    public static void main(String[] args) throws IOException {
        System.in.read();

        Controller controller = new Controller();

        ServerSocket listener = null;

        try {
            listener = new ServerSocket(37152, 1, InetAddress.getByName("127.0.0.1"));
            Log("server is started");
        } catch (Exception e) {
            Log("failed to start server: " + e.getMessage());
            return;
        }

        Log("server is listening");

        Socket talkingPlayer1 = null;
        Socket talkingPlayer2 = null;

        DataInputStream inPlayer1 = null;
        DataOutputStream outPlayer1 = null;

        DataInputStream inPlayer2 = null;
        DataOutputStream outPlayer2 = null; // 1 | 1

        try {
            talkingPlayer1 = listener.accept();
            Log("player1 is connected");

            inPlayer1 = new DataInputStream(talkingPlayer1.getInputStream());
            outPlayer1 = new DataOutputStream(talkingPlayer1.getOutputStream());

            outPlayer1.writeUTF("player1");

        } catch (Exception e)
        {
            Log("player1 error: " + e.getMessage());
            return;
        }

        ProcessPlayer processPlayer1 = new ProcessPlayer(inPlayer1, outPlayer1, controller, "player1");
        processPlayer1.start();

        try {
            talkingPlayer2 = listener.accept();
            Log("player2 is connected");

            inPlayer2 = new DataInputStream(talkingPlayer1.getInputStream());
            outPlayer2 = new DataOutputStream(talkingPlayer1.getOutputStream());

            outPlayer1.writeUTF("player2");

        } catch (Exception e)
        {
            Log("player2 error: " + e.getMessage());
            return;
        }

        ProcessPlayer processPlayer2 = new ProcessPlayer(inPlayer2, outPlayer2, controller, "player2");
        processPlayer2.start();
    }
}