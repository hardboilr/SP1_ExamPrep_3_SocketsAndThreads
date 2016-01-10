package server;

import exception.InvalidInputException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Command;
import utils.ParseCommands;

/**
 * @author Tobias Jacobsen
 */
public class ClientHandler extends Thread {

    private final BufferedReader input;
    private final PrintWriter writer;
    private final Socket socket;
    private final Server server;
    private int turnstileID;
    private int visitorCount;
    private boolean isLoggedIn;
    private final ParseCommands parseCmd;

    public ClientHandler(Socket socket, Server server) throws IOException {
        isLoggedIn = false;
        this.socket = socket;
        this.server = server;
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        parseCmd = new ParseCommands(this);
    }

    public void send(String message) {
        writer.println(message);
    }

    @Override
    public void run() {
        try {
            String msg = input.readLine(); //IMPORTANT blocking call
            while (!msg.equalsIgnoreCase("stop")) {
                if (!isLoggedIn) {
                    try {
                        turnstileID = parseCmd.parseUser(msg);
                        send("Succesfully logged in\nType \"help\" to see available commands");
                        isLoggedIn = true;
                        server.addHandler(this);
                    } catch (InvalidInputException ex) {
                        send(ex.getMessage());
                    }
                } else {
                    try {
                        Command cmd = new Command(msg, turnstileID);
                        server.send(parseCmd.parseClientMessage(cmd));
                    } catch (InvalidInputException ex) {
                        send(ex.getMessage());
                    }
                }
                msg = input.readLine(); //IMPORTANT blocking call
            }
            send("Logging off...");//Echo the stop message back to the client for a nice closedown
            socket.close();
            server.removeHandler(this);
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Server getServer() {
        return server;
    }

    public int getTurnstileID() {
        return turnstileID;
    }

    public int getVisitorCount() {
        return visitorCount;
    }

    public void addVisitorCount(int count) {
        visitorCount += count;
    }
}
