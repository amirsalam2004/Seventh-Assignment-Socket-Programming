package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket client;
    private static String messages;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    public ClientHandler(Socket client) throws IOException {
        this.client = client;
        this.in = new DataInputStream(client.getInputStream());
        this.out = new DataOutputStream(client.getOutputStream());
        this.username= in.readUTF();
        this.messages="";
    }
    @Override
    public void run() {
        try {
            String request;
            while (true) {
                request = this.in.readUTF();
                if (request != null) {
                    // S -> Send
                    if (request.startsWith("S")) {
                        String[] x = request.split(" ", 2);
                        messages +=username+" : "+ x[1] + "\n";
                    }
                    // R -> Read
                    if (request.startsWith("R")){
                        out.writeUTF(messages);
                        out.flush();
                    }
                    // SF -> See Files
                    if (request.startsWith("SF")){
                        out.writeUTF(getFileNames());
                    }
                    // D -> Download
                    if (request.startsWith("D")){
                        String[] x = request.split(" ", 2);
                        sendFile(x[1]);
                    }
                    // E -> Exit
                    if (request.startsWith("E")){
                        closeAll();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            // Handle any I/O exceptions that occur during communication with the client
            System.err.println("IO Exception in client handler!!!!!!");
            e.printStackTrace();
        } finally {
            try {
                // Close input and output streams and the client socket when done
                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getFileNames(){
        File[] files = new File("data").listFiles();
        if (files == null) {
            return("");
        }
        String fileNames="";
        for (File file : files) {
            try {
                fileNames+=file.getName()+"@";
            } catch (Exception e) {
                closeAll();
            }
        }
        return(fileNames.substring(0,fileNames.length()-1));
    }
    public void sendFile(String fileName) throws IOException{
        File[] files = new File("data").listFiles();
        assert files != null;
        int bytes;
        File file=null;
        for (File f : files) {
            if(f.getName().equals(fileName)){
                file=f;
            }
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        out.writeInt((int) file.length());
        out.flush();
        byte[] buffer = new byte[4 * 1024];
        while ((bytes = fileInputStream.read(buffer)) != -1) {
            out.write(buffer, 0, bytes);
            out.flush();
        }
        fileInputStream.close();
    }
    public void closeAll() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
