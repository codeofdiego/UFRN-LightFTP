package com.server;

import com.utils.FileManager;
import com.utils.Request;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LightFTPServer {
    public static void main(String[] args) throws IOException {
        // Setup
        ServerSocket connectionSocket = new ServerSocket(7777);
        Socket socket;
        DataInputStream inBytes;
        DataOutputStream outBytes;
        String filename = "";
        // Debug
        System.out.println("Servidor em execução...");

        try {
            // Loop de execução do servidor
            while (true) {
                // Aceita nova conexão do cliente
                System.out.println("Aguardando conexão do cliente...");
                socket = connectionSocket.accept();

                // Debug
                System.out.println("Cliente " + socket.getInetAddress().getHostAddress() + " conectado!");

                // Prepara stream de entrada de dados
                inBytes = new DataInputStream(socket.getInputStream());

                // Prepara stream de retorno de dados
                outBytes = new DataOutputStream(socket.getOutputStream());

                // Instancia gerenciador de arquivos
                FileManager fileManager = new FileManager("./ftp/");

                try {
                    // Lê dados enviados pelo cliente e converte para string
                    String rcvdMessage;
                    do {
                        rcvdMessage = inBytes.readUTF();

                        if (rcvdMessage == "-1") {
                            System.out.println("Conexão perdida...");
                            break;
                        };

                        Request request = new Request(rcvdMessage);

                        System.out.println("Mensagem recebida: " + request.toString());

                        // Interpreta mensagem do cliente
                        System.out.println("Command: " + request.getCommand());

                        if (request.getCommand().equals("LIST")) {
                            String fileList = fileManager.listFiles();
                            System.out.println("fileList: " + fileList);
                            request.setData(fileList);
                            request.setStatus("SUCCESS");
                        } else if (request.getCommand().equals("GET")) {
                            filename = request.getData();
                            if (filename == null) {
                                request.setStatus("FAILURE");
                            } else {
                                String fileString = fileManager.getFile(filename);
                                if (fileString == null) {
                                    request.setStatus("FAILURE");
                                } else {
                                    request.setData(fileString);
                                    request.setStatus("SUCCESS");
                                }
                            }
                        }else if (request.getCommand().equals("CHECKFILE")) {
                            filename = request.getData();
                            if (filename == null) {
                                request.setStatus("FAILURE");
                            } else {
                                String fileString = fileManager.getFile(filename);
                                if (fileString == null) {
                                    request.setStatus("FAILURE");
                                } else {
                                    request.setStatus("SUCCESS");
                                }
                            }
                        }else if (request.getCommand().equals("POST")) {
                            String data = request.getData();
                            filename = data.substring(0, data.indexOf(","));
                            String file = data.substring(data.indexOf(",") + 1, data.indexOf("\r\n"));
                            Boolean success = fileManager.saveFile(filename, file);
                            if (success) {
                                request.setData(null);
                                request.setStatus("SUCCESS");
                            } else {
                                request.setData(null);
                                request.setStatus("FAILURE");
                            }
                        } else if (request.getCommand().equals("END")) {
                                request.setStatus("SUCCESS");
                        } else {
                                System.out.println("Nenhum comando reconhecido");
                                request.setStatus("FAILURE");
                                request.setData(null);
                        }
                        System.out.println(request.toString());

                        // Ecoa mensagem recebida de volta para o cliente com a resposta
                        outBytes.writeUTF(request.toString());
                    } while (!(rcvdMessage.equals("bye")));

                    // Fecha conexão
                    inBytes.close();
                    outBytes.close();
                    socket.close();
                } catch (NullPointerException | EOFException error) {
                    System.err.println(error);

                    inBytes.close();
                    outBytes.close();
                    socket.close();
                }

            }
        } catch (Exception error) {
            System.err.println(error);
        }
    }
}
