package Client;
import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 3000;
    public static void main(String[] args) throws IOException {
        Socket client = new Socket(SERVER_IP, SERVER_PORT);
        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        DataInputStream in= new DataInputStream(client.getInputStream());
        String username;
        username = JOptionPane.showInputDialog(null, "Welcome\nEnter your name", "Hello", JOptionPane.QUESTION_MESSAGE);
        out.writeUTF(username);
        out.flush();
        while (true) {
            Object[] options={"See chat and Send message","Download file","Exit"};
            Object s=JOptionPane.showInputDialog(null,"Choose an option",
                    "Information Options",JOptionPane.INFORMATION_MESSAGE,null,options,options[0]);
            String selection=s.toString();
            if(s.equals("See chat and Send message")){
                try {
                    //R -> Read
                    out.writeUTF("R ");
                    out.flush();
                    String message = in.readUTF();
                    String newMessage = JOptionPane.showInputDialog(null, message + "\n\nENTER NEW MESSAGE", "CHAT", JOptionPane.QUESTION_MESSAGE);
                    //S -> Send
                    out.writeUTF("S " + newMessage);
                    out.flush();
                }
                catch (Exception E){
                    closeAll(in,out,client);
                }
            }
            if(s.equals("Download file")){
                try {
                    out.writeUTF("SF ");
                    out.flush();
                    String[] Names = in.readUTF().split("@", 0);
                    Object[] ops = Names;
                    Object o = JOptionPane.showInputDialog(null, "Choose an file",
                            "SELECT FILE", JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                    String fileName = s.toString();
                    out.writeUTF("D " + fileName);
                    downloadFile(in, out, client, fileName);
                }
                catch (Exception e){
                    closeAll(in,out,client);
                }
            }
            if(s.equals(("Exit"))){
                out.writeUTF("E ");
                closeAll(in,out,client);
                break;
            }
        }
    }
    public static void downloadFile(DataInputStream in,DataOutputStream out,Socket client,String fileName){
        try {
            int bytes;
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            int size = in.readInt();
            byte[] buffer = new byte[4 * 1024];
            while (size > 0 && (bytes = in.read(buffer, 0, Math.min(buffer.length, size))) != -1) {
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes;
            }
            fileOutputStream.close();
            JOptionPane.showMessageDialog(null,"The file was downloaded successfully!");
        } catch (IOException e) {
            closeAll(in,out,client);
        }
    }
    public static void closeAll(DataInputStream in,DataOutputStream out,Socket client) {
        try {
            if (in != null) {
                in.close();
            }
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
