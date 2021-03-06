package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

public class Controller {

    @FXML
    Canvas canvasMyField, canvasShootField;

    @FXML
    Button btnConnect;

    private GraphicsContext gc1 = null;
    private GraphicsContext gc2 = null;
    private ServerConnection serverConnection = null;
    private String sign = null;
    private String field = null;

    private double dy1, dx1, w1, h1, dy2, dx2, w2, h2;
    private final char ATTACK = 'X';
    private final char SHIP = 'K';
    private final char MISS = 'O';

    private final String WIN_FIRST_PLAYER = "First player wins";
    private final String WIN_SECOND_PLAYER = "Second player wins";
    private final String CONTINUE_GAME = "Continue game";

    @FXML
    public void initialize() {
        gc1 = canvasMyField.getGraphicsContext2D();
        gc2 = canvasShootField.getGraphicsContext2D();

        dy1 = canvasMyField.getHeight() / 10;
        dx1 = canvasMyField.getHeight() / 10;
        w1 = canvasMyField.getWidth();
        h1 = canvasMyField.getHeight();

        dy2 = canvasShootField.getHeight() / 10;
        dx2 = canvasShootField.getHeight() / 10;
        w2 = canvasShootField.getWidth();
        h2 = canvasShootField.getHeight();

        DrawGrid();
    }

    private void ShowDialog(String message) {
        new Alert(Alert.AlertType.CONFIRMATION,message).showAndWait();
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

    private void DrawField()
    {
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

    public void btnConnectClick(ActionEvent actionEvent) {
        try{
            serverConnection = new ServerConnection();

            ShowDialog("?????????????? 2-???? ????????????");

            field = serverConnection.ReceiveResponseFromServer();
            DrawField();

            btnConnect.setDisable(true);
        }catch (Exception e){
            ShowDialog(e.getMessage());
        }
    }


    public void canvasShootFieldClicked(MouseEvent mouseEvent) {

        try {
            int j = (int) ((mouseEvent.getSceneX() - canvasShootField.getLayoutX()) / dx2);
            int i = (int) ((mouseEvent.getSceneY() - canvasShootField.getLayoutY()) / dy2);

            serverConnection.SendRequestToServer(i + "|" + j);

            String setSignResult = serverConnection.ReceiveResponseFromServer();

            if(setSignResult.equals("ok")==true){

                field = serverConnection.ReceiveResponseFromServer();
                DrawField();

                String gameResult = serverConnection.ReceiveResponseFromServer();

                if(gameResult.equals(CONTINUE_GAME)==false){
                    ShowDialog(gameResult);
                }

                field = serverConnection.ReceiveResponseFromServer();
                DrawField();
            }
            else if(setSignResult.equals("error")==true){
                ShowDialog("???????????????? ?????? ???????????????? ??????");
            }
        } catch (Exception e) {
            ShowDialog(e.getMessage());
        }
    }
}
