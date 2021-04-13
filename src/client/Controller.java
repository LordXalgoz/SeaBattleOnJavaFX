package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.text.Font;

public class Controller {

    @FXML
    Canvas canvasField, canvasField2;

    private GraphicsContext gc = null;
    private ServerConnection serverConnection = null;
    private String sign = null;
    private String field = null;

    private double dy, dx, w, h;
    private final char ATTACK = 'X';
    private final char SHIP = 'K';
    private final char MISS = 'O';

    private final String WIN_FIRST_PLAYER = "First player wins";
    private final String WIN_SECOND_PLAYER = "Second player wins";
    private final String CONTINUE_GAME = "Continue game";

    @FXML
    public void initialize() {
        gc = canvasField.getGraphicsContext2D();

        dy = canvasField.getHeight() / 10;
        dx = canvasField.getHeight() / 10;
        w = canvasField.getWidth();
        h = canvasField.getHeight();

        DrawGrid();
    }

    private void ShowDialog(String message) {
        new Alert(Alert.AlertType.CONFIRMATION,message).showAndWait();
    }

    private void DrawGrid() {
        gc.setLineWidth(2.0);

        for (int i = 1; i < 10; i++)
        {
            gc.moveTo(0, dy*i);
            gc.lineTo(w, dy*i);
            gc.stroke();
        }

        for (int i = 1; i < 10; i++)
        {
            gc.moveTo(dx*i, 0);
            gc.lineTo(dx*i, h);
            gc.stroke();
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

        gc.setFont(new Font("Arial", dy / 2));

        for (int i = 0; i < fieldSize; i++)
        {
            for (int j = 0; j < fieldSize; j++)
            {
                if(field[i][j] == ATTACK)
                {
                    ShowDialog("ATTACK");
                    gc.fillText(Character.toString(ATTACK),  j * dx + dx / 10, i * dy + 2 * dy / 10);
                }

                if(field[i][j] == MISS)
                {
                    ShowDialog("MISS");
                    gc.fillText(Character.toString(MISS),  j * dx + dx / 10, i * dy + 2 * dy / 10);
                }
            }
        }
    }

    public void btnConnectClick(ActionEvent actionEvent) {
        try{
            serverConnection = new ServerConnection();
            sign = serverConnection.ReceiveResponseFromServer();

            ShowDialog("Ожидаем 2-го игрока");

            field = serverConnection.ReceiveResponseFromServer();

            ShowDialog(field);

        }catch (Exception e){
            ShowDialog(e.getMessage());
        }
    }
}
