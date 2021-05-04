package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

import java.io.IOException;

class ProcessServer extends Thread {
    private Canvas canvasMyField, canvasShootField;
    private Button btnConnect;
    Label labelAlive, labelKill;

    private ServerConnection serverConnection;

    private GraphicsContext gcMf, gcSf;
    private String myField = null;
    private String shootField = null;
    private String player = null;

    private double dy1, dx1, w1, h1, dy2, dx2, w2, h2;

    private final char EMPTY = '*';
    private final char DEAD = 'X';
    private final char SHIP = 'K';
    private final char MISS = 'M';

    private final String WIN_FIRST_PLAYER = "First player wins";
    private final String WIN_SECOND_PLAYER = "Second player wins";
    private final String CONTINUE_GAME = "Continue";

    private void ShowDialog(String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                new Alert(Alert.AlertType.CONFIRMATION, message).showAndWait();
            }
        });
    }

    public ProcessServer(ServerConnection serverConnection, Canvas canvasMyField, Canvas canvasShootField, String player, Button btnConnect, Label labelAlive, Label labelKill) {
        this.canvasMyField = canvasMyField;
        this.canvasShootField = canvasShootField;
        this.serverConnection = serverConnection;
        this.btnConnect = btnConnect;
        this.labelAlive = labelAlive;
        this.labelKill = labelKill;
        this.player = player;


        gcMf = canvasMyField.getGraphicsContext2D();
        gcSf = canvasShootField.getGraphicsContext2D();

        dy1 = canvasMyField.getHeight() / 10;
        dx1 = canvasMyField.getWidth() / 10;
        w1 = canvasMyField.getWidth();
        h1 = canvasMyField.getHeight();

        dy2 = canvasShootField.getHeight() / 10;
        dx2 = canvasShootField.getWidth() / 10;
        w2 = canvasShootField.getWidth();
        h2 = canvasShootField.getHeight();
    }

    private void DrawGridMyField() {
        gcMf.setLineWidth(2.0);

        for (int i = 1; i < 10; i++) {
            gcMf.moveTo(0, dy1 * i);
            gcMf.lineTo(w1, dy1 * i);
            gcMf.stroke();
        }

        for (int i = 1; i < 10; i++) {
            gcMf.moveTo(dx1 * i, 0);
            gcMf.lineTo(dx1 * i, h1);
            gcMf.stroke();
        }
    }

    private void DrawGridShootField() {
        gcSf.setLineWidth(2.0);

        for (int i = 1; i < 10; i++) {
            gcSf.moveTo(0, dy2 * i);
            gcSf.lineTo(w2, dy2 * i);
            gcSf.stroke();
        }

        for (int i = 1; i < 10; i++) {
            gcSf.moveTo(dx2 * i, 0);
            gcSf.lineTo(dx2 * i, h2);
            gcSf.stroke();
        }
    }

    private void DrawMyField() {
        String[] lines = myField.split("\n");

        int fieldSize = 10;
        char[][] field = new char[fieldSize][fieldSize];

        for (int l = 0; l < fieldSize; l++) {
            for (int e = 0; e < fieldSize; e++) {
                field[l][e] = lines[l].charAt(e);
            }
        }

        gcMf.setFont(new Font("Arial", dy1 / 2));

        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (field[i][j] == SHIP) {
                    gcMf.fillText(Character.toString(SHIP), j * dx1 + dx1 / 3, i * dy1 + 2 * dy1 / 3);
                }

                if (field[i][j] == DEAD) {
                    gcMf.fillText(Character.toString(DEAD), j * dx1 + dx1 / 3, i * dy1 + 2 * dy1 / 3);
                }

                if (field[i][j] == MISS) {
                    gcMf.fillText(Character.toString(MISS), j * dx1 + dx1 / 3, i * dy1 + 2 * dy1 / 3);
                }
            }
        }
    }

    private void DrawShootField() {
        String[] lines = shootField.split("\n");

        int fieldSize = 10;
        char[][] field = new char[fieldSize][fieldSize];

        for (int l = 0; l < fieldSize; l++) {
            for (int e = 0; e < fieldSize; e++) {
                field[l][e] = lines[l].charAt(e);
            }
        }

        gcSf.setFont(new Font("Arial", dy2 / 2));

        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (field[i][j] == DEAD) {
                    gcSf.fillText(Character.toString(DEAD), j * dx2 + dx2 / 3, i * dy2 + 2 * dy2 / 3);
                }

                if (field[i][j] == MISS) {
                    gcSf.fillText(Character.toString(MISS), j * dx2 + dx2 / 3, i * dy2 + 2 * dy2 / 3);
                }
            }
        }
    }

    public void run() {
        boolean doing = true;
        while (doing == true) {
            try {
                serverConnection.SendRequestToServer("getMyField"+player+"|9|9");
                myField = serverConnection.ReceiveResponseFromServer();

                serverConnection.SendRequestToServer("getShootField"+player+"|9|9");
                shootField = serverConnection.ReceiveResponseFromServer();

                gcMf.clearRect(0,0,canvasMyField.getWidth(),canvasMyField.getHeight());
                gcSf.clearRect(0,0,canvasShootField.getWidth(),canvasShootField.getHeight());

                DrawGridMyField();
                DrawGridShootField();
                DrawMyField();
                DrawShootField();

                serverConnection.SendRequestToServer("currentStep|9|9");
                int currentStep = Integer.parseInt(serverConnection.ReceiveResponseFromServer());

                if(player.equals("Player1")==true && currentStep%2==1){
                    canvasShootField.setDisable(false);
                }

                if(player.equals("Player2")==true && currentStep%2==0){
                    canvasShootField.setDisable(false);
                }

                if(player.equals("Player1")==true){
                    serverConnection.SendRequestToServer("getAliveFirstPlayer|9|9");
                    String aliveShips = serverConnection.ReceiveResponseFromServer();

                    serverConnection.SendRequestToServer("getKillFirstPlayer|9|9");
                    String killShips = serverConnection.ReceiveResponseFromServer();


                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            labelAlive.setText("Осталось живых кораблей: "+aliveShips);
                            labelKill.setText("Кораблей убито: "+killShips);
                        }
                    });

                }

                if(player.equals("Player2")==true){
                    serverConnection.SendRequestToServer("getAliveSecondPlayer|9|9");
                    String aliveShips = serverConnection.ReceiveResponseFromServer();

                    serverConnection.SendRequestToServer("getKillSecondPlayer|9|9");
                    String killShips = serverConnection.ReceiveResponseFromServer();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            labelAlive.setText("Осталось живых кораблей: "+aliveShips);
                            labelKill.setText("Кораблей убито: "+killShips);
                        }
                    });
                }

                serverConnection.SendRequestToServer("gameResult|9|9");
                String gameResult = serverConnection.ReceiveResponseFromServer();

                if(gameResult.equals(CONTINUE_GAME)==false){

                    switch (gameResult)
                    {
                        case WIN_FIRST_PLAYER:
                            ShowDialog("Победил Player1");
                            break;
                        case WIN_SECOND_PLAYER:
                            ShowDialog("Победил Player2");
                            break;
                    }

                    doing=false;
                    canvasShootField.setDisable(true);
                    btnConnect.setDisable(false);
                }

                Thread.sleep(500);

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                doing=false;
            }
        }
    }
}

