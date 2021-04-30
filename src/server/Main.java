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
        boolean doing = true;
        while(doing == true){
            try
            {
                String request = inPlayer.readUTF();
                String response;

                String[] params = request.split("\\|");

                String command = params[0];
                int i = Integer.parseInt(params[1]);
                int j = Integer.parseInt(params[2]);

                switch (command) {
                    case "getMyFieldPlayer1":
                        Log("from " + playerName + ": " + request);

                        response = controller.GetFirstPlayerMyField();

                        outPlayer.writeUTF(response);

                        Log("to " + playerName + ":\n" + response);
                        break;

                    case "getMyFieldPlayer2":
                        Log("from " + playerName + ": " + request);

                        response = controller.GetSecondPlayerMyField();

                        outPlayer.writeUTF(response);

                        Log("to " + playerName + ":\n" + response);
                        break;

                    case "getShootFieldPlayer1":
                        Log("from " + playerName + ": " + request);

                        response = controller.GetFirstPlayerShootField();

                        outPlayer.writeUTF(response);

                        Log("to " + playerName + ":\n" + response);
                        break;

                    case "getShootFieldPlayer2":
                        Log("from " + playerName + ": " + request);

                        response = controller.GetSecondPlayerShootField();

                        outPlayer.writeUTF(response);

                        Log("to " + playerName + ":\n" + response);
                        break;

                    case "shootPlayer1":
                        Log("from " + playerName + ": " + request);

                        boolean shootResultPlayer1 = controller.FirstPlayerShootToSecondPlayer(i,j);

                        response = shootResultPlayer1 == true ? "ok" : "error";

                        outPlayer.writeUTF(response);

                        Log("to " + playerName + ":" + response);
                        break;

                    case "shootPlayer2":
                        Log("from " + playerName + ": " + request);

                        boolean shootResultPlayer2 = controller.SecondPlayerShootToFirstPlayer(i,j);

                        response = shootResultPlayer2 == true ? "ok" : "error";

                        outPlayer.writeUTF(response);

                        Log("to " + playerName + ":" + response);
                        break;

                    case "currentStep":
                        Log("from " + playerName + ": " + request);

                        response = Integer.toString(controller.GetCurrentStep());

                        outPlayer.writeUTF(response);

                        Log("to " + playerName + ":" + response);
                        break;

                    case "gameResult":
                        Log("from " + playerName + ": " + request);

                        response = controller.GetGameResult();


                        outPlayer.writeUTF(response);

                        Log("to " + playerName + ":" + response);

                        if(response.equals("Continue")==false){
                            doing=false;
                        }

                        break;
                }
            }
            catch (IOException e)
            {
                Log("some error: "+ e.getMessage());
                doing=false;
            }
        }
    }
}

public class Main
{
    static void Log(String msg) {
        System.out.println(msg);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        while(true) {

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
            DataOutputStream outPlayer2 = null;

            try {
                talkingPlayer1 = listener.accept();
                Log("player1 is connected");

                inPlayer1 = new DataInputStream(talkingPlayer1.getInputStream());
                outPlayer1 = new DataOutputStream(talkingPlayer1.getOutputStream());

                outPlayer1.writeUTF("Player1");

            } catch (Exception e) {
                Log("player1 error: " + e.getMessage());
                return;
            }

            ProcessPlayer processPlayer1 = new ProcessPlayer(inPlayer1, outPlayer1, controller, "player1");
            processPlayer1.start();

            try {
                talkingPlayer2 = listener.accept();
                Log("player2 is connected");

                inPlayer2 = new DataInputStream(talkingPlayer2.getInputStream());
                outPlayer2 = new DataOutputStream(talkingPlayer2.getOutputStream());

                outPlayer2.writeUTF("Player2");
            } catch (Exception e) {
                Log("player2 error: " + e.getMessage());
                return;
            }

            ProcessPlayer processPlayer2 = new ProcessPlayer(inPlayer2, outPlayer2, controller, "player2");
            processPlayer2.start();

            processPlayer1.join();
            processPlayer2.join();

            listener.close();
        }
    }
}