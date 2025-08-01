import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class TcpClient {

    private static final int PORT = 5000;

    public static void main(String[] args){

        try(Socket socket = new Socket();){

            socket.connect(new InetSocketAddress(PORT));

            // get data from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // sending data to the client
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            Scanner scanner = new Scanner(System.in);

            Thread readingThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("Server: " + line);
                    }
                } catch (IOException e) {
                    System.out.println("Server disconnected.");
                }
            });

            Thread writingThread = new Thread(() -> {
                try {
                    while (true){
                        String msg =  scanner.nextLine();
                        System.out.println("message sent");
                        writer.write(msg + "\n");
                        writer.flush();
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            });

            readingThread.start();
            writingThread.start();

            readingThread.join();
            writingThread.join();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
