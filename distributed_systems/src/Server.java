import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private ArrayList<ConnectionHandler> connections;
    private boolean running;
    private ServerSocket server;
    private ExecutorService pool;

    public Server() {
        connections = new ArrayList<>();
        running = true;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(8000);
            pool = Executors.newCachedThreadPool();
            while (running){
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            }


        } catch (IOException e) {
            shutdown();
        }

    }

    public void broadcase(String message){
        for (ConnectionHandler ch: connections){
            if (ch != null){
                ch.sendMessage(message);
            }
        }
    }
    private void shutdown() {
        try {
            running = false;
            if (!server.isClosed()){
                server.close();
            }
            for (ConnectionHandler ch: connections){
                if (ch != null){
                    ch.shutdown();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    class ConnectionHandler implements Runnable {
        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String name;
        public ConnectionHandler(Socket client) {
            this.client = client;
        }


        @Override
        public void run() {
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println(("Enter Name"));
                name = in.readLine();
                System.out.println(name + " Connected");
                broadcase(name + " joined the chat!");
                String message;
                while ((message = in.readLine()) != null){
                    if (message.startsWith("/quit")){

                    }
                    else {
                        broadcase(name + ": " + message);
                    }
                }
            } catch (IOException e){
                shutdown();
            }
        }

        public void sendMessage(String message){
            out.println(message);
        }

        public void shutdown() {
            try{
                in.close();
                out.close();
                if (!client.isClosed()){
                    client.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
