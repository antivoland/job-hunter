package antivoland.jh.linkedin;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

@Deprecated
public class Controller {
    enum Command {
        GRAB(1, "Iterate and grab search results"),
        SCROLL(2, "Scroll down"),
        QUIT(3, "Quit");

        Command(int no, String description) {
            this.no = no;
            this.description = description;
        }

        final int no;
        final String description;

        static Command get(int no) {
            for (Command command : values()) {
                if (command.no == no) return command;
            }
            return null;
        }
    }

    final Scanner in;
    final PrintStream out;

    public Controller() {
        this(System.in, System.out);
    }

    public Controller(InputStream in, PrintStream out) {
        this.in = new Scanner(in);
        this.out = out;
    }

    public void listen(Grabber grabber) {
        Command command;
        while ((command = readCommand()) != Command.QUIT) {
            switch (command) {
                case GRAB -> {
                    out.println("Grabbing...");
                   // grabber.grab();
                }
                case SCROLL -> {
                    out.println("Grabbing...");
                   // grabber.scroll();
                }
            }
        }
    }

    private Command readCommand() {
        Command command;
        do {
            printCommands();
        } while ((command = doReadCommand()) == null);
        return command;
    }

    private Command doReadCommand() {
        var input = in.nextLine();
        int commandNo;
        try {
            commandNo = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            out.printf("Illegal input '%s'%n", input);
            return null;
        }
        var command = Command.get(commandNo);
        if (command == null) {
            out.printf("Unknown command no %s%n", commandNo);
        }
        return command;
    }

    private void printCommands() {
        out.println("Available commands:");
        for (var command : Command.values()) {
            out.printf("%s. %s%n", command.no, command.description);
        }
    }
}