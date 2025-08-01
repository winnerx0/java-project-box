import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TcpServer {

    private static final int PORT = 5000;

    public static void main(String[] args) throws IOException {

       try(ServerSocket serverSocket = new ServerSocket(PORT)){

           System.out.println("TCP Server running!");

           // waits for a socket to connect then does processing
           while (true){

               Socket socket = serverSocket.accept();

               // getting data from the client
               BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


               // sending data to the client
               PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

               new Thread(() -> {
                   try {
                       String line;
                       while ((line = reader.readLine()) != null) {
                           System.out.println("Client: " + line);
                       }
                   } catch (IOException e) {
                       System.out.println("Client disconnected.");
                   }
               }).start();

               Scanner scanner = new Scanner(System.in);

               new Thread(() -> {
                   while (true){
                       String msg = scanner.nextLine();
                       writer.println(msg);
                   }
               }).start();
           }


       } catch (IOException e) {
           e.printStackTrace();
       }
    }
}
