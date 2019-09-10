package Server;

import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Integer.parseInt;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author mina2
 */
public class ServerThread implements Runnable {

    // All client names, so we can check for duplicates upon registration.
    private static Set<String> names = new HashSet<>();

    // The set of all the print writers for all the clients, used for broadcast.
    private static Set<PrintWriter> writers = new HashSet<>();

    /**
     * The client handler task.
     */
    private String name;
    private String machine;
    private String port;
    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    /**
     * Constructs a handler thread, squirreling away the socket. All the
     * interesting work is done in the run method. Remember the constructor is
     * called from the server's main method, so this has to be as short as
     * possible.
     */
//    public ServerThread(Socket socket) throws IOException {
    public ServerThread() throws IOException {
//        this.socket = socket;
        try (ServerSocket listener = new ServerSocket(59001)) {
            this.socket = listener.accept();
        }
    }

    /**
     * Services this thread's client by repeatedly requesting a screen name
     * until a unique one has been submitted, then acknowledges the name and
     * registers the output stream for the client in a global set, then
     * repeatedly gets inputs and broadcasts them.
     */
    public static String connectHandler(String sendValue) {//function to split a string to surnom, machine and port
        String surnom = "Invalid surnom";
        String machine = "Invalid Machine";
        int port = 0;

        String msgStartSubString = "_connect <";
        String machineAndPortString = null;
        int indexMachineStart;
        int indexMachineEnd = 0;
        String start = "<";
        String end = ">";
        if (!sendValue.isEmpty() && sendValue.toLowerCase().startsWith(msgStartSubString)) {
            int indexNameStart = sendValue.toLowerCase().indexOf(msgStartSubString) + msgStartSubString.length();
            int indexNameEnd = sendValue.toLowerCase().indexOf(end);
            if (indexNameStart != -1 && indexNameEnd != -1) {
                surnom = sendValue.substring(indexNameStart, indexNameEnd);
            }
            if (!surnom.toLowerCase().equals("invalid surnom")) {
                machineAndPortString = sendValue.substring(indexNameEnd + 1).trim();
                indexMachineStart = machineAndPortString.toLowerCase().indexOf(start) + 1;
                indexMachineEnd = machineAndPortString.toLowerCase().indexOf(end);
                if (indexMachineStart != -1 && indexMachineEnd != -1) {
                    machine = machineAndPortString.substring(indexMachineStart, indexMachineEnd);
                }
            }
            if (!machine.toLowerCase().equals("invalid machine")) {
                String portString = machineAndPortString.substring(indexMachineEnd + 1).trim();
                int indexPortStart = portString.toLowerCase().indexOf(start) + 1;
                int indexPortEnd = portString.toLowerCase().indexOf(end);
                if (indexPortStart != -1 && indexPortEnd != -1) {
                    port = parseInt(portString.substring(indexPortStart, indexPortEnd));
                }
            }
        }
        return surnom + "~" + machine + "~" + port;
    }

    public void run() {
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            // Keep requesting a name until we get a unique one.
            while (true) {
                out.println("SUBMITNAME");
//                String[] returnedSurnomMachinePort = connectHandler(in.nextLine()).split("~");
                String[] returnedSurnomMachinePort = in.nextLine().split("~");

                name = returnedSurnomMachinePort[0];
                machine = returnedSurnomMachinePort[1];
                port = returnedSurnomMachinePort[2];

                System.out.println("name " + name);
                System.out.println("machine " + machine);
                System.out.println("port " + port);

                if (name == null) {
                    return;
                }
                synchronized (names) {//synchronize name bcz --> do not allow 2 user with same name in the same time to add it in names
                    if (name != "" && !names.contains(name)) {
                        names.add(name);
                        break;
                    }
                }
            }

            // Now that a successful name has been chosen, add the socket's print writer
            // to the set of all writers so this client can receive broadcast messages.
            // But BEFORE THAT, let everyone else know that the new person has joined!
            out.println("NAMEACCEPTED " + name);
            for (PrintWriter writer : writers) {
                writer.println("MESSAGE " + name + " has joined");
            }
            writers.add(out);

            // Accept messages from this client and broadcast them.
            while (true) {
                String input = in.nextLine();
                if (input.toLowerCase().startsWith("_quit")) {
                    return;
                }
                if (input.toLowerCase().equals("_who")) {
                    int count = 0;
                    String test = names.size() > 1
                            ? "The users are (" + (names.size() - 1) + ") : "
                            : "The user is : ";
                    out.println("MESSAGE " + test);
                    for (String userName : names) {
                        if (!userName.toLowerCase().equals(name)) {
                            count++;
                            out.println("MESSAGE " + count + " : " + userName + " Is Connected");
                        }
                    }
                    out.println("MESSAGE " + "End of WHO");
                }
                if (!input.toLowerCase().equals("_who")) {
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + name + ": " + input);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (out != null) {
                writers.remove(out);
            }
            if (name != null) {
                System.out.println(name + " is leaving");
                names.remove(name);
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + name + " has left");
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}
