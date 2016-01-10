package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import utils.ParseCommands;

/**
 * @author Tobias Jacobsen
 */
public class Client implements Runnable {

    Socket socket;
    private int port;
    private InetAddress serverAddress;
    private BufferedReader input;
    private PrintWriter output;
    private String msg = "";
    private ParseCommands parseCmd;
    private List<ObserverInterface> observers;

    public Client() {
        parseCmd = new ParseCommands();
        observers = new ArrayList<>();
    }

    public void connect(String address, int port) throws UnknownHostException, IOException {
        this.port = port;
        serverAddress = InetAddress.getByName(address);
        socket = new Socket(serverAddress, port);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
        run();
    }

    public void send(String msg) {
        output.println(msg);
    }

    public void disconnect() {
        output.println("stop");
    }

    public void addObserver(ObserverInterface observer) {
        observers.add(observer);
    }

    public void notifyObservers(String msg) {
        for (ObserverInterface observerinterface : observers) {
            observerinterface.updateMessages(msg);
        }
    }

    @Override
    public void run() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> map = new HashMap();
                while (true) {
                    try {
                        msg = input.readLine();
                        if (msg.equals("stop")) {
                            try {
                                socket.close();
                            } catch (IOException ex) {
                                System.out.println(ex.getMessage());
                            }
                        } else {
                            notifyObservers(msg);
                        }
                    } catch (IOException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                }
            }
        });
        t.start();
    }

    public BufferedReader getInput() {
        return input;
    }
}
