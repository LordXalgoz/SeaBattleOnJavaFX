package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

import java.io.IOException;

class ProcessServer extends Thread {
    private Canvas canvasMyField, canvasShootField;
    private ServerConnection serverConnection;

    private GraphicsContext gc1,gc2;
    private String field = null;

    private String sign = null;

    private double dy1, dx1, w1, h1, dy2, dx2, w2, h2;
    private final char ATTACK = 'X';
    private final char SHIP = 'K';
    private final char MISS = 'M';

    private final String WIN_FIRST_PLAYER = "First player wins";
    private final String WIN_SECOND_PLAYER = "Second player wins";
    private final String CONTINUE_GAME = "Continue game";

    private void ShowDialog(String message) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                new Alert(Alert.AlertType.CONFIRMATION, message).showAndWait();
            }
        });
    }

    public ProcessServer(ServerConnection serverConnection, Canvas canvasMyField, Canvas canvasShootField) {
        this.canvasMyField = canvasMyField;
        this.canvasShootField = canvasShootField;
        this.serverConnection = serverConnection;

        gc1 = canvasMyField.getGraphicsContext2D();
        gc2 = canvasShootField.getGraphicsContext2D();

        dy1 = canvasMyField.getHeight() / 10;
        dx1 = canvasMyField.getWidth() / 10;
        w1 = canvasMyField.getWidth();
        h1 = canvasMyField.getHeight();

        dy2 = canvasShootField.getHeight() / 10;
        dx2 = canvasShootField.getWidth() / 10;
        w2 = canvasShootField.getWidth();
        h2 = canvasShootField.getHeight();


    }

    private void DrawGrid() {
        gc1.setLineWidth(2.0);
        gc2.setLineWidth(2.0);

        for (int i = 1; i < 10; i++)
        {
            gc1.moveTo(0, dy1*i);
            gc1.lineTo(w1, dy1*i);
            gc1.stroke();
        }

        for (int i = 1; i < 10; i++)
        {
            gc1.moveTo(dx1*i, 0);
            gc1.lineTo(dx1*i, h1);
            gc1.stroke();
        }

        for (int i = 1; i < 10; i++)
        {
            gc2.moveTo(0, dy2*i);
            gc2.lineTo(w2, dy2*i);
            gc2.stroke();
        }

        for (int i = 1; i < 10; i++)
        {
            gc2.moveTo(dx2*i, 0);
            gc2.lineTo(dx2*i, h2);
            gc2.stroke();
        }
    }

    private void DrawField() {
        String[] lines = field.split("\n");

        int fieldSize = 10;
        char[][] field = new char[fieldSize][fieldSize];

        for (int l = 0; l < fieldSize; l++)
        {
            for (int e = 0; e < fieldSize; e++)
            {
                field[l][e]=lines[l].charAt(e);
            }
        }

        gc1.setFont(new Font("Arial", dy1/2));
        gc2.setFont(new Font("Arial", dy2/2));

        for (int i = 0; i < fieldSize; i++)
        {
            for (int j = 0; j < fieldSize; j++)
            {
                if(field[i][j] == SHIP)
                {
                    gc1.fillText(Character.toString(SHIP),j*dx1+dx1/3, i*dy1+2*dy1/3);
                }
            }
        }

        for (int i = 0; i < fieldSize; i++)
        {
            for (int j = 0; j < fieldSize; j++)
            {
                if(field[i][j] == ATTACK)
                {
                    gc2.fillText(Character.toString(ATTACK),  j*dx2+dx2/3,i*dy2+2*dy2/3);
                }

                if(field[i][j] == MISS)
                {
                    gc2.fillText(Character.toString(MISS),j*dx2+dx2/3,i*dy2+2*dy2/3);
                }
            }
        }
    }

    public void run() {
        while (true) {
            try {
                field = serverConnection.ReceiveResponseFromServer();

                int currentStep = Integer.parseInt(serverConnection.ReceiveResponseFromServer());

                DrawGrid();
                DrawField();

                if(currentStep%2==1){
                    canvasShootField.setDisable(false);
                }

                if(currentStep%2==0){
                    canvasShootField.setDisable(false);
                }


                serverConnection.SendRequestToServer("gameresult|9|9");
                String gameResult = serverConnection.ReceiveResponseFromServer();

                if(gameResult.equals(CONTINUE_GAME)==false){
                    canvasShootField.setDisable(true);

                    switch (gameResult)
                    {
                        case WIN_FIRST_PLAYER:
                            ShowDialog("Победил первый игрок");
                            break;
                        case WIN_SECOND_PLAYER:
                            ShowDialog("Победил второй игрок");
                            break;
                    }

                    break;
                }


                Thread.sleep(500);

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}

public class Controller {

    @FXML
    Canvas canvasMyField, canvasShootField;

    @FXML
    Button btnConnect;

    private ServerConnection serverConnection = null;
    private String player = null;



    @FXML
    public void initialize() {

    }

    private void ShowDialog(String message) {
        new Alert(Alert.AlertType.CONFIRMATION,message).showAndWait();
    }

    public void btnConnectClick(ActionEvent actionEvent) {
        try{
            serverConnection = new ServerConnection();
            player = serverConnection.ReceiveResponseFromServer();

            ShowDialog("Ожидаем 2-го игрока");

            btnConnect.setDisable(true);

            ProcessServer processServer = new ProcessServer(serverConnection, canvasMyField, canvasShootField);
            processServer.start();
        }catch (Exception e){
            ShowDialog(e.getMessage());
        }
    }


    public void canvasShootFieldClicked(MouseEvent mouseEvent) {

        try {
            int j = (int) ((mouseEvent.getSceneX() - canvasShootField.getLayoutX()) / canvasShootField.getWidth() / 10);
            int i = (int) ((mouseEvent.getSceneY() - canvasShootField.getLayoutY()) / canvasShootField.getHeight() / 10);

            serverConnection.SendRequestToServer(i + "|" + j);

            String setPlayerResult = serverConnection.ReceiveResponseFromServer();

            if(setPlayerResult.equals("error")==true){
                ShowDialog("Неверный ход походите ещё");
            }
        } catch (Exception e) {
            ShowDialog(e.getMessage());
        }
    }
}