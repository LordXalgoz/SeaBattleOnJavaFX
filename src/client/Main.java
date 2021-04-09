package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Sea Battle Client");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }

    static void Log(String msg) {
        System.out.println(msg);
    }

    public static void main(String[] args) throws IOException {
        launch(args);

        System.in.read();

        Scanner scanner = new Scanner(System.in);

        Socket talking = null;
        DataInputStream in = null;
        DataOutputStream out = null;


        boolean isRun;

        try {
            talking = new Socket(InetAddress.getByName("127.0.0.1"), 37152);
            Log("server connect us");

            in = new DataInputStream(talking.getInputStream());
            out = new DataOutputStream(talking.getOutputStream());

            isRun = true;
        } catch (Exception e) {
            Log("server connection failed: " + e.getMessage());
            isRun = false;
            return;
        }

        while (isRun) {
            try {
                Log("input coordinate (to exit write /exit)");
                String request = scanner.nextLine();

                out.writeUTF(request);
                out.flush();

                String response = in.readUTF();
                Log("from server: " + response);

                if (request.equals("/exit")) {
                    isRun = false;
                }

            } catch (Exception e) {
                Log("server error: " + e.getMessage());
                isRun = false;
            }
        }

        talking.close();
        Log("client closed");
    }
}
