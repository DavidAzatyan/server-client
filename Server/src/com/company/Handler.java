package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Handler implements Runnable{
    private Socket socket;
    private List<Handler> list;
    private DataInputStream objectInputStream;
    private DataOutputStream objectOutputStream;
    private SimpleDateFormat date = new SimpleDateFormat( "HH:mm:ss");
    private String clientName;
    private boolean flag=true;

    Handler(Socket socket, List<Handler> list){
        this.socket = socket;
        this.list = list;
        this.list.add(this);
        try {
            objectInputStream = new DataInputStream(socket.getInputStream());
            objectOutputStream = new DataOutputStream(socket.getOutputStream());
            clientName = objectInputStream.readUTF();
            System.out.println("[" + date.format(new Date()) + "] " + clientName + ": Join this server");
            send("[" + date.format(new Date()) + "]" + clientName + "join to Server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(String s) throws IOException{
        list.forEach((handler)-> {
            try {
                if(handler.socket!=socket) {
                    handler.objectOutputStream.writeUTF(s);
                    handler.objectOutputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        if(s.contains("GET")){
            objectOutputStream.writeUTF("Get query");
        }
    }

    public void receive(){
        while (flag){
            try {
                String answer = (String)objectInputStream.readUTF();

                if(!answer.equalsIgnoreCase("bye")){
                    System.out.println("[" + date.format(new Date()) + "] "+ ": " + answer);
                    send("[" + date.format(new Date()) + "]" +": " + answer);
                }
                else{
                    System.out.println("[" + date.format(new Date()) + "] ");
                    send("[" + date.format(new Date()) + "] ");
                    objectOutputStream.flush();
                    list.remove(this);
                    flag=false;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void run() {
        receive();
    }
}
