package utils;

import exception.InvalidInputException;
import model.Command;
import server.ClientHandler;

public class ParseCommands {

    private final String prefix = "t";
    private final ClientHandler clientHandler;

    public ParseCommands() {
        this.clientHandler = null;
    }

    public ParseCommands(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public int parseUser(String username) throws InvalidInputException {
        if (username.equalsIgnoreCase("monitor")) { //is monitor
            return 0;
        } else { //is turnstile
            int id = parseID(removePrefix(username));
            for (ClientHandler clientHandler : clientHandler.getServer().getClientHandlers()) {
                if (clientHandler.getTurnstileID() == id) {
                    throw new InvalidInputException("Turnstile already exist");
                }
            }
            return id;
        }
    }

    public Command parseClientMessage(Command cmd) throws InvalidInputException {
        if (cmd.getSenderID() == 0) {  // is monitor
            if (cmd.getMessage().equalsIgnoreCase("help")) {
                return new Command("Type \"monitor\" to see all turnstile activity" + "\n"
                        + "Type \"monitor off\" to go back", cmd.getSenderID());
            }
            if (cmd.getMessage().equalsIgnoreCase("monitor")) {
                clientHandler.getServer().isMonitorOn();
                return new Command("", cmd.getSenderID());
            } else if (cmd.getMessage().equalsIgnoreCase("monitor off")) {
                clientHandler.getServer().isMonitorOff();
                return new Command("", cmd.getSenderID());
            } else {
                return new Command("Unknown command", cmd.getSenderID());
            }
        }
        if (cmd.getMessage().equalsIgnoreCase("help")) { // is turnstile
            return new Command("something", cmd.getSenderID());
        } else {
            int count = parseNumberOfVisitors(cmd.getMessage());
            clientHandler.getServer().addTotalVisitorCount(count);
            clientHandler.addVisitorCount(count);
            return new Command("Added " + count + " visitors to turnstile " + cmd.getSenderID(), cmd.getSenderID());
        }
    }

    private String removePrefix(String input) throws InvalidInputException {
        if (input.length() >= 2) {
            if (input.substring(0, 1).equalsIgnoreCase(prefix)) {
                return input.substring(1);
            }
        }
        throw new InvalidInputException("Invalid prefix: " + input);
    }

    private int parseID(String input) throws InvalidInputException {
        int id;
        try {
            id = Integer.parseInt(input);
            if (id < 1) {
                throw new InvalidInputException("ID must be a positive number");
            }
        } catch (NumberFormatException ex) {
            throw new InvalidInputException("Invalid ID: " + input);
        }
        return id;
    }

    private int parseNumberOfVisitors(String input) throws InvalidInputException {
        int count;
        try {
            count = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Invalid amount: " + input);
        }
        return count;
    }
}
