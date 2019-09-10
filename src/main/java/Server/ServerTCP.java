/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * A multithreaded chat room server. When a client connects the server requests
 * a screen name by sending the client the text "SUBMITNAME", and keeps
 * requesting a name until a unique one is received. After a client submits a
 * unique name, the server acknowledges with "NAMEACCEPTED". Then all messages
 * from that client will be broadcast to all other clients that have submitted a
 * unique screen name. The broadcast messages are prefixed with "MESSAGE".
 *
 * @author mina2
 */
public class ServerTCP {

    static JFrame frame = new JFrame("Server");
    static JTextField textField = new JTextField(50);
    static JTextArea messageArea = new JTextArea(16, 50);

    public static void main(String[] args) throws Exception {
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        textField.setEditable(true);
        messageArea.append("The chat server is running ... \n");
//        ExecutorService pool = Executors.newFixedThreadPool(500); //another method to use thread (ExecutorService specify number of users)
//        try (ServerSocket listener = new ServerSocket(59001)) {//every new user needs a new thread
        int count = 0;
        while (true) {
            count++;
            ClientThread clientThread = new ClientThread(messageArea);
            Thread thread = new Thread(clientThread);
            thread.start(); //start the thread

            if (count == 1) {
                ServerThread serverThread = new ServerThread(textField, messageArea, frame, clientThread, thread);
                Thread thread1 = new Thread(serverThread);
                thread1.start(); //start the thread
            }

            //ServerThread clientThread = new ClientThread(listener.accept());
            //pool.execute(new ClientThread(listener.accept())); //another method to use thread (ExecutorService specify number of users)
        }
    }
}
