package com.client;

import com.utils.ClientWindow;
import com.utils.FileManager;
import com.utils.Request;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class LightFTPClient {
    // Comm Variables
    private static Socket socket;
    private static DataOutputStream outBytes;
    private static DataInputStream inBytes;
    private static ClientWindow cWindow;
    private static JFileChooser fileChooser = new JFileChooser();
    public static void initalize (){
        try {
            // Prepara socket e streams de entrada e saída
            socket = new Socket("localhost", 7777);
            outBytes = new DataOutputStream(socket.getOutputStream());
            inBytes = new DataInputStream(socket.getInputStream());

            // Gerar janela
            cWindow = new ClientWindow();

            // Setup Save Button
            cWindow.getSaveButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int returnVal = fileChooser.showSaveDialog(cWindow.mainPanel);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File folder = fileChooser.getSelectedFile();

                        try {
                            System.out.println(cWindow.getFileList().getSelectedValue());
                            String data = getFileFromServer(cWindow.getFileList().getSelectedValue());
                            String filename = data.substring(0, data.indexOf(","));
                            String filestring = data.substring(data.indexOf(",") + 1, data.indexOf("\r\n"));

                            FileManager fm = new FileManager(folder.getPath());

                            fm.saveFile(filename, filestring);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                    } else {
                        System.out.println("Cancelado");
                    }
                }
            });

            // Setup Upload Button
            cWindow.getUploadButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int returnVal = fileChooser.showOpenDialog(cWindow.mainPanel);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        // Seleciona arquivo
                        File file = fileChooser.getSelectedFile();

                        try {
                            sendFile(file);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                    } else {
                        System.out.println("Cancelado");
                    }
                }
            });
        } catch (IOException e) {
            System.out.println(e + " \nFalha na conexão com o servidor!");
        }
    }


    public static String getFileFromServer(String filename) throws IOException{
        Request outRequest = new Request();

        outRequest.setCommand("GET");
        outRequest.setData(filename);

        outBytes.writeUTF(outRequest.toString());

        String rcvdMessage = inBytes.readUTF();

        try {
            Request inRequest = new Request(rcvdMessage);

            if (inRequest.getStatus() == "SUCCESS") {

                return inRequest.getData();
            } else {
                System.out.println("ERROR");
            }
        } catch (IllegalArgumentException e) {

            System.out.println("ERROR");
            e.printStackTrace();
        }
        return null;
    }

    public static void getFileList() throws IOException{
        Request outRequest = new Request();

        outRequest.setCommand("LIST");

        outBytes.writeUTF(outRequest.toString());

        String rcvdMessage = inBytes.readUTF();

        try {
            Request inRequest = new Request(rcvdMessage);
            cWindow.updateList(inRequest.getData());
        } catch (IllegalArgumentException e) {

            System.out.println("ERROR");
            e.printStackTrace();
        }

    }

    public static void sendFile(File file) throws IOException {
        // Prepara requisição
        Request outRequest = new Request();
        outRequest.setCommand("POST");
        outRequest.setData(file.getName() + "," + FileManager.fileToString(file));

        // Envia requisição
        outBytes.writeUTF(outRequest.toString());

        String rcvdMessage = inBytes.readUTF();

        try {
            Request inRequest = new Request(rcvdMessage);
            if (inRequest.getStatus() == "SUCCESS") {
                getFileList();
            } else {
                System.out.println("ERROR");
            }
        } catch (IllegalArgumentException e) {

            System.out.println("ERROR");
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {

        // Inicializa sockets
        initalize();

        // Get file list
        getFileList();
    }
}
