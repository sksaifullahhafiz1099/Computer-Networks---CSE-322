package main;


import util.NetworkUtil;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {

    private ServerSocket serverSocket;
    public HashMap<String, NetworkUtil> clientMap;

    List<String> userList = new ArrayList<String>();
    List<String> offlineUsers = new ArrayList<String>();
    List<String> chat = new ArrayList<String>();
    List<String> request =new ArrayList<>();

    Server() {
        clientMap = new HashMap<>();
        try {
            serverSocket = new ServerSocket(33333);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                serve(clientSocket);
            }
        } catch (Exception e) {
            System.out.println("Server starts:" + e);
        }
    }

    public void serve(Socket clientSocket) throws IOException, ClassNotFoundException, InterruptedException {
        NetworkUtil networkUtil = new NetworkUtil(clientSocket);
        String clientName = (String) networkUtil.read();
        System.out.println(clientName+" arrived");
        if(clientMap.containsKey(clientName)){
            System.out.println("client exists");
            Message message = new Message();
            message.setFrom("server");
            message.setTo(clientName);
            message.setText("Same username exists");
            networkUtil.write(message);
        }
        else {
            clientMap.put(clientName, networkUtil);
            userList.add(clientName);
            if(offlineUsers.contains(clientName))
                offlineUsers.remove(clientName);
            //creating client directory in server:
            File filePublic = new File("files/" + clientName + "/public");
            if (!filePublic.exists()) {
                if (filePublic.mkdirs()) {
                    System.out.println("Directory is created!");
                } else {
                    System.out.println("Failed to create directory!");
                }
            } else
                System.out.println("Directory already exists");

            File filePrivate = new File("files/" + clientName + "/private");
            if (!filePrivate.exists()) {
                if (filePrivate.mkdirs()) {
                    System.out.println("Directory is created!");
                } else {
                    System.out.println("Failed to create directory!");
                }
            } else
                System.out.println("Directory already exists");

            //opening server thread for this specific client:
            new ReadThreadServer(clientMap, networkUtil,userList,chat,offlineUsers,request);

        }
    }

    public static void main(String args[]) {
        new Server();
    }
}
