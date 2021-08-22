package sample;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<Integer> sessionIDs = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            Socket client;
            while(true){
                try{
                    synchronized (serverSocket){
                        System.out.println("Waiting for a new client");
                        client = serverSocket.accept();
                    }
                    int id = (int)(Math.random()*10000)+5000;
                    sessionIDs.add(id);
                    Thread thread = new ClientHandler(client,id);
                    System.out.println("**Connected** "+id);
                    thread.start();
                }catch(IOException e){
                    serverSocket.close();
                    System.out.println("couldn't create server socket :"+e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("ERR: "+e.getMessage());
//            e.printStackTrace();
        }
    }

    public static void fileSaver(String data,String filepath, boolean append) throws IOException{
        PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filepath,append)));
        printWriter.write(data);
        printWriter.close();
    }
}
