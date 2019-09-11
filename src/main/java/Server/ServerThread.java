/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.awt.event.ActionEvent;
import static java.lang.System.exit;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author mina2
 */
public class ServerThread implements Runnable {

    JTextField textField;
    JTextArea messageArea;
    JFrame frame;
    ClientThread clientThread;
    Thread thread;

    public ServerThread(JTextField textField, JTextArea messageArea, JFrame frame, ClientThread clientThread) {
        this.textField = textField;
        this.messageArea = messageArea;
        this.frame = frame;
        this.clientThread = clientThread;
    }

    @Override
    public void run() {
        // Send on enter then clear to prepare for next message
        textField.addActionListener((ActionEvent e) -> {
            String serverMsg = textField.getText();
            if (!serverMsg.equals("") && serverMsg != null) {
                if (serverMsg.toLowerCase().trim().equals("_who")) {
                    messageArea.append("Server : " + clientThread.getClients("server") + ". \n");
                } else if (serverMsg.toLowerCase().trim().startsWith("_kill")) {
                    JTextField surnomField = new JTextField(5);

                    Object[] inputFields = {
                        "Surnom :", surnomField
                    };

                    String surnom = serverMsg.substring(5);
                    if (!surnom.equals("")) {
                        surnomField.setText(surnom);
                    }

                    int option = JOptionPane.showConfirmDialog(//open confirm modal
                            frame, //parent component
                            inputFields,//panel
                            "Please Enter a User Name",//title
                            JOptionPane.OK_CANCEL_OPTION
                    );

                    if (option == JOptionPane.OK_OPTION) {//if user clicks on OK
                        surnom = surnomField.getText();
                        clientThread.Kill(surnom);
                        messageArea.append("Server : (_kill command) " + surnom + " is disconnected by the server. \n");
                    } else {//if user clicks on cancel
                        messageArea.append("Server : (_kill command) has been cancelled. \n");
                    }

                } else if (serverMsg.toLowerCase().trim().equals("_shutdown")) {
                    messageArea.append("Server : " + serverMsg + ". \n");
                    exit(0);
                } else {
                    messageArea.append("Server : " + serverMsg + " (Invalid Input). \n");
                }
            }
            textField.setText("");
        });
    }
}
