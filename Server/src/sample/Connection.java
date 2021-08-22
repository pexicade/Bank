package sample;

import java.io.*;
import java.net.Socket;

public class Connection {

    private Socket socket;
    private ObjectOutputStream objOutput;
    private ObjectInputStream objInput;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private boolean connected = false;

    public Connection(int port) throws IOException {
        socket = new Socket("localhost",port);
//        System.out.println("Connected");
        connected = true;
        setter();
    }

    public Connection(Socket socket) throws IOException{
        this.socket = socket;
        connected = true;
        setter();
    }

    private void setter() throws IOException{
//        System.out.println("Connected at: "+System.currentTimeMillis());
        objOutput = new ObjectOutputStream(socket.getOutputStream());
        objInput = new ObjectInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
    }

    public void connect(){

    }

    public void sendObject(Object object) throws IOException{
        objOutput.writeObject(object);
    }
    public void sendText(String text) throws IOException {
//        System.out.println("SEND TIME: "+System.currentTimeMillis());
        dataOutputStream.writeUTF(text);
    }

    public Object getObject() throws IOException, ClassNotFoundException {
        return objInput.readObject();
    }

    public String getText() throws IOException {
//        System.out.println("GET TIME: "+System.currentTimeMillis());
        return dataInputStream.readUTF();
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isConnected(){
        return connected;
    }
}

