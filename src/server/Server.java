package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import model.Command;

public class Server {

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private int visitorCount = 0;
    private boolean isMonitor = false;

    private List<ClientHandler> clientHandlerList = new CopyOnWriteArrayList(); //brug concurrent list

    public static void stopServer() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
        }
        keepRunning = false;
    }

    private void initClientHandler(Socket socket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(socket, this);
        clientHandler.start();
    }

    private void runServer() {
        int port = 9090;
        String ip = "localhost";
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            do {
                Socket socket = serverSocket.accept(); //Important Blocking call
                initClientHandler(socket);
            } while (keepRunning);
            System.out.println("Stopping server...");
        } catch (IOException ex) {
        }
    }

    public void addHandler(ClientHandler clienthandler) {
        clientHandlerList.add(clienthandler);
    }

    public void removeHandler(ClientHandler ch) {
        clientHandlerList.remove(ch);
    }

    public void send(Command cmd) {
        for (ClientHandler clientHandler : clientHandlerList) {
            if (clientHandler.getTurnstileID() == cmd.getSenderID()) {
                clientHandler.send(cmd.getMessage());
            }
            if (clientHandler.getTurnstileID() == 0) {
                if (isMonitor) {
                    StringBuilder sb = new StringBuilder();
                    for (ClientHandler ch : clientHandlerList) {
                        if (ch.getTurnstileID() != 0) {
                            sb.append("Turnstile ").append(ch.getTurnstileID()).append(": ").append(ch.getVisitorCount());
                            sb.append("\n");
                        }
                    }
                    sb.append("Total visitors: " + visitorCount);
                    sb.append("\n");
                    clientHandler.send(sb.toString());
                } else {
                    clientHandler.send("Total visitors: " + visitorCount);
                }
            }
        }
    }

    public int getTotalVisitorCount() {
        return visitorCount;
    }

    public synchronized void addTotalVisitorCount(int input) {
        visitorCount += input;
    }

    public List<ClientHandler> getClientHandlers() {
        return clientHandlerList;
    }

    public void isMonitorOn() {
        isMonitor = true;
    }

    public void isMonitorOff() {
        isMonitor = false;
    }

    public static void main(String[] args) {
        try {
            new Server().runServer();
        } finally {
        }
    }
}
