import java.applet.AppletStub;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.spec.RSAOtherPrimeInfo;

public class Client implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean running;


    public Client() {
        running = true;
    }



    @Override
    public void run() {
         try {
             client = new Socket("127.0.0.1", 8000);
             out = new PrintWriter(client.getOutputStream(), true);
             in = new BufferedReader(new InputStreamReader(client.getInputStream()));

             Inputandler inHandler = new Inputandler();
             Thread t = new Thread(inHandler);
             t.start();

             String inMessage;
             while ((inMessage = in.readLine()) != null){
                 System.out.println(inMessage);
             }

         } catch (IOException e){
             System.out.println(e);
         }
    }

    public void shutdown() {
        running = false;
        try {
            in.close();
            out.close();
            if (!client.isClosed()){
                client.close();
            }
        } catch (IOException e){

        }
    }


    class Inputandler implements Runnable {

        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (running){
                    String message = null;
                    message = inReader.readLine();
                    if (message.equals("/quit")) {
                        inReader.close();
                        shutdown();
                    }
                    else {
                        out.println(message);
                    }

                }
            } catch (IOException e){
                shutdown();
            }



        }
    }

    public static void main(String[] args) {
        Client client1 = new Client();
        client1.run();
    }

}
