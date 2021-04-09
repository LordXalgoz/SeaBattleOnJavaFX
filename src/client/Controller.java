package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;

public class Controller {

    @FXML
    Canvas canvasField;

    private GraphicsContext gc = null;
    private ServerConnection serverConnection = null;
    private String sign = null;
    private String field = null;


    @FXML
    public void initialize() {
        gc = canvasField.getGraphicsContext2D();
        DrawGrid();
    }

    private void ShowDialog(String message) {
        new Alert(Alert.AlertType.CONFIRMATION,message).showAndWait();
    }

    private void DrawGrid() {
        gc.setLineWidth(2.0);

        double dy = canvasField.getHeight() / 10.0;
        double dx = canvasField.getWidth() / 10.0;
        double w = canvasField.getWidth();
        double h = canvasField.getHeight();

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

        /* //line x 1
        gc.moveTo(0, dy);
        gc.lineTo(w, dy);
        gc.stroke();

        //line x 2
        gc.moveTo(0, dy*2);
        gc.lineTo(w, dy*2);
        gc.stroke();

        //line x 3
        gc.moveTo(0, dy*3);
        gc.lineTo(w, dy*3);
        gc.stroke();

        //line x 4
        gc.moveTo(0, dy*4);
        gc.lineTo(w, dy*4);
        gc.stroke();

        //line x 5
        gc.moveTo(0, dy*5);
        gc.lineTo(w, dy*5);
        gc.stroke();

        //line x 6
        gc.moveTo(0, dy*6);
        gc.lineTo(w, dy*6);
        gc.stroke();

        //line x 7
        gc.moveTo(0, dy*7);
        gc.lineTo(w, dy*7);
        gc.stroke();

        //line x 8
        gc.moveTo(0, dy*8);
        gc.lineTo(w, dy*8);
        gc.stroke();

        //line x 9
        gc.moveTo(0, dy*9);
        gc.lineTo(w, dy*9);
        gc.stroke();

        //line y 1
        gc.moveTo(dx, 0);
        gc.lineTo(dx, h);
        gc.stroke();

        //line y 2
        gc.moveTo(dx*2, 0);
        gc.lineTo(dx*2, h);
        gc.stroke();

        //line y 3
        gc.moveTo(dx*3, 0);
        gc.lineTo(dx*3, h);
        gc.stroke();

        //line y 4
        gc.moveTo(dx*4, 0);
        gc.lineTo(dx*4, h);
        gc.stroke();

        //line y 5
        gc.moveTo(dx*5, 0);
        gc.lineTo(dx*5, h);
        gc.stroke();

        //line y 6
        gc.moveTo(dx*6, 0);
        gc.lineTo(dx*6, h);
        gc.stroke();

        //line y 7
        gc.moveTo(dx*7, 0);
        gc.lineTo(dx*7, h);
        gc.stroke();

        //line y 8
        gc.moveTo(dx*8, 0);
        gc.lineTo(dx*8, h);
        gc.stroke();

        //line y 9
        gc.moveTo(dx*9, 0);
        gc.lineTo(dx*9, h);
        gc.stroke(); */
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
