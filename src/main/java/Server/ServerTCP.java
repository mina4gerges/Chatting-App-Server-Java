package Server;

/**
 * A multithreaded chat room server. When a client connects the server requests
 * a screen name by sending the client the text "SUBMITNAME", and keeps
 * requesting a name until a unique one is received. After a client submits a
 * unique name, the server acknowledges with "NAMEACCEPTED". Then all messages
 * from that client will be broadcast to all other clients that have submitted a
 * unique screen name. The broadcast messages are prefixed with "MESSAGE".
 */
public class ServerTCP {

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
//        ExecutorService pool = Executors.newFixedThreadPool(500); //another method to use thread (ExecutorService specify number of users)
//        try (ServerSocket listener = new ServerSocket(59001)) {//every new user needs a new thread
        while (true) {
//            ServerThread serverThread = new ServerThread(listener.accept());
            ServerThread serverThread = new ServerThread();
            Thread thread = new Thread(serverThread);
            thread.start(); //start the thread
//                pool.execute(new ServerThread(listener.accept())); //another method to use thread (ExecutorService specify number of users)
        }
    }
//    }

}