public class Controller {

    @FXML
    Canvas canvasMyField, canvasShootField;

    @FXML
    Button btnConnect;

    @FXML
    Label labelPlayer, labelAlive, labelKill;


    private ServerConnection serverConnection = null;
    private String player = null;

    @FXML
    public void initialize() {
        canvasMyField.setDisable(true);
        canvasShootField.setDisable(true);
    }

    private void ShowDialog(String message) {
        new Alert(Alert.AlertType.CONFIRMATION, message).showAndWait();
    }

    public void btnConnectClick(ActionEvent actionEvent) {
        try {
            serverConnection = new ServerConnection();
            player = serverConnection.ReceiveResponseFromServer();

            if (player.equals("Player1") == true) {
                ShowDialog("Вы играете за Player1 - ожидаем подключение другого игрока");
                canvasShootField.setDisable(false);

                labelPlayer.setText("Вы играете за: " + player);
            }

            if (player.equals("Player2") == true) {
                ShowDialog("Вы играете за Player2");

                labelPlayer.setText("Вы играете за: " + player);
            }

            btnConnect.setDisable(true);

            ProcessServer processServer = new ProcessServer(serverConnection, canvasMyField, canvasShootField, player, btnConnect, labelAlive, labelKill);
            processServer.start();

        } catch (Exception e) {
            ShowDialog(e.getMessage());
        }
    }


    public void canvasShootFieldClicked(MouseEvent mouseEvent) {

        try {
            int j = (int) ((mouseEvent.getSceneX() - canvasShootField.getLayoutX()) / (canvasShootField.getWidth() / 10.0));
            int i = (int) ((mouseEvent.getSceneY() - canvasShootField.getLayoutY()) / (canvasShootField.getHeight() / 10.0));

            serverConnection.SendRequestToServer("shoot"+player+"|" + i + "|" + j);

            String shootResult = serverConnection.ReceiveResponseFromServer();

            if (shootResult.equals("error") == true) {
                ShowDialog("Неверный ход походите ещё");
            }else{
                canvasShootField.setDisable(true);
            }

        } catch (Exception e) {
            ShowDialog(e.getMessage());
        }
    }
}