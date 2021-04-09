package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ServerConnection {

    private Socket talking = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    public ServerConnection() throws IOException {
        talking = new Socket(InetAddress.getByName("127.0.0.1"), 37152);

        in = new DataInputStream(talking.getInputStream());
        out = new DataOutputStream(talking.getOutputStream());
    }

    public String ReceiveResponseFromServer() throws IOException {
        return in.readUTF();
    }

    public void SendRequestToServer(String data) throws IOException {
        out.writeUTF(data);
    }

}
